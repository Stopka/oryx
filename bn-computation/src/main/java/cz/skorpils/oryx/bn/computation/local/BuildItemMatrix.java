package cz.skorpils.oryx.bn.computation.local;

import com.cloudera.oryx.common.io.DelimitedDataUtils;
import com.cloudera.oryx.common.io.IOUtils;
import com.cloudera.oryx.common.iterator.FileLineIterable;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import cz.skorpils.oryx.bn.common.model.FeatureNode;
import cz.skorpils.oryx.bn.common.model.ItemNode;
import cz.skorpils.oryx.bn.common.model.NodeContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Callable;

/**
 * Created by stopka on 6.5.14.
 */
public class BuildItemMatrix implements Callable<NodeContainer<ItemNode>> {
    File inputDir;
    String featureFileTag;
    Integer[] featureColumns;
    String[] featureNames;
    Integer name_col;
    Integer id_col;
    NodeContainer<FeatureNode> features;
    private static final Logger log = LoggerFactory.getLogger(BuildItemMatrix.class);

    public BuildItemMatrix(NodeContainer<FeatureNode> features, File inputDir) {
        this.features = features;
        this.inputDir = inputDir;
        Config config = ConfigUtils.getDefaultConfig();
        featureFileTag = config.getString("model.item.file-tag");
        featureColumns = (Integer[]) config.getIntList("model.item.feature.cols").toArray(new Integer[0]);
        featureNames = (String[]) config.getStringList("model.item.feature.names").toArray(new String[0]);
        name_col = config.getInt("model.item.name-col");
        id_col = config.getInt("model.item.id-col");
    }

    @Override
    public NodeContainer<ItemNode> call() throws IOException {
        log.info("Building item nodes");
        File[] inputFiles = getDemandedFiles();
        if (inputFiles == null || inputFiles.length == 0) {
            log.info("  no item files in {}", inputDir);
            return null;
        }

        NodeContainer<ItemNode> nodes = new NodeContainer<ItemNode>(features);
        for (File inputFile : inputFiles) {
            log.info("  reading {}", inputFile);
            for (CharSequence line : new FileLineIterable(inputFile)) {
                String[] columns = DelimitedDataUtils.decode(line);
                ItemNode node = new ItemNode(Long.valueOf(columns[id_col]), columns[name_col]);
                for (int i = 0; i < featureColumns.length; i++) {
                    if (columns[featureColumns[i]].equals("1") || columns[featureColumns[i]].toLowerCase().equals("true")) {
                        node.addParent(features.get(i));
                    }
                }
                nodes.put(node);
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
        return file.getName().contains(featureFileTag);
    }
}
