/*
 * @(#) net.bis5.mattermost.model.IncomingWebhookRequest
 * Copyright (c) 2016-present, Maruyama Takayuki
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * TODO 型の説明
 * 
 * @author takayuki
 * @since 2016/10/09
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncomingWebhookRequest {

	@JsonProperty("text")
	private String text;
	@JsonProperty("username")
	private String username;
	@JsonProperty("icon_url")
	private String iconUrl;
	@JsonProperty("channel")
	private String channel;
	@JsonProperty("props")
	private Map<String, String> props;
	@JsonProperty("attachments")
	private List<SlackAttachment> attachments;
	@JsonProperty("type")
	private String type;

}
