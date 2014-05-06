package cz.skorpils.oryx.bn.computation.model;

import com.cloudera.oryx.common.collection.LongObjectMap;

/**
 * Created by stopka on 6.5.14.
 */
public class UserNode extends Node<ItemNode> {
    LongObjectMap<Integer> ratings = new LongObjectMap<Integer>();
    int maxValue;
    int ratingSum=0;
    public UserNode(long id,int maxValue){
        super(id);
        this.maxValue = maxValue;
    }

    public void addParent(ItemNode userNode, int rating) {
        ratings.put(userNode.getId(), rating);
        ratingSum+=rating;
        addParent(userNode);
    }

    public int getRating(long itemId){
        return ratings.get(itemId);
    }

    public double getMeanRating(){
        return (double)ratingSum/(double)ratings.size();
    }
}
