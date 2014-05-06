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

package cz.skorpils.oryx.bn.computation;

import com.cloudera.oryx.computation.common.DependsOn;
import com.cloudera.oryx.computation.common.DistributedGenerationRunner;
import com.cloudera.oryx.computation.common.JobStep;
import com.cloudera.oryx.computation.common.JobStepConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public final class BNDistributedGenerationRunner extends DistributedGenerationRunner {

    private static final Logger log = LoggerFactory.getLogger(BNDistributedGenerationRunner.class);


    @Override
    protected List<DependsOn<Class<? extends JobStep>>> getPreDependencies() {
        return null;
    }

    @Override
    protected List<DependsOn<Class<? extends JobStep>>> getIterationDependencies() {
        return null;
    }

    @Override
    protected List<DependsOn<Class<? extends JobStep>>> getPostDependencies() {
        return null;
    }

    @Override
    protected JobStepConfig buildConfig(int iteration) {
        return null;
    }
}
