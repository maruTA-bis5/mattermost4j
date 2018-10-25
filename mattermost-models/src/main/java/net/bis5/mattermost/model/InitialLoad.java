/*
 * Copyright (c) 2016-present, Takayuki Maruyama
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 */
package net.bis5.mattermost.model;

import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Initial load payload
 * 
 * @author Takayuki Maruyama
 */
@Data
public class InitialLoad {

	@JsonProperty("user")
	private User user;
	@JsonProperty("team_members")
	private List<TeamMember> teamMembers;
	@JsonProperty("teams")
	private List<Team> teams;
	@JsonProperty("preferences")
	private Preferences preferences;
	@JsonProperty("client_cfg")
	private Map<String, String> clientCfg;
	@JsonProperty("license_cfg")
	private Map<String, String> licenseCfg;
	@JsonProperty("no_accounts")
	private boolean noAccounts;

}
