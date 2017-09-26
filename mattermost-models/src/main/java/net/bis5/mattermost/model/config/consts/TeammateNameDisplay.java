/*
 * @(#) net.bis5.mattermost.model.config.ClusterSettings
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
package net.bis5.mattermost.model.config.consts;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.bis5.mattermost.model.HasCode;
import net.bis5.mattermost.model.config.consts.TeammateNameDisplay.TeammateNameDisplayDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * @author Maruyama Takayuki
 *
 */
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = TeammateNameDisplayDeserializer.class)
@Getter
@RequiredArgsConstructor
public enum TeammateNameDisplay implements HasCode<TeammateNameDisplay> {
	UserName("username"), NickNameFullName("nickname_full_name"), FullName("full_name");

	private final String code;

	public static TeammateNameDisplay of(String code) {
		return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
	}

	public static class TeammateNameDisplayDeserializer extends JsonDeserializer<TeammateNameDisplay> {

		@Override
		public TeammateNameDisplay deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			String jsonValue = p.getText();
			return of(jsonValue);
		}
	}

}
