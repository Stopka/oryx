package cz.skorpils.oryx.bn.computation.local;

import com.cloudera.oryx.common.collection.LongObjectMap;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import cz.skorpils.oryx.bn.computation.model.ItemNode;
import cz.skorpils.oryx.bn.computation.model.UserNode;
import cz.skorpils.oryx.bn.computation.model.VoteNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * Created by stopka on 6.5.14.
 */
public class BuildVoteMatrix implements Callable<LongObjectMap<VoteNode>> {
    LongObjectMap<UserNode> users;
    int coefficient;
    private static final Logger log = LoggerFactory.getLogger(BuildVoteMatrix.class);

    public BuildVoteMatrix(LongObjectMap<UserNode> users) {
        this.users = users;
        Config config = ConfigUtils.getDefaultConfig();
        coefficient = config.getInt("model.correlation-coefficient");
    }

    @Override
    public LongObjectMap<VoteNode> call() throws IOException {
        log.info("Building vote nodes");
        LongObjectMap<VoteNode> nodes = new LongObjectMap<VoteNode>();
        Iterator<Long> users_keys = users.keySetIterator();
        while (users_keys.hasNext()) {
            long user = users_keys.next();
            UserNode nodeA = users.get(user);
            VoteNode node = new VoteNode(nodeA);
            nodes.put(node.getId(), node);
            Iterator<Long> users_keysB = users.keySetIterator();
            while (users_keysB.hasNext()) {
                long userB = users_keysB.next();
                UserNode nodeB = users.get(userB);
                if (evaluateSimilarity(nodeA, nodeB) >= coefficient) {
                    node.addParent(nodeB);
                }
            }
        }
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
