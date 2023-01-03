/*
 * Copyright (c) 2017-present, Takayuki Maruyama
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

package net.bis5.mattermost.client4;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.bis5.mattermost.client4.MultiPartAdapter.FormMultiPart;
import net.bis5.mattermost.client4.api.AuditsApi;
import net.bis5.mattermost.client4.api.AuthenticationApi;
import net.bis5.mattermost.client4.api.BotsApi;
import net.bis5.mattermost.client4.api.BrandApi;
import net.bis5.mattermost.client4.api.ChannelApi;
import net.bis5.mattermost.client4.api.ClusterApi;
import net.bis5.mattermost.client4.api.CommandsApi;
import net.bis5.mattermost.client4.api.ComplianceApi;
import net.bis5.mattermost.client4.api.ElasticsearchApi;
import net.bis5.mattermost.client4.api.EmojiApi;
import net.bis5.mattermost.client4.api.FilesApi;
import net.bis5.mattermost.client4.api.LdapApi;
import net.bis5.mattermost.client4.api.LogsApi;
import net.bis5.mattermost.client4.api.OAuthApi;
import net.bis5.mattermost.client4.api.OpenGraphApi;
import net.bis5.mattermost.client4.api.PluginApi;
import net.bis5.mattermost.client4.api.PostApi;
import net.bis5.mattermost.client4.api.PreferencesApi;
import net.bis5.mattermost.client4.api.ReactionApi;
import net.bis5.mattermost.client4.api.SamlApi;
import net.bis5.mattermost.client4.api.StatusApi;
import net.bis5.mattermost.client4.api.SystemApi;
import net.bis5.mattermost.client4.api.TeamApi;
import net.bis5.mattermost.client4.api.UserApi;
import net.bis5.mattermost.client4.api.WebhookApi;
import net.bis5.mattermost.client4.factory.MattermostClientFactory;
import net.bis5.mattermost.client4.model.AddChannelMemberRequest;
import net.bis5.mattermost.client4.model.AnalyticsCategory;
import net.bis5.mattermost.client4.model.AttachDeviceIdRequest;
import net.bis5.mattermost.client4.model.CheckUserMfaRequest;
import net.bis5.mattermost.client4.model.CreateEphemeralPostRequest;
import net.bis5.mattermost.client4.model.DeauthorizeOAuthAppRequest;
import net.bis5.mattermost.client4.model.DisableEnableTokenRequest;
import net.bis5.mattermost.client4.model.FileUploadResult;
import net.bis5.mattermost.client4.model.GetBotsOption;
import net.bis5.mattermost.client4.model.LoginRequest;
import net.bis5.mattermost.client4.model.PublicFileLink;
import net.bis5.mattermost.client4.model.ResetPasswordRequest;
import net.bis5.mattermost.client4.model.RevokeSessionRequest;
import net.bis5.mattermost.client4.model.RevokeTokenRequest;
import net.bis5.mattermost.client4.model.SearchEmojiRequest;
import net.bis5.mattermost.client4.model.SearchPostsRequest;
import net.bis5.mattermost.client4.model.SearchTokensRequest;
import net.bis5.mattermost.client4.model.SendPasswordResetEmailRequest;
import net.bis5.mattermost.client4.model.SendVerificationEmailRequest;
import net.bis5.mattermost.client4.model.SwitchAccountTypeResult;
import net.bis5.mattermost.client4.model.UpdateRolesRequest;
import net.bis5.mattermost.client4.model.UpdateUserActiveRequest;
import net.bis5.mattermost.client4.model.UpdateUserMfaRequest;
import net.bis5.mattermost.client4.model.UpdateUserPasswordRequest;
import net.bis5.mattermost.client4.model.UserAccessTokenCreateRequest;
import net.bis5.mattermost.client4.model.UsersOrder;
import net.bis5.mattermost.client4.model.VerifyUserEmailRequest;
import net.bis5.mattermost.model.AnalyticsRows;
import net.bis5.mattermost.model.Audits;
import net.bis5.mattermost.model.AuthorizeRequest;
import net.bis5.mattermost.model.Bot;
import net.bis5.mattermost.model.BotPatch;
import net.bis5.mattermost.model.Bots;
import net.bis5.mattermost.model.Channel;
import net.bis5.mattermost.model.ChannelList;
import net.bis5.mattermost.model.ChannelMember;
import net.bis5.mattermost.model.ChannelMembers;
import net.bis5.mattermost.model.ChannelPatch;
import net.bis5.mattermost.model.ChannelSearch;
import net.bis5.mattermost.model.ChannelStats;
import net.bis5.mattermost.model.ChannelUnread;
import net.bis5.mattermost.model.ChannelView;
import net.bis5.mattermost.model.ChannelViewResponse;
import net.bis5.mattermost.model.ClusterInfo;
import net.bis5.mattermost.model.Command;
import net.bis5.mattermost.model.CommandArgs;
import net.bis5.mattermost.model.CommandList;
import net.bis5.mattermost.model.CommandResponse;
import net.bis5.mattermost.model.Compliance;
import net.bis5.mattermost.model.Compliances;
import net.bis5.mattermost.model.Config;
import net.bis5.mattermost.model.Emoji;
import net.bis5.mattermost.model.EmojiList;
import net.bis5.mattermost.model.FileInfo;
import net.bis5.mattermost.model.IncomingWebhook;
import net.bis5.mattermost.model.IncomingWebhookList;
import net.bis5.mattermost.model.OAuthApp;
import net.bis5.mattermost.model.OutgoingWebhook;
import net.bis5.mattermost.model.OutgoingWebhookList;
import net.bis5.mattermost.model.PluginManifest;
import net.bis5.mattermost.model.Plugins;
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.model.PostList;
import net.bis5.mattermost.model.PostPatch;
import net.bis5.mattermost.model.PostSearchResults;
import net.bis5.mattermost.model.Preference;
import net.bis5.mattermost.model.PreferenceCategory;
import net.bis5.mattermost.model.Preferences;
import net.bis5.mattermost.model.Reaction;
import net.bis5.mattermost.model.ReactionList;
import net.bis5.mattermost.model.Role;
import net.bis5.mattermost.model.SamlCertificateStatus;
import net.bis5.mattermost.model.SessionList;
import net.bis5.mattermost.model.Status;
import net.bis5.mattermost.model.StatusList;
import net.bis5.mattermost.model.SwitchRequest;
import net.bis5.mattermost.model.Team;
import net.bis5.mattermost.model.TeamExists;
import net.bis5.mattermost.model.TeamInviteInfo;
import net.bis5.mattermost.model.TeamList;
import net.bis5.mattermost.model.TeamMember;
import net.bis5.mattermost.model.TeamMemberList;
import net.bis5.mattermost.model.TeamPatch;
import net.bis5.mattermost.model.TeamSearch;
import net.bis5.mattermost.model.TeamStats;
import net.bis5.mattermost.model.TeamUnread;
import net.bis5.mattermost.model.TeamUnreadList;
import net.bis5.mattermost.model.User;
import net.bis5.mattermost.model.UserAccessToken;
import net.bis5.mattermost.model.UserAccessTokenList;
import net.bis5.mattermost.model.UserAutocomplete;
import net.bis5.mattermost.model.UserList;
import net.bis5.mattermost.model.UserPatch;
import net.bis5.mattermost.model.UserSearch;
import net.bis5.mattermost.model.license.MfaSecret;
import net.bis5.opengraph.models.OpenGraph;

/**
 * Mattermost API Version4 Client default implementation.
 *
 * @author Maruyama Takayuki
 * @since 2017/06/10
 */
public class MattermostClient implements AutoCloseable, AuditsApi, AuthenticationApi, BotsApi,
    BrandApi, ChannelApi, ClusterApi, CommandsApi, ComplianceApi, ElasticsearchApi, EmojiApi,
    FilesApi, SystemApi, LdapApi, LogsApi, OAuthApi, OpenGraphApi, PluginApi, PostApi,
    PreferencesApi, ReactionApi, SamlApi, StatusApi, TeamApi, UserApi, WebhookApi {

  protected static final String API_URL_SUFFIX = "/api/v4";
  private final String url;
  private final String apiUrl;
  private final Client httpClient;
  private final MultiPartAdapter multiPartAdapter;
  private String authToken;
  private AuthType authType;

  public static MattermostClientBuilder builder() {
    return new MattermostClientBuilder();
  }

  Client getHttpClientForUnitTest() {
    return httpClient;
  }

  protected Client buildClient(boolean ignoreUnknownProperties, Level clientLogLevel, Consumer<ClientBuilder> httpClientConfig) {
    ClientBuilder builder = MattermostClientFactory.createClientBuilder(ignoreUnknownProperties, clientLogLevel);

    httpClientConfig.accept(builder);

    return builder.build();
  }

  public static class MattermostClientBuilder {

    private Level logLevel;
    private String url;
    private boolean ignoreUnknownProperties;
    private Consumer<ClientBuilder> httpClientConfig = clientBuilder -> {
    };

    public MattermostClientBuilder logLevel(Level logLevel) {
      this.logLevel = logLevel;
      return this;
    }

    public MattermostClientBuilder url(String url) {
      this.url = url;
      return this;
    }

    public MattermostClientBuilder ignoreUnknownProperties() {
      this.ignoreUnknownProperties = true;
      return this;
    }

    public MattermostClientBuilder httpConfig(Consumer<ClientBuilder> httpClientConfig) {
      this.httpClientConfig = httpClientConfig;
      return this;
    }

    public MattermostClient build() {
      return new MattermostClient(url, ignoreUnknownProperties, logLevel, httpClientConfig);
    }
  }

  @Override
  public void close() {
    httpClient.close();
  }

  /**
   * Create new MattermostClient instance.
   * @param url
   */
  public MattermostClient(String url) {
    this(url, null);
  }

  /**
   * Create new MattermostClient instance.
   * @param url
   * @param logLevel
   */
  public MattermostClient(String url, Level logLevel) {
    this(url, false, logLevel, clientBuilder -> {});
  }

  /**
   * Create new MattermostClient instance.
   * @param url Mattermost url
   * @param ignoreUnknownProperties
   * @param logLevel
   * @param httpClientConfig
   */
  protected MattermostClient(String url, boolean ignoreUnknownProperties, Level logLevel, Consumer<ClientBuilder> httpClientConfig) {
    this.url = url;
    this.apiUrl = url + API_URL_SUFFIX;
    this.httpClient = buildClient(ignoreUnknownProperties, logLevel, httpClientConfig);
    this.multiPartAdapter = MattermostClientFactory.createMultiPartAdapter();
  }

  public void setOAuthToken(String token) {
    this.authToken = token;
    this.authType = AuthType.TOKEN;
  }

  public void clearOAuthToken() {
    this.authToken = null;
    this.authType = AuthType.BEARER;
  }

  /**
   * Set Personal Access Token that use to access Mattermost API to this client.
   *
   * @since Mattermost Server 4.1
   */
  public void setAccessToken(String token) {
    this.authToken = token;
    this.authType = AuthType.BEARER;
  }

  public String getUsersRoute() {
    return "/users";
  }

  public String getUserRoute(String userId) {
    return getUsersRoute() + String.format("/%s", StringUtils.stripToEmpty(userId));
  }

  public String getUserByUsernameRoute(String userName) {
    return getUsersRoute() + String.format("/username/%s", StringUtils.stripToEmpty(userName));
  }

  public String getUserByEmailRoute(String email) {
    return getUsersRoute() + String.format("/email/%s", StringUtils.stripToEmpty(email));
  }

  public String getUserSessionsRoute(String userId) {
    return getUserRoute(userId) + "/sessions";
  }

  public String getUserTokensRoute(String userId) {
    return getUserRoute(userId) + "/tokens";
  }

  public String getUserTokensRoute() {
    return getUsersRoute() + "/tokens";
  }

  public String getUserTokenRoute(String tokenId) {
    return getUserTokensRoute() + String.format("/%s", StringUtils.stripToEmpty(tokenId));
  }

  public String getTeamsRoute() {
    return "/teams";
  }

  public String getTeamRoute(String teamId) {
    return getTeamsRoute() + String.format("/%s", StringUtils.stripToEmpty(teamId));
  }

  public String getTeamAutoCompleteCommandsRoute(String teamId) {
    return getTeamsRoute()
        + String.format("/%s/commands/autocomplete", StringUtils.stripToEmpty(teamId));
  }

  public String getTeamByNameRoute(String teamName) {
    return getTeamsRoute() + String.format("/name/%s", StringUtils.stripToEmpty(teamName));
  }

  public String getTeamMemberRoute(String teamId, String userId) {
    return getTeamRoute(teamId) + String.format("/members/%s", StringUtils.stripToEmpty(userId));
  }

  public String getTeamMembersRoute(String teamId) {
    return getTeamRoute(teamId) + "/members";
  }

  public String getTeamStatsRoute(String teamId) {
    return getTeamRoute(teamId) + "/stats";
  }

  public String getTeamIconRoute(String teamId) {
    return getTeamRoute(teamId) + "/image";
  }

  public String getTeamImportRoute(String teamId) {
    return getTeamRoute(teamId) + "/import";
  }

  public String getTeamInviteRoute(String inviteId) {
    return String.format("/teams/invite/%s", inviteId);
  }

  public String getChannelsRoute() {
    return "/channels";
  }

  public String getChannelsForTeamRoute(String teamId) {
    return getTeamRoute(teamId) + "/channels";
  }

  public String getChannelRoute(String channelId) {
    return getChannelsRoute() + String.format("/%s", StringUtils.stripToEmpty(channelId));
  }

  public String getChannelByNameRoute(String channelName, String teamId) {
    return getTeamRoute(teamId)
        + String.format("/channels/name/%s", StringUtils.stripToEmpty(channelName));
  }

  public String getChannelByNameForTeamNameRoute(String channelName, String teamName) {
    return getTeamByNameRoute(teamName)
        + String.format("/channels/name/%s", StringUtils.stripToEmpty(channelName));
  }

  public String getChannelMembersRoute(String channelId) {
    return getChannelRoute(channelId) + "/members";
  }

  public String getChannelMemberRoute(String channelId, String userId) {
    return getChannelMembersRoute(channelId)
        + String.format("/%s", StringUtils.stripToEmpty(userId));
  }

  public String getPostsRoute() {
    return "/posts";
  }

  public String getConfigRoute() {
    return "/config";
  }

  public String getLicenseRoute() {
    return "/license";
  }

  public String getPostRoute(String postId) {
    return getPostsRoute() + String.format("/%s", StringUtils.stripToEmpty(postId));
  }

  public String getFilesRoute() {
    return "/files";
  }

  public String getFileRoute(String fileId) {
    return getFilesRoute() + String.format("/%s", StringUtils.stripToEmpty(fileId));
  }

  public String getSystemRoute() {
    return "/system";
  }

  public String getTestEmailRoute() {
    return "/email/test";
  }

  public String getDatabaseRoute() {
    return "/database";
  }

  public String getCacheRoute() {
    return "/caches";
  }

  public String getClusterRoute() {
    return "/cluster";
  }

  public String getIncomingWebhooksRoute() {
    return "/hooks/incoming";
  }

  public String getIncomingWebhookRoute(String hookId) {
    return getIncomingWebhooksRoute() + String.format("/%s", StringUtils.stripToEmpty(hookId));
  }

  public String getComplianceReportsRoute() {
    return "?compliance/reports";
  }

  public String getComplianceReportRoute(String reportId) {
    return String.format("/compliance/reports/%s", StringUtils.stripToEmpty(reportId));
  }

  public String getOutgoingWebhooksRoute() {
    return "/hooks/outgoing";
  }

  public String getOutgoingWebhookRoute(String hookId) {
    return getOutgoingWebhooksRoute() + String.format("/%s", StringUtils.stripToEmpty(hookId));
  }

  public String getPreferencesRoute(String userId) {
    return getUserRoute(userId) + "/preferences";
  }

  public String getUserStatusRoute(String userId) {
    return getUserRoute(userId) + "/status";
  }

  public String getUserStatusesRoute() {
    return getUsersRoute() + "/status";
  }

  public String getUserProfileImageRoute(String userId) {
    return getUserRoute(userId) + "/image";
  }

  public String getSamlRoute() {
    return "/saml";
  }

  public String getLdapRoute() {
    return "/ldap";
  }

  public String getBrandImageRoute() {
    return "/brand/image";
  }

  public String getCommandsRoute() {
    return "/commands";
  }

  public String getCommandRoute(String commandId) {
    return getCommandsRoute() + String.format("/%s", StringUtils.stripToEmpty(commandId));
  }

  public String getEmojisRoute() {
    return "/emoji";
  }

  public String getEmojiRoute(String emojiId) {
    return getEmojisRoute() + String.format("/%s", StringUtils.stripToEmpty(emojiId));
  }

  public String getEmojiByNameRoute(String emojiName) {
    return getEmojisRoute() + String.format("/name/%s", StringUtils.stripToEmpty(emojiName));
  }

  public String getReactionsRoute() {
    return "/reactions";
  }

  public String getOAuthAppsRoute() {
    return "/oauth/apps";
  }

  public String getOAuthAppRoute(String appId) {
    return String.format("/oauth/apps/%s", StringUtils.stripToEmpty(appId));
  }

  public String getElasticsearchRoute() {
    return "/elasticsearch";
  }

  public String getPluginsRoute() {
    return "/plugins";
  }

  public String getPluginRoute(String pluginId) {
    return String.format("/plugins/%s", StringUtils.stripToEmpty(pluginId));
  }

  public String getOpenGraphRoute() {
    return "/opengraph";
  }

  public String getBotsRoute() {
    return "/bots";
  }

  public String getBotsRoute(String botUserId) {
    return String.format("%s/%s", getBotsRoute(), StringUtils.stripToEmpty(botUserId));
  }

  protected <T> ApiResponse<T> doApiGet(String url, String etag, Class<T> responseType) {
    return doApiRequest(HttpMethod.GET, apiUrl + url, null, etag, responseType);
  }

  protected <T> ApiResponse<T> doApiGet(String url, String etag, GenericType<T> responseType) {
    return doApiRequest(HttpMethod.GET, apiUrl + url, null, etag, responseType);
  }

  protected ApiResponse<Void> doApiGet(String url, String etag) {
    return doApiRequest(HttpMethod.GET, apiUrl + url, null, etag);
  }

  protected <T, U> ApiResponse<T> doApiPost(String url, U data, Class<T> responseType) {
    return doApiRequest(HttpMethod.POST, apiUrl + url, data, null, responseType);
  }

  protected <T, U> ApiResponse<T> doApiPost(String url, U data, GenericType<T> responseType) {
    return doApiRequest(HttpMethod.POST, apiUrl + url, data, null, responseType);
  }

  protected <U> ApiResponse<Void> doApiPost(String url, U data) {
    return doApiRequest(HttpMethod.POST, apiUrl + url, data, null);
  }

  protected ApiResponse<Void> doApiPostMultiPart(String url, FormMultiPart multiPart) {
    return doApiPostMultiPart(url, multiPart, Void.class);
  }

  protected <T> ApiResponse<T> doApiPostMultiPart(String url, FormMultiPart multiPart,
      Class<T> responseType) {
    return multiPartAdapter.doApiPostMultiPart(httpClient, apiUrl + url, getAuthority(), multiPart, responseType);
  }

  protected <T, U> ApiResponse<T> doApiPut(String url, U data, Class<T> responseType) {
    return doApiRequest(HttpMethod.PUT, apiUrl + url, data, null, responseType);
  }

  protected <U> ApiResponse<Void> doApiPut(String url, U data) {
    return doApiRequest(HttpMethod.PUT, apiUrl + url, data, null);
  }

  protected <T> ApiResponse<T> doApiDelete(String url, Class<T> responseType) {
    return doApiRequest(HttpMethod.DELETE, apiUrl + url, null, null, responseType);
  }

  protected ApiResponse<Void> doApiDelete(String url) {
    return doApiRequest(HttpMethod.DELETE, apiUrl + url, null, null);
  }

  protected <T, U> ApiResponse<T> doApiRequest(String method, String url, U data, String etag,
      Class<T> responseType) {
    return ApiResponse.of(httpClient.target(url).request(MediaType.APPLICATION_JSON_TYPE)
        .header(HEADER_ETAG_CLIENT, etag).header(HEADER_AUTH, getAuthority())
        .method(method, Entity.json(data)), responseType);
  }

  protected <T, U> ApiResponse<T> doApiRequest(String method, String url, U data, String etag,
      GenericType<T> responseType) {
    return ApiResponse.of(httpClient.target(url).request(MediaType.APPLICATION_JSON_TYPE)
        .header(HEADER_ETAG_CLIENT, etag).header(HEADER_AUTH, getAuthority())
        .method(method, Entity.json(data)), responseType);
  }

  protected <U> ApiResponse<Void> doApiRequest(String method, String url, U data, String etag) {
    return ApiResponse.of(httpClient.target(url).request(MediaType.APPLICATION_JSON_TYPE)
        .header(HEADER_ETAG_CLIENT, etag).header(HEADER_AUTH, getAuthority())
        .method(method, Entity.json(data)), Void.class);
  }

  private String getAuthority() {
    return authToken != null ? authType.getCode() + " " + authToken : null;
  }

  protected ApiResponse<Path> doApiGetFile(String url, String etag) throws IOException {
    ApiResponse<InputStream> fileResponse = doApiGet(url, etag, InputStream.class);
    if (fileResponse.hasError()) {
      return ApiResponse.of(fileResponse.getRawResponse(), Path.class);
    }

    String suffix = detectSuffix(fileResponse.getRawResponse());
    Path imageFile = Files.createTempFile(null, suffix);
    Files.copy(fileResponse.readEntity(), imageFile, StandardCopyOption.REPLACE_EXISTING);
    return ApiResponse.of(fileResponse.getRawResponse(), imageFile);
  }

  protected static final String HEADER_ETAG_CLIENT = "If-None-Match";
  public static final String HEADER_AUTH = "Authorization";

  // Authentication Section

  @Override
  public ApiResponse<User> loginById(String id, String password) {
    return login(LoginRequest.builder().id(id).password(password).build());
  }

  protected ApiResponse<User> onLogin(ApiResponse<Void> loginResponse) {
    authToken = loginResponse.getRawResponse().getHeaderString(HEADER_TOKEN);
    authType = AuthType.BEARER;
    return ApiResponse.of(loginResponse.getRawResponse(), User.class);
  }

  private static final String HEADER_TOKEN = "token";

  protected ApiResponse<User> login(LoginRequest param) {
    return onLogin(doApiPost("/users/login", param));
  }


  @Override
  public ApiResponse<User> login(String loginId, String password) {
    return login(LoginRequest.builder().loginId(loginId).password(password).build());
  }


  @Override
  public ApiResponse<User> loginByLdap(String loginId, String password) {
    return login(LoginRequest.builder().loginId(loginId).password(password).ldapOnly(true).build());
  }

  @Override
  public ApiResponse<User> loginWithDevice(String loginId, String password, String deviceId) {
    return login(
        LoginRequest.builder().loginId(loginId).password(password).deviceId(deviceId).build());
  }


  @Override
  public ApiResponse<Boolean> logout() {
    return onLogout(doApiPost("/users/logout", ""));
  }

  protected ApiResponse<Boolean> onLogout(ApiResponse<Void> logoutResponse) {
    authToken = null;
    authType = AuthType.BEARER;

    return logoutResponse.checkStatusOk();
  }

  @Override
  public ApiResponse<SwitchAccountTypeResult> switchAccountType(SwitchRequest switchRequest) {
    return doApiPost(getUsersRoute() + "/login/switch", switchRequest,
        SwitchAccountTypeResult.class);
  }

  // User Section

  @Override
  public ApiResponse<User> createUser(User user) {
    return doApiPost(getUsersRoute(), user, User.class);
  }

  @Override
  public ApiResponse<User> getMe(String etag) {
    return doApiGet(getUserRoute(ME), etag, User.class);
  }

  private static final String ME = "me";

  @Override
  public ApiResponse<User> getUser(String userId, String etag) {
    return doApiGet(getUserRoute(userId), etag, User.class);
  }

  @Override
  public ApiResponse<User> getUserByUsername(String userName, String etag) {
    return doApiGet(getUserByUsernameRoute(userName), etag, User.class);
  }

  @Override
  public ApiResponse<User> getUserByEmail(String email, String etag) {
    return doApiGet(getUserByEmailRoute(email), etag, User.class);
  }

  @Override
  public ApiResponse<UserAutocomplete> autocompleteUsersInTeam(String teamId, String username,
      String etag) {
    String query = new QueryBuilder().set("in_team", teamId).set("name", username).toString();
    return doApiGet(getUsersRoute() + "/autocomplete" + query, etag, UserAutocomplete.class);
  }

  @Override
  public ApiResponse<UserAutocomplete> autocompleteUsersInChannel(String teamId, String channelId,
      String username, String etag) {
    String query = new QueryBuilder().set("in_team", teamId).set("in_channel", channelId)
        .set("name", username).toString();
    return doApiGet(getUsersRoute() + "/autocomplete" + query, etag, UserAutocomplete.class);
  }

  @Override
  public ApiResponse<UserAutocomplete> autocompleteUsers(String username, String etag) {
    String query = new QueryBuilder().set("name", username).toString();
    return doApiGet(getUsersRoute() + "/autocomplete" + query, etag, UserAutocomplete.class);
  }

  @Override
  public ApiResponse<byte[]> getProfileImage(String userId, String etag) {
    // XXX byte[]で返すの微妙・・・というかreadEntityでこけない?
    return doApiGet(getUserProfileImageRoute(userId), etag, byte[].class);
  }

  private GenericType<Map<String, String>> stringMapType() {
    return new GenericType<Map<String, String>>() {};
  }

  private <T> GenericType<List<T>> listType() {
    return new GenericType<List<T>>() {};
  }

  @Override
  public ApiResponse<UserList> getUsers(Pager pager, String etag) {
    return doApiGet(getUsersRoute() + pager.toQuery(), etag, UserList.class);
  }

  @Override
  public ApiResponse<UserList> getUsersInTeam(String teamId, UsersOrder.InTeam order, Pager pager,
      String etag) {
    String query = new QueryBuilder() //
        .set("in_team", teamId) //
        .set("sort", order.getSort())//
        .toString();
    return doApiGet(getUsersRoute() + query + pager.toQuery(false), etag, UserList.class);
  }

  @Override
  public ApiResponse<UserList> getUsersNotInTeam(String teamId, Pager pager, String etag) {
    String query = new QueryBuilder().set("not_in_team", teamId).toString();
    return doApiGet(getUsersRoute() + query + pager.toQuery(false), etag, UserList.class);
  }

  @Override
  public ApiResponse<UserList> getUsersInChannel(String channelId, UsersOrder.InChannel order,
      Pager pager, String etag) {
    String query = new QueryBuilder() //
        .set("in_channel", channelId) //
        .set("sort", order.getSort())//
        .toString();
    return doApiGet(getUsersRoute() + query + pager.toQuery(false), etag, UserList.class);
  }

  @Override
  public ApiResponse<UserList> getUsersNotInChannel(String teamId, String channelId, Pager pager,
      String etag) {
    String query =
        new QueryBuilder().set("in_team", teamId).set("not_in_channel", channelId).toString();
    return doApiGet(getUsersRoute() + query + pager.toQuery(false), etag, UserList.class);
  }

  @Override
  public ApiResponse<UserList> getUsersWithoutTeam(Pager pager, String etag) {
    String query = new QueryBuilder().set("without_team", 1).toString();
    return doApiGet(getUsersRoute() + query + pager.toQuery(false), etag, UserList.class);
  }

  @Override
  public ApiResponse<UserList> getUsersByIds(String... userIds) {
    return doApiPost(getUsersRoute() + "/ids", userIds, UserList.class);
  }

  @Override
  public ApiResponse<UserList> getUsersByUsernames(String... usernames) {
    return doApiPost(getUsersRoute() + "/usernames", usernames, UserList.class);
  }

  @Override
  public ApiResponse<UserList> searchUsers(UserSearch search) {
    return doApiPost(getUsersRoute() + "/search", search, UserList.class);
  }

  @Override
  public ApiResponse<User> updateUser(User user) {
    return doApiPut(getUserRoute(user.getId()), user, User.class);
  }

  @Override
  public ApiResponse<User> patchUser(String userId, UserPatch patch) {
    return doApiPut(getUserRoute(userId) + "/patch", patch, User.class);
  }

  @Override
  public ApiResponse<Boolean> updateUserMfa(String userId, String code, boolean activate) {
    UpdateUserMfaRequest request =
        UpdateUserMfaRequest.builder().activate(activate).code(code).build();
    return doApiPut(getUserRoute(userId) + "/mfa", request).checkStatusOk();
  }

  @Override
  public boolean checkUserMfa(String loginId) {
    CheckUserMfaRequest request = CheckUserMfaRequest.builder().loginId(loginId).build();
    return Boolean.valueOf(doApiPost(getUsersRoute() + "/mfa", request, stringMapType())
        .readEntity().getOrDefault("mfa_required", "false"));
  }

  @Override
  public ApiResponse<MfaSecret> generateMfaSecret(String userId) {
    return doApiPost(getUserRoute(userId) + "/mfa/generate", null, MfaSecret.class);
  }

  @Override
  public ApiResponse<Boolean> updateUserPassword(String userId, String currentPassword,
      String newPassword) {
    UpdateUserPasswordRequest request = UpdateUserPasswordRequest.builder()
        .currentPassword(currentPassword).newPassword(newPassword).build();
    return doApiPut(getUserRoute(userId) + "/password", request).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> updateUserRoles(String userId, Role... roles) {
    UpdateRolesRequest request = new UpdateRolesRequest(roles);
    return doApiPut(getUserRoute(userId) + "/roles", request).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> updateUserActive(String userId, boolean active) {
    UpdateUserActiveRequest request = UpdateUserActiveRequest.builder().active(active).build();
    return doApiPut(getUserRoute(userId) + "/active", request).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> deleteUser(String userId) {
    return doApiDelete(getUserRoute(userId)).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> sendPasswordResetEmail(String email) {
    SendPasswordResetEmailRequest request =
        SendPasswordResetEmailRequest.builder().email(email).build();
    return doApiPost(getUsersRoute() + "/password/reset/send", request).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> resetPassword(String token, String newPassword) {
    ResetPasswordRequest request =
        ResetPasswordRequest.builder().token(token).newPassword(newPassword).build();
    return doApiPost(getUsersRoute() + "/password/reset", request).checkStatusOk();
  }

  @Override
  public ApiResponse<SessionList> getSessions(String userId, String etag) {
    return doApiGet(getUserSessionsRoute(userId), etag, SessionList.class);
  }

  @Override
  public ApiResponse<Boolean> revokeSession(String userId, String sessionId) {
    RevokeSessionRequest request = RevokeSessionRequest.builder().sessionId(sessionId).build();
    return doApiPost(getUserSessionsRoute(userId) + "/revoke", request).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> attachDeviceId(String deviceId) {
    AttachDeviceIdRequest request = AttachDeviceIdRequest.builder().deviceId(deviceId).build();
    return doApiPut(getUsersRoute() + "/sessions/device", request).checkStatusOk();
  }

  @Override
  public ApiResponse<TeamUnreadList> getTeamUnreadForUser(String userId, String teamIdToExclude) {
    String optional = "";
    if (teamIdToExclude != null) { // TODO use StringUtils.isNotEmpty
      try {
        optional = String.format("?exclude_team=%s",
            URLEncoder.encode(teamIdToExclude, StandardCharsets.UTF_8.displayName()));
      } catch (UnsupportedEncodingException e) {
        throw new AssertionError(e);
      }
    }
    return doApiGet(getUserRoute(userId) + "/teams/unread" + optional, null, TeamUnreadList.class);
  }

  @Override
  public ApiResponse<Audits> getUserAudits(String userId, Pager pager, String etag) {
    return doApiGet(getUserRoute(userId) + "/audits" + pager.toQuery(), etag, Audits.class);
  }

  @Override
  public ApiResponse<Boolean> verifyUserEmail(String token) {
    VerifyUserEmailRequest request = VerifyUserEmailRequest.builder().token(token).build();
    return doApiPost(getUsersRoute() + "/email/verify", request).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> sendVerificationEmail(String email) {
    SendVerificationEmailRequest request =
        SendVerificationEmailRequest.builder().email(email).build();
    return doApiPost(getUsersRoute() + "/email/verify/send", request).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> setProfileImage(String userId, Path imageFilePath) throws IOException {
    FormMultiPart multiPart = new FormMultiPart(MediaType.MULTIPART_FORM_DATA_TYPE);

    try (InputStream stream = Files.newInputStream(imageFilePath)) {
      multiPart.bodyPart("image", stream);
      return doApiPostMultiPart(getUserProfileImageRoute(userId), multiPart).checkStatusOk();
    }
  }

  @Override
  public ApiResponse<UserAccessToken> createUserAccessToken(String userId, String description) {
    return doApiPost(getUserTokensRoute(userId), UserAccessTokenCreateRequest.of(description),
        UserAccessToken.class);
  }

  @Override
  public ApiResponse<UserAccessTokenList> getUserAccessTokens(String userId, Pager pager) {
    return doApiGet(getUserTokensRoute(userId) + pager.toQuery(), null, UserAccessTokenList.class);
  }

  @Override
  public ApiResponse<UserAccessTokenList> getUserAccessTokensAllUsers(Pager pager) {
    return doApiGet(getUserTokensRoute() + pager.toQuery(), null, UserAccessTokenList.class);
  }

  @Override
  public ApiResponse<Boolean> revokeUserAccessToken(String tokenId) {
    return doApiPost(getUserTokensRoute() + "/revoke", RevokeTokenRequest.of(tokenId))
        .checkStatusOk();
  }

  @Override
  public ApiResponse<UserAccessToken> getUserAccessToken(String tokenId) {
    return doApiGet(getUserTokenRoute(tokenId), null, UserAccessToken.class);
  }

  @Override
  public ApiResponse<Boolean> disableUserAccessToken(String tokenId) {
    return doApiPost(getUserTokensRoute() + "/disable", DisableEnableTokenRequest.of(tokenId))
        .checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> enableUserAccessToken(String tokenId) {
    return doApiPost(getUserTokensRoute() + "/enable", DisableEnableTokenRequest.of(tokenId))
        .checkStatusOk();
  }

  @Override
  public ApiResponse<UserAccessTokenList> searchTokens(String term) {
    return doApiPost(getUserTokensRoute() + "/search", SearchTokensRequest.of(term),
        UserAccessTokenList.class);
  }

  @Override
  public ApiResponse<Boolean> revokeAllActiveSessionForUser(String userId) {
    return doApiPost(getUserSessionsRoute(userId) + "/revoke/all", null).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> deleteProfileImage(String userId) {
    return doApiDelete(getUserProfileImageRoute(userId)).checkStatusOk();
  }

  // Team Section

  @Override
  public ApiResponse<Team> createTeam(Team team) {
    return doApiPost(getTeamsRoute(), team, Team.class);
  }

  @Override
  public ApiResponse<Team> getTeam(String teamId, String etag) {
    return doApiGet(getTeamRoute(teamId), etag, Team.class);
  }

  @Override
  public ApiResponse<TeamList> getAllTeams(Pager pager, String etag) {
    return doApiGet(getTeamsRoute() + pager.toQuery(), etag, TeamList.class);
  }

  @Override
  public ApiResponse<Team> getTeamByName(String name, String etag) {
    return doApiGet(getTeamByNameRoute(name), etag, Team.class);
  }

  @Override
  public ApiResponse<TeamList> searchTeams(TeamSearch search) {
    return doApiPost(getTeamsRoute() + "/search", search, TeamList.class);
  }

  @Override
  public ApiResponse<TeamExists> teamExists(String name, String etag) {
    return doApiGet(getTeamByNameRoute(name) + "/exists", etag, TeamExists.class);
  }

  @Override
  public ApiResponse<TeamList> getTeamsForUser(String userId, String etag) {
    return doApiGet(getUserRoute(userId) + "/teams", etag, TeamList.class);
  }

  @Override
  public ApiResponse<TeamMember> getTeamMember(String teamId, String userId, String etag) {
    return doApiGet(getTeamMemberRoute(teamId, userId), etag, TeamMember.class);
  }

  @Override
  public ApiResponse<Boolean> updateTeamMemberRoles(String teamId, String userId,
      Role... newRoles) {
    UpdateRolesRequest request = new UpdateRolesRequest(newRoles);
    return doApiPut(getTeamMemberRoute(teamId, userId) + "/roles", request).checkStatusOk();
  }

  @Override
  public ApiResponse<Team> updateTeam(Team team) {
    return doApiPut(getTeamRoute(team.getId()), team, Team.class);
  }

  @Override
  public ApiResponse<Team> patchTeam(String teamId, TeamPatch patch) {
    return doApiPut(getTeamRoute(teamId) + "/patch", patch, Team.class);
  }

  @Override
  public ApiResponse<Boolean> deleteTeam(String teamId) {
    return doApiDelete(getTeamRoute(teamId)).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> deleteTeam(String teamId, boolean permanent) {
    String query = new QueryBuilder().set("permanent", Boolean.toString(permanent)).toString();
    return doApiDelete(getTeamRoute(teamId) + query).checkStatusOk();
  }

  @Override
  public ApiResponse<TeamMemberList> getTeamMembers(String teamId, Pager pager, String etag) {
    return doApiGet(getTeamMembersRoute(teamId) + pager.toQuery(), etag, TeamMemberList.class);
  }

  @Override
  public ApiResponse<TeamMemberList> getTeamMembersForUser(String userId, String etag) {
    return doApiGet(getUserRoute(userId) + "/teams/members", etag, TeamMemberList.class);
  }

  @Override
  public ApiResponse<TeamMemberList> getTeamMembersByIds(String teamId, String... userIds) {
    String url = String.format("/teams/%s/members/ids", teamId);
    return doApiPost(url, userIds, TeamMemberList.class);
  }

  @Override
  public ApiResponse<TeamMember> addTeamMember(TeamMember teamMemberToAdd) {
    return doApiPost(getTeamMembersRoute(teamMemberToAdd.getTeamId()), teamMemberToAdd,
        TeamMember.class);
  }

  @Deprecated
  @Override
  public ApiResponse<TeamMember> addTeamMember(String teamId, String userId, String hash,
      String dataToHash, String inviteId) {
    QueryBuilder query = new QueryBuilder();
    if (StringUtils.isNotEmpty(inviteId)) {
      query.set("invite_id", inviteId);
    }
    if (StringUtils.isNotEmpty(hash) && StringUtils.isNotEmpty(dataToHash)) {
      query.set("hash", hash).set("data", dataToHash);
    }
    TeamMember teamMember = new TeamMember(teamId, userId);

    return doApiPost(getTeamMembersRoute(teamId) + query, teamMember, TeamMember.class);
  }

  @Override
  @Deprecated
  public ApiResponse<TeamMember> addTeamMember(String hash, String dataToHash, String inviteId) {
    QueryBuilder query = new QueryBuilder();
    if (StringUtils.isNotEmpty(inviteId)) {
      query.set("invite_id", inviteId);
    }
    if (StringUtils.isNotEmpty(hash) && StringUtils.isNotEmpty(dataToHash)) {
      query.set("hash", hash).set("data", dataToHash);
    }

    return doApiPost(getTeamsRoute() + "/members/invite" + query.toString(), null,
        TeamMember.class);
  }

  @Override
  public ApiResponse<TeamMember> addTeamMemberFromInvite(String token, String inviteId) {
    QueryBuilder query = new QueryBuilder();
    if (StringUtils.isNotEmpty(token)) {
      query.set("token", token);
    }
    if (StringUtils.isNotEmpty(inviteId)) {
      query.set("invite_id", inviteId);
    }
    return doApiPost(getTeamsRoute() + "/members/invite" + query.toString(), null,
        TeamMember.class);
  }

  @Override
  public ApiResponse<TeamMemberList> addTeamMembers(String teamId, String... userIds) {
    List<TeamMember> members =
        Arrays.stream(userIds).map(u -> new TeamMember(teamId, u)).collect(Collectors.toList());

    return doApiPost(getTeamMembersRoute(teamId) + "/batch", members, TeamMemberList.class);
  }

  @Override
  public ApiResponse<Boolean> removeTeamMember(String teamId, String userId) {
    return doApiDelete(getTeamMemberRoute(teamId, userId)).checkStatusOk();
  }

  @Override
  public ApiResponse<TeamStats> getTeamStats(String teamId, String etag) {
    return doApiGet(getTeamStatsRoute(teamId), etag, TeamStats.class);
  }

  @Override
  public ApiResponse<Path> getTeamIcon(String teamId) throws IOException {
    return doApiGetFile(getTeamIconRoute(teamId), null);
  }

  @Override
  public ApiResponse<Boolean> setTeamIcon(String teamId, Path iconFilePath) throws IOException {
    FormMultiPart multiPart = new FormMultiPart(MediaType.MULTIPART_FORM_DATA_TYPE);

    try (InputStream stream = Files.newInputStream(iconFilePath)) {
      multiPart.bodyPart("image", stream);
      return doApiPostMultiPart(getTeamIconRoute(teamId), multiPart).checkStatusOk();
    }
  }

  @Override
  public ApiResponse<Boolean> removeTeamIcon(String teamId) {
    return doApiDelete(getTeamIconRoute(teamId)).checkStatusOk();
  }

  @Override
  public ApiResponse<TeamUnread> getTeamUnread(String teamId, String userId) {
    return doApiGet(getUserRoute(userId) + getTeamRoute(teamId) + "/unread", null,
        TeamUnread.class);
  }

  @Override
  public ApiResponse<byte[]> importTeam(byte[] data, int filesize, String importFrom,
      String fileName, String teamId) {
    // FIXME
    throw new UnsupportedOperationException();
  }

  @Override
  public ApiResponse<Boolean> inviteUsersToTeam(String teamId, Collection<String> userEmails) {
    return doApiPost(getTeamRoute(teamId) + "/invite/email", userEmails).checkStatusOk();
  }

  @Override
  public ApiResponse<TeamInviteInfo> getInviteInfo(String inviteId) {
    return doApiGet(getTeamInviteRoute(inviteId), null, TeamInviteInfo.class);
  }

  // Channel Section

  @Override
  public ApiResponse<Channel> createChannel(Channel channel) {
    return doApiPost(getChannelsRoute(), channel, Channel.class);
  }

  @Override
  public ApiResponse<Channel> updateChannel(Channel channel) {
    return doApiPut(getChannelRoute(channel.getId()), channel, Channel.class);
  }

  @Override
  public ApiResponse<Channel> patchChannel(String channelId, ChannelPatch patch) {
    return doApiPut(getChannelRoute(channelId) + "/patch", patch, Channel.class);
  }

  @Override
  public ApiResponse<Channel> createDirectChannel(String userId1, String userId2) {
    return doApiPost(getChannelsRoute() + "/direct", Arrays.asList(userId1, userId2),
        Channel.class);
  }

  @Override
  public ApiResponse<Channel> createGroupChannel(String... userIds) {
    return doApiPost(getChannelsRoute() + "/group", userIds, Channel.class);
  }

  @Override
  public ApiResponse<Channel> getChannel(String channelId, String etag) {
    return doApiGet(getChannelRoute(channelId), etag, Channel.class);
  }

  @Override
  public ApiResponse<ChannelStats> getChannelStats(String channelId, String etag) {
    return doApiGet(getChannelRoute(channelId) + "/stats", etag, ChannelStats.class);
  }

  @Override
  public ApiResponse<PostList> getPinnedPosts(String channelId, String etag) {
    return doApiGet(getChannelRoute(channelId) + "/pinned", etag, PostList.class);
  }

  @Override
  public ApiResponse<ChannelList> getPublicChannelsForTeam(String teamId, Pager pager,
      String etag) {
    return doApiGet(getChannelsForTeamRoute(teamId) + pager.toQuery(), etag, ChannelList.class);
  }

  @Override
  public ApiResponse<ChannelList> getPublicChannelsByIdsForTeam(String teamId,
      String... channelIds) {
    return doApiPost(getChannelsForTeamRoute(teamId) + "/ids", channelIds, ChannelList.class);
  }

  @Override
  public ApiResponse<ChannelList> getChannelsForTeamForUser(String teamId, String userId,
      String etag) {
    return doApiGet(getUserRoute(userId) + getTeamRoute(teamId) + "/channels", etag,
        ChannelList.class);
  }

  @Override
  public ApiResponse<ChannelList> searchChannels(String teamId, ChannelSearch search) {
    return doApiPost(getChannelsForTeamRoute(teamId) + "/search", search, ChannelList.class);
  }

  @Override
  public ApiResponse<Boolean> deleteChannel(String channelId) {
    return doApiDelete(getChannelRoute(channelId)).checkStatusOk();
  }

  @Override
  public ApiResponse<Channel> getChannelByName(String channelName, String teamId, String etag) {
    return doApiGet(getChannelByNameRoute(channelName, teamId), etag, Channel.class);
  }

  @Override
  public ApiResponse<Channel> getChannelByNameForTeamName(String channelName, String teamName,
      String etag) {
    return doApiGet(getChannelByNameForTeamNameRoute(channelName, teamName), etag, Channel.class);
  }

  @Override
  public ApiResponse<ChannelMembers> getChannelMembers(String channelId, Pager pager, String etag) {
    return doApiGet(getChannelMembersRoute(channelId) + pager.toQuery(), etag,
        ChannelMembers.class);
  }

  @Override
  public ApiResponse<ChannelMembers> getChannelMembersByIds(String channelId, String... userIds) {
    return doApiPost(getChannelMembersRoute(channelId) + "/ids", userIds, ChannelMembers.class);
  }

  @Override
  public ApiResponse<ChannelMember> getChannelMember(String channelId, String userId, String etag) {
    return doApiGet(getChannelMemberRoute(channelId, userId), etag, ChannelMember.class);
  }

  @Override
  public ApiResponse<ChannelMembers> getChannelMembersForUser(String userId, String teamId,
      String etag) {
    return doApiGet(getUserRoute(userId) + String.format("/teams/%s/channels/members", teamId),
        etag, ChannelMembers.class);
  }

  @Override
  public ApiResponse<ChannelViewResponse> viewChannel(String userId, ChannelView view) {
    String url = String.format(getChannelsRoute() + "/members/%s/view", userId);
    return doApiPost(url, view, ChannelViewResponse.class);
  }

  @Override
  public ApiResponse<ChannelUnread> getChannelUnread(String channelId, String userId) {
    return doApiGet(getUserRoute(userId) + getChannelRoute(channelId) + "/unread", null,
        ChannelUnread.class);
  }

  @Override
  public ApiResponse<Boolean> updateChannelRoles(String channelId, String userId, Role... roles) {
    UpdateRolesRequest request = new UpdateRolesRequest(roles);
    return doApiPut(getChannelMemberRoute(channelId, userId) + "/roles", request).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> updateChannelNotifyProps(String channelId, String userId,
      Map<String, String> props) {
    return doApiPut(getChannelMemberRoute(channelId, userId) + "/notify_props", props)
        .checkStatusOk();
  }

  @Override
  public ApiResponse<ChannelMember> addChannelMember(String channelId, String userId) {
    AddChannelMemberRequest request = AddChannelMemberRequest.builder().userId(userId).build();
    return doApiPost(getChannelMembersRoute(channelId), request, ChannelMember.class);
  }

  @Override
  public ApiResponse<Boolean> removeUserFromChannel(String channelId, String userId) {
    return doApiDelete(getChannelMemberRoute(channelId, userId)).checkStatusOk();
  }

  @Override
  public ApiResponse<Channel> restoreChannel(String channelId) {
    return doApiPost(getChannelRoute(channelId) + "/restore", null, Channel.class);
  }

  @Override
  public ApiResponse<ChannelList> getDeletedChannels(String teamId, Pager pager) {
    return doApiGet(getChannelsForTeamRoute(teamId) + "/deleted" + pager.toQuery(), null,
        ChannelList.class);
  }

  @Override
  public ApiResponse<Channel> convertChannelToPrivate(String channelId) {
    return doApiPost(getChannelRoute(channelId) + "/convert", null, Channel.class);
  }

  @Override
  public ApiResponse<ChannelList> autocompleteChannels(String teamId, String searchTerm) {
    return doApiGet(getChannelsForTeamRoute(teamId) + "/autocomplete" + "?name=" + searchTerm, null, ChannelList.class);
  }

  @Override
  public ApiResponse<ChannelList> autocompleteChannelsForSearch(String teamId, String searchTerm) {
    return doApiGet(getChannelsForTeamRoute(teamId) + "/search_autocomplete" + "?name=" + searchTerm, null, ChannelList.class);
  }

  // Post Section

  @Override
  public ApiResponse<Post> createPost(Post post) {
    return doApiPost(getPostsRoute(), post, Post.class);
  }

  @Override
  public ApiResponse<Post> createEphemeralPost(String sendToUserId, Post post) {
    CreateEphemeralPostRequest payload = CreateEphemeralPostRequest.builder() //
        .userId(sendToUserId) //
        .post(post) //
        .build();
    return doApiPost(getPostsRoute() + "/ephemeral", payload, Post.class);
  }

  @Override
  public ApiResponse<Post> updatePost(Post post) {
    return doApiPut(getPostRoute(post.getId()), post, Post.class);
  }

  @Override
  public ApiResponse<Post> patchPost(String postId, PostPatch patch) {
    return doApiPut(getPostRoute(postId) + "/patch", patch, Post.class);
  }

  @Override
  public ApiResponse<Boolean> pinPost(String postId) {
    return doApiPost(getPostRoute(postId) + "/pin", null).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> unpinPost(String postId) {
    return doApiPost(getPostRoute(postId) + "/unpin", null).checkStatusOk();
  }

  @Override
  public ApiResponse<Post> getPost(String postId, String etag) {
    return doApiGet(getPostRoute(postId), etag, Post.class);
  }

  @Override
  public ApiResponse<Boolean> deletePost(String postId) {
    return doApiDelete(getPostRoute(postId)).checkStatusOk();
  }

  @Override
  public ApiResponse<PostList> getPostThread(String postId, String etag) {
    return doApiGet(getPostRoute(postId) + "/thread", etag, PostList.class);
  }

  @Override
  public ApiResponse<PostList> getPostsForChannel(String channelId, Pager pager, String etag) {
    return doApiGet(getChannelRoute(channelId) + "/posts" + pager.toQuery(), etag, PostList.class);
  }

  @Override
  public ApiResponse<PostList> getFlaggedPostsForUser(String userId, Pager pager) {
    return doApiGet(getUserRoute(userId) + "/posts/flagged" + pager.toQuery(), null,
        PostList.class);
  }

  @Override
  public ApiResponse<PostList> getFlaggedPostsForUserInTeam(String userId, String teamId,
      Pager pager) {
    // TODO teamId length validation
    String query = new QueryBuilder().set("in_team", teamId).toString();
    return doApiGet(getUserRoute(userId) + "/posts/flagged" + query + pager.toQuery(false), null,
        PostList.class);
  }

  @Override
  public ApiResponse<PostList> getFlaggedPostsForUserInChannel(String userId, String channelId,
      Pager pager) {
    // TODO channelId length validation
    String query = new QueryBuilder().set("in_channel", channelId).toString();
    return doApiGet(getUserRoute(userId) + "/posts/flagged" + query + pager.toQuery(false), null,
        PostList.class);
  }

  @Override
  public ApiResponse<PostList> getPostsSince(String channelId, long time) {
    String query = String.format("?since=%d", time);
    return doApiGet(getChannelRoute(channelId) + "/posts" + query, null, PostList.class);
  }

  @Override
  public ApiResponse<PostList> getPostsAfter(String channelId, String postId, Pager pager,
      String etag) {
    String query = new QueryBuilder().set("after", postId).toString();
    return doApiGet(getChannelRoute(channelId) + "/posts" + query + pager.toQuery(false), etag,
        PostList.class);
  }

  @Override
  public ApiResponse<PostList> getPostsBefore(String channelId, String postId, Pager pager,
      String etag) {
    String query = new QueryBuilder().set("before", postId).toString();
    return doApiGet(getChannelRoute(channelId) + "/posts" + query + pager.toQuery(false), etag,
        PostList.class);
  }

  @Override
  public ApiResponse<PostSearchResults> searchPosts(String teamId, String terms,
      boolean isOrSearch) {
    SearchPostsRequest request =
        SearchPostsRequest.builder().terms(terms).isOrSearch(isOrSearch).build();
    return doApiPost(getTeamRoute(teamId) + "/posts/search", request, PostSearchResults.class);
  }

  @Override
  public ApiResponse<FileInfo[]> getFileInfoForPost(String postId) {
    return doApiGet(getPostRoute(postId) + "/files/info", null, FileInfo[].class);
  }

  // File Section

  @Override
  public ApiResponse<FileUploadResult> uploadFile(String channelId, Path... filePaths)
      throws IOException {

    if (filePaths.length == 0) {
      throw new IllegalArgumentException("At least one filePath required.");
    }

    FormMultiPart multiPart = new FormMultiPart(MediaType.MULTIPART_FORM_DATA_TYPE);

    List<InputStream> streams = new ArrayList<>();
    try {
      for (Path filePath : filePaths) {
        InputStream stream = Files.newInputStream(filePath);
        multiPart.bodyPart("files", stream, filePath.getFileName().toString());
        streams.add(stream);
      }
      multiPart.field("channel_id", channelId);
      return doApiPostMultiPart(getFilesRoute(), multiPart, FileUploadResult.class);
    } finally {
      streams.forEach(s -> ioOperation(InputStream::close, s));
    }
  }

  @FunctionalInterface
  private interface IOCallable<T> {
    void call(T io) throws IOException;
  }
  void ioOperation(IOCallable<InputStream> callable, InputStream stream) {
    try (InputStream s = stream) {
      callable.call(s);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public ApiResponse<Path> getFile(String fileId) throws IOException {
    return doApiGetFile(getFileRoute(fileId), null);
  }

  @Override
  public ApiResponse<Path> getFileThumbnail(String fileId) throws IOException {
    return doApiGetFile(getFileRoute(fileId) + "/thumbnail", null);
  }

  @Override
  public ApiResponse<Path> getFilePreview(String fileId) throws IOException {
    return doApiGetFile(getFileRoute(fileId) + "/preview", null);
  }

  @Override
  public ApiResponse<String> getPublicFileLink(String fileId) {
    ApiResponse<PublicFileLink> response =
        doApiGet(getFileRoute(fileId) + "/link", null, PublicFileLink.class);
    if (response.hasError()) {
      return ApiResponse.of(response.getRawResponse(), String.class);
    }
    return ApiResponse.of(response.getRawResponse(), response.readEntity().getLink());
  }

  @Override
  public ApiResponse<FileInfo> getFileMetadata(String fileId) {
    return doApiGet(getFileRoute(fileId) + "/info", null, FileInfo.class);
  }

  // General Section

  @Override
  public ApiResponse<Boolean> getPing() {
    return doApiGet(getSystemRoute() + "/ping", null).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> testEmail() {
    return doApiPost(getTestEmailRoute(), null).checkStatusOk();
  }

  @Override
  public ApiResponse<Config> getConfig() {
    return doApiGet(getConfigRoute(), null, Config.class);
  }

  @Override
  public ApiResponse<Boolean> reloadConfig() {
    return doApiPost(getConfigRoute() + "/reload", null).checkStatusOk();
  }

  @Override
  public ApiResponse<Map<String, String>> getOldClientConfig(String etag) {
    return doApiGet(getConfigRoute() + "/client?format=old", etag, stringMapType());
  }

  @Override
  public ApiResponse<Map<String, String>> getOldClientLicense(String etag) {
    return doApiGet(getLicenseRoute() + "/client?format=old", etag, stringMapType());
  }

  @Override
  public ApiResponse<Boolean> databaseRecycle() {
    return doApiPost(getDatabaseRoute() + "/recycle", null).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> invalidateCaches() {
    return doApiPost(getCacheRoute() + "/invalidate", null).checkStatusOk();
  }

  @Override
  public ApiResponse<Config> updateConfig(Config config) {
    return doApiPut(getConfigRoute(), config, Config.class);
  }

  @Override
  public ApiResponse<AnalyticsRows> getAnalytics(AnalyticsCategory category, String teamId) {
    QueryBuilder queryBuilder = new QueryBuilder();
    queryBuilder.set("name", category.getCode());
    if (StringUtils.isNotEmpty(teamId)) {
      queryBuilder.set("team_id", teamId);
    }
    return doApiGet("/analytics/old" + queryBuilder.toString(), null, AnalyticsRows.class);
  }

  @Override
  public ApiResponse<Boolean> uploadLicenseFile(Path licenseFile) throws IOException {
    FormMultiPart multiPart = new FormMultiPart(MediaType.MULTIPART_FORM_DATA_TYPE);

    try (InputStream stream = Files.newInputStream(licenseFile)) {
      multiPart.bodyPart("license", stream);
      return doApiPostMultiPart("/license", multiPart).checkStatusOk();
    }
  }

  @Override
  public ApiResponse<Boolean> removeLicense() {
    return doApiDelete("/license").checkStatusOk();
  }

  // Webhooks Section

  @Override
  public ApiResponse<IncomingWebhook> createIncomingWebhook(IncomingWebhook hook) {
    return doApiPost(getIncomingWebhooksRoute(), hook, IncomingWebhook.class);
  }

  @Override
  public ApiResponse<IncomingWebhook> updateIncomingWebhook(IncomingWebhook hook) {
    return doApiPut(getIncomingWebhookRoute(hook.getId()), hook, IncomingWebhook.class);
  }

  @Override
  public ApiResponse<IncomingWebhookList> getIncomingWebhooks(Pager pager, String etag) {
    return doApiGet(getIncomingWebhooksRoute() + pager.toQuery(), etag, IncomingWebhookList.class);
  }

  @Override
  public ApiResponse<IncomingWebhookList> getIncomingWebhooksForTeam(String teamId, Pager pager,
      String etag) {
    String query = new QueryBuilder().set("team_id", teamId).toString();
    return doApiGet(getIncomingWebhooksRoute() + query + pager.toQuery(false), etag,
        IncomingWebhookList.class);
  }

  @Override
  public ApiResponse<IncomingWebhook> getIncomingWebhook(String hookId, String etag) {
    return doApiGet(getIncomingWebhookRoute(hookId), etag, IncomingWebhook.class);
  }

  @Override
  public ApiResponse<Boolean> deleteIncomingWebhook(String hookId) {
    return doApiDelete(getIncomingWebhookRoute(hookId)).checkStatusOk();
  }

  @Override
  public ApiResponse<OutgoingWebhook> createOutgoingWebhook(OutgoingWebhook hook) {
    return doApiPost(getOutgoingWebhooksRoute(), hook, OutgoingWebhook.class);
  }

  @Override
  public ApiResponse<OutgoingWebhook> updateOutgoingWebhook(OutgoingWebhook hook) {
    return doApiPut(getOutgoingWebhookRoute(hook.getId()), hook, OutgoingWebhook.class);
  }

  @Override
  public ApiResponse<OutgoingWebhookList> getOutgoingWebhooks(Pager pager, String etag) {
    return doApiGet(getOutgoingWebhooksRoute() + pager.toQuery(), etag, OutgoingWebhookList.class);
  }

  @Override
  public ApiResponse<OutgoingWebhook> getOutgoingWebhook(String hookId) {
    return doApiGet(getOutgoingWebhookRoute(hookId), null, OutgoingWebhook.class);
  }

  @Override
  public ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForChannel(String channelId,
      Pager pager, String etag) {
    String query = new QueryBuilder().set("channel_id", channelId).toString();
    return doApiGet(getOutgoingWebhooksRoute() + query + pager.toQuery(false), etag,
        OutgoingWebhookList.class);
  }

  @Override
  public ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForTeam(String teamId, Pager pager,
      String etag) {
    String query = new QueryBuilder().set("team_id", teamId).toString();
    return doApiGet(getOutgoingWebhooksRoute() + query + pager.toQuery(false), etag,
        OutgoingWebhookList.class);
  }

  @Override
  public ApiResponse<OutgoingWebhook> regenOutgoingHookToken(String hookId) {
    return doApiPost(getOutgoingWebhookRoute(hookId) + "/regen_token", null, OutgoingWebhook.class);
  }

  @Override
  public ApiResponse<Boolean> deleteOutgoingWebhook(String hookId) {
    return doApiDelete(getOutgoingWebhookRoute(hookId)).checkStatusOk();
  }

  // Preferences Section

  @Override
  public ApiResponse<Preferences> getPreferences(String userId) {
    return doApiGet(getPreferencesRoute(userId), null, Preferences.class);
  }

  @Override
  public ApiResponse<Boolean> updatePreferences(String userId, Preferences preferences) {
    return doApiPut(getPreferencesRoute(userId), preferences).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> deletePreferences(String userId, Preferences preferences) {
    return doApiPost(getPreferencesRoute(userId) + "/delete", preferences).checkStatusOk();
  }

  @Override
  public ApiResponse<Preferences> getPreferencesByCategory(String userId,
      PreferenceCategory category) {
    String url = String.format(getPreferencesRoute(userId) + "/%s", category.getCode());
    return doApiGet(url, null, Preferences.class);
  }

  @Override
  public ApiResponse<Preference> getPreferenceByCategoryAndName(String userId,
      PreferenceCategory category, String preferenceName) {
    String url = String.format(getPreferencesRoute(userId) + "/%s/name/%s", category.getCode(),
        preferenceName);
    return doApiGet(url, null, Preference.class);
  }

  // SAML section

  @Override
  public ApiResponse<Path> getSamlMetadata() throws IOException {
    return doApiGetFile(getSamlRoute() + "/metadata", null);
  }

  @Override
  public ApiResponse<Boolean> uploadSamlIdpCertificate(Path dataFile, String fileName) throws IOException {
    FormMultiPart multiPart = new FormMultiPart(MediaType.MULTIPART_FORM_DATA_TYPE);

    try (InputStream stream = Files.newInputStream(dataFile)) {
      multiPart.bodyPart("certificate", stream);
      return doApiPostMultiPart(getSamlRoute() + "/certificate/idp", multiPart).checkStatusOk();
    }
  }

  @Override
  public ApiResponse<Boolean> uploadSamlPublicCertificate(Path dataFile, String fileName) throws IOException {
    FormMultiPart multiPart = new FormMultiPart(MediaType.MULTIPART_FORM_DATA_TYPE);

    try (InputStream stream = Files.newInputStream(dataFile)) {
      multiPart.bodyPart("certificate", stream);
      return doApiPostMultiPart(getSamlRoute() + "/certificate/public", multiPart).checkStatusOk();
    }
  }

  @Override
  public ApiResponse<Boolean> uploadSamlPrivateCertificate(Path dataFile, String fileName) throws IOException {
    FormMultiPart multiPart = new FormMultiPart(MediaType.MULTIPART_FORM_DATA_TYPE);

    try (InputStream stream = Files.newInputStream(dataFile)) {
      multiPart.bodyPart("certificate", stream);
      return doApiPostMultiPart(getSamlRoute() + "/certificate/private", multiPart).checkStatusOk();
    }
  }

  @Override
  public ApiResponse<Boolean> deleteSamlIdpCertificate() {
    return doApiDelete(getSamlRoute() + "/certificate/idp").checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> deleteSamlPublicCertificate() {
    return doApiDelete(getSamlRoute() + "/certificate/public").checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> deleteSamlPrivateCertificate() {
    return doApiDelete(getSamlRoute() + "/certificate/private").checkStatusOk();
  }

  @Override
  public ApiResponse<SamlCertificateStatus> getSamlCertificateStatus() {
    return doApiGet(getSamlRoute() + "/certificate/status", null, SamlCertificateStatus.class);
  }

  // Compliance Section

  @Override
  public ApiResponse<Compliance> createComplianceReport(Compliance report) {
    return doApiPost(getComplianceReportsRoute(), report, Compliance.class);
  }

  @Override
  public ApiResponse<Compliances> getComplianceReports(Pager pager) {
    return doApiGet(getComplianceReportsRoute() + pager.toQuery(), null, Compliances.class);
  }

  @Override
  public ApiResponse<Compliance> getComplianceReport(String reportId) {
    return doApiGet(getComplianceReportRoute(reportId), null, Compliance.class);
  }

  @Override
  public ApiResponse<Object> downloadComplianceReport(String reportId) {
    throw new UnsupportedOperationException("not impl"); // FIXME
  }

  // Cluster Section

  @Override
  public ApiResponse<ClusterInfo[]> getClusterStatus() {
    return doApiGet(getClusterRoute() + "/status", null, ClusterInfo[].class);
  }

  // LDAP Section

  @Override
  public ApiResponse<Boolean> syncLdap() {
    return doApiPost(getLdapRoute() + "/sync", null).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> testLdap() {
    return doApiPost(getLdapRoute() + "/test", null).checkStatusOk();
  }

  // Audits Section

  @Override
  public ApiResponse<Audits> getAudits(Pager pager, String etag) {
    return doApiGet("/audits" + pager.toQuery(), etag, Audits.class);
  }

  // Brand Section

  @Override
  public ApiResponse<Path> getBrandImage() throws IOException {
    return doApiGetFile(getBrandImageRoute(), null);
  }

  @Override
  public ApiResponse<Boolean> uploadBrandImage(Path dataFile) throws IOException {
    FormMultiPart multiPart = new FormMultiPart(MediaType.MULTIPART_FORM_DATA_TYPE);

    try (InputStream stream = Files.newInputStream(dataFile)) {
      multiPart.bodyPart("image", stream);
      return doApiPostMultiPart(getBrandImageRoute(), multiPart).checkStatusOk();
    }
  }

  @Override
  public ApiResponse<Boolean> deleteBrandImage() {
    return doApiDelete(getBrandImageRoute()).checkStatusOk();
  }

  // Logs Section

  @Override
  public ApiResponse<List<String>> getLogs(Pager pager) {
    return doApiGet("/logs" + pager.toQuery(), null, listType());
  }

  @Override
  public ApiResponse<Map<String, String>> postLog(Map<String, String> message) {
    return doApiPost("/logs", message, stringMapType());
  }

  // OAuth Section

  @Override
  public ApiResponse<OAuthApp> createOAuthApp(OAuthApp app) {
    return doApiPost(getOAuthAppsRoute(), app, OAuthApp.class);
  }

  @Override
  public ApiResponse<List<OAuthApp>> getOAuthApps(Pager pager) {
    return doApiGet(getOAuthAppsRoute() + pager.toQuery(), null, listType());
  }

  @Override
  public ApiResponse<OAuthApp> getOAuthApp(String appId) {
    return doApiGet(getOAuthAppRoute(appId), null, OAuthApp.class);
  }

  @Override
  public ApiResponse<OAuthApp> getOAuthAppInfo(String appId) {
    return doApiGet(getOAuthAppRoute(appId) + "/info", null, OAuthApp.class);
  }

  @Override
  public ApiResponse<Boolean> deleteOAuthApp(String appId) {
    return doApiDelete(getOAuthAppRoute(appId)).checkStatusOk();
  }

  @Override
  public ApiResponse<OAuthApp> regenerateOAuthAppSecret(String appId) {
    return doApiPost(getOAuthAppRoute(appId) + "/regen_secret", null, OAuthApp.class);
  }

  @Override
  public ApiResponse<List<OAuthApp>> getAuthorizedOAuthAppsForUser(String userId, Pager pager) {
    return doApiGet(getUserRoute(userId) + "/oauth/apps/authorized" + pager.toQuery(), null,
        listType());
  }

  @Override
  public String authorizeOAuthApp(AuthorizeRequest authRequest) {
    return doApiRequest(HttpMethod.POST, url + "/oauth/authorize", authRequest, null,
        stringMapType()).readEntity().get("redirect");
  }

  @Override
  public ApiResponse<Boolean> deauthorizeOAuthApp(String appId) {
    DeauthorizeOAuthAppRequest request =
        DeauthorizeOAuthAppRequest.builder().clientId(appId).build();
    return doApiRequest(HttpMethod.POST, url + "/oauth/deauthorize", request, null).checkStatusOk();
  }

  // Commands Section

  @Override
  public ApiResponse<Command> createCommand(Command cmd) {
    return doApiPost(getCommandsRoute(), cmd, Command.class);
  }

  @Override
  public ApiResponse<Command> updateCommand(Command cmd) {
    return doApiPut(getCommandRoute(cmd.getId()), cmd, Command.class);
  }

  @Override
  public ApiResponse<Boolean> deleteCommand(String commandId) {
    return doApiDelete(getCommandRoute(commandId)).checkStatusOk();
  }

  @Override
  public ApiResponse<CommandList> listCommands(String teamId, boolean customOnly) {
    String query =
        new QueryBuilder().set("team_id", teamId).set("custom_only", customOnly).toString();
    return doApiGet(getCommandsRoute() + query, null, CommandList.class);
  }

  @Override
  public ApiResponse<CommandResponse> executeCommand(String channelId, String command) {
    CommandArgs args = new CommandArgs();
    args.setChannelId(channelId);
    args.setCommand(command);
    return doApiPost(getCommandsRoute() + "/execute", args, CommandResponse.class);
  }

  @Override
  public ApiResponse<CommandList> listAutocompleteCommands(String teamId) {
    return doApiGet(getTeamAutoCompleteCommandsRoute(teamId), null, CommandList.class);
  }

  @Override
  public ApiResponse<String> regenCommandToken(String commandId) {
    return doApiPut(getCommandRoute(commandId) + "/regen_token", null, String.class);
  }

  // Status Section

  @Override
  public ApiResponse<Status> getUserStatus(String userId, String etag) {
    return doApiGet(getUserStatusRoute(userId), etag, Status.class);
  }

  @Override
  public ApiResponse<StatusList> getUsersStatusesByIds(String... userIds) {
    return doApiPost(getUserStatusesRoute() + "/ids", userIds, StatusList.class);
  }

  @Override
  public ApiResponse<Status> updateUserStatus(String userId, Status userStatus) {
    return doApiPut(getUserStatusRoute(userId), userStatus, Status.class);
  }

  // Emoji Section

  @Override
  public ApiResponse<Emoji> createEmoji(Emoji emoji, Path imageFile) throws IOException {
    FormMultiPart multiPart = new FormMultiPart(MediaType.MULTIPART_FORM_DATA_TYPE);

    try (InputStream stream = Files.newInputStream(imageFile)) {
      multiPart.bodyPart("image", stream);
      multiPart.field("emoji", emoji, MediaType.APPLICATION_JSON_TYPE);
      return doApiPostMultiPart(getEmojisRoute(), multiPart, Emoji.class);
    }
  }

  @Override
  public ApiResponse<EmojiList> getEmojiList(Pager pager) {
    return getEmojiList(pager, false);
  }

  @Override
  public ApiResponse<EmojiList> getEmojiListSorted(Pager pager) {
    return getEmojiList(pager, true);
  }

  private ApiResponse<EmojiList> getEmojiList(Pager pager, boolean sort) {
    QueryBuilder query = new QueryBuilder();
    query.set(pager);
    if (sort) {
      query.set("sort", "name");
    }
    return doApiGet(getEmojisRoute() + query.toString(), null, EmojiList.class);
  }

  @Override
  public ApiResponse<Boolean> deleteEmoji(String emojiId) {
    return doApiDelete(getEmojiRoute(emojiId)).checkStatusOk();
  }

  @Override
  public ApiResponse<Emoji> getEmoji(String emojiId) {
    return doApiGet(getEmojiRoute(emojiId), null, Emoji.class);
  }

  @Override
  public ApiResponse<Path> getEmojiImage(String emojiId) throws IOException {
    return doApiGetFile(getEmojiRoute(emojiId) + "/image", null);
  }

  private String detectSuffix(Response response) {
    MediaType mediaType = response.getMediaType();
    if (mediaType.isCompatible(MediaType.valueOf("image/png"))) {
      return ".png";
    } else if (mediaType.isCompatible(MediaType.valueOf("image/jpeg"))) {
      return ".jpg";
    } else if (mediaType.isCompatible(MediaType.valueOf("image/gif"))) {
      return ".gif";
    } else if (mediaType.isCompatible(MediaType.valueOf("image/bmp"))) {
      return ".bmp";
    } else {
      String contentDispositionHeader =
          String.class.cast(response.getHeaders().getFirst("Content-Disposition"));
      return multiPartAdapter.detectSuffix(contentDispositionHeader);
    }
  }

  @Override
  public ApiResponse<Emoji> getEmojiByName(String emojiName) {
    return doApiGet(getEmojiByNameRoute(emojiName), null, Emoji.class);
  }

  @Override
  public ApiResponse<EmojiList> searchEmoji(SearchEmojiRequest searchRequest) {
    return doApiPost(getEmojisRoute() + "/search", searchRequest, EmojiList.class);
  }

  @Override
  public ApiResponse<EmojiList> autocompleteEmoji(String name) {
    QueryBuilder query = new QueryBuilder().set("name", name);
    return doApiGet(getEmojisRoute() + "/autocomplete" + query.toString(), null, EmojiList.class);
  }

  // Reaction Section

  @Override
  public ApiResponse<Reaction> saveReaction(Reaction reaction) {
    return doApiPost(getReactionsRoute(), reaction, Reaction.class);
  }

  @Override
  public ApiResponse<ReactionList> getReactions(String postId) {
    return doApiGet(getPostRoute(postId) + "/reactions", null, ReactionList.class);
  }

  @Override
  public ApiResponse<Boolean> deleteReaction(Reaction reaction) {
    return doApiDelete(getUserRoute(reaction.getUserId()) + getPostRoute(reaction.getPostId())
        + String.format("/reactions/%s", reaction.getEmojiName())).checkStatusOk();
  }

  // Elasticsearch Section

  @Override
  public ApiResponse<Boolean> testElasticsearchConfiguration() {
    return doApiPost(getElasticsearchRoute() + "/test", null).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> purgeElasticsearchIndexes() {
    return doApiPost(getElasticsearchRoute() + "/purge_indexes", null).checkStatusOk();
  }

  // Plugin Section

  @Override
  public ApiResponse<PluginManifest> uploadPlugin(Path plugin, boolean force) throws IOException {
    FormMultiPart multiPart = new FormMultiPart(MediaType.MULTIPART_FORM_DATA_TYPE);

    try (InputStream stream = Files.newInputStream(plugin)) {
      multiPart.bodyPart("plugin", stream);
      multiPart.field("force", force, MediaType.APPLICATION_JSON_TYPE);
      return doApiPostMultiPart(getPluginsRoute(), multiPart, PluginManifest.class);
    }
  }

  @Override
  public ApiResponse<Plugins> getPlugins() {
    return doApiGet(getPluginsRoute(), null, Plugins.class);
  }

  @Override
  public ApiResponse<Boolean> removePlugin(String pluginId) {
    return doApiDelete(getPluginRoute(pluginId)).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> enablePlugin(String pluginId) {
    return doApiPost(getPluginRoute(pluginId) + "/enable", null).checkStatusOk();
  }

  @Override
  public ApiResponse<Boolean> disablePlugin(String pluginId) {
    return doApiPost(getPluginRoute(pluginId) + "/disable", null).checkStatusOk();
  }

  @Override
  public ApiResponse<PluginManifest[]> getWebappPlugins() {
    return doApiGet(getPluginsRoute() + "/webapp", null, PluginManifest[].class);
  }

  // OpenGraph section

  @Override
  public ApiResponse<OpenGraph> getOpenGraphMetadata(String url) {
    return doApiPost(getOpenGraphRoute(), Collections.singletonMap("url", url), OpenGraph.class);
  }

  // Bots section

  @Override
  public ApiResponse<Bot> createBot(BotPatch bot) {
    return doApiPost(getBotsRoute(), bot, Bot.class);
  }

  @Override
  public ApiResponse<Bot> patchBot(String botUserId, BotPatch patch) {
    return doApiPut(getBotsRoute(botUserId), patch, Bot.class);
  }

  @Override
  public ApiResponse<Bot> getBot(String botUserId, boolean includeDeleted) {
    String query = new QueryBuilder().set("include_deleted", includeDeleted).toString();
    return doApiGet(getBotsRoute(botUserId) + query, null, Bot.class);
  }

  @Override
  public ApiResponse<Bots> getBots(Pager pager, GetBotsOption option) {
    String query = new QueryBuilder() //
        .set(pager) //
        .set("include_deleted", option.isIncludeDeleted()) //
        .set("only_orphaned", option.isOnlyOrphaned()) //
        .toString();
    return doApiGet(getBotsRoute() + query, null, Bots.class);
  }

  @Override
  public ApiResponse<Bot> disableBot(String botUserId) {
    return doApiPost(getBotsRoute(botUserId) + "/disable", null, Bot.class);
  }

  @Override
  public ApiResponse<Bot> enableBot(String botUserId) {
    return doApiPost(getBotsRoute(botUserId) + "/enable", null, Bot.class);
  }

  @Override
  public ApiResponse<Bot> assignBotToUser(String botUserId, String ownerUserId) {
    return doApiPost(String.format("%s/assign/%s", getBotsRoute(botUserId), ownerUserId), null,
        Bot.class);
  }

}
