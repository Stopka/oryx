package cz.skorpils.oryx.bn.common.model;

import com.cloudera.oryx.common.collection.LongObjectMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by stopka on 6.5.14.
 */
public class NodeContainer<NodeType extends Node> {
    LongObjectMap<NodeType> nodes=new LongObjectMap<NodeType>();
    NodeContainer upper;
    NodeContainer lower;
    int maxValue;
    Map<String,Double> storage=new HashMap<String, Double>();

    public NodeContainer(NodeContainer upper,int maxValue,Map<String,Double> storage){
        this(upper,maxValue);
        this.storage=storage;
    }

    public NodeContainer(NodeContainer upper,int maxValue){
        this.maxValue=maxValue;
        this.upper=upper;
        if(upper!=null) {
            upper.lower = this;
        }
    }

    public NodeContainer(NodeContainer upper){
        this(upper,2);
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

    @Override
    public String toString() {
        return "Container"+nodes.toString()+storage.toString();
    }

    public double getCondProbability(long id,int val, Evidence evidence){
        return nodes.get(id).getCondProbability(val,evidence);
    }

    public int getMaxValue() {
        return maxValue;
    }
}
