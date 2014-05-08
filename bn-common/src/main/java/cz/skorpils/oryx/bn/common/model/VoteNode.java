package cz.skorpils.oryx.bn.common.model;

import com.cloudera.oryx.common.collection.LongObjectMap;

import java.util.Iterator;

/**
 * Created by stopka on 6.5.14.
 */
public class VoteNode extends Node<UserNode,Node> {
    UserNode originalNode;

    public VoteNode(UserNode node) {
        super(node.getId());
        this.originalNode = node;
    }

    @Override
    protected double weight(long parentId, int parentVal, int myVal) {
        double alpha=container.getStorageValue("alpha");
        if(parentId==getId()){
            if(parentVal==myVal) {
                return alpha;
            }
            return 0;
        }
        double beta=container.getStorageValue("beta");
        double qs=container.getStorageValue("qs");
        LongObjectMap<ItemNode> unification=getParentUnification(getParents().get(parentId),originalNode);
        double topLeft=1-alpha;
        double topRight=n(parentId,parentVal,myVal,unification)+beta*qs;
        double bottomLeft=parents.size()-1;
        double bottomRight=n(parentId,parentVal,unification)+beta;
        return (topLeft*topRight)/(bottomLeft*bottomRight);
    }

    /**
     * @param parentId
     * @param parentVal
     * @param myVal
     * @return number of items from parent.getParents+this.getParents voted with parentVal by parent and myVal by Ua=this user
     */
    private double n(long parentId, int parentVal, int myVal,LongObjectMap<ItemNode> unification){
        double result=0;
        Iterator<Long> it=unification.keySetIterator();
        while (it.hasNext()){
            long item=it.next();
            if(originalNode.getRating(item)==myVal&&getParents().get(parentId).getRating(item)==parentVal){
                result+=1;
            }
        }
        return result;
    }

    /**
     * @param parentId
     * @param parentVal
     * @return number of items from parent.getParents+this.getParents voted with parentVal by parent
     */
    private double n(long parentId, int parentVal, LongObjectMap<ItemNode> unification){
        double result=0;
        Iterator<Long> it=unification.keySetIterator();
        while (it.hasNext()){
            long item=it.next();
            if(getParents().get(parentId).getRating(item)==parentVal){
                result+=1;
            }
        }
        return result;
    }

    private LongObjectMap<ItemNode> getParentUnification(UserNode a, UserNode b){
        LongObjectMap<ItemNode> result = new LongObjectMap<ItemNode>();
        LongObjectMap<ItemNode> nodesA = a.getParents();
        LongObjectMap<ItemNode> nodesB = b.getParents();
        Iterator<Long> iterator = nodesA.keySetIterator();
        while (iterator.hasNext()) {
            long node = iterator.next();
            result.put(node, nodesA.get(node));
        }
        iterator = nodesB.keySetIterator();
        while (iterator.hasNext()) {
            long node = iterator.next();
            if (!result.containsKey(node)) {
                result.put(node, nodesB.get(node));
            }
        }
        return result;
    }

    @Override
    protected boolean isLayer(String layer) {
        return layer.equals("vote");
    }

    @Override
    public String toString() {
        return "V"+super.toString()+" "+originalNode.ratings.toString();
    }
}
