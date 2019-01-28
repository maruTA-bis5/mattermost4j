/*
 * Copyright (c) 2017 Takayuki Maruyama
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

package net.bis5.mattermost.client4.api;

import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.model.Command;
import net.bis5.mattermost.model.CommandList;
import net.bis5.mattermost.model.CommandResponse;

/**
 * Command API.
 * 
 * @author Takayuki Maruyama
 */
public interface CommandsApi {

  /**
   * will create a new command if the user have the right permissions.
   */
  ApiResponse<Command> createCommand(Command cmd);

  /**
   * updates a command based on the provided Command object.
   */
  ApiResponse<Command> updateCommand(Command cmd);

  /**
   * deletes a command based on the provided command id string.
   */
  ApiResponse<Boolean> deleteCommand(String commandId);

  /**
   * will retrieve a list of commands available in the team.
   */
  default ApiResponse<CommandList> listCommands(String teamId) {
    return listCommands(teamId, false);
  }

  /**
   * will retrieve a list of commands available in the team.
   */
  ApiResponse<CommandList> listCommands(String teamId, boolean customOnly);

  /**
   * executes a given command.
   */
  ApiResponse<CommandResponse> executeCommand(String channelId, String command);

  /**
   * will retrieve a list of commands available in the team.
   */
  ApiResponse<CommandList> listAutocompleteCommands(String teamId);

  /**
   * will create a new token if the user have the right permissions.
   */
  ApiResponse<String> regenCommandToken(String commandId);

}
