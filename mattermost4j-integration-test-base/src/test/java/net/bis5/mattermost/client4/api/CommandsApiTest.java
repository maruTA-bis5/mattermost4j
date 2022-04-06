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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

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
import net.bis5.mattermost.model.Command;
import net.bis5.mattermost.model.CommandList;
import net.bis5.mattermost.model.CommandMethod;
import net.bis5.mattermost.model.CommandResponse;
import net.bis5.mattermost.model.CommandResponseType;

// Commands
@ExtendWith(MattermostClientTestExtension.class)
class CommandsApiTest implements MattermostClientTest {
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
  void createCommand() {
    th.loginTeamAdmin();
    Command command = new Command();
    // required params
    command.setTeamId(th.basicTeam().getId());
    command.setMethod(CommandMethod.GET);
    command.setTrigger("trigger");
    command.setUrl("http://url");
    // optional params
    command.setDescription(th.newRandomString(32));
    command.setUserName("username");
    command.setIconUrl("http://icon-url");
    command.setAutoComplete(true);
    command.setAutoCompleteDesc(th.newRandomString(32));
    command.setAutoCompleteHint(th.newRandomString(32));

    ApiResponse<Command> response = assertNoError(client.createCommand(command));
    Command created = response.readEntity();

    assertThat(created.getId(), is(not(nullValue())));
    assertThat(created.getToken(), is(not(nullValue())));
    assertThat(created.getTeamId(), is(command.getTeamId()));
    assertThat(created.getMethod(), is(command.getMethod()));
    assertThat(created.getTrigger(), is(command.getTrigger()));
    assertThat(created.getUrl(), is(command.getUrl()));
    assertThat(created.getDescription(), is(command.getDescription()));
    assertThat(created.getUserName(), is(command.getUserName()));
    assertThat(created.getIconUrl(), is(command.getIconUrl()));
    assertThat(created.isAutoComplete(), is(command.isAutoComplete()));
    assertThat(created.getAutoCompleteDesc(), is(command.getAutoCompleteDesc()));
    assertThat(created.getAutoCompleteHint(), is(command.getAutoCompleteHint()));
  }

  @Test
  void listCommandsForTeam() {
    th.logout().loginTeamAdmin();
    String teamId = th.basicTeam().getId();
    Command command = new Command();
    {
      command.setTeamId(teamId);
      command.setMethod(CommandMethod.GET);
      command.setTrigger("trigger");
      command.setUrl("http://url");
      command = assertNoError(client.createCommand(command)).readEntity();
    }

    ApiResponse<CommandList> response = assertNoError(client.listCommands(teamId));
    List<Command> commands = response.readEntity();

    assertThat(commands.size(), is(greaterThanOrEqualTo(1)));
    assertThat(commands.stream().map(Command::getId).collect(Collectors.toSet()), hasItem(command.getId()));
  }

  @Test
  void listCommandForTeamExcludeSystemCommands() {
    th.logout().loginTeamAdmin();
    String teamId = th.basicTeam().getId();
    Command command = new Command();
    {
      command.setTeamId(teamId);
      command.setMethod(CommandMethod.GET);
      command.setTrigger("trigger");
      command.setUrl("http://ur)");
      command = assertNoError(client.createCommand(command)).readEntity();
    }

    ApiResponse<CommandList> response = assertNoError(client.listCommands(teamId, true));
    List<Command> commands = response.readEntity();

    assertThat(commands.size(), is(1));
    assertThat(commands.stream().map(Command::getId).collect(Collectors.toSet()), hasItem(command.getId()));
  }

  @Test
  void getAutoCompleteCommands() {
    th.loginTeamAdmin();
    String teamId = th.basicTeam().getId();
    Command autoCompleteCmd = new Command();
    autoCompleteCmd.setAutoComplete(true);
    autoCompleteCmd.setAutoCompleteDesc(th.newRandomString(32));
    autoCompleteCmd.setAutoCompleteHint(th.newRandomString(32));
    {
      autoCompleteCmd.setTeamId(teamId);
      autoCompleteCmd.setMethod(CommandMethod.GET);
      autoCompleteCmd.setTrigger("trigger1");
      autoCompleteCmd.setUrl("http://url1");
      autoCompleteCmd = assertNoError(client.createCommand(autoCompleteCmd)).readEntity();
    }
    Command noCompleteCmd = new Command();
    {
      noCompleteCmd.setTeamId(teamId);
      noCompleteCmd.setMethod(CommandMethod.GET);
      noCompleteCmd.setTrigger("trigger2");
      noCompleteCmd.setUrl("http://url2");
      noCompleteCmd = assertNoError(client.createCommand(noCompleteCmd)).readEntity();
    }
    th.logout();

    th.loginBasic();
    ApiResponse<CommandList> response = assertNoError(client.listAutocompleteCommands(teamId));
    List<Command> commandList = response.readEntity();

    assertThat(commandList.stream().map(Command::getId).collect(Collectors.toSet()),
        hasItem(autoCompleteCmd.getId()));
  }

  @Test
  void updateCommand() {
    th.logout().loginTeamAdmin();
    Command command = new Command();
    {
      command.setTeamId(th.basicTeam().getId());
      command.setMethod(CommandMethod.GET);
      command.setTrigger("trigger");
      command.setUrl("http://url");
      command.setUserName(th.newRandomString(8));
      command.setIconUrl("http://icon-url");
      command.setAutoComplete(true);
      command.setAutoCompleteDesc(th.newRandomString(32));
      command.setAutoCompleteHint(th.newRandomString(32));
      command.setDisplayName(th.newRandomString(32));
      command.setDescription(th.newRandomString(32));
      command = assertNoError(client.createCommand(command)).readEntity();
    }

    command.setMethod(CommandMethod.POST);
    command.setTrigger("new" + command.getTrigger());
    command.setUrl(command.getUrl() + "/new");
    command.setUserName("new" + command.getUserName());
    command.setIconUrl(command.getIconUrl() + "/new");
    // command.setAutoComplete(false);
    command.setAutoCompleteDesc("new" + command.getAutoCompleteDesc());
    command.setAutoCompleteHint("new" + command.getAutoCompleteHint());
    command.setDisplayName("new" + command.getDisplayName());
    command.setDescription("new" + command.getDescription());
    ApiResponse<Command> response = assertNoError(client.updateCommand(command));
    Command updated = response.readEntity();

    assertThat(updated.getId(), is(command.getId()));
    assertThat(updated.getMethod(), is(command.getMethod()));
    assertThat(updated.getTrigger(), is(command.getTrigger()));
    assertThat(updated.getUrl(), is(command.getUrl()));
    assertThat(updated.getUserName(), is(command.getUserName()));
    assertThat(updated.getIconUrl(), is(command.getIconUrl()));
    assertThat(updated.isAutoComplete(), is(command.isAutoComplete()));
    assertThat(updated.getAutoCompleteDesc(), is(command.getAutoCompleteDesc()));
    assertThat(updated.getAutoCompleteHint(), is(command.getAutoCompleteHint()));
    assertThat(updated.getDisplayName(), is(command.getDisplayName()));
    assertThat(updated.getDescription(), is(command.getDescription()));
  }

  @Test
  void deleteCommand() {
    th.logout().loginTeamAdmin();
    Command command = new Command();
    {
      command.setTeamId(th.basicTeam().getId());
      command.setMethod(CommandMethod.GET);
      command.setTrigger("trigger");
      command.setUrl("http://url");
      command = assertNoError(client.createCommand(command)).readEntity();
    }

    String commandId = command.getId();
    ApiResponse<Boolean> response = assertNoError(client.deleteCommand(commandId));
    boolean result = response.readEntity();

    assertThat(result, is(true));
    assertThat(client.listCommands(th.basicTeam().getId(), true).readEntity().stream().map(Command::getId)
        .collect(Collectors.toSet()), not(hasItem(commandId)));
  }

  @Test
  void generateNewToken() {
    th.logout().loginTeamAdmin();
    Command command = new Command();
    {
      command.setTeamId(th.basicTeam().getId());
      command.setMethod(CommandMethod.GET);
      command.setTrigger("trigger");
      command.setUrl("http://url");
      command = assertNoError(client.createCommand(command)).readEntity();
    }
    String currentToken = command.getToken();
    String commandId = command.getId();

    ApiResponse<String> response = assertNoError(client.regenCommandToken(commandId));
    String newToken = response.readEntity();

    assertThat(newToken, is(not(currentToken)));
  }

  @Test
  void executeCommand() {
    String inChannel = th.basicChannel().getId();
    String command = "/away";

    ApiResponse<CommandResponse> response = assertNoError(client.executeCommand(inChannel, command));
    CommandResponse commandResponse = response.readEntity();

    // "/away" command should return an ephemeral message.
    assertThat(commandResponse.getResponseType(), is(CommandResponseType.Ephemeral));
    assertThat(commandResponse.getText(), is(not(nullValue())));
  }
}