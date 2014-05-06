package cz.skorpils.oryx.bn.computation.local;

import com.cloudera.oryx.common.collection.LongObjectMap;
import com.cloudera.oryx.common.io.DelimitedDataUtils;
import com.cloudera.oryx.common.io.IOUtils;
import com.cloudera.oryx.common.iterator.FileLineIterable;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import cz.skorpils.oryx.bn.computation.model.FeatureNode;
import cz.skorpils.oryx.bn.computation.model.ItemNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Callable;

/**
 * Created by stopka on 6.5.14.
 */
public class BuildItemMatrix implements Callable<LongObjectMap<ItemNode>> {
    File inputDir;
    String featureFileTag;
    Integer[] featureColumns;
    String[] featureNames;
    Integer name_col;
    Integer id_col;
    LongObjectMap<FeatureNode> features;
    private static final Logger log = LoggerFactory.getLogger(BuildItemMatrix.class);

    public BuildItemMatrix(LongObjectMap<FeatureNode> features, File inputDir) {
        this.features = features;
        this.inputDir = inputDir;
        Config config = ConfigUtils.getDefaultConfig();
        featureFileTag = config.getString("model.items-tag");
        featureColumns = (Integer[]) config.getIntList("model.items-feature-cols").toArray();
        featureNames = (String[]) config.getStringList("model.items-feature-names").toArray();
        name_col = config.getInt("model.items-name-col");
        id_col = config.getInt("model.items-id-col");
    }

    @Override
    public LongObjectMap<ItemNode> call() throws IOException {
        log.info("Building item nodes");
        File[] inputFiles = getDemandedFiles();
        if (inputFiles == null || inputFiles.length == 0) {
            log.info("  no item files in {}", inputDir);
            return null;
        }

        LongObjectMap<ItemNode> nodes = new LongObjectMap<ItemNode>();
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
                nodes.put(node.getId(), node);
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
        return file.getName().contains(featureFileTag);
    }
}
