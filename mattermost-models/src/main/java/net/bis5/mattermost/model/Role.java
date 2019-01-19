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

import lombok.Getter;

/**
 * Built-in role definitions.
 * 
 * @author Takayuki Maruyama
 */
@Getter
public enum Role {

  SYSTEM_USER, SYSTEM_ADMIN, SYSTEM_POST_ALL, SYSTEM_POST_ALL_PUBLIC, SYSTEM_USER_ACCESS_TOKEN, //
  TEAM_USER, TEAM_ADMIN, TEAM_POST_ALL, TEAM_POST_ALL_PUBLIC, //
  CHANNEL_USER, CHANNEL_ADMIN;

  private final String roleName;

  Role() {
    this.roleName = name().toLowerCase();
  }

}
