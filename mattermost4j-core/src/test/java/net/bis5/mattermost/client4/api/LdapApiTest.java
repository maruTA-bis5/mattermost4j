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

import static net.bis5.mattermost.client4.Assertions.assertStatus;
import static net.bis5.mattermost.client4.Assertions.isSupportVersion;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.MattermostClientTest;
import net.bis5.mattermost.client4.MattermostClientTestExtension;
import net.bis5.mattermost.client4.TestHelper;

@ExtendWith(MattermostClientTestExtension.class)
class LdapApiTest implements MattermostClientTest {
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

  @Test
  public void syncLdap() {
    th.logout().loginSystemAdmin();

    // Enterprise Edition required
    // Note: Server >= 5.11 returns 200 OK
    ApiResponse<Boolean> response = client.syncLdap();
    if (isSupportVersion("5.12.0", response)) {
      assertStatus(response, Status.NOT_IMPLEMENTED);
    }
  }

  @Test
  public void testLdap() {
    th.logout().loginSystemAdmin();

    // Enterprise Edition required
    // Note: Server >= 5.11 returns 200 OK
    ApiResponse<Boolean> response = client.testLdap();
    if (isSupportVersion("5.12.0", response)) {
      assertStatus(response, Status.NOT_IMPLEMENTED);
    }
  }
}