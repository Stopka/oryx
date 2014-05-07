package cz.skorpils.oryx.bn.computation.model;

/**
 * Created by stopka on 6.5.14.
 */
public class VoteNode extends Node<UserNode,Node> {
    UserNode originalNode;

    public VoteNode(UserNode node) {
        super(node.getId());
        this.originalNode = node;
    }

    @Override
    protected double weight(long idA, int valA, long idB, int valB) {
        if(idA==idB&&valB==originalNode.getRating(idB)){
            if(valA==valB) {
                return container.getStorageValue("alpha");
            }
            return 0;
        }
        //TODO
        return (1-container.getStorageValue("alpha"))*-1/((parents.size()-1)*(-1+container.getStorageValue("beta")));
    }

    @Override
    public String toString() {
        return "V"+super.toString()+" "+originalNode.ratings.toString();
    }
}
