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

module net.bis5.mattermost4j.core {
    requires org.apache.commons.lang3;

    requires com.fasterxml.jackson.annotation;
    requires transitive com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.module.jakarta.xmlbind;
    requires transitive jakarta.ws.rs;
    requires transitive jakarta.xml.bind;
    requires transitive java.logging;
    requires transitive java.sql;
    requires transitive net.bis5.mattermost4j.models;
    requires transitive opengraph4j;

    requires static lombok;

    exports net.bis5.mattermost.client4;
    exports net.bis5.mattermost.client4.api;
    exports net.bis5.mattermost.client4.hook;
    exports net.bis5.mattermost.client4.model;
    exports net.bis5.mattermost.client4.spi;
    exports net.bis5.mattermost.provider;

    opens net.bis5.mattermost.client4.model to com.fasterxml.jackson.databind;
}
