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

package net.bis5.mattermost.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * A plugin manifest.
 */
@Data
public class PluginManifest {

  private String id;
  private String name;
  private String description;
  private String version;
  private String minServerVersion;
  private PluginServer server;
  private PluginServer backend;
  private PluginWebapp webapp;
  private PluginSettingsSchema settingsSchema;
  /** @since Mattermost Server 5.22 */
  private String releaseNotesUrl;
  /* @since Mattermost Server 5.23 */
  private String homepageUrl;
  /* @since Mattermost Server 5.23 */
  private String supportUrl;
  /* @since Mattermost Server 5.23 */
  private String iconPath;

  /**
   * Server side plugin executables.
   */
  @Data
  public static class PluginServer {
    private PluginExecutables executables;
    private String executable;
  }

  @Data
  public static class PluginExecutables {
    @JsonProperty("linux-amd64")
    private String linuxAmd64;
    @JsonProperty("darwin-amd64")
    private String darwinAmd64;
    @JsonProperty("windows-amd64")
    private String windowsAmd64;
    @JsonProperty("darwin-arm64")
    private String darwinArm64;
    @JsonProperty("linux-arm64")
    private String linuxArm64;
  }

  @Data
  public static class PluginSettingsSchema {
    private String header;
    private String footer;
    private PluginSetting[] settings;
  }

  @Data
  public static class PluginSetting {
    private String key;
    private String displayName;
    private String type; // enum?
    private String helpText;
    private String regenerateHelpText;
    private String placeholder;
    @JsonProperty("default")
    private String defaultValue;
    /* @since Mattermost Server 5.14 */
    private List<PluginOption> options;
  }

}
