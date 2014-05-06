package cz.skorpils.oryx.bn.computation.model;

import com.cloudera.oryx.common.collection.LongObjectMap;

import java.util.Iterator;

/**
 * Created by stopka on 6.5.14.
 */
public abstract class Node<ParentNodeType extends Node,ChildrenNodeType extends Node> {
    LongObjectMap<ParentNodeType> parents = new LongObjectMap<ParentNodeType>();
    LongObjectMap<ParentNodeType> children = new LongObjectMap<ParentNodeType>();
    NodeContainer<?> container=null;
    long id;

    protected Node(long id) {
        this.id = id;
    }

    public void addParent(ParentNodeType node) {
        parents.put(node.getId(), node);
        node.children.put(id,this);
    }

    public long getId() {
        return id;
    }

    public LongObjectMap<ParentNodeType> getParents() {
        return parents;
    }

    public LongObjectMap<ParentNodeType> getChildren() {
        return children;
    }

    protected double getProb(long id,int val,LongObjectMap<Integer> cond){
        double result=0;
        Iterator<Long> iterator=cond.keySetIterator();
        while (iterator.hasNext()){
            long idA=iterator.next();
            result+=weight(idA,cond.get(idA),id,val);
        }
        return result;
    }

    abstract protected double weight(long idA,int valA,long idB,int valB);
}
