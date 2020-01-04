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
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.MattermostClientTest;
import net.bis5.mattermost.client4.MattermostClientTestExtension;
import net.bis5.mattermost.client4.TestHelper;
import net.bis5.mattermost.model.ChannelView;
import net.bis5.mattermost.model.StatusList;
import net.bis5.mattermost.model.StatusType;

// Status
@ExtendWith(MattermostClientTestExtension.class)
class StatusApiTest implements MattermostClientTest {
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
  public void getUserStatus() {
    String userId = th.basicUser().getId();
    assertNoError(client.viewChannel(userId, new ChannelView(th.basicChannel().getId())));

    net.bis5.mattermost.model.Status status = assertNoError(client.getUserStatus(userId)).readEntity();

    assertEquals(StatusType.ONLINE.getCode(), status.getStatus());
  }

  @Test
  public void getUserStatusesByIds() {
    String loginUserId = th.basicUser().getId();
    assertNoError(client.viewChannel(loginUserId, new ChannelView(th.basicChannel().getId())));
    String otherUserId = th.basicUser2().getId();

    List<String> userIds = Arrays.asList(loginUserId, otherUserId);
    StatusList statuses = assertNoError(client.getUsersStatusesByIds(userIds)).readEntity();

    assertEquals(2, statuses.size());
    for (net.bis5.mattermost.model.Status status : statuses) {
      if (status.getUserId().equals(loginUserId)) {
        assertEquals(StatusType.ONLINE.getCode(), status.getStatus());
      } else {
        assertEquals(StatusType.OFFLINE.getCode(), status.getStatus());
      }
    }
  }

  @Test
  public void updateUserStatus() {
    String userId = th.basicUser().getId();
    net.bis5.mattermost.model.Status newStatus = new net.bis5.mattermost.model.Status();
    newStatus.setUserId(userId);
    newStatus.setStatus(StatusType.DND.getCode());

    newStatus = assertNoError(client.updateUserStatus(userId, newStatus)).readEntity();

    assertEquals(StatusType.DND.getCode(), newStatus.getStatus());
  }
}