/*
 * Copyright (c) 2020-present, Takayuki Maruyama
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
 * Experimental audit log settings.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class ExperimentalAuditSettings {

  private boolean sysLogEnabled;
  private String sysLogIp = "localhost";
  private int sysLogPort = 6514;
  private String sysLogTag;
  private String sysLogCert;
  private boolean sysLogInsecure = false;
  private int sysLogMaxQueueSize = 1000;

  private boolean fileEnabled;
  private String fileName;
  private int fileMaxSizeMb = 100;
  private int fileMaxAgeDays;
  private int fileMaxBackups;
  private boolean fileCompress;
  private int fileMaxQueueSize = 1000;

}
