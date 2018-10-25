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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.bis5.mattermost.model.ChannelType.ChannelTypeDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * The type of {@link Channel}
 * 
 * @author Takayuki Maruyama
 */
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = ChannelTypeDeserializer.class)
public enum ChannelType implements HasCode<ChannelType> {

	Open("O"), Private("P"), Direct("D"), Group("G");
	private final String code;

	ChannelType(String code) {
		this.code = code;
	}

	@Override
	public String getCode() {
		return code;
	}

	public static ChannelType of(String code) {
		for (ChannelType type : ChannelType.values()) {
			if (type.getCode().equals(code)) {
				return type;
			}
		}
		return null;
	}

	public static class ChannelTypeDeserializer extends JsonDeserializer<ChannelType> {

		/**
		 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
		 *      com.fasterxml.jackson.databind.DeserializationContext)
		 */
		@Override
		public ChannelType deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {

			final String jsonValue = p.getText();
			return ChannelType.of(jsonValue);
		}

	}
}
