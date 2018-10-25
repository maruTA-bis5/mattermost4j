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

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * User preferences
 * 
 * @author Takayuki Maruyama
 */
@Data
public class Preference {

	@JsonProperty("user_id")
	private String userid;
	@JsonProperty("category")
	private PreferenceCategory category;
	@JsonProperty("name")
	private String name;
	@JsonProperty("value")
	private String value;

	@RequiredArgsConstructor
	public static enum Name {
		CollapseSetting("collapse_previews"), DisplayNameFormat("name_format"), LastChannel("channel"), LastTeam(
				"team"), EmailInterval("email_interval"), ChannelDisplayMode("channel_display_mode");
		@Getter
		private final String key;
	}
}
