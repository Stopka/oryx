package cz.skorpils.oryx.bn.computation.local;

import com.cloudera.oryx.common.collection.LongObjectMap;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import cz.skorpils.oryx.bn.computation.model.ItemNode;
import cz.skorpils.oryx.bn.computation.model.NodeContainer;
import cz.skorpils.oryx.bn.computation.model.UserNode;
import cz.skorpils.oryx.bn.computation.model.VoteNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * Created by stopka on 6.5.14.
 */
public class BuildVoteMatrix implements Callable<NodeContainer<VoteNode>> {
    NodeContainer<UserNode> users;
    int correlation;
    double alpha;
    double beta;
    double qi;
    private static final Logger log = LoggerFactory.getLogger(BuildVoteMatrix.class);

    public BuildVoteMatrix(NodeContainer<UserNode> users) {
        this.users = users;
        Config config = ConfigUtils.getDefaultConfig();
        alpha = config.getInt("model.vote.alpha");
        beta = config.getInt("model.vote.beta");
        qi = config.getInt("model.vote.qi");
        correlation = config.getInt("model.vote.correlation");
    }

    @Override
    public NodeContainer<VoteNode> call() throws IOException {
        log.info("Building vote nodes");
        HashMap<String,Double> storage=new HashMap<String, Double>();
        storage.put("alpha",alpha);
        storage.put("beta",beta);
        storage.put("qi",qi);
        NodeContainer<VoteNode> nodes = new NodeContainer<VoteNode>(users,storage);
        Iterator<Long> users_keys = users.keySetIterator();
        while (users_keys.hasNext()) {
            long user = users_keys.next();
            UserNode nodeA = users.get(user);
            VoteNode node = new VoteNode(nodeA);
            nodes.put(node);
            Iterator<Long> users_keysB = users.keySetIterator();
            while (users_keysB.hasNext()) {
                long userB = users_keysB.next();
                UserNode nodeB = users.get(userB);
                if (evaluateSimilarity(nodeA, nodeB) >= correlation) {
                    node.addParent(nodeB);
                }
            }
        }
        log.info("  result: {} nodes", nodes.size());
        return nodes;
    }

    protected double evaluateSimilarity(UserNode a, UserNode b) {
        LongObjectMap<ItemNode> itemInIntersection = getParentIntersection(a, b);
        if (itemInIntersection.isEmpty()) {
            return 0;
        }
        double sumTop = 0;
        double sumBottomLeft = 0;
        double sumBottomRight = 0;
        Iterator<Long> iterator = itemInIntersection.keySetIterator();
        while (iterator.hasNext()) {
            long itemJ = iterator.next();
            sumTop += ((double) a.getRating(itemJ) - a.getMeanRating()) * ((double) b.getRating(itemJ) - b.getMeanRating());
            sumBottomLeft += countPower((double) a.getRating(itemJ) - a.getMeanRating());
            sumBottomRight += countPower((double) b.getRating(itemJ) - b.getMeanRating());
        }
        return sumTop / Math.sqrt(sumBottomLeft * sumBottomRight);
    }

    protected double countPower(double num) {
        return num + num;
    }

    protected LongObjectMap<ItemNode> getParentIntersection(UserNode a, UserNode b) {
        LongObjectMap<ItemNode> result = new LongObjectMap<ItemNode>();
        LongObjectMap<ItemNode> nodesA = a.getParents();
        LongObjectMap<ItemNode> nodesB = b.getParents();
        Iterator<Long> iterator = nodesA.keySetIterator();
        while (iterator.hasNext()) {
            long node = iterator.next();
            if (nodesB.containsKey(node)) {
                result.put(node, nodesA.get(node));
            }
        }
        return result;
    }
}
