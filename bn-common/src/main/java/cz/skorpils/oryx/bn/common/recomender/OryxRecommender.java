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

package cz.skorpils.oryx.bn.common.recomender;

import cz.skorpils.oryx.bn.common.NoSuchUserException;
import cz.skorpils.oryx.bn.common.NotReadyException;
import cz.skorpils.oryx.bn.common.rescorer.PairRescorer;
import cz.skorpils.oryx.bn.common.rescorer.Rescorer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Interface to the BN-based recommender in Oryx. This interface represents the Java API exposed
 * by the implementation.
 *
 * @author Sean Owen
 */
public interface OryxRecommender {

  /**
   * @param userID user for which recommendations are to be computed
   * @param howMany desired number of recommendations
   * @return {@link java.util.List} of recommended s, ordered from most strongly recommend to least
   * @throws cz.skorpils.oryx.bn.common.NoSuchUserException if the user is not known
   */
  List<Long> recommend(String userID, int howMany) throws NoSuchUserException, NotReadyException;

}
