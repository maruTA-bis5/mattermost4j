/*
 * @(#) net.bis5.mattermost.model.UserPatch
 * Copyright (c) 2017-present, Maruyama Takayuki
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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * TODO 型の説明
 * 
 * @author Maruyama Takayuki
 * @since 2017/06/10
 */
@Data
public class UserPatch {

	@JsonProperty("username")
	private String username;
	@JsonProperty("nickname")
	private String nickname;
	@JsonProperty("first_name")
	private String firstName;
	@JsonProperty("last_name")
	private String lastName;
	@JsonProperty("position")
	private String position;
	@JsonProperty("email")
	private String email;
	@JsonProperty("props")
	private Map<String, String> props;
	@JsonProperty("notify_props")
	private Map<String, String> notifyProps;
	@JsonProperty("locale")
	private String locale;

}
