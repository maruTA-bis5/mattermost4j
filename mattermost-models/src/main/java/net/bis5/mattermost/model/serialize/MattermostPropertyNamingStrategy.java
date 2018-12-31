/*
 * Copyright (c) 2018 Takayuki Maruyama
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
package net.bis5.mattermost.model.serialize;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import net.bis5.mattermost.model.Config;

/**
 * The {@link PropertyNamingStrategy} for Mattermost datamodels
 * 
 * @author Takayuki Maruyama
 */
@SuppressWarnings("serial")
public class MattermostPropertyNamingStrategy extends PropertyNamingStrategy {

  @Override
  public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
    return judgeStrategy(field).nameForField(config, field, defaultName);
  }

  @Override
  public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method,
      String defaultName) {
    return judgeStrategy(method).nameForGetterMethod(config, method, defaultName);
  }

  @Override
  public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method,
      String defaultName) {
    return judgeStrategy(method).nameForSetterMethod(config, method, defaultName);
  }

  @Override
  public String nameForConstructorParameter(MapperConfig<?> config, AnnotatedParameter ctorParam,
      String defaultName) {
    return judgeStrategy(ctorParam).nameForConstructorParameter(config, ctorParam, defaultName);
  }

  protected PropertyNamingStrategy judgeStrategy(AnnotatedMember member) {
    Class<?> clazz = member.getDeclaringClass();
    if (Config.class.isAssignableFrom(clazz)) {
      return PropertyNamingStrategy.UPPER_CAMEL_CASE;
    } else if (clazz.getCanonicalName().startsWith("net.bis5.mattermost.model.config.")) {
      return PropertyNamingStrategy.UPPER_CAMEL_CASE;
    } else {
      return PropertyNamingStrategy.SNAKE_CASE;
    }
  }
}
