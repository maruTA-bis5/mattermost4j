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
 * Webhook API.
 * 
 * @author Takayuki Maruyama
 */
public interface WebhookApi {

  /**
   * creates an incoming webhook for a channel.
   */
  ApiResponse<IncomingWebhook> createIncomingWebhook(IncomingWebhook hook);

  /**
   * updates an incoming webhook for a channel.
   */
  ApiResponse<IncomingWebhook> updateIncomingWebhook(IncomingWebhook hook);

  /**
   * returns a page of incoming webhooks on the system. Page counting starts at 0.
   */
  default ApiResponse<IncomingWebhookList> getIncomingWebhooks() {
    return getIncomingWebhooks(Pager.defaultPager());
  }

  /**
   * returns a page of incoming webhooks on the system. Page counting starts at 0.
   */
  default ApiResponse<IncomingWebhookList> getIncomingWebhooks(Pager pager) {
    return getIncomingWebhooks(pager, null);
  }

  /**
   * returns a page of incoming webhooks on the system. Page counting starts at 0.
   */
  ApiResponse<IncomingWebhookList> getIncomingWebhooks(Pager pager, String etag);

  /**
   * returns a page of incoming webhooks for a team. Page counting starts at 0.
   */
  default ApiResponse<IncomingWebhookList> getIncomingWebhooksForTeam(String teamId) {
    return getIncomingWebhooksForTeam(teamId, Pager.defaultPager());
  }

  /**
   * returns a page of incoming webhooks for a team. Page counting starts at 0.
   */
  default ApiResponse<IncomingWebhookList> getIncomingWebhooksForTeam(String teamId, Pager pager) {
    return getIncomingWebhooksForTeam(teamId, pager, null);
  }

  /**
   * returns a page of incoming webhooks for a team. Page counting starts at 0.
   */
  ApiResponse<IncomingWebhookList> getIncomingWebhooksForTeam(String teamId, Pager pager,
      String etag);

  /**
   * returns an Incoming webhook given the hook id.
   */
  default ApiResponse<IncomingWebhook> getIncomingWebhook(String hookId) {
    return getIncomingWebhook(hookId, null);
  }

  /**
   * returns an Incoming webhook given the hook id.
   */
  ApiResponse<IncomingWebhook> getIncomingWebhook(String hookId, String etag);

  /**
   * deletes an Incoming Webhook given the hook id.
   */
  ApiResponse<Boolean> deleteIncomingWebhook(String hookId);

  /**
   * creates an outgoing webhook for a team or channel.
   */
  ApiResponse<OutgoingWebhook> createOutgoingWebhook(OutgoingWebhook hook);

  /**
   * updates an outgoing webhook.
   */
  ApiResponse<OutgoingWebhook> updateOutgoingWebhook(OutgoingWebhook hook);

  /**
   * returns a page of outgoing webhooks ont eh system. Page counting starts at 0.
   */
  default ApiResponse<OutgoingWebhookList> getOutgoingWebhooks() {
    return getOutgoingWebhooks(Pager.defaultPager());
  }

  /**
   * returns a page of outgoing webhooks ont eh system. Page counting starts at 0.
   */
  default ApiResponse<OutgoingWebhookList> getOutgoingWebhooks(Pager pager) {
    return getOutgoingWebhooks(pager, null);
  }

  /**
   * returns a page of outgoing webhooks ont eh system. Page counting starts at 0.
   */
  ApiResponse<OutgoingWebhookList> getOutgoingWebhooks(Pager pager, String etag);

  /**
   * outgoing webhooks on the system requested by hook id.
   */
  ApiResponse<OutgoingWebhook> getOutgoingWebhook(String hookId);

  /**
   * returns a page of outgoing webhooks for a channel. Page counting starts at 0.
   */
  default ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForChannel(String channelId) {
    return getOutgoingWebhooksForChannel(channelId, Pager.defaultPager());
  }

  /**
   * returns a page of outgoing webhooks for a channel. Page counting starts at 0.
   */
  default ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForChannel(String channelId,
      Pager pager) {
    return getOutgoingWebhooksForChannel(channelId, pager, null);
  }

  /**
   * returns a page of outgoing webhooks for a channel. Page counting starts at 0.
   */
  ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForChannel(String channelId, Pager pager,
      String etag);

  /**
   * returns a page of outgoing webhooks for a team. Page counting starts at 0.
   */
  default ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForTeam(String teamId) {
    return getOutgoingWebhooksForTeam(teamId, Pager.defaultPager());
  }

  /**
   * returns a page of outgoing webhooks for a team. Page counting starts at 0.
   */
  default ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForTeam(String teamId, Pager pager) {
    return getOutgoingWebhooksForTeam(teamId, pager, null);
  }

  /**
   * returns a page of outgoing webhooks for a team. Page counting starts at 0.
   */
  ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForTeam(String teamId, Pager pager,
      String etag);

  /**
   * regenerate the outgoing webhook token.
   */
  ApiResponse<OutgoingWebhook> regenOutgoingHookToken(String hookId);

  /**
   * delete the outgoing webhook on the system requested by hook id.
   */
  ApiResponse<Boolean> deleteOutgoingWebhook(String hookId);

}
