package cz.skorpils.oryx.bn.computation.model;

/**
 * Created by stopka on 6.5.14.
 */
public class FeatureNode extends Node {
    String name;

    public FeatureNode(long id, String name) {
        super(id);
        this.name = name;
    }
}
