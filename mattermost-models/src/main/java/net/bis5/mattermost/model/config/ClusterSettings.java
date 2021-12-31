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

import java.util.List;
import lombok.Data;

/**
 * Cluster settings.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class ClusterSettings {

  private boolean enable;
  /* @deprecated removed in Mattermost Server 4.0 */
  @Deprecated
  private String interNodeListenAddress;
  private List<String> interNodeUrls;
  /* @since Mattermost Server 4.0 */
  private String clusterName;
  /* @since Mattermost Server 4.0 */
  private String overrideHostname;
  /* @since Mattermost Server 4.0 */
  private boolean useIpAddress;
  /* @since Mattermost Server 4.0 */
  private boolean useExperimentalGossip;
  /* @since Mattermost Server 4.0 */
  private boolean readOnlyConfig;
  /* @since Mattermost Server 4.0 */
  private int gossipPort;
  /* @since Mattermost Server 4.0 */
  private int streamingPort;

  /* @since Mattermost Server 5.14.0 */
  private String networkInterface;
  /* @since Mattermost Server 5.14.0 */
  private String bindAddress;
  /* @since Mattermost Server 5.14.0 */
  private String advertiseAddress;
  /* @since Mattermost Server 5.0.0 */
  private int maxIdleConns;
  /* @since Mattermost Server 5.0.0 */
  private int maxIdleConnsPerHost;
  /* @since Mattermost Server 5.0.0 */
  private int idleConnTimeoutMilliseconds;
  /** @since Mattermost Server 5.26 */
  private boolean enableExperimentalGossipEncryption;
  /** @since Mattermost Server 5.33 */
  private boolean enableGossipCompression;

}
