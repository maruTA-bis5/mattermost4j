/*
 * Copyright (c) 2017 Takayuki Maruyama
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Elasticsearch settings.
 * 
 * @author Takayuki Maruyama
 * @since Mattermost Server 4.1 (Enterprise Edition)
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticsearchSettings {

  private String connectionUrl;
  private String username;
  private String password;
  private boolean enableIndexing;
  private boolean enableSearching;
  private boolean sniff = true;
  private int postIndexReplicas;
  private int postIndexShards;
  /* @since Mattermost Server 4.2 (Enterprise Edition) */
  private int aggregatePostsAfterDays = 365;
  /* @since Mattermost Server 4.2 (Enterprise Edition) */
  private String postsAggregatorJobStartTime = "03:00";
  /* @since Mattermost Server 4.3 (Enterprise Edition) */
  private String indexPrefix;
  /* @since Mattermost Server 4.4 (Enterprise Edition) */
  private int liveIndexingBatchSize = 1;
  /* @since Mattermost Server 4.4 (Enterprise Edition) */
  private int requestTimeoutSeconds = 30;
  /* @since Mattermost Server 4.4 (Enterprise Edition) */
  private int bulkIndexingTimeWindowSeconds = 3600;
}
