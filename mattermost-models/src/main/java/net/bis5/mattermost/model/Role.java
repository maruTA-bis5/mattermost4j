/*
 * Copyright (c) 2016-present, Takayuki Maruyama
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
import java.util.Collections;
import java.util.List;
import lombok.Getter;

/**
 * The type of roles
 * 
 * @author Takayuki Maruyama
 */
@Getter
public enum Role {

  ROLE_CHANNEL_USER("channel_user", "authentication.roles.channel_user",
      Arrays.asList(Permission.READ_CHANNEL, Permission.MANAGE_PUBLIC_CHANNEL_MEMBERS,
          Permission.UPLOAD_FILE, Permission.GET_PUBLIC_LINK, Permission.CREATE_POST,
          Permission.EDIT_POST, Permission.USE_SLASH_COMMANDS)), //
  ROLE_CHANNEL_ADMIN("channel_admin", "authentication.roles.channel_admin",
      Arrays.asList(Permission.MANAGE_CHANNEL_ROLES)), //
  ROLE_CHANNEL_GUEST("guest", "authentication.roles.global_guest", Collections.emptyList()), //
  ROLE_TEAM_USER("team_user", "authentication.roles.team_user",
      Arrays.asList(Permission.LIST_TEAM_CHANNELS, Permission.JOIN_PUBLIC_CHANNELS,
          Permission.READ_PUBLIC_CHANNEL, Permission.VIEW_TEAM)), //
  ROLE_TEAM_ADMIN("team_admin", "authentication.roles.team_admin",
      Arrays.asList(Permission.EDIT_OTHERS_POSTS, Permission.REMOVE_USER_FROM_TEAM,
          Permission.MANAGE_TEAM, Permission.IMPORT_TEAM, Permission.MANAGE_TEAM_ROLES,
          Permission.MANAGE_CHANNEL_ROLES, Permission.MANAGE_OTHER_WEBHOOKS,
          Permission.MANAGE_SLASH_COMMANDS, Permission.MANAGE_OTHERS_SLASH_COMMANDS,
          Permission.MANAGE_WEBHOOKS)), //
  ROLE_SYSTEM_USER("system_user", "authentication.roles.global_user",
      Arrays.asList(Permission.CREATE_DIRECT_CHANNEL, Permission.CREATE_GROUP_CHANNEL,
          Permission.PERMANENT_DELETE_USER)), //
  ROLE_SYSTEM_ADMIN("system_admin", "authentication.roles.global_admin",
      Arrays.asList(Permission.values()));

  private String id;
  private String name;
  private String description;
  private List<Permission> permissions;

  private Role(String id, String key, List<Permission> permissions) {
    this.id = id;
    this.name = key + ".name";
    this.description = key + ".description";
    this.permissions = permissions;
  }

  public boolean is(String roles) {
    if (roles == null) {
      return false;
    }
    for (String role : roles.split(" ")) {
      if (this.getId().equals(role)) {
        return true;
      }
    }
    return false;
  }

}
