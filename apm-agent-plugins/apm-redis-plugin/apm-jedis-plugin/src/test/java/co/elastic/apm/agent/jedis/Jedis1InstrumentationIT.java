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
package co.elastic.apm.agent.jedis;

import co.elastic.apm.agent.redis.AbstractRedisInstrumentationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import redis.clients.jedis.Jedis;

import static org.assertj.core.api.Assertions.assertThat;

class Jedis1InstrumentationIT extends AbstractRedisInstrumentationTest {

    protected Jedis jedis;

    @BeforeEach
    void setUpJedis() {
        jedis = new Jedis("localhost", redisPort);
    }

    @AfterEach
    void tearDownJedis() {
        try {
            // this method does not exist in Jedis 1
            Jedis.class.getMethod("close").invoke(jedis);
        } catch (NoSuchMethodException e) {
            // ignore, this version of redis does not support close
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisabledOnJre(JRE.JAVA_15) // https://github.com/elastic/apm-agent-java/issues/1944
    void testJedis() {
        jedis.set("foo", "bar");
        assertThat(jedis.get("foo".getBytes())).isEqualTo("bar".getBytes());

        verifyBasicJedisSpans();
    }

    protected void verifyBasicJedisSpans() {
        assertTransactionWithRedisSpans("SET", "GET");
    }

}
