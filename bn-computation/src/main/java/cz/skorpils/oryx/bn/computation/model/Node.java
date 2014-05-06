package cz.skorpils.oryx.bn.computation.model;

import com.cloudera.oryx.common.collection.LongObjectMap;

/**
 * Created by stopka on 6.5.14.
 */
public abstract class Node<ParentNodeType extends Node> {
    LongObjectMap<ParentNodeType> parents = new LongObjectMap<ParentNodeType>();
    long id;

    protected Node(long id) {
        this.id = id;
    }

    public void addParent(ParentNodeType node) {
        parents.put(node.getId(), node);
    }

    public long getId() {
        return id;
    }

    public LongObjectMap<ParentNodeType> getParents() {
        return parents;
    }
}
