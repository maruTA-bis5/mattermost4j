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
package net.bis5.mattermost.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * Command execution response
 * 
 * @author Takayuki Maruyama
 */
@Data
public class CommandResponse {

  @JsonProperty("response_type")
  private CommandResponseType responseType;
  @JsonProperty("text")
  private String text;
  @JsonProperty("username")
  private String username;
  @JsonProperty("icon_url")
  private String iconUrl;
  @JsonProperty("type")
  private PostType type;
  @JsonProperty("props")
  private Map<String, String> props;
  @JsonProperty("goto_location")
  private String gotoLocation;
  @JsonProperty("attachments")
  private List<SlackAttachment> attachments;
  /** @since Mattermost Server 5.6 */
  private String triggerId;
  /** @since Mattermost Server 5.6 */
  private List<CommandResponse> extraResponses;

}
