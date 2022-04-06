/*
 * Copyright (c) 2019 Takayuki Maruyama
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package net.bis5.mattermost.client4.api;

import java.io.IOException;
import java.nio.file.Path;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.model.PluginManifest;
import net.bis5.mattermost.model.Plugins;

/**
 * Plugin API.
 */
public interface PluginApi {

  default ApiResponse<PluginManifest> uploadPlugin(Path plugin) throws IOException {
    return uploadPlugin(plugin, false);
  }

  ApiResponse<PluginManifest> uploadPlugin(Path plugin, boolean force) throws IOException;

  ApiResponse<Plugins> getPlugins();

  ApiResponse<Boolean> removePlugin(String pluginId);

  ApiResponse<Boolean> enablePlugin(String pluginId);

  ApiResponse<Boolean> disablePlugin(String pluginId);

  ApiResponse<PluginManifest[]> getWebappPlugins();
}
