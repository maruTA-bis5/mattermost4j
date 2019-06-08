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

package net.bis5.mattermost.model.config;

import lombok.Data;

/**
 * Notification Log Settings.
 * 
 * @author Takayuki Maruyama
 * @since Mattermost Server 5.12
 */
@Data
public class NotificationLogSettings {

  private boolean enableConsole = true;
  private String consoleLevel = "DEBUG";
  private boolean consoleJson = true;
  private boolean enableFile = true;
  private String fileLevel = "INFO";
  private boolean fileJson = true;
  private String fileLocation;

}
