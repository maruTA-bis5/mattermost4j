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

package net.bis5.mattermost.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;

/**
 * Post action.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class PostAction {
  @JsonProperty("id")
  private String id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("integration")
  private PostActionIntegration integration;

  @Data
  public static class PostActionIntegration {
    @JsonProperty("url")
    private String url;
    @JsonProperty("context")
    private Map<String, String> context;
  }

  @Data
  public static class PostActionIntegrationRequest {
    @JsonProperty("user_id")
    private String userId;
    /* @since Mattermost Server 5.3 */
    @JsonProperty("post_id")
    private String postId;
    /* @since Mattermost Server 5.3 */
    @JsonProperty("type")
    private String type;
    /* @since Mattermost Server 5.3 */
    @JsonProperty("data_source")
    private String dataSource;
    /* @since Mattermost Server 5.4 */
    @JsonProperty("channel_id")
    private String channelId;
    /* @since Mattermost Server 5.4 */
    @JsonProperty("team_id")
    private String teamId;
    @JsonProperty("context")
    private Map<String, String> context;
  }

  @Data
  public static class PostActionIntegrationResponse {
    @JsonProperty("update")
    private Post update;
    @JsonProperty("ephemeral_text")
    private String ephemeralText;
  }
}
