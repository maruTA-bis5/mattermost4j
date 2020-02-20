/*
 * Copyright (c) 2018 Takayuki Maruyama
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

package net.bis5.mattermost.model.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import net.bis5.mattermost.model.config.plugin.PluginState;

/**
 * Plugin settings.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class PluginSettings {

  private boolean enable = true;
  private boolean enableUploads;
  private Map<String, Map<String, String>> plugins;
  private Map<String, PluginState> pluginStates;
  /* @since Mattermost Server 4.5 */
  private String clientDirectory;
  /* @since Mattermost Server 5.14 */
  private boolean allowInsecureDownloadUrl;
  /* @since Mattermost Server 5.14 */
  private boolean enableHealthCheck = true;
  private String directory;
  /* @since Mattermost Server 5.16 */
  private boolean enableMarketplace = true;
  /* @since Mattermost Server 5.16 */
  private String marketplaceUrl = "https://marketplace.integrations.mattermost.com";
  /* @since Mattermost Server 5.18 */
  private boolean requirePluginSignature;
  /* @since Mattermost Server 5.18 */
  private List<String> signaturePublicKeyFiles = new ArrayList<>();
  /* @since Mattermost Server 5.20 */
  private boolean enableRemoteMarketplace = true;
  /* @since Mattermost Server 5.20 */
  private boolean automaticPrepackagedPlugins = true;

}
