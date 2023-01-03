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

package net.bis5.mattermost.client4.integrationtest.api;

import static net.bis5.mattermost.client4.integrationtest.Assertions.assertNoError;
import static net.bis5.mattermost.client4.integrationtest.Assertions.isNotSupportVersion;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.logging.Level;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.hook.IncomingWebhookClient;
import net.bis5.mattermost.client4.integrationtest.MattermostClientTest;
import net.bis5.mattermost.client4.integrationtest.MattermostClientTestExtension;
import net.bis5.mattermost.client4.integrationtest.TestHelper;
import net.bis5.mattermost.model.IncomingWebhook;
import net.bis5.mattermost.model.IncomingWebhookRequest;
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.model.PostList;

@ExtendWith(MattermostClientTestExtension.class)
class IncomingWebhookClientTest implements MattermostClientTest {
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
  void testHook_IncomingWebhook_Post() {
    th.logout().loginTeamAdmin();
    IncomingWebhook webhook = new IncomingWebhook();
    {
      String channelId = th.basicChannel().getId();
      String displayName = "webhook" + th.newId();
      String description = "description" + th.newId();
      webhook.setChannelId(channelId);
      webhook.setDisplayName(displayName);
      webhook.setDescription(description);
    }
    ApiResponse<IncomingWebhook> createWebhookResponse = assertNoError(client.createIncomingWebhook(webhook));
    webhook = createWebhookResponse.readEntity();
    String hookUrl = getApplicationUrl() + "/hooks/" + webhook.getId();

    IncomingWebhookRequest payload = new IncomingWebhookRequest();
    payload.setText("Hello Webhook World");
    IncomingWebhookClient webhookClient = new IncomingWebhookClient(hookUrl, Level.WARNING);

    ApiResponse<Boolean> response = webhookClient.postByIncomingWebhook(payload);

    assertNoError(response);
    assertFalse(response.hasError());
  }

  @Test
  void testHook_IncomingWebhookWithIconEmoji() {
    th.logout().loginTeamAdmin();
    IncomingWebhook webhook = new IncomingWebhook();
    {
      String channelId = th.basicChannel().getId();
      String displayName = "webhook" + th.newId();
      String description = "description" + th.newId();
      webhook.setChannelId(channelId);
      webhook.setDisplayName(displayName);
      webhook.setDescription(description);
    }
    ApiResponse<IncomingWebhook> createWebhookResponse = assertNoError(client.createIncomingWebhook(webhook));
    if (isNotSupportVersion("5.14.0", createWebhookResponse)) {
      return;
    }
    webhook = createWebhookResponse.readEntity();
    String hookUrl = getApplicationUrl() + "/hooks/" + webhook.getId();

    IncomingWebhookRequest payload = new IncomingWebhookRequest();
    payload.setText("Webhook with icon_emoji" + th.newId());
    payload.setIconEmoji("mattermost");
    IncomingWebhookClient webhookClient = new IncomingWebhookClient(hookUrl, Level.WARNING);

    ApiResponse<Boolean> response = webhookClient.postByIncomingWebhook(payload);
    assertNoError(response);

    PostList posts = assertNoError(client.getPostsForChannel(th.basicChannel().getId())).readEntity();
    Post webhookPost = posts.getPosts().values().stream().filter(p -> p.getMessage().equals(payload.getText()))
        .findFirst().get();
    assertThat(webhookPost.getProps().get("override_icon_url"), is(not(nullValue())));
  }
}