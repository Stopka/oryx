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

import com.cloudera.oryx.common.io.DelimitedDataUtils;
import com.cloudera.oryx.serving.web.AbstractOryxServlet;
import com.google.common.base.Preconditions;
import cz.skorpils.oryx.bn.common.OryxRecommender;
import cz.skorpils.oryx.bn.common.rescorer.RescorerProvider;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

/**
 * Superclass of all servletes used in the ALS recommender API.
 *
 * @author Sean Owen
 */
public abstract class AbstractBNServlet extends AbstractOryxServlet {

  private static final int DEFAULT_HOW_MANY = 10;

  private static final String KEY_PREFIX = AbstractBNServlet.class.getName();
  public static final String RECOMMENDER_KEY = KEY_PREFIX + ".RECOMMENDER";
  public static final String RESCORER_PROVIDER_KEY = KEY_PREFIX + ".RESCORER_PROVIDER";
  private static final String[] NO_PARAMS = new String[0];

  private OryxRecommender recommender;
  private RescorerProvider rescorerProvider;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ServletContext context = config.getServletContext();
    recommender = (OryxRecommender) context.getAttribute(RECOMMENDER_KEY);
    rescorerProvider = (RescorerProvider) context.getAttribute(RESCORER_PROVIDER_KEY);
  }

  final OryxRecommender getRecommender() {
    return recommender;
  }

  final RescorerProvider getRescorerProvider() {
    return rescorerProvider;
  }

  private static int getHowMany(ServletRequest request) {
    String howManyString = request.getParameter("howMany");
    if (howManyString == null) {
      return DEFAULT_HOW_MANY;
    }
    int howMany = Integer.parseInt(howManyString);
    Preconditions.checkArgument(howMany > 0, "howMany must be positive");
    return howMany;
  }

  private static int getOutputOffset(ServletRequest request) {
    String offsetString = request.getParameter("offset");
    if (offsetString == null) {
      return 0;
    }
    int offset = Integer.parseInt(offsetString);
    Preconditions.checkArgument(offset >= 0, "offset must be nonnegative");
    return offset;
  }

  /**
   * @param request current request object
   * @return number of results to fetch. This is typically the {@code howMany} parameter's value
   *  plus the {@code offset} parameter's value.
   */
  static int getNumResultsToFetch(ServletRequest request) {
    return getHowMany(request) + getOutputOffset(request);
  }

  static String[] getRescorerParams(ServletRequest request) {
    String[] rescorerParams = request.getParameterValues("rescorerParams");
    return rescorerParams == null ? NO_PARAMS : rescorerParams;
  }

  static boolean getConsiderKnownItems(ServletRequest request) {
    return Boolean.valueOf(request.getParameter("considerKnownItems"));
  }

  /**
   * <p>CSV output contains one recommendation per line, and each line is of the form {@code itemID,strength},
   * like {@code "ABC",0.53}. Strength is an opaque indicator of the relative quality of the recommendation.</p>
   *
   * @param request current request object
   * @param response current response object to write to
   * @param items raw list of results from the very first. Only a sublist will be output if
   *  {@code offset} has been specified
   */
  final void output(HttpServletRequest request, ServletResponse response, List<Long> items) throws IOException {

    int offset = getOutputOffset(request);
    if (offset > 0) {
      if (offset < items.size()) {
        items = items.subList(offset, items.size());
      } else {
        items = Collections.emptyList();
      }
    }

    Writer writer = response.getWriter();
    switch (determineResponseType(request)) {
      case JSON:
        writer.write('[');
        boolean first = true;
        for (Long item : items) {
          if (first) {
            first = false;
          } else {
            writer.write(',');
          }
          writer.write("[\"");
          writer.write(item.toString());
          // Not using DelimitedDataUtils as JSON needs comma
          writer.write("\",");
          writer.write(Float.toString(item));
          writer.write(']');
        }
        writer.write("]\n");
        break;
      case DELIMITED:
        for (Long item : items) {
          writer.write(DelimitedDataUtils.encode(',', item, Float.toString(item)));
          writer.write('\n');
        }
        break;
      default:
        throw new IllegalStateException("Unknown response type");
    }
  }

}
