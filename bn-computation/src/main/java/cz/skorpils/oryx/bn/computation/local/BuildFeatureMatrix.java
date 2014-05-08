package cz.skorpils.oryx.bn.computation.local;

import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import cz.skorpils.oryx.bn.common.model.FeatureNode;
import cz.skorpils.oryx.bn.common.model.NodeContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by stopka on 6.5.14.
 */
public class BuildFeatureMatrix implements Callable<NodeContainer<FeatureNode>> {
    String[] featureNames;
    private static final Logger log = LoggerFactory.getLogger(BuildFeatureMatrix.class);

    public BuildFeatureMatrix(){
        Config config = ConfigUtils.getDefaultConfig();
        featureNames=(String[])config.getStringList("model.item.feature.names").toArray(new String[0]);
    }

    @Override
    public NodeContainer<FeatureNode> call() throws IOException {
        log.info("Building feature nodes");
        int i=0;
        NodeContainer<FeatureNode> nodes=new NodeContainer<FeatureNode>();
        for(String name:featureNames){
            FeatureNode node=new FeatureNode(i,featureNames[i]);
            nodes.put(node);
            i++;
        }
        log.info("  result: {} nodes", nodes.size());
        return nodes;
    }
}
