/*
 * Copyright (c) 2021-present, Takayuki Maruyama
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

package net.bis5.mattermost.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.bis5.mattermost.model.AutocompleteArg.AutocompleteArgType.AutocompleteArgTypeDeserializer;
import net.bis5.mattermost.model.serialize.HasCodeSerializer;

/**
 * Slash Command Autocomplete Argument
 * 
 * @since Mattermost Server 5.24.0
 */
@Data
public class AutocompleteArg {

  private String name;
  @JsonProperty("HelpText")
  private String helpText;
  private AutocompleteArgType type;
  private boolean required;
  private AutocompleteArgData data;

  @JsonSerialize(using = HasCodeSerializer.class)
  @JsonDeserialize(using = AutocompleteArgTypeDeserializer.class)
  @Getter
  @RequiredArgsConstructor
  public enum AutocompleteArgType implements HasCode<AutocompleteArgType> {
    TEXT_INPUT("TextInput"), STATIC_LIST("StaticList"), DYNAMIC_LIST("DynamicList");
    private final String code;

    static AutocompleteArgType of(String code) {
      return Arrays.stream(values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
    }
    public static class AutocompleteArgTypeDeserializer extends JsonDeserializer<AutocompleteArgType> {

      @Override
      public AutocompleteArgType deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
        String jsonValue = p.getText();
        return of(jsonValue);
      }
    }
  }

  @Data
  public static class AutocompleteArgData implements AutocompleteTextArg, AutocompleteStaticListArg, AutocompleteDynamicListArg {
    private String hint;
    private String pattern;
    @JsonProperty("PossibleArguments")
    private List<AutocompleteListItem> possibleArguments;
    @JsonProperty("FetchURL")
    private String fetchUrl;
  }

  public static interface AutocompleteTextArg {
    String getHint();
    void setHint(String hint);
    String getPattern();
    void setPattern(String pattern);
  }

  public static interface AutocompleteStaticListArg {
    List<AutocompleteListItem> getPossibleArguments();
    void setPossibleArguments(List<AutocompleteListItem> arguments);
  }

  @Data
  public static class AutocompleteListItem {
    private String item;
    private String hint;
    @JsonProperty("HelpText")
    private String helpText;
  }

  public static interface AutocompleteDynamicListArg {
    String getFetchUrl();
    void setFetchUrl(String fetchUrl);
  }

  public AutocompleteTextArg getTextArgumentData() {
    return data;
  }

  public AutocompleteStaticListArg getStaticListArgumentData() {
    return data;
  }

  public AutocompleteDynamicListArg getDynamicListArgumentData() {
    return data;
  }

}
