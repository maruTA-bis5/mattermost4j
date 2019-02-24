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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import lombok.RequiredArgsConstructor;
import net.bis5.mattermost.jersey.provider.MattermostModelMapperProvider;
import net.bis5.mattermost.websocket.model.EventPayload;
import net.bis5.mattermost.websocket.model.EventPayload.GenericEventPayload;
import net.bis5.mattermost.websocket.model.EventPayload.KeyMarker;

/**
 * TODO 型の説明
 * 
 * @author Takayuki Maruyama
 */
@ClientEndpoint
@RequiredArgsConstructor
public class MattermostEndpoint {

  private final HandlerRegistry handlers;
  private final AuthenticateHandler authHandler;
  private final MattermostModelMapperProvider objectMapperProvider =
      new MattermostModelMapperProvider();

  @OnOpen
  public void onOpen(Session session) throws IOException {
    authHandler.authenticate(session);
  }


  @OnClose
  public void onClose(Session session) {
    // TODO
    Logger.getLogger(getClass().getName()).warning("#onClose");
    handlers.fireOnClose(session);
  }

  @OnError
  public void onError(Throwable thrown) {
    // TODO
    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "#onError", thrown);
    handlers.fireOnError(thrown);
  }

  @OnMessage
  public void onMessage(String message)
      throws JsonParseException, JsonMappingException, IOException {
    Logger.getLogger(getClass().getName()).info(message);
    ObjectMapper mapper = objectMapperProvider.getContext(getClass());
    // TODO Mattermost4Jからメッセージを送れるようにしたら、seq_replyを見てレスポンスかどうか判定できるようにする
    GenericEventPayload payload = mapper.readValue(message, GenericEventPayload.class);

    // 対応するハンドラがあれば、そのハンドラ向けに固有のモデルに変換する(GenericだとdataがMapになる)
    if (payload.getEvent() != null && handlers.hasHandlerForType(payload.getEvent())) {
      Class<? extends EventPayload<?>> payloadType = payload.getEvent().getPayloadType();
      if (payloadType == GenericEventPayload.class) {
        handlers.fireEvent(payload);
        return;
      }

      // dataの各値がなぜか文字列として格納されていて殻割りが必要になっていて死
      // XXX user updatedイベントだとちゃんとオブジェクトが入っているのでむしろStringにキャストすると死ぬ
      @SuppressWarnings("unchecked")
      Map<String, Object> payloadMap = mapper.readValue(message, Map.class);
      Map<String, Object> actualDataMap = new HashMap<>();
      payloadMap.put("data", actualDataMap);
      try {
        EventPayload<?> actualPayloadTemp = payloadType.getConstructor().newInstance();
        for (Enum<?> property : actualPayloadTemp.getMarkerEnum().getEnumConstants()) {
          String propertyName =
              CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, property.name());
          String rawValue = (String) payload.getData().get(propertyName);
          if (rawValue == null) {
            continue;
          }
          KeyMarker marker = KeyMarker.class.cast(property);
          Class<?> valueClass = marker.getValueClass();
          Object value;
          if (valueClass.isPrimitive() || valueClass.isEnum() || valueClass == String.class) {
            value = mapper.readValue("\"" + rawValue + "\"", valueClass);
          } else {
            value = mapper.readValue(rawValue, valueClass);
          }
          actualDataMap.put(propertyName, value);
        }

      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        // TODO Auto-generated catch block
        throw new IllegalStateException(e);
      }
      EventPayload<?> actualPayload =
          mapper.readValue(mapper.writeValueAsString(payloadMap), payloadType);
      handlers.fireEvent(actualPayload);
    }
  }

}
