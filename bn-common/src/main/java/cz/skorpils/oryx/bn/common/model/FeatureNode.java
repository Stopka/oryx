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
            return 1d- getNodeCondProbability(1, parentValuesCond);
        }
        if(val==1){
            return (double)children.size()/(double)container.getLower().size();
        }
        return 0d;
    }

    @Override
    protected double weight(long parentId,int parentVal,int myVal){return 0d;}

    @Override
    protected boolean isLayer(String layer) {
        return layer.equals(Layer.FEATURE);
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
