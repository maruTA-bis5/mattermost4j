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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.bis5.mattermost.model.ChannelType;
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.websocket.HandlerRegistry;
import net.bis5.mattermost.websocket.MattermostEndpoint;
import net.bis5.mattermost.websocket.WebSocketEvent;
import net.bis5.mattermost.websocket.model.EventPayload.GenericEventPayload;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * TODO 型の説明
 * 
 * @author Takayuki Maruyama
 */
public class PostedEventPayloadDeserializeTest {

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Test
  public void testDeserialize() throws JsonParseException, JsonMappingException, IOException {
    HandlerRegistry handlerRegistryMock = spy(new HandlerRegistry());
    handlerRegistryMock.addHandler(WebSocketEvent.POSTED, e -> {
    });
    ArgumentCaptor<EventPayload> eventCaptor = ArgumentCaptor.forClass(EventPayload.class);

    MattermostEndpoint endpoint = new MattermostEndpoint(handlerRegistryMock, s -> {
    });
    String message =
        "{\"event\": \"posted\", \"data\": {\"channel_name\":\"CHANNEL\", \"channel_type\":\"O\", "
            + "\"mentions\":\"[\\\"userid1\\\",\\\"userid2\\\"]\", \"post\":\"{\\\"message\\\":\\\"Hello!\\\"}\"}}";

    endpoint.onMessage(message);
    verify(handlerRegistryMock).fireEvent(eventCaptor.capture());

    EventPayload<?> payload = eventCaptor.getValue();
    assertTrue(payload instanceof PostedEventPayload);
    PostedEventPayload postedEvent = payload.cast();
    assertEquals("CHANNEL", postedEvent.getData().getChannelName());
    assertEquals(ChannelType.Open, postedEvent.getData().getChannelType());
    Set<String> mentionToIds = new HashSet<>(Arrays.asList(postedEvent.getData().getMentions()));
    assertEquals(mentionToIds.size(), 2);
    assertTrue(mentionToIds.contains("userid1"));
    assertTrue(mentionToIds.contains("userid2"));
    Post post = postedEvent.getData().getPost();
    assertEquals("Hello!", post.getMessage());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Test
  public void testUserUpdatedEventDeserialize()
      throws JsonParseException, JsonMappingException, IOException {
    HandlerRegistry handlerRegistryMock = spy(new HandlerRegistry());
    handlerRegistryMock.addHandler(WebSocketEvent.USER_UPDATED, e -> {
    });
    ArgumentCaptor<EventPayload> eventCaptor = ArgumentCaptor.forClass(EventPayload.class);

    MattermostEndpoint endpoint = new MattermostEndpoint(handlerRegistryMock, s -> {
    });
    String message =
        "{\"event\": \"user_updated\", \"data\": {\"user\": {\"username\": \"sampleuser\"}}}";

    endpoint.onMessage(message);
    verify(handlerRegistryMock).fireEvent(eventCaptor.capture());

    EventPayload<?> payload = eventCaptor.getValue();
    assertTrue(payload instanceof GenericEventPayload);
    GenericEventPayload userUpdatedEvent = payload.cast();
    Map<String, Object> user = (Map<String, Object>) userUpdatedEvent.getData().get("user");
    assertEquals("sampleuser", user.get("username"));
  }
}
