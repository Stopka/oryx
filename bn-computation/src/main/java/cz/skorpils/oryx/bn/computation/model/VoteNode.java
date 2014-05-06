package cz.skorpils.oryx.bn.computation.model;

/**
 * Created by stopka on 6.5.14.
 */
public class VoteNode extends Node<UserNode> {
    UserNode originalNode;

    public VoteNode(UserNode node) {
        super(node.getId());
        this.originalNode = node;
    }
}
