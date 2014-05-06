package cz.skorpils.oryx.bn.computation.model;

/**
 * Created by stopka on 6.5.14.
 */
public class ItemNode extends Node<FeatureNode> {
    String name;

    public ItemNode(long id, String name) {
        super(id);
        this.name = name;
    }
}
