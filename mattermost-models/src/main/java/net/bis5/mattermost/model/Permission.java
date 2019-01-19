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

package net.bis5.mattermost.model;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The type of permission.
 * 
 * @author Takayuki Maruyama
 */
@Getter
@RequiredArgsConstructor
public enum Permission {


  INVITE_USER(Scope.TEAM), ADD_USER_TO_TEAM(Scope.TEAM), //
  USE_SLASH_COMMANDS(Scope.CHANNEL), //
  MANAGE_SLASH_COMMANDS(Scope.SYSTEM), MANAGE_OTHERS_SLASH_COMMANDS(Scope.SYSTEM), //
  CREATE_PUBLIC_CHANNEL(Scope.TEAM), CREATE_PRIVATE_CHANNEL(Scope.TEAM), //
  MANAGE_PUBLIC_CHANNEL_MEMBERS(Scope.CHANNEL), MANAGE_PRIVATE_CHANNEL_MEMBERS(Scope.CHANNEL), //
  ASSIGN_SYSTEM_ADMIN_ROLE(Scope.SYSTEM), MANAGE_ROLES(Scope.SYSTEM), //
  CREATE_DIRECT_CHANNEL(Scope.SYSTEM), CREATE_GROUP_CHANNEL(Scope.SYSTEM), //
  MANAGE_PUBLIC_CHANNEL_PROPERTIES(Scope.CHANNEL), //
  MANAGE_PRIVATE_CHANNEL_PROPERTIES(Scope.CHANNEL), //
  LIST_TEAM_CHANNELS(Scope.TEAM), JOIN_PUBLIC_CHANNELS(Scope.TEAM), //
  DELETE_PUBLIC_CHANNEL(Scope.CHANNEL), DELETE_PRIVATE_CHANNEL(Scope.CHANNEL), //
  EDIT_OTHER_USERS(Scope.SYSTEM), //
  READ_CHANNEL(Scope.CHANNEL), //
  ADD_REACTION(Scope.CHANNEL), REMOVE_REACTION(Scope.CHANNEL), //
  REMOVE_OTHER_REACTIONS(Scope.CHANNEL), //
  PERMANENT_DELETE_USER(Scope.SYSTEM), //
  UPLOAD_FILE(Scope.SYSTEM), //
  MANAGE_WEBHOOKS(Scope.TEAM), MANAGE_OTHERS_WEBHOOKS(Scope.TEAM), //
  MANAGE_OAUTH(Scope.SYSTEM), MANAGE_SYSTEM_WIDE_OAUTH(Scope.SYSTEM), //
  CREATE_POST(Scope.CHANNEL), EDIT_POST(Scope.CHANNEL), EDIT_OTHERS_POSTS(Scope.CHANNEL), //
  DELETE_POST(Scope.CHANNEL), DELETE_OTHERS_POSTS(Scope.CHANNEL), //
  REMOVE_USER_FROM_TEAM(Scope.TEAM), //
  CREATE_TEAM(Scope.SYSTEM), MANAGE_TEAM(Scope.TEAM), IMPORT_TEAM(Scope.SYSTEM), //
  ViEW_TEAM(Scope.TEAM), LIST_USERS_WITHOUT_TEAM(Scope.SYSTEM), //
  CREATE_USER_ACCESS_TOKEN(Scope.SYSTEM), READ_USER_ACCESS_TOKEN(Scope.SYSTEM), //
  REVOKE_USER_ACCESS_TOKEN(Scope.SYSTEM), //
  MANAGE_JOBS(Scope.SYSTEM);

  private final Scope scope;


  public String getPermissionName() {
    return name().toLowerCase();
  }

  enum Scope {
    SYSTEM, TEAM, CHANNEL;
    static Scope of(String scope) {
      return Arrays.stream(values()).filter(s -> s.name().equals(scope)).findFirst().orElse(null);
    }
  }

}
