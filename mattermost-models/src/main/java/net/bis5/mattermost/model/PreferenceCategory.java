/*
 * @(#) net.bis5.mattermost.model.PreferenceCategory
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

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import net.bis5.mattermost.model.PreferenceCategory.PreferenceCategoryDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * TODO 型の説明
 * 
 * @author Maruyama Takayuki
 * @since 2017/06/09
 */
@JsonSerialize(using = HasCodeSerializer.class)
@JsonDeserialize(using = PreferenceCategoryDeserializer.class)
@Getter
public enum PreferenceCategory implements HasCode<PreferenceCategory> {

	DIRECT_CHANNEL_SHOW("direct_channel_show"), TUTORIAL_STEPS("tutorial_steps"), ADVANCED_SETTINGS(
			"advanced_settings"), FLAGGED_POST("flagged_post"), //
	DISPLAY_SETTINGS("display_settings"), THEME("theme"), AUTHORIZED_OAUTH_APP("oauth_app"), LAST(
			"last"), NOTIFICATIONS("notifications");
	private final String code;

	private PreferenceCategory(String code) {
		this.code = code;
	}

	public static PreferenceCategory of(String code) {
		return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
	}

	static class PreferenceCategoryDeserializer extends JsonDeserializer<PreferenceCategory> {

		/**
		 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
		 *      com.fasterxml.jackson.databind.DeserializationContext)
		 */
		@Override
		public PreferenceCategory deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			String jsonValue = p.getText();
			return of(jsonValue);
		}
	}
}
