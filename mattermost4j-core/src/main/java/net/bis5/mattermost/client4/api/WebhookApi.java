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
package net.bis5.mattermost.client4.api;

import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.Pager;
import net.bis5.mattermost.model.IncomingWebhook;
import net.bis5.mattermost.model.IncomingWebhookList;
import net.bis5.mattermost.model.OutgoingWebhook;
import net.bis5.mattermost.model.OutgoingWebhookList;

/**
 * Webhook API
 * 
 * @author Takayuki Maruyama
 */
public interface WebhookApi {
  ApiResponse<IncomingWebhook> createIncomingWebhook(IncomingWebhook hook);

  ApiResponse<IncomingWebhook> updateIncomingWebhook(IncomingWebhook hook);

  default ApiResponse<IncomingWebhookList> getIncomingWebhooks() {
    return getIncomingWebhooks(Pager.defaultPager());
  }

  default ApiResponse<IncomingWebhookList> getIncomingWebhooks(Pager pager) {
    return getIncomingWebhooks(pager, null);
  }

  ApiResponse<IncomingWebhookList> getIncomingWebhooks(Pager pager, String etag);

  default ApiResponse<IncomingWebhookList> getIncomingWebhooksForTeam(String teamId) {
    return getIncomingWebhooksForTeam(teamId, Pager.defaultPager());
  }

  default ApiResponse<IncomingWebhookList> getIncomingWebhooksForTeam(String teamId, Pager pager) {
    return getIncomingWebhooksForTeam(teamId, pager, null);
  }

  ApiResponse<IncomingWebhookList> getIncomingWebhooksForTeam(String teamId, Pager pager,
      String etag);

  default ApiResponse<IncomingWebhook> getIncomingWebhook(String hookId) {
    return getIncomingWebhook(hookId, null);
  }

  ApiResponse<IncomingWebhook> getIncomingWebhook(String hookId, String etag);

  ApiResponse<Boolean> deleteIncomingWebhook(String hookId);

  ApiResponse<OutgoingWebhook> createOutgoingWebhook(OutgoingWebhook hook);

  ApiResponse<OutgoingWebhook> updateOutgoingWebhook(OutgoingWebhook hook);

  default ApiResponse<OutgoingWebhookList> getOutgoingWebhooks() {
    return getOutgoingWebhooks(Pager.defaultPager());
  }

  default ApiResponse<OutgoingWebhookList> getOutgoingWebhooks(Pager pager) {
    return getOutgoingWebhooks(pager, null);
  }

  ApiResponse<OutgoingWebhookList> getOutgoingWebhooks(Pager pager, String etag);

  ApiResponse<OutgoingWebhook> getOutgoingWebhook(String hookId);

  default ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForChannel(String channelId) {
    return getOutgoingWebhooksForChannel(channelId, Pager.defaultPager());
  }

  default ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForChannel(String channelId,
      Pager pager) {
    return getOutgoingWebhooksForChannel(channelId, pager, null);
  }

  ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForChannel(String channelId, Pager pager,
      String etag);

  default ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForTeam(String teamId) {
    return getOutgoingWebhooksForTeam(teamId, Pager.defaultPager());
  }

  default ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForTeam(String teamId, Pager pager) {
    return getOutgoingWebhooksForTeam(teamId, pager, null);
  }

  ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForTeam(String teamId, Pager pager,
      String etag);

  ApiResponse<OutgoingWebhook> regenOutgoingHookToken(String hookId);

  ApiResponse<Boolean> deleteOutgoingWebhook(String hookId);

}
