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
package co.elastic.apm.agent.log4j1.error;

import co.elastic.apm.agent.loginstr.error.AbstractErrorLoggingInstrumentationTest;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

class Log4j1LoggerErrorCapturingInstrumentationTest extends AbstractErrorLoggingInstrumentationTest {

    private static final Logger logger = LogManager.getLogger(Log4j1LoggerErrorCapturingInstrumentationTest.class);

    @Test
    void captureErrorExceptionWithStringMessage() {
        logger.error("exception captured", new RuntimeException("some business exception"));
        verifyExceptionCaptured("some business exception", RuntimeException.class);
    }

    @Test
    void captureFatalException() {
        logger.fatal("exception captured", new RuntimeException("some business exception"));
        verifyExceptionCaptured("some business exception", RuntimeException.class);
    }
}
