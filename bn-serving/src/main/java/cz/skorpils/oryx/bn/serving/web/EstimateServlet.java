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

package cz.skorpils.oryx.bn.serving.web;


import com.google.common.collect.Lists;
import cz.skorpils.oryx.bn.common.NotReadyException;
import cz.skorpils.oryx.bn.common.recomender.OryxRecommender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>Responds to a GET request to {@code /estimate/[userID]/[itemID]} and in turn calls
 * {@link OryxRecommender#estimatePreference(String, String)}.</p>
 *
 * <p>Outputs the result of the method call as a value on one line.</p>
 *
 * <p>This servlet can also compute several estimates at once. Send a GET request to
 * {@code /estimate/[userID]/[itemID1](/[itemID2]/...)}. The output are estimates, in the same
 * order as the item ID, one per line.</p>
 *
 * @author Sean Owen
 */
public final class EstimateServlet extends AbstractBNServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    CharSequence pathInfo = request.getPathInfo();
    if (pathInfo == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No path");
      return;
    }
    Iterator<String> pathComponents = SLASH.split(pathInfo).iterator();
    String userID;
    List<String> itemIDsList;
    try {
      userID = pathComponents.next();
      itemIDsList = Lists.newArrayList();
      while (pathComponents.hasNext()) {
        itemIDsList.add(pathComponents.next());
      }
    } catch (NoSuchElementException nsee) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, nsee.toString());
      return;
    }

    String[] itemIDs = itemIDsList.toArray(new String[itemIDsList.size()]);
    unescapeSlashHack(itemIDs);

  }

}