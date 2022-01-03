/*
 * Copyright (c) 2016-present, Takayuki Maruyama
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bis5.mattermost.client4.api;

import static net.bis5.mattermost.client4.Assertions.assertNoError;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import fi.iki.elonen.NanoHTTPD;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.MattermostClientTest;
import net.bis5.mattermost.client4.MattermostClientTestExtension;
import net.bis5.mattermost.client4.TestHelper;
import net.bis5.mattermost.model.Config;
import net.bis5.opengraph.models.OpenGraph;

@ExtendWith(MattermostClientTestExtension.class)
class OpenGraphApiTest implements MattermostClientTest {
  private TestHelper th;
  private MattermostClient client;

  @Override
  public void setHelper(TestHelper helper) {
    this.th = helper;
  }

  @BeforeEach
  void setup() {
    client = createClient();
    th.changeClient(client).initBasic();
  }

  @AfterEach
  void tearDown() {
    th.logout();
    client.close();
  }

  private String dummyHttpServerHost() {
    return getEnv("DUMMY_HTTP_SERVER_HOST", "static.bis5.net");
  }

  private int dummyHttpServerPort() {
    return Integer.valueOf(getEnv("DUMMY_HTTP_SERVER_PORT", "0"));
  }

  private boolean useLocalDummyServer() {
    return Boolean.valueOf(getEnv("USE_LOCAL_DUMMY_SERVER", "false"));
  }

  NanoHTTPD server;

  @BeforeEach
  void setupServer() throws IOException {
    if (!useLocalDummyServer()) {
      return;
    }
    server = new NanoHTTPD("0.0.0.0", dummyHttpServerPort()) {
      @Override
      public Response serve(IHTTPSession session) {
        List<String> contents = Arrays.asList("<html><head>", "<meta property=\"og:type\" content=\"article\" />",
            "<meta property=\"og:title\" content=\"The Great WebSite\" />",
            "<meta property=\"og:url\" content=\"http://localhost:8888/\" />",
            "</head><body>Hello World!</body></html>");
        return newFixedLengthResponse(StringUtils.join(contents, StringUtils.CR + StringUtils.LF));
      }
    };
    server.start();
  }

  @AfterEach
  void tearDownServer() {
    if (!useLocalDummyServer()) {
      return;
    }
    server.stop();
  }

  @Test
  void getMetadata() {
    th.logout().loginSystemAdmin();
    Config config = client.getConfig().readEntity();
    config.getServiceSettings().setAllowedUntrustedInternalConnections(
        config.getServiceSettings().getAllowedUntrustedInternalConnections() + " " + dummyHttpServerHost());
    assertNoError(client.updateConfig(config));
    th.logout().loginBasic();

    int port = dummyHttpServerPort();
    String url = "http://" + dummyHttpServerHost() + (port != 0 ? ":" + port : "");

    ApiResponse<OpenGraph> response = client.getOpenGraphMetadata(url);

    OpenGraph og = response.readEntity();
    assertNotNull(og);
  }
}