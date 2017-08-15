/*
 * @(#) net.bis5.mattermost.model.PostType
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

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import net.bis5.mattermost.model.PostType.PostTypeDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * TODO 型の説明
 * 
 * @author Maruyama Takayuki
 * @since 2017/06/09
 */
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = PostTypeDeserializer.class)
@Getter
public enum PostType implements HasCode<PostType> {

	DEFAULT(""), //
	SLACK_ATTACHMENT("slack_attachment"), //
	SYSTEM_GENERIC("system_generic"), //
	/**
	 * @deprecated use {@link POST_JOIN_CHANNEL} or {@link POST_LEAVE_CHANNEL}
	 *             instead.
	 */
	@Deprecated
	JOIN_LEAVE("system_join_leave"), //
	JOIN_CHANNEL("system_join_channel"), //
	LEAVE_CHANNEL("system_leave_channel"), //
	/**
	 * @deprecated use {@link POST_ADD_TO_CHANNEL} or
	 *             {@link POST_REMOVE_FROM_CHANNEL} instead.
	 */
	@Deprecated
	ADD_REMOVE("system_add_remove"), //
	ADD_TO_CHANNEL("system_add_to_channel"), //
	REMOVE_FROM_CHANNEL("system_remove_from_channel"), //
	HEADER_CHANGE("system_header_change"), //
	DISPLAYNAME_CHANGE("system_displayname_change"), //
	PURPOSE_CHANGE("system_purpose_change"), //
	CHANNEL_DELETED("system_channel_deleted"), //
	EPHEMERAL("system_ephemeral");
	private final String code;

	private PostType(String code) {
		this.code = code;
	}

	public static PostType of(String code) {
		return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(DEFAULT);
	}

	static class PostTypeDeserializer extends JsonDeserializer<PostType> {

		/**
		 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
		 *      com.fasterxml.jackson.databind.DeserializationContext)
		 */
		@Override
		public PostType deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			String jsonValue = p.getText();
			return of(jsonValue);
		}
	}

}
