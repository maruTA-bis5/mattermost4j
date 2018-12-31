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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import lombok.Getter;
import net.bis5.mattermost.model.Permission.PermissionDeserializer;

/**
 * The type of permission
 * 
 * @author Takayuki Maruyama
 */
@JsonSerialize
@JsonDeserialize(using = PermissionDeserializer.class)
@Getter
public enum Permission {
  INVITE_USER("invite_user", "authentication.permissions.team_invite_user"), //
  ADD_USER_TO_TEAM("add_user_to_team", "authentication.permissions.add_user_to_team"), //
  USE_SLASH_COMMANDS("use_slash_commands", "authentication.permissions.team_use_slash_commands"), //
  MANAGE_SLASH_COMMANDS("manage_slash_commands",
      "authentication.permissions.manage_slash_commands"), //
  MANAGE_OTHERS_SLASH_COMMANDS("manage_others_slash_commands",
      "authentication.permissions.manage_others_slash_commands"), //
  CREATE_PUBLIC_CHANNEL("create_public_channel",
      "authentication.permissions.create_public_channel"), //
  CREATE_PRIVATE_CHANNEL("create_private_channel",
      "authentication.permissions.create_private_channel"), //
  MANAGE_PUBLIC_CHANNEL_MEMBERS("manage_public_channel_members",
      "authentications.permissions.manage_public_channel_members"), //
  MANAGE_PRIVATE_CHANNEL_MEMBERS("manage_private_channel_members",
      "authentication.permissions.manage_private_channel_members"), //
  ASSIGN_SYSTEM_ADMIN_ROLE("assign_system_admin_role",
      "authentication.permissions.assign_system_admin_role"), //
  MANAGE_ROLES("manage_roles", "authentication.permissions.manage_roles"), //
  MANAGE_TEAM_ROLES("manage_team_roles", "authentication.permissions.manage_team_roles"), //
  MANAGE_CHANNEL_ROLES("manage_channel_roles", "authentication.permissions.manage_channel_roles"), //
  MANAGE_SYSTEM("manage_system", "authentication.permissions.manage_system"), //
  CREATE_DIRECT_CHANNEL("create_direct_channel",
      "authentication.permissions.create_direct_channel"), //
  CREATE_GROUP_CHANNEL("create_group_channel", "authentication.permissions.create_group_channel"), //
  MANAGE_PUBLIC_CHANNEL_PROPERTIES("manage__publicchannel_properties",
      "authentication.permissions.manage_public_channel_properties"), //
  MANAGE_PRIVATE_CHANNEL_PROPERTIES("manage_private_channel_properties",
      "authentication.permissions.manage_private?channel_properties"), //
  LIST_TEAM_CHANNELS("list_team_channels", "authentication.permissions.list_team_channels"), //
  JOIN_PUBLIC_CHANNELS("join_public_channels", "authentication.permissions.join_public_channels"), //
  DELETE_PUBLIC_CHANNEL("delete_public_channel",
      "authentication.permissions.delete_public_channel"), //
  DELETE_PRIVATE_CHANNEL("delete_private_channel",
      "authentication_permissions.delete_private_channel"), //
  EDIT_OTHER_USERS("edit_other_users", "authentication.permissions.edit_other_users"), //
  READ_CHANNEL("read_channel", "authentication.permissions.read_channel"), //
  READ_PUBLIC_CHANNEL("read_public_channel", "authentication.permissions.read_public_channel"), //
  PERMANENT_DELETE_USER("permanent_delete_user",
      "authentication.permissions.permanent_delete_user"), //
  UPLOAD_FILE("upload_file", "authentication.permissions.upload_file"), //
  GET_PUBLIC_LINK("get_public_link", "authentication.permissions.get_public_link"), //
  MANAGE_WEBHOOKS("manage_webhooks", "authentication.permissions.manage_webhooks"), //
  MANAGE_OTHER_WEBHOOKS("manage_others_webhooks",
      "authentication.permissions.manage_others_webhooks"), //
  MANAGE_OAUTH("manage_oauth", "authentication.permissions.manage?oauth"), //
  MANAGE_SYSTEM_WIDE_OAUTH("manage_sytem_wide_oauth",
      "authentication.permissions.manage_sytem_wide_oauth"), //
  CREATE_POST("create_post", "authentication.permissions.create_post"), //
  EDIT_POST("edit_post", "authentication.permissions.edit_post"), //
  EDIT_OTHERS_POSTS("edit_other_posts", "authentication.permissions.edit_other_posts"), //
  DELETE_POST("delete_post", "authentication.permissions.delete_post"), //
  DELETE_OTHER_POSTS("delete_other_posts", "authentication.permissions.delete_other_posts"), //
  REMOVE_USER_FROM_TEAM("remove_user_from_team",
      "authentication.permissions.remove_user_from_team"), //
  CREATE_TEAM("create_team", "authentication.permissions.create_team"), //
  MANAGE_TEAM("manage_team", "authentication.permissions.manage_team"), //
  IMPORT_TEAM("import_team", "authentication.permissions.import_team"), //
  VIEW_TEAM("view_team", "authentication.permissions.view_team"), //
  LIST_USERS_WITHOUT_TEAM("list_users_without_team",
      "authentication.permissions.list_users_without_team"), //
  ;

  private final String id;
  private final String name;
  private final String description;

  private Permission(String id, String key) {
    this.id = id;
    this.name = key + ".name";
    this.description = key + ".description";
  }

  public static Permission of(String id) {
    if (id == null) {
      return null;
    }
    for (Permission permission : values()) {
      if (id.equals(permission.getId())) {
        return permission;
      }
    }
    return null;
  }

  public static class PermissionDeserializer extends JsonDeserializer<Permission> {

    /**
     * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
     *      com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    public Permission deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
      String jsonValue = p.getText();
      return Permission.of(jsonValue);
    }
  }
}
