package cz.skorpils.oryx.bn.computation.local;

import com.cloudera.oryx.common.collection.LongObjectMap;
import com.cloudera.oryx.common.io.DelimitedDataUtils;
import com.cloudera.oryx.common.io.IOUtils;
import com.cloudera.oryx.common.iterator.FileLineIterable;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import cz.skorpils.oryx.bn.computation.model.ItemNode;
import cz.skorpils.oryx.bn.computation.model.UserNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Callable;

/**
 * Created by stopka on 6.5.14.
 */
public class BuildUserMatrix implements Callable<LongObjectMap<UserNode>> {
    File inputDir;
    String featureFileTag;
    int item_col;
    int user_col;
    int value_col;
    int max_value;
    LongObjectMap<ItemNode> items;
    private static final Logger log = LoggerFactory.getLogger(BuildUserMatrix.class);

    public BuildUserMatrix(LongObjectMap<ItemNode> items, File inputDir) {
        this.items = items;
        this.inputDir = inputDir;
        Config config = ConfigUtils.getDefaultConfig();
        featureFileTag = config.getString("model.feature-file-tag");
        item_col = config.getInt("model.users-item-col");
        user_col = config.getInt("model.users-user-col");
        value_col = config.getInt("model.users-rating-col");
        max_value = config.getInt("model.users-rating-max");
    }

    @Override
    public LongObjectMap<UserNode> call() throws IOException {
        log.info("Building user nodes");
        File[] inputFiles = getDemandedFiles();
        if (inputFiles == null || inputFiles.length == 0) {
            log.info("  no input files in {}", inputDir);
            return null;
        }
        LongObjectMap<UserNode> nodes = new LongObjectMap<UserNode>();
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
                    node = new UserNode(user, max_value);
                    nodes.put(node.getId(), node);
                }
                node.addParent(items.get(item), value);
            }
        }
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
        return (File[]) result.toArray();
    }

    protected boolean isFileDemanded(File file) {
        return !file.getName().contains(featureFileTag);
    }
}
