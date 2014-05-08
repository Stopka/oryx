package cz.skorpils.oryx.bn.common.model;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by stopka on 8.5.14.
 */
public class BayesNetwork {
    HashMap<String,NodeContainer> containers=new HashMap<String, NodeContainer>();
    public BayesNetwork(NodeContainer<FeatureNode> features,
                        NodeContainer<ItemNode> items,
                        NodeContainer<UserNode> users,
                        NodeContainer<VoteNode> votes){
        containers.put("feature",features);
        containers.put("item",items);
        containers.put("user",users);
        containers.put("vote",votes);
    }

    public double getCondProbability(String layer,long id,int val, Evidence evidence){
        return containers.get(layer).getCondProbability(id,val,evidence);
    }
}
