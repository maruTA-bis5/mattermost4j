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

package net.bis5.mattermost.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 * TODO 型の説明
 * 
 * @author Takayuki Maruyama
 */
@ClientEndpoint
public class MattermostWebSocketClient implements AuthenticateHandler {

  private final URI wsUri;
  private final String authToken;
  private final HandlerRegistry handlers = new HandlerRegistry();

  private final WebSocketContainer wsContainer = ContainerProvider.getWebSocketContainer();
  private Session session;

  public MattermostWebSocketClient(URI wsUri, String authToken) {
    this.wsUri = Objects.requireNonNull(wsUri);
    this.authToken = Objects.requireNonNull(authToken);
  }

  /**
   * @param apiUrl
   * @param authToken
   * @return
   */
  public static MattermostWebSocketClient newInstance(String apiUrl, String authToken) {
    URI wsUri = URI.create(apiUrl.replace("https://", "wss://").replace("http://", "ws://"));
    return new MattermostWebSocketClient(wsUri, authToken);
  }

  public void openConnection() throws DeploymentException, IOException {
    if (session != null && session.isOpen()) {
      close();
    }
    session = wsContainer.connectToServer(createEndpointInstance(handlers), wsUri);
  }

  public void close() throws IOException {
    session.close();
  }

  protected Object createEndpointInstance(HandlerRegistry hub) {
    return new MattermostEndpoint(hub, this);
  }

  @Override
  public void authenticate(Session session) throws IOException {
    // TODO ちゃんとモデル作る
    String json =
        "{\"seq\": 1, \"action\": \"authentication_challenge\", \"data\": {\"token\": \"%s\"}}";
    json = String.format(json, authToken);
    session.getBasicRemote().sendText(json);
  }

  public HandlerRegistry getHandlers() {
    return handlers;
  }
}
