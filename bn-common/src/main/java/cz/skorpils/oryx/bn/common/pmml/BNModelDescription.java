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

package cz.skorpils.oryx.bn.common.pmml;

import com.cloudera.oryx.common.io.IOUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.dmg.pmml.Extension;
import org.dmg.pmml.PMML;
import org.jpmml.model.ImportFilter;
import org.jpmml.model.JAXBUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * A model description for an ALS model, which in practice is purely a set of paths containing actual data.
 * It uses PMML serialization but the serialization only uses a few {@code <Extension>} elements to encode
 * these paths.
 */
public final class BNModelDescription {

  private final Map<String,String> pathByKey = Maps.newHashMap();

  private Map<String,String> getPathByKey() {
    return pathByKey;
  }

  public String getNetworkPath() {
    return pathByKey.get("networkPath");
  }

  public void setNetworkPath(String path) {
    pathByKey.put("networkPath", path);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof BNModelDescription)) {
      return false;
    }
      BNModelDescription other = (BNModelDescription) o;
    return pathByKey.equals(other.pathByKey);
  }

  @Override
  public int hashCode() {
    return pathByKey.hashCode();
  }

  @Override
  public String toString() {
    return pathByKey.toString();
  }

  public static BNModelDescription read(File f) throws IOException {
    InputStream in = IOUtils.openMaybeDecompressing(f);
    try {
      return read(in);
    } catch (JAXBException jaxbe) {
      throw new IOException(jaxbe);
    } catch (SAXException saxe) {
      throw new IOException(saxe);
    } finally {
      in.close();
    }
  }

  /**
   * Quite manually parse our fake model representation in PMML.
   */
  private static BNModelDescription read(InputStream in) throws JAXBException, SAXException {

    PMML pmml = JAXBUtil.unmarshalPMML(ImportFilter.apply(new InputSource(in)));
    List<Extension> extensions = pmml.getExtensions();
    Preconditions.checkNotNull(extensions);
    Preconditions.checkArgument(!extensions.isEmpty());

      BNModelDescription model = new BNModelDescription();

    for (Extension extension : extensions) {
      String name  = extension.getName();
      String value = extension.getValue();
      Preconditions.checkNotNull(name);
      Preconditions.checkNotNull(value);
      model.getPathByKey().put(name, value);
    }

    return model;
  }

  public static void write(File f, BNModelDescription model) throws IOException {
    OutputStream out = IOUtils.buildGZIPOutputStream(new FileOutputStream(f));
    try {
      write(out, model);
    } catch (JAXBException jaxbe) {
      throw new IOException(jaxbe);
    } finally {
      out.close();
    }
  }

  /**
   * Quite manually write our fake model representation in PMML.
   */
  private static void write(OutputStream out, BNModelDescription model) throws JAXBException {
    PMML pmml = new PMML(null, null, "4.2");
    for (Map.Entry<String,String> entry : model.getPathByKey().entrySet()) {
      Extension extension = new Extension();
      extension.setName(entry.getKey());
      extension.setValue(entry.getValue());
      pmml.getExtensions().add(extension);
    }
    JAXBUtil.marshalPMML(pmml, new StreamResult(out));
  }

}
