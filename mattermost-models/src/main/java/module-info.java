/*
 * Copyright (c) 2016-present, Takayuki Maruyama
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

module net.bis5.mattermost4j.models {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
    requires java.prefs;
    requires static lombok;

    exports net.bis5.mattermost.model;
    exports net.bis5.mattermost.model.config;
    exports net.bis5.mattermost.model.config.consts;
    exports net.bis5.mattermost.model.config.consts.saml;
    exports net.bis5.mattermost.model.config.plugin;
    exports net.bis5.mattermost.model.gitlab;
    exports net.bis5.mattermost.model.license;
    exports net.bis5.mattermost.model.serialize;

    opens net.bis5.mattermost.model to com.fasterxml.jackson.databind;
    opens net.bis5.mattermost.model.config to com.fasterxml.jackson.databind;
    opens net.bis5.mattermost.model.config.consts to com.fasterxml.jackson.databind;
    opens net.bis5.mattermost.model.config.consts.saml to com.fasterxml.jackson.databind;
    opens net.bis5.mattermost.model.config.plugin to com.fasterxml.jackson.databind;
    opens net.bis5.mattermost.model.gitlab to com.fasterxml.jackson.databind;
    opens net.bis5.mattermost.model.license to com.fasterxml.jackson.databind;
    opens net.bis5.mattermost.model.serialize to com.fasterxml.jackson.databind;
}
