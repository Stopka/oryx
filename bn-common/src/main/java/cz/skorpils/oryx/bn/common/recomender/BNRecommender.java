package cz.skorpils.oryx.bn.common.recomender;

import cz.skorpils.oryx.bn.common.NoSuchUserException;
import cz.skorpils.oryx.bn.common.NotReadyException;
import cz.skorpils.oryx.bn.common.model.BayesNetwork;
import cz.skorpils.oryx.bn.common.model.Evidence;
import cz.skorpils.oryx.bn.common.model.Layer;

import java.util.List;
import java.util.Set;

/**
 * Created by stopka on 11.6.14.
 */
public class BNRecommender implements OryxRecommender {
    BayesNetwork model;
    public BNRecommender(BayesNetwork model){
        this.model=model;
    }
    @Override
    public List<Long> recommend(String userIDString, int howMany) throws NoSuchUserException, NotReadyException {
        long userID=getID(userIDString);
        Set<Long> itemIDs=model.getUnratedItemIDs(userID);
        System.out.println("USER "+userID);
        for (Long itemID:itemIDs){
            System.out.println("ITEM "+itemID);
            for(int rate=0;rate<=model.getMaxRate();rate++){
                double prob=model.getCondProbability(Layer.VOTE, userID, rate, new Evidence(Layer.ITEM).addValue(itemID, 1));
                System.out.println("RATE "+rate+" probability "+prob);
            }
        }
        return null;
    }

    private long getID(String ID){
        return Long.parseLong(ID);
    }
}
