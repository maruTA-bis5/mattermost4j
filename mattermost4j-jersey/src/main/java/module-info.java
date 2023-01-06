/*
 * Copyright (c) 2022-present, Takayuki Maruyama
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

module net.bis5.mattermost4j.jersey {
    requires jersey.client;
    requires jersey.common;
    requires jersey.media.multipart;
    requires jersey.media.json.jackson;
    requires org.jvnet.mimepull;

    requires net.bis5.mattermost4j.core;

    requires jakarta.annotation;
    requires jakarta.inject;

    exports net.bis5.mattermost.jersey.provider;
    provides net.bis5.mattermost.client4.spi.MattermostClientProvider with net.bis5.mattermost.jersey.provider.MattermostJerseyClientProvider;

}
