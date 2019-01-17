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

package net.bis5.mattermost.client4.model;

import lombok.Getter;

/**
 * Users fetch order.
 */
public class UsersOrder {

  /**
   * Order in team.
   * 
   * @since Mattermost Server 4.0
   */
  public enum InTeam {
    /** sort by username. */
    NONE(""),
    /** sort by last_activity_at. */
    LAST_ACTIVITY_AT,
    /** sort by create_at. */
    CREATE_AT;
    @Getter
    private final String sort;

    InTeam() {
      this.sort = name().toLowerCase();
    }

    InTeam(String sort) {
      this.sort = sort;
    }

  }

  /**
   * Order in channel.
   * 
   * @since Mattermost Server 4.7
   */
  public enum InChannel {
    /** sort by username. */
    NONE(""),
    /** sort by status (online, away, dnd, offline). */
    STATUS;
    @Getter
    private final String sort;

    InChannel() {
      this.sort = name().toLowerCase();
    }

    InChannel(String sort) {
      this.sort = sort;
    }
  }
}
