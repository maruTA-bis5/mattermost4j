/*
 * Copyright (c) 2016-present, Takayuki Maruyama
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

import lombok.Data;

/**
 * Log settings.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class LogSettings {

  private boolean enableConsole;
  private String consoleLevel;
  private boolean enableFile;
  private String fileLevel;
  private String fileLocation;
  private boolean enableWebhookDebugging;
  private boolean enableDiagnostics;
  /* @since Mattermost Server 4.10 */
  private boolean fileJson = true;
  /* @since Mattermost Server 4.10 */
  private boolean consoleJson = true;
  /** @since Mattermost Server 5.26 */
  private boolean enableSentry;
  /** @since Mattermost Server 5.26 */
  private String advancedLoggingConfig;
  /** @since Mattermost Server 5.36 */
  private boolean enableColor;
}
