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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.websocket.Session;
import net.bis5.mattermost.websocket.model.EventPayload;

/**
 * TODO 型の説明
 * 
 * @author Takayuki Maruyama
 */
public class HandlerRegistry {

  private final EnumMap<WebSocketEvent, Collection<WebSocketEventHandler>> eventHandlers =
      new EnumMap<>(WebSocketEvent.class);
  private final Set<WebSocketEventHandler> allEventHandler = new LinkedHashSet<>();
  private final Set<Consumer<Session>> closeHandler = new LinkedHashSet<>();
  private final Set<Consumer<Throwable>> errorHandler = new LinkedHashSet<>();

  public <T> void fireEvent(EventPayload<T> payload) {
    WebSocketEvent eventType = payload.getEvent();
    if (eventType == null) {
      // may be response message
      return;
    }
    Collection<WebSocketEventHandler> handlers = ensureHandlersForType(eventType);
    handlers.forEach(h -> h.handleEvent(payload));
  }

  public void fireOnClose(Session session) {
    closeHandler.forEach(h -> h.accept(session));
  }

  public void fireOnError(Throwable throwable) {
    errorHandler.forEach(h -> h.accept(throwable));
  }

  public HandlerRegistration addHandler(WebSocketEvent eventType, WebSocketEventHandler handler) {
    ensureHandlersForType(eventType);
    eventHandlers.get(eventType).add(handler);
    return new HandlerRegistration(() -> eventHandlers.get(eventType).remove(handler));
  }

  public HandlerRegistration addAllEventHandler(WebSocketEventHandler handler) {
    allEventHandler.add(handler);
    return new HandlerRegistration(() -> allEventHandler.remove(handler));
  }

  protected Collection<WebSocketEventHandler> ensureHandlersForType(WebSocketEvent eventType) {
    List<WebSocketEventHandler> handlers = new ArrayList<>();
    handlers.addAll(allEventHandler);
    handlers.addAll(eventHandlers.computeIfAbsent(eventType, e -> new ArrayList<>()));
    return handlers;
  }

  public boolean hasHandlerForType(WebSocketEvent eventType) {
    return !ensureHandlersForType(eventType).isEmpty();
  }
}
