package cz.skorpils.oryx.bn.computation.model;

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
    public double getProb(long id,int val,LongObjectMap<Integer> cond){
        if(val==0){
            return 1-super.getProb(id,1,cond);
        }
        if(val==1){
            return super.getProb(id,val,cond);
        }
        return 0;
    }

    @Override
    protected double weight(long idA,int valA,long idB,int valB){
        if(valA==0){
            return 0;
        }
        double top=Math.log((1/parents.get(idA).getProb(0,1,null))+1);
        double bottomLeft=Math.log(container.size()+1);
        double bottomRight=0;
        Iterator<Long> it=parents.keySetIterator();
        while (it.hasNext()){
            long id=it.next();
            bottomRight+=Math.log((1/parents.get(id).getProb(0,1,null))+1)/Math.log(container.size()+1);
        }
        return top/(bottomLeft*bottomRight);
    }
}
