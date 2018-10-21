/*
 * @(#) net.bis5.mattermost.model.config.PluginSettings
 * Copyright (c) 2018 Maruyama Takayuki
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
package net.bis5.mattermost.model.config;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import net.bis5.mattermost.model.config.plugin.PluginState;

/**
 * TODO 型の説明
 * 
 * @author Maruyama Takayuki
 * @since 2018/08/03
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PluginSettings {

	private boolean enable = true;
	private boolean enableUploads;
	private Map<String, Map<String, String>> plugins;
	private Map<String, PluginState> pluginStates;
	/** @since Mattermost Server 4.5 */
	private String clientDirectory;
}
