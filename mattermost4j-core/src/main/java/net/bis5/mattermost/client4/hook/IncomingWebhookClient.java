/*
 * @(#) net.bis5.mattermost.client4.hook.IncomingWebhookClient Copyright (c) 2018 Takayuki Maruyama
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

package net.bis5.mattermost.client4.hook;

import java.util.logging.Level;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.api.hook.IncomingWebhook;
import net.bis5.mattermost.client4.factory.MattermostClientFactory;
import net.bis5.mattermost.model.IncomingWebhookRequest;

/**
 * Incoming Webhook client.
 * 
 * @author Takayuki Maruyama
 */
public class IncomingWebhookClient implements IncomingWebhook {

  private final Client hookClient;
  private final WebTarget hookTarget;

  public IncomingWebhookClient(String hookUrl) {
    this(hookUrl, null);
  }

  public IncomingWebhookClient(String hookUrl, Level clientLogLevel) {
    hookClient = createClient(clientLogLevel);
    hookTarget = hookClient.target(hookUrl);
  }

  protected Client createClient(Level clientLogLevel) {
    ClientBuilder builder = MattermostClientFactory.createClientBuilder(false, clientLogLevel);
    return builder.build();
  }

  @Override
  public ApiResponse<Boolean> postByIncomingWebhook(IncomingWebhookRequest payload) {
    return ApiResponse
        .of(hookTarget.request(MediaType.TEXT_PLAIN_TYPE, MediaType.APPLICATION_JSON_TYPE)
            .method(HttpMethod.POST, Entity.json(payload)), Void.class)
        .checkStatusOk();
  }

}
