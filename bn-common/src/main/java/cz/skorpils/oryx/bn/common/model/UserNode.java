package cz.skorpils.oryx.bn.common.model;

import com.cloudera.oryx.common.collection.LongObjectMap;

/**
 * Created by stopka on 6.5.14.
 */
public class UserNode extends Node<ItemNode,VoteNode> {
    LongObjectMap<Integer> ratings = new LongObjectMap<Integer>();
    int ratingSum=0;
    public UserNode(long id){
        super(id);
    }

    public void addParent(ItemNode userNode, int rating) {
        ratings.put(userNode.getId(), rating);
        ratingSum+=rating;
        addParent(userNode);
    }

    public int getRating(long itemId){
        if(ratings.containsKey(itemId)) {
            return ratings.get(itemId);
        }
        return 0;
    }

    public double getMeanRating(){
        return (double)ratingSum/(double)ratings.size();
    }

    @Override
    protected double weight(long parentId, int parentVal, int myVal) {
        switch (parentVal){
            case 0:
                if(myVal==0){
                    return 1/parents.size();
                }
                return 0;
            case 1:
                if(myVal==ratings.get(parentId)){
                    return 1/parents.size();
                }
                return 0;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return "U"+super.toString()+" "+ratings.toString();
    }
}
