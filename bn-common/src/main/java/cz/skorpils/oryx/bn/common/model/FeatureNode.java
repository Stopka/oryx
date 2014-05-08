package cz.skorpils.oryx.bn.common.model;

import com.cloudera.oryx.common.collection.LongObjectMap;

/**
 * Created by stopka on 6.5.14.
 */
public class FeatureNode extends Node<Node,ItemNode> {
    String name;

    public FeatureNode(long id, String name) {
        super(id);
        this.name = name;
    }

    public double getCondProb(int val, LongObjectMap<Integer> parentValuesCond){
        if(val==0){
            return 1- getCondProb(1, parentValuesCond);
        }
        if(val==1){
            return children.size()/container.getLower().size();
        }
        return 0;
    }

    @Override
    protected double weight(long parentId,int parentVal,int myVal){return 0;}

    @Override
    protected boolean isLayer(String layer) {
        return layer.equals("feature");
    }

    @Override
    public String toString() {
        return "F"+super.toString()+" "+name;
    }

}
