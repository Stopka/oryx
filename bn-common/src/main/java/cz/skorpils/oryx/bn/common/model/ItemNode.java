package cz.skorpils.oryx.bn.common.model;

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
    public double getNodeCondProbability(int val, Evidence parentValuesCond){
        if(val==0){
            return 1d-super.getNodeCondProbability(1, parentValuesCond);
        }
        if(val==1){
            return super.getNodeCondProbability(val, parentValuesCond);
        }
        return 0;
    }

    @Override
    protected double weight(long parentId,int parentVal,int myVal){
        if(parentVal==0){
            return 0;
        }
        double top=Math.log(1d/(double)parents.get(parentId).getNodeCondProbability(parentVal, null)+1);
        double bottomLeft=Math.log((double)container.size()+1d);
        double bottomRight=0d;
        Iterator<Long> it=parents.keySetIterator();
        while (it.hasNext()){
            long id=it.next();
            bottomRight+=Math.log((1d/parents.get(id).getNodeCondProbability(1, null))+1)/Math.log((double)container.size()+1d);
        }
        return top/(bottomLeft*bottomRight);
    }

    @Override
    protected boolean isLayer(String layer) {
        return layer.equals(Layer.ITEM);
    }

    @Override
    protected boolean isParentLayer(String layer) {
        return layer.equals(Layer.FEATURE);
    }

    @Override
    public String toString() {
        return "I"+super.toString()+" "+name;
    }
}
