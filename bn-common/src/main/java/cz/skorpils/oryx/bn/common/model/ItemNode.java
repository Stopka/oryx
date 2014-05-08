package cz.skorpils.oryx.bn.common.model;

import com.cloudera.oryx.common.collection.LongObjectMap;

import java.util.Iterator;

/**
 * Created by stopka on 6.5.14.
 */
public class ItemNode extends Node<FeatureNode,UserNode> {
    String name;

    public ItemNode(long id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public double getCondProbability(int val, LongObjectMap<Integer> parentValuesCond){
        if(val==0){
            return 1-super.getCondProbability(1, parentValuesCond);
        }
        if(val==1){
            return super.getCondProbability(val, parentValuesCond);
        }
        return 0;
    }

    @Override
    protected double weight(long parentId,int parentVal,int myVal){
        if(parentVal==0){
            return 0;
        }
        double top=Math.log(1/parents.get(parentId).getCondProb(parentVal,null)+1);
        double bottomLeft=Math.log(container.size()+1);
        double bottomRight=0;
        Iterator<Long> it=parents.keySetIterator();
        while (it.hasNext()){
            long id=it.next();
            bottomRight+=Math.log((1/parents.get(id).getCondProb(1, null))+1)/Math.log(container.size()+1);
        }
        return top/(bottomLeft*bottomRight);
    }

    @Override
    protected boolean isLayer(String layer) {
        return layer.equals("item");
    }

    @Override
    public String toString() {
        return "I"+super.toString()+" "+name;
    }
}
