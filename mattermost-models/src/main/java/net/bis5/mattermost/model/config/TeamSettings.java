/*
 * Copyright (c) 2016-present, Takayuki Maruyama
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

package net.bis5.mattermost.model.config;

import java.util.List;
import lombok.Data;
import net.bis5.mattermost.model.config.consts.Permissions;
import net.bis5.mattermost.model.config.consts.RestrictDirectMessage;
import net.bis5.mattermost.model.config.consts.TeammateNameDisplay;

/**
 * Team settings.
 * 
 * @author Takayuki Maruyama
 */
@Data
public class TeamSettings {

  private String siteName;
  private int maxUsersPerTeam;
  private boolean enableTeamCreation;
  private boolean enableUserCreation;
  private boolean enableOpenServer;
  /* @since Mattermost Server XXX what ver? */
  private boolean enableUserDeactivate;
  private String restrictCreationToDomains;
  private boolean enableCustomBrand;
  private String customBrandText;
  private String customDescriptionText;
  private RestrictDirectMessage restrictDirectMessage;
  private Permissions restrictTeamInvite;
  private Permissions restrictPublicChannelManagement;
  private Permissions restrictPrivateChannelManagement;
  private Permissions restrictPublicChannelCreation;
  private Permissions restrictPrivateChannelCreation;
  private Permissions restrictPublicChannelDeletion;
  private Permissions restrictPrivateChannelDeletion;
  private Permissions restrictPrivateChannelManageMembers;
  private long userStatusAwayTimeout;
  private long maxChannelsPerTeam;
  private long maxNotificationsPerChannel;
  /* @since Mattermost Server 4.0 */
  private TeammateNameDisplay teammateNameDisplay;
  /* @since Mattermost Server 4.2 */
  private boolean enableXtoLeaveChannelsFromLhs;
  /* @since Mattermost Server 4.2 (Enterprise Edition) */
  private boolean experimentalTownSquareIsReadOnly;
  /* @since Mattermost Server 4.4 */
  private boolean enableConfirmNotificationsToChannel = true;
  /* @since Mattermost Server 4.6 */
  private String experimentalPrimaryTeam;
  /* @since Mattermost Server 4.10 */
  private boolean experimentalEnableAutomaticReplies;
  /* @since Mattermost Server 5.0 */
  private boolean enableUserDeactivation;
  /* @since Mattermost Server 5.0 */
  private boolean experimentalHideTownSquareinLhs;
  /* @since Mattermost Server 5.2 */
  private boolean experimentalViewArchivedChannels;
  /* @since Mattermost Server 5.2 */
  private List<String> experimentalDefaultChannels;

}
