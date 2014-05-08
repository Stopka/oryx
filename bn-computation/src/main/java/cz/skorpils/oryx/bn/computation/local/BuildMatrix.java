package cz.skorpils.oryx.bn.computation.local;

import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import cz.skorpils.oryx.bn.common.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by stopka on 6.5.14.
 */
public class BuildMatrix implements Callable<BayesNetwork> {
    private static final Logger log = LoggerFactory.getLogger(BuildMatrix.class);
    File inputDir;


    public BuildMatrix(File inputDir){
        this.inputDir=inputDir;
    }

    @Override
    public BayesNetwork call() throws IOException {
        log.info("Building network");
        NodeContainer<FeatureNode> features = new BuildFeatureMatrix().call();
        NodeContainer<ItemNode> items = new BuildItemMatrix(features, inputDir).call();
        NodeContainer<UserNode> users = new BuildUserMatrix(items, inputDir).call();
        NodeContainer<VoteNode> votes = new BuildVoteMatrix(users).call();
        return new BayesNetwork(features,items,users,votes);
    }
}
