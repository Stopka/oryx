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
        //TODO
    }
}
