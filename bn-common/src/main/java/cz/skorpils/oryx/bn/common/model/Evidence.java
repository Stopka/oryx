package cz.skorpils.oryx.bn.common.model;

import com.cloudera.oryx.common.collection.LongObjectMap;

/**
 * Created by stopka on 8.5.14.
 */
public class Evidence {
    LongObjectMap<Integer> values;
    String layer;

    public Evidence(String layer,LongObjectMap<Integer> values){
        this.values=values;
        this.layer=layer;
    }

    public Evidence(String layer){
        this(layer, new LongObjectMap<Integer>());
    }

    public Evidence addValue(long id,int value){
        this.values.put(id,value);
        return this;
    }

    public int get(long id){
        if(!values.containsKey(id)){
            return 0;
        }
        return values.get(id);
    }

    public boolean isForNode(Node node){
        return node.isParentLayer(layer);
    }

    @Override
    public String toString() {
        return "Evidence "+layer+values.toString();
    }
}
