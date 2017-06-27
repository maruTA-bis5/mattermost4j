/*
 * @(#) net.bis5.mattermost.model.SlackAttachment
 * Copyright (c) 2017 Maruyama Takayuki <bis5.wsys@gmail.com>
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

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * TODO 型の説明
 * 
 * @author Maruyama Takayuki
 * @since 2017/06/07
 */
@Data
public class SlackAttachment {

	@JsonProperty("id")
	private long id;
	@JsonProperty("fallback")
	private String fallback;
	@JsonProperty("color")
	private String color;
	@JsonProperty("pretext")
	private String pretext;
	@JsonProperty("author_name")
	private String authorName;
	@JsonProperty("author_link")
	private String authorLink;
	@JsonProperty("author_icon")
	private String authorIcon;
	@JsonProperty("title")
	private String title;
	@JsonProperty("title_link")
	private String titleLink;
	@JsonProperty("text")
	private String text;
	@JsonProperty("fields")
	private List<SlackAttachmentField> fields;
	@JsonProperty("image_url")
	private String imageUrl;
	@JsonProperty("thumb_url")
	private String thumbUrl;
	@JsonProperty("footer")
	private String footer;
	@JsonProperty("footer_icon")
	private String footerIcon;
	@JsonProperty("ts")
	private Object timestamp; // This is either a string of an int64

}
