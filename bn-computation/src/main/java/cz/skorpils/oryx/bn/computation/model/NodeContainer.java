package cz.skorpils.oryx.bn.computation.model;

import com.cloudera.oryx.common.collection.LongObjectMap;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by stopka on 6.5.14.
 */
public class NodeContainer<NodeType extends Node> {
    LongObjectMap<NodeType> nodes;
    NodeContainer upper;
    NodeContainer lower;
    Map<String,Double> storage;

    public NodeContainer(NodeContainer upper,Map<String,Double> storage){
        this(upper);
        this.storage=storage;
    }

    public NodeContainer(NodeContainer upper){
        this.upper=upper;
        upper.lower=this;
    }

    public NodeContainer(){
        this(null);
    }

    public NodeContainer getUpper() {
        return upper;
    }

    public NodeContainer getLower() {
        return lower;
    }

    public void put(NodeType node){
        nodes.put(node.getId(),node);
        node.container=this;
    }

    public NodeType get(long id){
        return nodes.get(id);
    }

    public boolean isEmpty(){
        return nodes.isEmpty();
    }

    public int size(){
        return nodes.size();
    }

    public boolean containsKey(long id) {
        return nodes.containsKey(id);
    }

    public Iterator<Long> keySetIterator(){
        return nodes.keySetIterator();
    }

    public double getStorageValue(String key){
        return storage.get(key);
    }
}
