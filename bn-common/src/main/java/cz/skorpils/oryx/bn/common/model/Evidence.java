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

    public LongObjectMap<Integer> getValues(){
        return values;
    }

    public boolean isForMe(Node node){
        return node.isLayer(layer);
    }
}
