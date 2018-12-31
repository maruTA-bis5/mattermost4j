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
import java.util.logging.Logger;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.api.hook.IncomingWebhook;
import net.bis5.mattermost.jersey.provider.MattermostModelMapperProvider;
import net.bis5.mattermost.model.IncomingWebhookRequest;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.logging.LoggingFeature.Verbosity;

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
    ClientBuilder builder = ClientBuilder.newBuilder().register(MattermostModelMapperProvider.class)
        .register(JacksonFeature.class);
    if (clientLogLevel != null) {
      builder.register(new LoggingFeature(Logger.getLogger(getClass().getName()), clientLogLevel,
          Verbosity.PAYLOAD_ANY, 1000));
    }
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
