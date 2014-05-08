package cz.skorpils.oryx.bn.computation.local;

import com.cloudera.oryx.common.io.DelimitedDataUtils;
import com.cloudera.oryx.common.io.IOUtils;
import com.cloudera.oryx.common.iterator.FileLineIterable;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import cz.skorpils.oryx.bn.common.model.ItemNode;
import cz.skorpils.oryx.bn.common.model.NodeContainer;
import cz.skorpils.oryx.bn.common.model.UserNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;

/**
 * Created by stopka on 6.5.14.
 */
public class BuildUserMatrix implements Callable<NodeContainer<UserNode>> {
    File inputDir;
    String featureFileTag;
    int item_col;
    int user_col;
    int value_col;
    int max_value;
    NodeContainer<ItemNode> items;
    private static final Logger log = LoggerFactory.getLogger(BuildUserMatrix.class);

    public BuildUserMatrix(NodeContainer<ItemNode> items, File inputDir) {
        this.items = items;
        this.inputDir = inputDir;
        Config config = ConfigUtils.getDefaultConfig();
        featureFileTag = config.getString("model.item.file-tag");
        item_col = config.getInt("model.user.item-col");
        user_col = config.getInt("model.user.user-col");
        value_col = config.getInt("model.user.rating-col");
        max_value = config.getInt("model.user.rating-max");
    }

    @Override
    public NodeContainer<UserNode> call() throws IOException {
        log.info("Building user nodes");
        File[] inputFiles = getDemandedFiles();
        if (inputFiles == null || inputFiles.length == 0) {
            log.info("  no input files in {}", inputDir);
            return null;
        }
        NodeContainer<UserNode> nodes = new NodeContainer<UserNode>(items,max_value);
        for (File inputFile : inputFiles) {
            log.info("  reading {}", inputFile);
            for (CharSequence line : new FileLineIterable(inputFile)) {
                String[] columns = DelimitedDataUtils.decode(line);
                long user = Long.valueOf(columns[user_col]);
                long item = Long.valueOf(columns[item_col]);
                int value = Integer.valueOf(columns[value_col]);
                UserNode node;
                if (nodes.containsKey(user)) {
                    node = nodes.get(user);
                } else {
                    node = new UserNode(user);
                    nodes.put(node);
                }
                node.addParent(items.get(item), value);
            }
        }
        log.info("  result: {} nodes", nodes.size());
        return nodes;
    }

    protected File[] getDemandedFiles() {
        File[] inputFiles = inputDir.listFiles(IOUtils.NOT_HIDDEN);
        LinkedList<File> result = new LinkedList<File>();
        for (File inputFile : inputFiles) {
            if (isFileDemanded(inputFile)) {
                result.add(inputFile);
            }
        }
        return result.toArray(new File[result.size()]);
    }

    protected boolean isFileDemanded(File file) {
        return !file.getName().contains(featureFileTag);
    }
}
