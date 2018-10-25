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

import net.bis5.mattermost.model.CommandResponseType.CommandResponseTypeDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * The type of {@link CommandResponse}
 * 
 * @author Takayuki Maruyama
 */
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = CommandResponseTypeDeserializer.class)
public enum CommandResponseType implements HasCode<CommandResponseType> {
	InChannel("in_channel"), Ephemeral("ephemeral");
	private final String code;

	CommandResponseType(String code) {
		this.code = code;
	}

	@Override
	public String getCode() {
		return code;
	}

	public static CommandResponseType of(String code) {
		for (CommandResponseType type : CommandResponseType.values()) {
			if (type.getCode().equals(code)) {
				return type;
			}
		}
		return null;
	}

	public static class CommandResponseTypeDeserializer extends JsonDeserializer<CommandResponseType> {

		/**
		 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
		 *      com.fasterxml.jackson.databind.DeserializationContext)
		 */
		@Override
		public CommandResponseType deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			String code = p.getText();
			return CommandResponseType.of(code);
		}
	}

}
