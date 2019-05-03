/*
 * Copyright (c) 2017-present, Takayuki Maruyama
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
import lombok.Data;

/**
 * WebRTC service info.
 * 
 * @author Takayuki Maruyama
 * @deprecated WebRTC feature removed in Mattermost Server 5.6. This class will be removed at July
 *             2019.
 */
@Data
@Deprecated
public class WebrtcInfoResponse {

  @JsonProperty("token")
  private String token;
  @JsonProperty("gateway_url")
  private String gatewayUrl;
  @JsonProperty("stun_url")
  private String stunUrl;
  @JsonProperty("turn_url")
  private String turnUrl;
  @JsonProperty("turn_password")
  private String turnPassword;
  @JsonProperty("turn_username")
  private String turnUsername;

}
