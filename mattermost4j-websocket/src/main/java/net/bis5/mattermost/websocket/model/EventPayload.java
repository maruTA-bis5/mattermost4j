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

package net.bis5.mattermost.websocket.model;

import java.util.Map;
import lombok.Data;
import net.bis5.mattermost.websocket.Broadcast;
import net.bis5.mattermost.websocket.WebSocketEvent;

/**
 * TODO 型の説明
 * 
 * @author Takayuki Maruyama
 */
@Data
public abstract class EventPayload<T> {

  private int seq;
  private int seq_reply;
  private WebSocketEvent event;
  private T data;
  private Broadcast broadcast;
  private Object status;

  @SuppressWarnings("unchecked")
  public <U extends EventPayload<V>, V> U cast() {
    return (U) this;
  }


  public static class GenericEventPayload extends EventPayload<Map<String, Object>> {

    @Override
    public Class<? extends Enum<? extends KeyMarker>> getMarkerEnum() {
      // TODO Auto-generated method stub
      return null;
    }
  }

  public abstract Class<? extends Enum<? extends KeyMarker>> getMarkerEnum();

  public static interface KeyMarker {
    Class<?> getValueClass();
  }
}
