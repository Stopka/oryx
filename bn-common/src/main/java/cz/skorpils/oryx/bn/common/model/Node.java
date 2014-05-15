package cz.skorpils.oryx.bn.common.model;

import com.cloudera.oryx.common.collection.LongObjectMap;

import java.util.Iterator;

/**
 * Created by stopka on 6.5.14.
 */
public abstract class Node<ParentNodeType extends Node,ChildrenNodeType extends Node> {
    LongObjectMap<ParentNodeType> parents = new LongObjectMap<ParentNodeType>();
    LongObjectMap<ChildrenNodeType> children = new LongObjectMap<ChildrenNodeType>();
    NodeContainer<?> container=null;
    long id;

    protected Node(long id) {
        this.id = id;
    }

    public void addParent(ParentNodeType node) {
        parents.put(node.getId(), node);
        node.children.put(id,this);
    }

    public long getId() {
        return id;
    }

    public LongObjectMap<ParentNodeType> getParents() {
        return parents;
    }

    public LongObjectMap<ChildrenNodeType> getChildren() {
        return children;
    }

    public double getNodeCondProbability(int val, Evidence parentValuesCond){
        double result=0d;
        Iterator<Long> iterator=getParents().keySetIterator();
        while (iterator.hasNext()){
            long parentId=iterator.next();
            result+=weight(parentId,parentValuesCond.get(parentId),val);
        }
        return result;
    }

    public double getCondProbability(int val, Evidence evidence){
        if(evidence.isForNode(this)){
            return getNodeCondProbability(val,evidence);
        }
        double result=0d;
        Iterator<Long> iterator=getParents().keySetIterator();
        while (iterator.hasNext()){
            long parentId=iterator.next();
            for(int parentVal=0;parentVal<container.upper.getMaxValue();parentVal++) {
                ParentNodeType parentNode=getParents().get(parentId);
                result += weight(parentId, parentVal, val)*parentNode.getCondProbability(parentVal,evidence);
            }
        }
        return result;
    }

    abstract protected double weight(long parentId,int parentVal,int myVal);

    abstract protected boolean isLayer(String layer);

    abstract protected boolean isParentLayer(String layer);

    @Override
    public String toString() {
        return Long.toString(id);
    }
}
