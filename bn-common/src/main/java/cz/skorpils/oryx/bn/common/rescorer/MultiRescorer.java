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

package cz.skorpils.oryx.bn.common.rescorer;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Convenience implementation that will aggregate the behavior of multiple {@link Rescorer}s.
 * It will filter an item if any of the given instances filter it, and will rescore by applying
 * the rescorings in the given order.
 *
 * @author Sean Owen
 * @see MultiLongPairRescorer
 * @see MultiRescorerProvider
 */
public final class MultiRescorer implements Rescorer {

  private final Rescorer[] rescorers;

  /**
   * @param rescorers {@link Rescorer} objects to delegate to
   */
  public MultiRescorer(List<Rescorer> rescorers) {
    Preconditions.checkNotNull(rescorers);
    Preconditions.checkState(!rescorers.isEmpty(), "rescorers is empty");
    this.rescorers = rescorers.toArray(new Rescorer[rescorers.size()]);
  }

  @Override
  public double rescore(String itemID, double value) {
    for (Rescorer rescorer : rescorers) {
      value = rescorer.rescore(itemID, value);
      if (Double.isNaN(value)) {
        return Double.NaN;
      }
    }
    return value;
  }

  @Override
  public boolean isFiltered(String itemID) {
    for (Rescorer rescorer : rescorers) {
      if (rescorer.isFiltered(itemID)) {
        return true;
      }
    }
    return false;
  }

}
