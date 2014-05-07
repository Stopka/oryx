package cz.skorpils.oryx.bn.computation.model;

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

    protected double getProb(long id,int val,LongObjectMap<Integer> cond){
        if(val==0){
            return 1-getProb(id, 1, cond);
        }
        if(val==1){
            return children.size()/container.getLower().size();
        }
        return 0;
    }

    @Override
    protected double weight(long idA, int valA, long idB, int valB) {
        return 0;
    }

    @Override
    public String toString() {
        return "F"+super.toString()+" "+name;
    }

}
