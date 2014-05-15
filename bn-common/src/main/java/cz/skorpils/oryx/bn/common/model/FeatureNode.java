package cz.skorpils.oryx.bn.common.model;

/**
 * Created by stopka on 6.5.14.
 */
public class FeatureNode extends Node<Node,ItemNode> {
    String name;

    public FeatureNode(long id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public double getNodeCondProbability(int val, Evidence parentValuesCond){
        if(val==0){
            return 1- getNodeCondProbability(1, parentValuesCond);
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
    protected boolean isParentLayer(String layer) {
        return false;
    }

    @Override
    public String toString() {
        return "F"+super.toString()+" "+name;
    }

}
