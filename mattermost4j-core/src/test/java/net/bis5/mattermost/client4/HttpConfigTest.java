/*
 * Copyright (c) 2017-present, Takayuki Maruyama
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package net.bis5.mattermost.client4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import jakarta.ws.rs.client.Client;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

/**
 * Tests if {@link net.bis5.mattermost.client4.MattermostClient.MattermostClientBuilder#httpConfig(Consumer)}
 * is applied.
 */
class HttpConfigTest {

  @Test
  void testHttpConfig()
      throws InterruptedException, NoSuchFieldException, IllegalAccessException {

    final int timeout = 1234;

    final MattermostClient client = MattermostClient.builder()
        .url("http://localhost:815")
        .httpConfig(clientBuilder ->
        {
          clientBuilder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
          clientBuilder.readTimeout(timeout, TimeUnit.MILLISECONDS);
        })
        .build();

    final Client rsClient = client.getHttpClientForUnitTest();

    assertThat(
        "Internally used httpClient uses configured read timeouts",
        rsClient.getConfiguration().getProperty("jersey.config.client.readTimeout"),
        is(timeout)
    );

    assertThat(
        "Internally used httpClient uses configured connect timeouts",
        rsClient.getConfiguration().getProperty("jersey.config.client.connectTimeout"),
        is(timeout)
    );
  }
}
