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
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.MattermostClientTest;
import net.bis5.mattermost.client4.MattermostClientTestExtension;
import net.bis5.mattermost.client4.TestHelper;
import net.bis5.mattermost.model.ContentType;
import net.bis5.mattermost.model.IncomingWebhook;
import net.bis5.mattermost.model.IncomingWebhookList;
import net.bis5.mattermost.model.OutgoingWebhook;
import net.bis5.mattermost.model.OutgoingWebhookList;
import net.bis5.mattermost.model.TriggerWhen;

// Webhooks
@ExtendWith(MattermostClientTestExtension.class)
class WebhooksApiTest implements MattermostClientTest {
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
  void createIncomingWebhook() {
    th.logout().loginTeamAdmin();
    String channelId = th.basicChannel().getId();
    String displayName = "webhook" + th.newId();
    String description = "description" + th.newId();
    IncomingWebhook webhook = new IncomingWebhook();
    webhook.setChannelId(channelId);
    webhook.setDisplayName(displayName);
    webhook.setDescription(description);

    ApiResponse<IncomingWebhook> response = assertNoError(client.createIncomingWebhook(webhook));
    webhook = response.readEntity();

    assertThat(webhook.getId(), is(not(nullValue())));
    assertThat(webhook.getChannelId(), is(channelId));
    assertThat(webhook.getDisplayName(), is(displayName));
    assertThat(webhook.getDescription(), is(description));
  }

  @Test
  void listIncomingWebhooks() {
    th.logout().loginTeamAdmin();
    String channelId = th.basicChannel().getId();
    IncomingWebhook webhook1 = new IncomingWebhook();
    webhook1.setChannelId(channelId);
    IncomingWebhook webhook2 = new IncomingWebhook();
    webhook2.setChannelId(channelId);
    webhook1 = assertNoError(client.createIncomingWebhook(webhook1)).readEntity();
    webhook2 = assertNoError(client.createIncomingWebhook(webhook2)).readEntity();

    th.loginSystemAdmin(); // needs permission for get other team's webhooks
    ApiResponse<IncomingWebhookList> response = assertNoError(client.getIncomingWebhooks());
    List<IncomingWebhook> webhooks = response.readEntity();

    assertThat(webhooks.size(), is(greaterThanOrEqualTo(2)));
    assertThat(webhooks.stream().map(IncomingWebhook::getId).collect(Collectors.toSet()),
        hasItems(webhook1.getId(), webhook2.getId()));
  }

  @Test
  void listIncomingWebhooksForTeam() {
    th.logout().loginTeamAdmin();
    String channelId = th.basicChannel().getId();
    String teamId = th.basicTeam().getId();
    IncomingWebhook webhook1 = new IncomingWebhook();
    webhook1.setChannelId(channelId);
    IncomingWebhook webhook2 = new IncomingWebhook();
    webhook2.setChannelId(channelId);
    webhook1 = assertNoError(client.createIncomingWebhook(webhook1)).readEntity();
    webhook2 = assertNoError(client.createIncomingWebhook(webhook2)).readEntity();

    ApiResponse<IncomingWebhookList> response = assertNoError(client.getIncomingWebhooksForTeam(teamId));
    List<IncomingWebhook> webhooks = response.readEntity();

    assertThat(webhooks.size(), is(2));
    assertThat(webhooks.stream().map(IncomingWebhook::getId).collect(Collectors.toSet()),
        containsInAnyOrder(webhook1.getId(), webhook2.getId()));
  }

  @Test
  void getIncomingWebhook() {
    th.logout().loginTeamAdmin();
    String channelId = th.basicChannel().getId();
    IncomingWebhook webhook = new IncomingWebhook();
    webhook.setChannelId(channelId);
    webhook = assertNoError(client.createIncomingWebhook(webhook)).readEntity();
    String webhookId = webhook.getId();

    ApiResponse<IncomingWebhook> response = assertNoError(client.getIncomingWebhook(webhookId));
    IncomingWebhook responseWebhook = response.readEntity();

    assertThat(responseWebhook, is(not(nullValue())));
    assertThat(responseWebhook.getId(), is(webhookId));
  }

  @Test
  void updateIncomingWebhook() {
    th.logout().loginTeamAdmin();
    String channelId = th.basicChannel().getId();
    IncomingWebhook webhook = new IncomingWebhook();
    {
      webhook.setChannelId(channelId);
      webhook.setDisplayName(th.newRandomString(32));
      webhook.setDescription(th.newRandomString(32));
      webhook = assertNoError(client.createIncomingWebhook(webhook)).readEntity();
    }

    String newDisplayName = "new" + webhook.getDisplayName();
    String newDescription = "new" + webhook.getDescription();
    String newChannelId = th.basicChannel2().getId();
    webhook.setDisplayName(newDisplayName);
    webhook.setDescription(newDescription);
    webhook.setChannelId(newChannelId);
    ApiResponse<IncomingWebhook> response = assertNoError(client.updateIncomingWebhook(webhook));
    IncomingWebhook updatedWebhook = response.readEntity();

    assertThat(updatedWebhook.getDisplayName(), is(webhook.getDisplayName()));
    assertThat(updatedWebhook.getDescription(), is(webhook.getDescription()));
    assertThat(updatedWebhook.getChannelId(), is(webhook.getChannelId()));
  }

  @Test
  void deleteIncomingWebhook() {
    th.logout().loginTeamAdmin();
    String channelId = th.basicChannel().getId();
    IncomingWebhook webhook = new IncomingWebhook();
    {
      webhook.setChannelId(channelId);
      webhook.setDisplayName(th.newRandomString(32));
      webhook.setDescription(th.newRandomString(32));
      webhook = assertNoError(client.createIncomingWebhook(webhook)).readEntity();
    }

    ApiResponse<Boolean> deleteResult = assertNoError(client.deleteIncomingWebhook(webhook.getId()));

    assertTrue(deleteResult.readEntity());
  }

  @Test
  void createOutgoingWebhook() {
    th.logout().loginTeamAdmin();
    String teamId = th.basicTeam().getId();
    String channelId = th.basicChannel().getId();
    String description = th.newRandomString(32);
    String displayName = th.newRandomString(32);
    String triggerWord = "trigger";
    TriggerWhen triggerWhen = TriggerWhen.CONTAINS;
    String callbackUrl = "http://callback-url";
    ContentType contentType = ContentType.FORM;
    OutgoingWebhook webhook = new OutgoingWebhook();
    webhook.setTeamId(teamId);
    webhook.setChannelId(channelId);
    webhook.setDescription(description);
    webhook.setDisplayName(displayName);
    webhook.setTriggerWords(Arrays.asList(triggerWord));
    webhook.setTriggerWhen(triggerWhen);
    webhook.setCallbackUrls(Arrays.asList(callbackUrl));
    webhook.setContentType(contentType);

    ApiResponse<OutgoingWebhook> response = assertNoError(client.createOutgoingWebhook(webhook));
    OutgoingWebhook created = response.readEntity();

    assertThat(created.getId(), is(not(nullValue())));
    assertThat(created.getTeamId(), is(webhook.getTeamId()));
    assertThat(created.getChannelId(), is(webhook.getChannelId()));
    assertThat(created.getDescription(), is(webhook.getDescription()));
    assertThat(created.getDisplayName(), is(webhook.getDisplayName()));
    assertThat(created.getTriggerWords(), containsInAnyOrder(webhook.getTriggerWords().toArray()));
    assertThat(created.getTriggerWhen(), is(webhook.getTriggerWhen()));
    assertThat(created.getCallbackUrls(), containsInAnyOrder(webhook.getCallbackUrls().toArray()));
    assertThat(created.getContentType(), is(webhook.getContentType()));
  }

  @Test
  void listOutgoingWebhooks() {
    String teamId = th.basicTeam().getId();
    String displayName = th.newRandomString(32);
    List<String> callbackUrls = Arrays.asList("http://callback-url");
    th.logout().loginTeamAdmin();
    OutgoingWebhook webhook1 = new OutgoingWebhook();
    webhook1.setTeamId(teamId);
    webhook1.setDisplayName(displayName);
    webhook1.setTriggerWords(Arrays.asList("trigger" + th.newRandomString(10)));
    webhook1.setCallbackUrls(callbackUrls);
    webhook1 = assertNoError(client.createOutgoingWebhook(webhook1)).readEntity();
    OutgoingWebhook webhook2 = new OutgoingWebhook();
    webhook2.setTeamId(teamId);
    webhook2.setDisplayName(displayName);
    webhook2.setTriggerWords(Arrays.asList("trigger" + th.newRandomString(10)));
    webhook2.setCallbackUrls(callbackUrls);
    webhook2 = assertNoError(client.createOutgoingWebhook(webhook2)).readEntity();

    th.loginSystemAdmin(); // needs permission for get other team's webhooks
    ApiResponse<OutgoingWebhookList> response = assertNoError(client.getOutgoingWebhooks());
    List<OutgoingWebhook> webhooks = response.readEntity();

    assertThat(webhooks.stream().map(OutgoingWebhook::getId).collect(Collectors.toSet()),
        hasItems(webhook1.getId(), webhook2.getId()));
  }

  @Test
  void listOutgoingWebhooksForTeam() {
    th.logout().loginTeamAdmin();
    String teamId = th.basicTeam().getId();
    String displayName = th.newRandomString(32);
    List<String> triggerWords = Arrays.asList("trigger");
    List<String> callbackUrls = Arrays.asList("http://callback-url");
    OutgoingWebhook webhook = new OutgoingWebhook();
    webhook.setTeamId(teamId);
    webhook.setDisplayName(displayName);
    webhook.setTriggerWords(triggerWords);
    webhook.setCallbackUrls(callbackUrls);
    webhook = assertNoError(client.createOutgoingWebhook(webhook)).readEntity();

    ApiResponse<OutgoingWebhookList> response = assertNoError(client.getOutgoingWebhooksForTeam(teamId));
    List<OutgoingWebhook> webhooks = response.readEntity();

    assertThat(webhooks.stream().map(OutgoingWebhook::getId).collect(Collectors.toSet()),
        containsInAnyOrder(webhook.getId()));
  }

  @Test
  void listOutgoingWebhooksForChannel() {
    th.logout().loginTeamAdmin();
    String teamId = th.basicTeam().getId();
    String channelId = th.basicChannel().getId();
    String displayName = th.newRandomString(32);
    List<String> callbackUrls = Arrays.asList("http://callback-url");
    OutgoingWebhook webhook = new OutgoingWebhook();
    webhook.setTeamId(teamId);
    webhook.setChannelId(channelId);
    webhook.setDisplayName(displayName);
    webhook.setTriggerWords(Arrays.asList("trigger" + th.newRandomString(10)));
    webhook.setCallbackUrls(callbackUrls);
    webhook = assertNoError(client.createOutgoingWebhook(webhook)).readEntity();
    {
      OutgoingWebhook otherChannelHook = new OutgoingWebhook();
      otherChannelHook.setTeamId(teamId);
      otherChannelHook.setChannelId(th.basicChannel2().getId());
      otherChannelHook.setDisplayName(displayName);
      otherChannelHook.setTriggerWords(Arrays.asList("trigger" + th.newRandomString(10)));
      otherChannelHook.setCallbackUrls(callbackUrls);
      otherChannelHook = assertNoError(client.createOutgoingWebhook(otherChannelHook)).readEntity();

      OutgoingWebhook noChannelHook = new OutgoingWebhook();
      noChannelHook.setTeamId(teamId);
      noChannelHook.setDisplayName(displayName);
      noChannelHook.setTriggerWords(Arrays.asList("trigger" + th.newRandomString(10)));
      noChannelHook.setCallbackUrls(callbackUrls);
      noChannelHook = assertNoError(client.createOutgoingWebhook(noChannelHook)).readEntity();
    }

    ApiResponse<OutgoingWebhookList> response = assertNoError(client.getOutgoingWebhooksForChannel(channelId));
    List<OutgoingWebhook> webhooks = response.readEntity();

    assertThat(webhooks.stream().map(OutgoingWebhook::getId).collect(Collectors.toSet()),
        containsInAnyOrder(webhook.getId()));
  }

  @Test
  void getOutgoingWebhook() {
    th.logout().loginTeamAdmin();
    OutgoingWebhook webhook = new OutgoingWebhook();
    webhook.setTeamId(th.basicTeam().getId());
    webhook.setDisplayName(th.newRandomString(32));
    webhook.setTriggerWords(Arrays.asList("trigger"));
    webhook.setCallbackUrls(Arrays.asList("http://callback-url"));
    webhook = assertNoError(client.createOutgoingWebhook(webhook)).readEntity();

    String webhookId = webhook.getId();
    ApiResponse<OutgoingWebhook> response = assertNoError(client.getOutgoingWebhook(webhookId));
    OutgoingWebhook responseWebhook = response.readEntity();

    assertThat(responseWebhook.getId(), is(webhookId));
  }

  @Test
  void deleteOutgoingWebhook() {
    th.logout().loginTeamAdmin();
    OutgoingWebhook webhook = new OutgoingWebhook();
    webhook.setTeamId(th.basicTeam().getId());
    webhook.setDisplayName(th.newRandomString(32));
    webhook.setTriggerWords(Arrays.asList("trigger"));
    webhook.setCallbackUrls(Arrays.asList("http://callback-url"));
    webhook = assertNoError(client.createOutgoingWebhook(webhook)).readEntity();

    String webhookId = webhook.getId();
    ApiResponse<Boolean> response = assertNoError(client.deleteOutgoingWebhook(webhookId));
    boolean result = response.readEntity();

    assertThat(result, is(true));
    assertThat(client.getOutgoingWebhooksForTeam(th.basicTeam().getId()).readEntity().stream()
        .map(OutgoingWebhook::getId).collect(Collectors.toSet()), not(hasItem(webhookId)));
  }

  @Test
  void updateOutgoingWebhook() {
    th.logout().loginTeamAdmin();
    OutgoingWebhook webhook = new OutgoingWebhook();
    webhook.setTeamId(th.basicTeam().getId());
    webhook.setDisplayName(th.newRandomString(32));
    webhook.setTriggerWords(Arrays.asList("trigger"));
    webhook.setCallbackUrls(Arrays.asList("http://callback-url"));
    webhook = assertNoError(client.createOutgoingWebhook(webhook)).readEntity();

    webhook.setChannelId(th.basicChannel().getId());
    webhook.setDisplayName("update" + webhook.getDisplayName());
    webhook.setDescription("update");
    ApiResponse<OutgoingWebhook> response = assertNoError(client.updateOutgoingWebhook(webhook));
    OutgoingWebhook updated = response.readEntity();

    assertThat(updated.getId(), is(webhook.getId()));
    assertThat(updated.getChannelId(), is(webhook.getChannelId()));
    assertThat(updated.getDisplayName(), is(webhook.getDisplayName()));
    assertThat(updated.getDescription(), is(webhook.getDescription()));
  }

  @Test
  void regenerateOutgoingWebhookToken() {
    th.logout().loginTeamAdmin();
    OutgoingWebhook webhook = new OutgoingWebhook();
    webhook.setTeamId(th.basicTeam().getId());
    webhook.setDisplayName(th.newRandomString(32));
    webhook.setTriggerWords(Arrays.asList("trigger"));
    webhook.setCallbackUrls(Arrays.asList("http://callback-url"));
    webhook = assertNoError(client.createOutgoingWebhook(webhook)).readEntity();

    String currentToken = webhook.getToken();
    ApiResponse<OutgoingWebhook> response = assertNoError(client.regenOutgoingHookToken(webhook.getId()));
    OutgoingWebhook updated = response.readEntity();
    String newToken = updated.getToken();

    assertThat(newToken, is(not(currentToken)));
  }
}