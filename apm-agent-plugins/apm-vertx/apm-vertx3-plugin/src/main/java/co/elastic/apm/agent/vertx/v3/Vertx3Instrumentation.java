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
package co.elastic.apm.agent.vertx.v3;

import co.elastic.apm.agent.sdk.ElasticApmInstrumentation;
import co.elastic.apm.agent.tracer.GlobalTracer;
import co.elastic.apm.agent.tracer.Tracer;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Arrays;
import java.util.Collection;

import static co.elastic.apm.agent.sdk.bytebuddy.CustomElementMatchers.classLoaderCanLoadClass;
import static net.bytebuddy.matcher.ElementMatchers.not;

public abstract class Vertx3Instrumentation extends ElasticApmInstrumentation {

    protected static final Tracer tracer = GlobalTracer.get();

    @Override
    public Collection<String> getInstrumentationGroupNames() {
        return Arrays.asList("vertx", "experimental");
    }

    @Override
    public ElementMatcher.Junction<ClassLoader> getClassLoaderMatcher() {
        // ensure only Vertx versions >= 3.6 and < 4.0 are instrumented
        // for now, everything is in vertx-core jar
        return classLoaderCanLoadClass("io.vertx.core.http.impl.HttpServerRequestImpl")
            .and(not(classLoaderCanLoadClass("io.vertx.core.impl.Action")));
    }
}
