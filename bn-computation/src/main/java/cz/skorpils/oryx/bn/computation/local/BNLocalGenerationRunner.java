/*
 * Copyright (c) 2013, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */

package cz.skorpils.oryx.bn.computation.local;

import com.cloudera.oryx.common.collection.LongObjectMap;
import com.cloudera.oryx.common.io.IOUtils;
import com.cloudera.oryx.common.servcomp.Namespaces;
import com.cloudera.oryx.common.servcomp.Store;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.cloudera.oryx.computation.common.JobException;
import com.cloudera.oryx.computation.common.LocalGenerationRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.common.io.Files;
import com.typesafe.config.Config;
import cz.skorpils.oryx.bn.computation.model.FeatureNode;
import cz.skorpils.oryx.bn.computation.model.ItemNode;
import cz.skorpils.oryx.bn.computation.model.NamedMatrix;
import cz.skorpils.oryx.bn.computation.model.UserNode;
import cz.skorpils.oryx.bn.computation.model.VoteNode;

public final class BNLocalGenerationRunner extends LocalGenerationRunner {

    @Override
    protected void runSteps() throws IOException, InterruptedException, JobException {
        String instanceDir = getInstanceDir();
        int generationID = getGenerationID();
        String generationPrefix = Namespaces.getInstanceGenerationPrefix(instanceDir, generationID);
        int lastGenerationID = getLastGenerationID();

        File currentInboundDir = Files.createTempDir();
        currentInboundDir.deleteOnExit();
        File tempOutDir = Files.createTempDir();
        tempOutDir.deleteOnExit();

      /*File lastInputDir = null;

      if (lastGenerationID >= 0) {
          lastInputDir = Files.createTempDir();
          lastInputDir.deleteOnExit();
      }*/

        try {

            Store store = Store.get();
            store.downloadDirectory(generationPrefix + "inbound/", currentInboundDir);
          /*if (lastGenerationID >= 0) {
              String lastGenerationPrefix = Namespaces.getInstanceGenerationPrefix(instanceDir, lastGenerationID);
              store.downloadDirectory(lastGenerationPrefix + "input/", lastInputDir);
          }*/

            Config config = ConfigUtils.getDefaultConfig();

            LongObjectMap<FeatureNode> features = new BuildFeatureMatrix().call();
            LongObjectMap<ItemNode> items = new BuildItemMatrix(features, currentInboundDir).call();
            LongObjectMap<UserNode> users = new BuildUserMatrix(items, currentInboundDir).call();
            LongObjectMap<VoteNode> votes = new BuildVoteMatrix(users).call();

            if (RbyRow.isEmpty() || RbyColumn.isEmpty()) {
                return;
            }

            MatrixFactorizer als = new FactorMatrix(RbyRow, RbyColumn).call();

            new WriteOutputs(tempOutDir, RbyRow, knownItemIDs, als.getX(), als.getY(), idMapping).call();

            if (config.getDouble("model.test-set-fraction") > 0.0) {
                new ComputeMAP(currentTestDir, als.getX(), als.getY()).call();
            }

            if (config.getBoolean("model.recommend.compute")) {
                new MakeRecommendations(tempOutDir, knownItemIDs, als.getX(), als.getY(), idMapping).call();
            }

            if (config.getBoolean("model.item-similarity.compute")) {
                new MakeItemSimilarity(tempOutDir, als.getY(), idMapping).call();
            }

            store.uploadDirectory(generationPrefix, tempOutDir, false);

        } finally {
            IOUtils.deleteRecursively(currentInboundDir);
            IOUtils.deleteRecursively(tempOutDir);
        }
    }

}
