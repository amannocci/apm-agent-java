/*
 * Licensed to Elasticsearch B.V. under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch B.V. licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package co.elastic.apm.agent.process;

import co.elastic.apm.agent.sdk.ElasticApmInstrumentation;
import co.elastic.apm.agent.tracer.GlobalTracer;
import co.elastic.apm.agent.tracer.Tracer;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Collection;
import java.util.Collections;

import static net.bytebuddy.matcher.ElementMatchers.isBootstrapClassLoader;

public abstract class BaseProcessInstrumentation extends ElasticApmInstrumentation {

    static final Tracer tracer = GlobalTracer.get();

    @Override
    public final ElementMatcher.Junction<ClassLoader> getClassLoaderMatcher() {
        // java.lang.* is loaded from bootstrap classloader
        return isBootstrapClassLoader();
    }

    @Override
    public final Collection<String> getInstrumentationGroupNames() {
        return Collections.singletonList("process");
    }
}
