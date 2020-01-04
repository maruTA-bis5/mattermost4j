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
import static net.bis5.mattermost.client4.Assertions.isNotSupportVersion;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Set;
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
import net.bis5.mattermost.model.Bot;
import net.bis5.mattermost.model.BotPatch;
import net.bis5.mattermost.model.Bots;
import net.bis5.mattermost.model.Config;
import net.bis5.mattermost.model.User;

@ExtendWith(MattermostClientTestExtension.class)
class BotsApiTest implements MattermostClientTest {
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
  public void createBot() {
    th.logout().loginSystemAdmin();
    ApiResponse<Config> configResponse = client.getConfig();
    if (isNotSupportVersion("5.10.0", configResponse)) {
      return;
    }
    Config config = configResponse.readEntity();
    config.getServiceSettings().setEnableBotAccountCreation(true);
    assertNoError(client.updateConfig(config));

    BotPatch bot = new BotPatch();
    bot.setUsername("bot_" + th.newId());
    bot.setDisplayName("botsApiTestName");
    bot.setDescription("Bot account for @" + bot.getUsername());

    ApiResponse<Bot> response = assertNoError(client.createBot(bot));

    Bot createdBot = response.readEntity();
    assertEquals(th.systemAdminUser().getId(), createdBot.getOwnerId());
    assertEquals(bot.getUsername(), createdBot.getUsername());
    assertEquals(bot.getDisplayName(), createdBot.getDisplayName());
    assertEquals(bot.getDescription(), createdBot.getDescription());
  }

  @Test
  public void patchBot() {
    th.logout().loginSystemAdmin();
    ApiResponse<Config> configResponse = client.getConfig();
    if (isNotSupportVersion("5.10.0", configResponse)) {
      return;
    }
    Config config = configResponse.readEntity();
    config.getServiceSettings().setEnableBotAccountCreation(true);
    assertNoError(client.updateConfig(config));

    BotPatch patch = new BotPatch();
    patch.setUsername("bot_" + th.newId());
    patch.setDisplayName("botsApiTestName");
    patch.setDescription("Bot account for @" + patch.getUsername());
    Bot originalBot = assertNoError(client.createBot(patch)).readEntity();

    patch.setUsername("up_" + th.newId());
    patch.setDisplayName("up_" + patch.getDisplayName());
    patch.setDescription("up_" + patch.getDescription());

    ApiResponse<Bot> response = assertNoError(client.patchBot(originalBot.getUserId(), patch));

    Bot patchedBot = response.readEntity();
    assertEquals(patch.getUsername(), patchedBot.getUsername());
    assertEquals(patch.getDisplayName(), patchedBot.getDisplayName());
    assertEquals(patch.getDescription(), patchedBot.getDescription());
  }

  @Test
  public void getBot() {
    th.logout().loginSystemAdmin();
    ApiResponse<Config> configResponse = client.getConfig();
    if (isNotSupportVersion("5.10.0", configResponse)) {
      return;
    }
    Config config = configResponse.readEntity();
    config.getServiceSettings().setEnableBotAccountCreation(true);
    assertNoError(client.updateConfig(config));

    BotPatch patch = new BotPatch();
    patch.setUsername("bot_" + th.newId());
    patch.setDisplayName("botsApiTestName");
    patch.setDescription("Bot account for @" + patch.getUsername());
    Bot originalBot = assertNoError(client.createBot(patch)).readEntity();

    ApiResponse<Bot> response = assertNoError(client.getBot(originalBot.getUserId()));

    Bot receiveddBot = response.readEntity();
    assertEquals(originalBot.getUsername(), receiveddBot.getUsername());
    assertEquals(originalBot.getDisplayName(), receiveddBot.getDisplayName());
    assertEquals(originalBot.getDescription(), receiveddBot.getDescription());
  }

  @Test
  public void getBots() {
    th.logout().loginSystemAdmin();
    ApiResponse<Config> configResponse = client.getConfig();
    if (isNotSupportVersion("5.10.0", configResponse)) {
      return;
    }
    Config config = configResponse.readEntity();
    config.getServiceSettings().setEnableBotAccountCreation(true);
    assertNoError(client.updateConfig(config));

    BotPatch patch = new BotPatch();
    patch.setUsername("bot1_" + th.newId());
    patch.setDisplayName("botsApiTestName1");
    patch.setDescription("Bot account for @" + patch.getUsername());
    Bot originalBot1 = assertNoError(client.createBot(patch)).readEntity();

    patch = new BotPatch();
    patch.setUsername("bot2_" + th.newId());
    patch.setDisplayName("botsApiTestName2");
    patch.setDescription("Bot account for @" + patch.getUsername());
    Bot originalBot2 = assertNoError(client.createBot(patch)).readEntity();

    Bots bots = assertNoError(client.getBots()).readEntity();
    Set<String> botUserIds = bots.stream() //
        .map(Bot::getUserId) //
        .collect(Collectors.toSet());
    assertThat(botUserIds, hasItems(originalBot1.getUserId(), originalBot2.getUserId()));
  }

  @Test
  public void disableBot() {
    th.logout().loginSystemAdmin();
    ApiResponse<Config> configResponse = client.getConfig();
    if (isNotSupportVersion("5.10.0", configResponse)) {
      return;
    }
    Config config = configResponse.readEntity();
    config.getServiceSettings().setEnableBotAccountCreation(true);
    assertNoError(client.updateConfig(config));

    BotPatch patch = new BotPatch();
    patch.setUsername("bot_" + th.newId());
    patch.setDisplayName("botsApiTestName");
    patch.setDescription("Bot account for @" + patch.getUsername());
    Bot originalBot = assertNoError(client.createBot(patch)).readEntity();

    ApiResponse<Bot> response = assertNoError(client.disableBot(originalBot.getUserId()));
    assertTrue(response.readEntity().getDeleteAt() != 0L);
  }

  @Test
  public void enableBot() {
    th.logout().loginSystemAdmin();
    ApiResponse<Config> configResponse = client.getConfig();
    if (isNotSupportVersion("5.10.0", configResponse)) {
      return;
    }
    Config config = configResponse.readEntity();
    config.getServiceSettings().setEnableBotAccountCreation(true);
    assertNoError(client.updateConfig(config));

    BotPatch patch = new BotPatch();
    patch.setUsername("bot_" + th.newId());
    patch.setDisplayName("botsApiTestName");
    patch.setDescription("Bot account for @" + patch.getUsername());
    Bot originalBot = assertNoError(client.createBot(patch)).readEntity();
    ApiResponse<Bot> response = assertNoError(client.disableBot(originalBot.getUserId()));
    assertTrue(response.readEntity().getDeleteAt() != 0L);

    response = assertNoError(client.enableBot(originalBot.getUserId()));
    assertTrue(response.readEntity().getDeleteAt() == 0L);
  }

  @Test
  public void assignBotToUser() {
    th.logout().loginSystemAdmin();
    ApiResponse<Config> configResponse = client.getConfig();
    if (isNotSupportVersion("5.10.0", configResponse)) {
      return;
    }
    Config config = configResponse.readEntity();
    config.getServiceSettings().setEnableBotAccountCreation(true);
    assertNoError(client.updateConfig(config));

    BotPatch patch = new BotPatch();
    patch.setUsername("bot_" + th.newId());
    patch.setDisplayName("botsApiTestName");
    patch.setDescription("Bot account for @" + patch.getUsername());
    Bot originalBot = assertNoError(client.createBot(patch)).readEntity();
    assertEquals(th.systemAdminUser().getId(), originalBot.getOwnerId());

    User secondSysAdmin = th.createSystemAdminUser();

    ApiResponse<Bot> response = assertNoError(
        client.assignBotToUser(originalBot.getUserId(), secondSysAdmin.getId()));
    assertEquals(secondSysAdmin.getId(), response.readEntity().getOwnerId());
  }
}