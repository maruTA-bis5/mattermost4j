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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Channel member
 * 
 * @author Takayuki Maruyama
 */
@Data
public class ChannelMember {

	@JsonProperty("channel_id")
	private String channelId;
	@JsonProperty("user_id")
	private String userId;
	@JsonProperty("roles")
	private String roles;
	@JsonProperty("last_viewed_at")
	private long lastViewedAt;
	@JsonProperty("msg_count")
	private long msgCount;
	@JsonProperty("mention_count")
	private long mentionCount;
	@JsonProperty("notify_props")
	private Map<String, String> notifyProps;
	@JsonProperty("last_update_at")
	private long lastUpdateAt;
	/** @since Mattermost Server XXX what ver? */
	private boolean schemeUser;
	/** @since Mattermost Server XXX what ver? */
	private boolean schemeAdmin;
	/** @since Mattermost Server XXX what ver? */
	private String explicitRoles;
}
