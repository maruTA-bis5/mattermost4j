/*
 * Copyright (c) 2021-present, Takayuki Maruyama
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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Slash Command Autocomplete
 * 
 * @since Mattermost Server 5.24
 */
@Data
public class AutocompleteData {

  private String trigger;
  private String hint;
  @JsonProperty("HelpText")
  private String helpText;
  @JsonProperty("RoleId")
  private String roleId;
  private List<AutocompleteArg> arguments = new ArrayList<>();
  @JsonProperty("SubCommands")
  private List<AutocompleteData> subCommands = new ArrayList<>();

}
