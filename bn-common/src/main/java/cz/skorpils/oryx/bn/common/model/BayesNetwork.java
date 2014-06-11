package cz.skorpils.oryx.bn.common.model;

import com.cloudera.oryx.common.collection.LongObjectMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by stopka on 8.5.14.
 */
public class BayesNetwork implements Serializable {
    HashMap<String,NodeContainer> containers=new HashMap<String, NodeContainer>();
    int maxValue;
    public BayesNetwork(NodeContainer<FeatureNode> features,
                        NodeContainer<ItemNode> items,
                        NodeContainer<UserNode> users,
                        NodeContainer<VoteNode> votes){
        containers.put(Layer.FEATURE,features);
        containers.put(Layer.ITEM,items);
        containers.put(Layer.USER,users);
        containers.put(Layer.VOTE,votes);
        maxValue=users.getMaxValue();
    }

    public double getCondProbability(String layer,long id,int val, Evidence evidence){
        return containers.get(layer).getCondProbability(id,val,evidence);
    }

    public int getMaxRate(){
        return containers.get(Layer.USER).getMaxValue();
    }

    public Set<Long> getUnratedItemIDs(long userID){
        UserNode user=((NodeContainer<UserNode>)containers.get(Layer.USER)).get(userID);
        LongObjectMap<ItemNode> votedItemIDs=user.getParents();
        NodeContainer<ItemNode> items=containers.get(Layer.ITEM);
        Set<Long> result=new HashSet<Long>();
        Iterator<Long> idIterator=items.keySetIterator();
        while (idIterator.hasNext()){
            long itemID=idIterator.next();
            if(!votedItemIDs.containsKey(itemID)){
                result.add(itemID);
            }
        }
        return result;
    }
}
