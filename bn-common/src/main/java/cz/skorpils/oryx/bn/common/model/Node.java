package cz.skorpils.oryx.bn.common.model;

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

    public double getCondProbability(int val, LongObjectMap<Integer> parentValuesCond){
        double result=0;
        Iterator<Long> iterator=getParents().keySetIterator();
        while (iterator.hasNext()){
            long parentId=iterator.next();
            result+=weight(parentId,parentValuesCond.get(parentId),val);
        }
        return result;
    }

    public double getCondProbability(int val, Evidence evidence){
        //TODO
        double result=0;
        Iterator<Long> iterator=getParents().keySetIterator();
        while (iterator.hasNext()){
            long parentId=iterator.next();
            for(int parentVal=0;parentVal<container.upper.getMaxValue();parentVal++) {
                result += weight(parentId, parentVal, val)*getParents().get(parentId).getCondProbability(parentVal,evidence);
            }
        }
        return result;
    }

    abstract protected double weight(long parentId,int parentVal,int myVal);

    abstract protected boolean isLayer(String layer);

    @Override
    public String toString() {
        return Long.toString(id);
    }
}
