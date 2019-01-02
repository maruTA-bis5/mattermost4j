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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import net.bis5.mattermost.client4.api.AuditsApi;
import net.bis5.mattermost.client4.api.AuthenticationApi;
import net.bis5.mattermost.client4.api.BrandApi;
import net.bis5.mattermost.client4.api.ChannelApi;
import net.bis5.mattermost.client4.api.ClusterApi;
import net.bis5.mattermost.client4.api.CommandsApi;
import net.bis5.mattermost.client4.api.ComplianceApi;
import net.bis5.mattermost.client4.api.EmojiApi;
import net.bis5.mattermost.client4.api.GeneralApi;
import net.bis5.mattermost.client4.api.LdapApi;
import net.bis5.mattermost.client4.api.LogsApi;
import net.bis5.mattermost.client4.api.OAuthApi;
import net.bis5.mattermost.client4.api.PostApi;
import net.bis5.mattermost.client4.api.PreferencesApi;
import net.bis5.mattermost.client4.api.ReactionApi;
import net.bis5.mattermost.client4.api.SamlApi;
import net.bis5.mattermost.client4.api.StatusApi;
import net.bis5.mattermost.client4.api.TeamApi;
import net.bis5.mattermost.client4.api.UserApi;
import net.bis5.mattermost.client4.api.WebhookApi;
import net.bis5.mattermost.client4.api.WebrtcApi;
import net.bis5.mattermost.client4.model.AddChannelMemberRequest;
import net.bis5.mattermost.client4.model.AttachDeviceIdRequest;
import net.bis5.mattermost.client4.model.CheckUserMfaRequest;
import net.bis5.mattermost.client4.model.DeauthorizeOAuthAppRequest;
import net.bis5.mattermost.client4.model.LoginRequest;
import net.bis5.mattermost.client4.model.ResetPasswordRequest;
import net.bis5.mattermost.client4.model.RevokeSessionRequest;
import net.bis5.mattermost.client4.model.SearchPostsRequest;
import net.bis5.mattermost.client4.model.SendPasswordResetEmailRequest;
import net.bis5.mattermost.client4.model.SendVerificationEmailRequest;
import net.bis5.mattermost.client4.model.SwitchAccountTypeResult;
import net.bis5.mattermost.client4.model.UpdateRolesRequest;
import net.bis5.mattermost.client4.model.UpdateUserActiveRequest;
import net.bis5.mattermost.client4.model.UpdateUserMfaRequest;
import net.bis5.mattermost.client4.model.UpdateUserPasswordRequest;
import net.bis5.mattermost.client4.model.VerifyUserEmailRequest;
import net.bis5.mattermost.jersey.provider.MattermostModelMapperProvider;
import net.bis5.mattermost.model.Audits;
import net.bis5.mattermost.model.AuthorizeRequest;
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
import net.bis5.mattermost.model.IncomingWebhook;
import net.bis5.mattermost.model.IncomingWebhookList;
import net.bis5.mattermost.model.OAuthApp;
import net.bis5.mattermost.model.OutgoingWebhook;
import net.bis5.mattermost.model.OutgoingWebhookList;
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.model.PostList;
import net.bis5.mattermost.model.PostPatch;
import net.bis5.mattermost.model.Preference;
import net.bis5.mattermost.model.PreferenceCategory;
import net.bis5.mattermost.model.Preferences;
import net.bis5.mattermost.model.Reaction;
import net.bis5.mattermost.model.Role;
import net.bis5.mattermost.model.SamlCertificateStatus;
import net.bis5.mattermost.model.SessionList;
import net.bis5.mattermost.model.Status;
import net.bis5.mattermost.model.SwitchRequest;
import net.bis5.mattermost.model.Team;
import net.bis5.mattermost.model.TeamExists;
import net.bis5.mattermost.model.TeamList;
import net.bis5.mattermost.model.TeamMember;
import net.bis5.mattermost.model.TeamMemberList;
import net.bis5.mattermost.model.TeamPatch;
import net.bis5.mattermost.model.TeamSearch;
import net.bis5.mattermost.model.TeamStats;
import net.bis5.mattermost.model.TeamUnread;
import net.bis5.mattermost.model.TeamUnreadList;
import net.bis5.mattermost.model.User;
import net.bis5.mattermost.model.UserAutocomplete;
import net.bis5.mattermost.model.UserList;
import net.bis5.mattermost.model.UserPatch;
import net.bis5.mattermost.model.UserSearch;
import net.bis5.mattermost.model.WebrtcInfoResponse;
import net.bis5.mattermost.model.license.MfaSecret;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.logging.LoggingFeature.Verbosity;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

/**
 * Mattermost API Version4 Client default implementation.
 * 
 * @author Maruyama Takayuki
 * @since 2017/06/10
 */
public class MattermostClient
    implements AutoCloseable, AuditsApi, AuthenticationApi, BrandApi, ChannelApi, ClusterApi,
    CommandsApi, ComplianceApi, EmojiApi, GeneralApi, LdapApi, LogsApi, OAuthApi, PostApi,
    PreferencesApi, ReactionApi, SamlApi, StatusApi, TeamApi, UserApi, WebhookApi, WebrtcApi {

  protected static final String API_URL_SUFFIX = "/api/v4";
  private final String url;
  private final String apiUrl;
  private String authToken;
  private AuthType authType;
  private final Level clientLogLevel;
  private final Client httpClient;

  protected Client buildClient() {
    ClientBuilder builder = ClientBuilder.newBuilder().register(MattermostModelMapperProvider.class)
        .register(JacksonFeature.class).register(MultiPartFeature.class)
        // needs for PUT request with null entity
        // (/commands/{command_id}/regen_token)
        .property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
    if (clientLogLevel != null) {
      builder.register(new LoggingFeature(Logger.getLogger(getClass().getName()), clientLogLevel,
          Verbosity.PAYLOAD_ANY, 1000));
    }
    return builder.build();
  }

  @Override
  public void close() throws Exception {
    httpClient.close();
  }

  public MattermostClient(String url) {
    this(url, null);
  }

  /**
   * Create new MattermosClient instance.
   */
  public MattermostClient(String url, Level logLevel) {
    this.url = url;
    this.apiUrl = url + API_URL_SUFFIX;
    this.clientLogLevel = logLevel;
    this.httpClient = buildClient();
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

  public String getTeamImportRoute(String teamId) {
    return getTeamRoute(teamId) + "/import";
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

  public String getSamlRoute() {
    return "/saml";
  }

  public String getLdapRoute() {
    return "/ldap";
  }

  public String getBrandRoute() {
    return "/brand";
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

  public String getReactionsRoute() {
    return "/reactions";
  }

  public String getOAuthAppsRoute() {
    return "/oauth/apps";
  }

  public String getOAuthAppRoute(String appId) {
    return String.format("/oauth/apps/%s", StringUtils.stripToEmpty(appId));
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

  protected ApiResponse<Void> doApiPostMultiPart(String url, MultiPart multiPart) {
    return doApiPostMultiPart(url, multiPart, Void.class);
  }

  protected <T> ApiResponse<T> doApiPostMultiPart(String url, MultiPart multiPart,
      Class<T> responseType) {
    return ApiResponse.of(httpClient.target(apiUrl + url).request(MediaType.APPLICATION_JSON_TYPE)
        .header(HEADER_AUTH, getAuthority())
        .method(HttpMethod.POST, Entity.entity(multiPart, multiPart.getMediaType())), responseType);
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

  protected static final String HEADER_ETAG_CLIENT = "If-None-Match";
  protected static final String HEADER_AUTH = "Authorization";

  // TODO upload api

  // Authentication Section

  /**
   * authenticates a user by user id and password.
   */
  @Override
  public User loginById(String id, String password) {
    return login(LoginRequest.builder().id(id).password(password).build());
  }

  protected ApiResponse<User> onLogin(ApiResponse<Void> loginResponse) {
    authToken = loginResponse.getRawResponse().getHeaderString(HEADER_TOKEN);
    authType = AuthType.BEARER;
    return ApiResponse.of(loginResponse.getRawResponse(), User.class);
  }

  private static final String HEADER_TOKEN = "token";

  protected User login(LoginRequest param) {
    return onLogin(doApiPost("/users/login", param)).readEntity();
  }


  /**
   * authenticates a user by login id, which can be username, email, or some sort of SSO identifier
   * based on server configuration, and a password.
   */
  @Override
  public User login(String loginId, String password) {
    return login(LoginRequest.builder().loginId(loginId).password(password).build());
  }


  /**
   * authenticates a user by LDAP id and password.
   */
  @Override
  public User loginByLdap(String loginId, String password) {
    return login(LoginRequest.builder().loginId(loginId).password(password).ldapOnly(true).build());
  }

  /**
   * authenticates a user by login id (username, email or some sort of SSO identifier based on
   * configuration), password and attaches a device id to the session.
   */
  @Override
  public User loginWithDevice(String loginId, String password, String deviceId) {
    return login(
        LoginRequest.builder().loginId(loginId).password(password).deviceId(deviceId).build());
  }


  /**
   * terminates the current user's session.
   * 
   * @return
   */
  @Override
  public ApiResponse<Boolean> logout() {
    return onLogout(doApiPost("/users/logout", ""));
  }

  protected ApiResponse<Boolean> onLogout(ApiResponse<Void> logoutResponse) {
    authToken = null;
    authType = AuthType.BEARER;

    return logoutResponse.checkStatusOk();
  }

  /**
   * changes a user's login type from one type to another.
   * 
   * @return
   */
  @Override
  public ApiResponse<SwitchAccountTypeResult> switchAccountType(SwitchRequest switchRequest) {
    return doApiPost(getUsersRoute() + "/login/switch", switchRequest,
        SwitchAccountTypeResult.class);
  }

  // User Section

  /**
   * creates a user in the system based on the provided user object.
   */
  @Override
  public ApiResponse<User> createUser(User user) {
    return doApiPost(getUsersRoute(), user, User.class);
  }

  /**
   * returns the logged in user.
   */
  @Override
  public ApiResponse<User> getMe(String etag) {
    return doApiGet(getUserRoute(ME), etag, User.class);
  }

  private static final String ME = "me";

  /**
   * returns a user based on the provided user id string.
   */
  @Override
  public ApiResponse<User> getUser(String userId, String etag) {
    return doApiGet(getUserRoute(userId), etag, User.class);
  }

  /**
   * returns a user based pn the provided user name string.
   */
  @Override
  public ApiResponse<User> getUserByUsername(String userName, String etag) {
    return doApiGet(getUserByUsernameRoute(userName), etag, User.class);
  }

  /**
   * returns a user based on the provided user email string.
   */
  @Override
  public ApiResponse<User> getUserByEmail(String email, String etag) {
    return doApiGet(getUserByEmailRoute(email), etag, User.class);
  }

  /**
   * returns the users on a team based on search term.
   */
  @Override
  public ApiResponse<UserAutocomplete> autocompleteUsersInTeam(String teamId, String username,
      String etag) {
    String query = new QueryBuilder().set("in_team", teamId).set("name", username).toString();
    return doApiGet(getUsersRoute() + "/autocomplete" + query, etag, UserAutocomplete.class);
  }

  /**
   * returns the users in a channel based on search term.
   */
  @Override
  public ApiResponse<UserAutocomplete> autocompleteUsersInChannel(String teamId, String channelId,
      String username, String etag) {
    String query = new QueryBuilder().set("in_team", teamId).set("in_channel", channelId)
        .set("name", username).toString();
    return doApiGet(getUsersRoute() + "/autocomplete" + query, etag, UserAutocomplete.class);
  }

  /**
   * returns the users in the system based on search term.
   */
  @Override
  public ApiResponse<UserAutocomplete> autocompleteUsers(String username, String etag) {
    String query = new QueryBuilder().set("name", username).toString();
    return doApiGet(getUsersRoute() + "/autocomplete" + query, etag, UserAutocomplete.class);
  }

  /**
   * gets user's profile image. Must be logged in or be a system administrator.
   */
  @Override
  public ApiResponse<byte[]> getProfileImage(String userId, String etag) {
    // XXX byte[]で返すの微妙・・・というかreadEntityでこけない?
    return doApiGet(getUserRoute(userId) + "/image", etag, byte[].class);
  }

  private GenericType<Map<String, String>> stringMapType() {
    return new GenericType<Map<String, String>>() {};
  }

  private <T> GenericType<List<T>> listType() {
    return new GenericType<List<T>>() {};
  }

  /**
   * returns a page of users on the system. Page counting starts at 0.
   */
  @Override
  public ApiResponse<UserList> getUsers(Pager pager, String etag) {
    return doApiGet(getUsersRoute() + pager.toQuery(), etag, UserList.class);
  }

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  @Override
  public ApiResponse<UserList> getUsersInTeam(String teamId, Pager pager, String etag) {
    String query = new QueryBuilder().set("in_team", teamId).toString();
    return doApiGet(getUsersRoute() + query + pager.toQuery(false), etag, UserList.class);
  }

  /**
   * returns a page of users who are not in a team. Page counting starts at 0.
   */
  @Override
  public ApiResponse<UserList> getUsersNotInTeam(String teamId, Pager pager, String etag) {
    String query = new QueryBuilder().set("not_in_team", teamId).toString();
    return doApiGet(getUsersRoute() + query + pager.toQuery(false), etag, UserList.class);
  }

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  @Override
  public ApiResponse<UserList> getUsersInChannel(String channelId, Pager pager, String etag) {
    String query = new QueryBuilder().set("in_channel", channelId).toString();
    return doApiGet(getUsersRoute() + query + pager.toQuery(false), etag, UserList.class);
  }

  /**
   * returns a page of users on a team. Page counting starts at 0.
   */
  @Override
  public ApiResponse<UserList> getUsersNotInChannel(String teamId, String channelId, Pager pager,
      String etag) {
    String query =
        new QueryBuilder().set("in_team", teamId).set("not_in_channel", channelId).toString();
    return doApiGet(getUsersRoute() + query + pager.toQuery(false), etag, UserList.class);
  }

  /**
   * returns a page of users on the system that aren't on any teams. Page counting starts at 0.
   */
  @Override
  public ApiResponse<UserList> getUsersWithoutTeam(Pager pager, String etag) {
    String query = new QueryBuilder().set("without_team", 1).toString();
    return doApiGet(getUsersRoute() + query + pager.toQuery(false), etag, UserList.class);
  }

  /**
   * returns a list of users based on the provided user ids.
   */
  @Override
  public ApiResponse<UserList> getUsersByIds(String... userIds) {
    return doApiPost(getUsersRoute() + "/ids", userIds, UserList.class);
  }

  /**
   * returns a list of users based on the provided usernames.
   */
  @Override
  public ApiResponse<UserList> getUsersByUsernames(String... usernames) {
    return doApiPost(getUsersRoute() + "/usernames", usernames, UserList.class);
  }

  /**
   * returns a list of users based on some search criteria.
   */
  @Override
  public ApiResponse<UserList> searchUsers(UserSearch search) {
    return doApiPost(getUsersRoute() + "/search", search, UserList.class);
  }

  /**
   * updates a user in the system based on the provided user object.
   */
  @Override
  public ApiResponse<User> updateUser(User user) {
    return doApiPut(getUserRoute(user.getId()), user, User.class);
  }

  /**
   * partially updates a user in the system. Any missing fields are not updated.
   */
  @Override
  public ApiResponse<User> patchUser(String userId, UserPatch patch) {
    return doApiPut(getUserRoute(userId) + "/patch", patch, User.class);
  }

  /**
   * activates multi-factor authentication for a user if activate is true and a valid code is
   * provided. If activate is false, then code is not required and multi-factor authentication is
   * disabled for the user.
   */
  @Override
  public ApiResponse<Boolean> updateUserMfa(String userId, String code, boolean activate) {
    UpdateUserMfaRequest request =
        UpdateUserMfaRequest.builder().activate(activate).code(code).build();
    return doApiPut(getUserRoute(userId) + "/mfa", request).checkStatusOk();
  }

  /**
   * checks whether a user has MFA active on their account or not based on the provided login id.
   */
  @Override
  public boolean checkUserMfa(String loginId) {
    CheckUserMfaRequest request = CheckUserMfaRequest.builder().loginId(loginId).build();
    return Boolean.valueOf(doApiPost(getUsersRoute() + "/mfa", request, stringMapType())
        .readEntity().getOrDefault("mfa_required", "false"));
  }

  /**
   * will generate a new MFA secret for a user and return it as a string and as a base64 encoded
   * image QR code.
   */
  @Override
  public ApiResponse<MfaSecret> generateMfaSecret(String userId) {
    return doApiPost(getUserRoute(userId) + "/mfa/generate", null, MfaSecret.class);
  }

  /**
   * updates a user's password. Must be logged in as the user or be a system administrator.
   */
  @Override
  public ApiResponse<Boolean> updateUserPassword(String userId, String currentPassword,
      String newPassword) {
    UpdateUserPasswordRequest request = UpdateUserPasswordRequest.builder()
        .currentPassword(currentPassword).newPassword(newPassword).build();
    return doApiPut(getUserRoute(userId) + "/password", request).checkStatusOk();
  }

  /**
   * updates a user's roles in the system. A user can have "system_user" and "system_admin" roles.
   */
  @Override
  public ApiResponse<Boolean> updateUserRoles(String userId, Role... roles) {
    UpdateRolesRequest request = new UpdateRolesRequest(roles);
    return doApiPut(getUserRoute(userId) + "/roles", request).checkStatusOk();
  }

  /**
   * updates status of a user whether active or not.
   */
  @Override
  public ApiResponse<Boolean> updateUserActive(String userId, boolean active) {
    UpdateUserActiveRequest request = UpdateUserActiveRequest.builder().active(active).build();
    return doApiPut(getUserRoute(userId) + "/active", request).checkStatusOk();
  }

  /**
   * deactivates a user in the system based on the provided user id string.
   */
  @Override
  public ApiResponse<Boolean> deleteUser(String userId) {
    return doApiDelete(getUserRoute(userId)).checkStatusOk();
  }

  /**
   * will send a link for password resetting to a user with the provided email.
   */
  @Override
  public ApiResponse<Boolean> sendPasswordResetEmail(String email) {
    SendPasswordResetEmailRequest request =
        SendPasswordResetEmailRequest.builder().email(email).build();
    return doApiPost(getUsersRoute() + "/password/reset/send", request).checkStatusOk();
  }

  /**
   * uses a recovery code to update reset a user's password.
   */
  @Override
  public ApiResponse<Boolean> resetPassword(String token, String newPassword) {
    ResetPasswordRequest request =
        ResetPasswordRequest.builder().token(token).newPassword(newPassword).build();
    return doApiPost(getUsersRoute() + "/password/reset", request).checkStatusOk();
  }

  /**
   * returns a list of sessions based on the provided user id string.
   */
  @Override
  public ApiResponse<SessionList> getSessions(String userId, String etag) {
    return doApiGet(getUserRoute(userId) + "/sessions", etag, SessionList.class);
  }

  /**
   * revokes a user session based on the provided user id and session id strings.
   */
  @Override
  public ApiResponse<Boolean> revokeSession(String userId, String sessionId) {
    RevokeSessionRequest request = RevokeSessionRequest.builder().sessionId(sessionId).build();
    return doApiPost(getUserRoute(userId) + "/sessions/revoke", request).checkStatusOk();
  }

  /**
   * attaches a mobile device ID to the current session.
   */
  @Override
  public ApiResponse<Boolean> attachDeviceId(String deviceId) {
    AttachDeviceIdRequest request = AttachDeviceIdRequest.builder().deviceId(deviceId).build();
    return doApiPut(getUsersRoute() + "/sessions/device", request).checkStatusOk();
  }

  /**
   * will return a list with TeamUnread objects that contain the amount of unread messages and
   * mentions the current user has for the teams it belongs to. An optional team ID can be set to
   * exclude that team from the results. Must be authenticated.
   */
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

  /**
   * returns a list of audit based on the provided user id string.
   */
  @Override
  public ApiResponse<Audits> getUserAudits(String userId, Pager pager, String etag) {
    return doApiGet(getUserRoute(userId) + "/audits" + pager.toQuery(), etag, Audits.class);
  }

  /**
   * will verify a user's email using the supplied token.
   */
  @Override
  public ApiResponse<Boolean> verifyUserEmail(String token) {
    VerifyUserEmailRequest request = VerifyUserEmailRequest.builder().token(token).build();
    return doApiPost(getUsersRoute() + "/email/verify", request).checkStatusOk();
  }

  /**
   * will send an email to the user with the provided email addresses, if that user exists. The
   * email will contain a link that can be used to verify the user's email address.
   */
  @Override
  public ApiResponse<Boolean> sendVerificationEmail(String email) {
    SendVerificationEmailRequest request =
        SendVerificationEmailRequest.builder().email(email).build();
    return doApiPost(getUsersRoute() + "/email/verify/send", request).checkStatusOk();
  }

  /**
   * sets profile image of the user.
   */
  @Override
  public ApiResponse<Boolean> setProfileImage(String userId, Path imageFilePath) {
    MultiPart multiPart = new MultiPart();
    multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

    FileDataBodyPart body = new FileDataBodyPart("image", imageFilePath.toFile());
    multiPart.bodyPart(body);

    return doApiPostMultiPart(getUserRoute(userId) + "/image", multiPart).checkStatusOk();
  }

  // Team Section

  /**
   * creates a team in the system based on the provided team object.
   */
  @Override
  public ApiResponse<Team> createTeam(Team team) {
    return doApiPost(getTeamsRoute(), team, Team.class);
  }

  /**
   * returns a team based on the provided team id string.
   */
  @Override
  public ApiResponse<Team> getTeam(String teamId, String etag) {
    return doApiGet(getTeamRoute(teamId), etag, Team.class);
  }

  /**
   * returns all teams based on permssions.
   */
  @Override
  public ApiResponse<TeamList> getAllTeams(Pager pager, String etag) {
    return doApiGet(getTeamsRoute() + pager.toQuery(), etag, TeamList.class);
  }

  /**
   * returns a team based on the provided team name string.
   */
  @Override
  public ApiResponse<Team> getTeamByName(String name, String etag) {
    return doApiGet(getTeamByNameRoute(name), etag, Team.class);
  }

  /**
   * returns teams matching the provided search term.
   */
  @Override
  public ApiResponse<TeamList> searchTeams(TeamSearch search) {
    return doApiPost(getTeamsRoute() + "/search", search, TeamList.class);
  }

  /**
   * returns true or false if the team exist or not.
   */
  @Override
  public ApiResponse<TeamExists> teamExists(String name, String etag) {
    return doApiGet(getTeamByNameRoute(name) + "/exists", etag, TeamExists.class);
  }

  /**
   * returns a list of teams a user is on. Must be logged in as the user or be a system
   * administrator.
   */
  @Override
  public ApiResponse<TeamList> getTeamsForUser(String userId, String etag) {
    return doApiGet(getUserRoute(userId) + "/teams", etag, TeamList.class);
  }

  /**
   * returns a team member based on the provided team and user id strings.
   */
  @Override
  public ApiResponse<TeamMember> getTeamMember(String teamId, String userId, String etag) {
    return doApiGet(getTeamMemberRoute(teamId, userId), etag, TeamMember.class);
  }

  /**
   * will update the roles on a team for a user.
   */
  @Override
  public ApiResponse<Boolean> updateTeamMemberRoles(String teamId, String userId,
      Role... newRoles) {
    UpdateRolesRequest request = new UpdateRolesRequest(newRoles);
    return doApiPut(getTeamMemberRoute(teamId, userId) + "/roles", request).checkStatusOk();
  }

  /**
   * will update a team.
   */
  @Override
  public ApiResponse<Team> updateTeam(Team team) {
    return doApiPut(getTeamRoute(team.getId()), team, Team.class);
  }

  /**
   * partially updates a team. Any missing fields are not updated.
   */
  @Override
  public ApiResponse<Team> patchTeam(String teamId, TeamPatch patch) {
    return doApiPut(getTeamRoute(teamId) + "/patch", patch, Team.class);
  }

  /**
   * deletes the team softly (archive only, not permanent delete).
   * 
   * @see #deleteTeam(String, boolean)
   */
  @Override
  public ApiResponse<Boolean> deleteTeam(String teamId) {
    return doApiDelete(getTeamRoute(teamId)).checkStatusOk();
  }

  /**
   * deletes the team.
   * 
   * @param permanent {@code true}: Permanently delete the team, to be used for compliance reasons
   *        only.
   * @see #deleteTeam(String)
   * @return
   */
  @Override
  public ApiResponse<Boolean> deleteTeam(String teamId, boolean permanent) {
    String query = new QueryBuilder().set("permanent", Boolean.toString(permanent)).toString();
    return doApiDelete(getTeamRoute(teamId) + query).checkStatusOk();
  }

  /**
   * returns team members based on the provided team id string.
   */
  @Override
  public ApiResponse<TeamMemberList> getTeamMembers(String teamId, Pager pager, String etag) {
    return doApiGet(getTeamMembersRoute(teamId) + pager.toQuery(), etag, TeamMemberList.class);
  }

  /**
   * returns the team member for a user.
   */
  @Override
  public ApiResponse<TeamMemberList> getTeamMembersForUser(String userId, String etag) {
    return doApiGet(getUserRoute(userId) + "/teams/members", etag, TeamMemberList.class);
  }

  /**
   * will return an array of team members based on the team id and a list of user ids provided. Must
   * be authenticated.
   */
  @Override
  public ApiResponse<TeamMemberList> getTeamMembersByIds(String teamId, String... userIds) {
    String url = String.format("/teams/%s/members/ids", teamId);
    return doApiPost(url, userIds, TeamMemberList.class);
  }

  /**
   * add user to a team and return a team member.
   */
  @Override
  public ApiResponse<TeamMember> addTeamMember(TeamMember teamMemberToAdd) {
    return doApiPost(getTeamMembersRoute(teamMemberToAdd.getTeamId()), teamMemberToAdd,
        TeamMember.class);
  }

  /**
   * Should not use this API because server api changed.
   * 
   * @deprecated API Change on Mattermost 4.0
   */
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

  /**
   * adds user to a team and return a team member.
   * 
   * @since Mattermost 4.0
   */
  @Override
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

  /**
   * adds a number of users to a team and returns the team members.
   */
  @Override
  public ApiResponse<TeamMemberList> addTeamMembers(String teamId, String... userIds) {
    List<TeamMember> members =
        Arrays.stream(userIds).map(u -> new TeamMember(teamId, u)).collect(Collectors.toList());

    return doApiPost(getTeamMembersRoute(teamId) + "/batch", members, TeamMemberList.class);
  }

  /**
   * will remove a user from a team.
   */
  @Override
  public ApiResponse<Boolean> removeTeamMember(String teamId, String userId) {
    return doApiDelete(getTeamMemberRoute(teamId, userId)).checkStatusOk();
  }

  /**
   * returns a team stats based on the team id string. Must be authenticated.
   */
  @Override
  public ApiResponse<TeamStats> getTeamStats(String teamId, String etag) {
    return doApiGet(getTeamStatsRoute(teamId), etag, TeamStats.class);
  }

  /**
   * will return a TeamUnread object that contains the amount of unread messages and mentions the
   * user has for the specified team. Must be authenticated.
   */
  @Override
  public ApiResponse<TeamUnread> getTeamUnread(String teamId, String userId) {
    return doApiGet(getUserRoute(userId) + getTeamRoute(teamId) + "/unread", null,
        TeamUnread.class);
  }

  /**
   * will import an exported team from other app into a existing team.
   */
  @Override
  public ApiResponse<byte[]> importTeam(byte[] data, int filesize, String importFrom,
      String fileName, String teamId) {
    // FIXME
    throw new UnsupportedOperationException();
  }

  /**
   * invite users by email to the team.
   */
  @Override
  public ApiResponse<Boolean> inviteUsersToTeam(String teamId, Collection<String> userEmails) {
    return doApiPost(getTeamRoute(teamId) + "/invite/email", userEmails).checkStatusOk();
  }

  // Channel Section

  /**
   * creates a channel based on the provided channel object.
   */
  @Override
  public ApiResponse<Channel> createChannel(Channel channel) {
    return doApiPost(getChannelsRoute(), channel, Channel.class);
  }

  /**
   * update a channel based on the provided channel object.
   */
  @Override
  public ApiResponse<Channel> updateChannel(Channel channel) {
    return doApiPut(getChannelRoute(channel.getId()), channel, Channel.class);
  }

  /**
   * partially updates a channel. Any missing fields are not updated.
   */
  @Override
  public ApiResponse<Channel> patchChannel(String channelId, ChannelPatch patch) {
    return doApiPut(getChannelRoute(channelId) + "/patch", patch, Channel.class);
  }

  /**
   * creates a direct message channel based on the two user ids provided.
   */
  @Override
  public ApiResponse<Channel> createDirectChannel(String userId1, String userId2) {
    return doApiPost(getChannelsRoute() + "/direct", Arrays.asList(userId1, userId2),
        Channel.class);
  }

  /**
   * creates a group message channel based on userIds provided.
   */
  @Override
  public ApiResponse<Channel> createGroupChannel(String... userIds) {
    return doApiPost(getChannelsRoute() + "/group", userIds, Channel.class);
  }

  /**
   * returns a channel based on the provided channel id string.
   */
  @Override
  public ApiResponse<Channel> getChannel(String channelId, String etag) {
    return doApiGet(getChannelRoute(channelId), etag, Channel.class);
  }

  /**
   * returns statistics for a channel.
   */
  @Override
  public ApiResponse<ChannelStats> getChannelStats(String channelId, String etag) {
    return doApiGet(getChannelRoute(channelId) + "/stats", etag, ChannelStats.class);
  }

  /**
   * gets a list of pinned posts.
   */
  @Override
  public ApiResponse<PostList> getPinnedPosts(String channelId, String etag) {
    return doApiGet(getChannelRoute(channelId) + "/pinned", etag, PostList.class);
  }

  /**
   * returns a list of public channels based on the provided team id string.
   */
  @Override
  public ApiResponse<ChannelList> getPublicChannelsForTeam(String teamId, Pager pager,
      String etag) {
    return doApiGet(getChannelsForTeamRoute(teamId) + pager.toQuery(), etag, ChannelList.class);
  }

  /**
   * returns a list of public channeld based on provided team id string.
   */
  @Override
  public ApiResponse<ChannelList> getPublicChannelsByIdsForTeam(String teamId,
      String... channelIds) {
    return doApiPost(getChannelsForTeamRoute(teamId) + "/ids", channelIds, ChannelList.class);
  }

  /**
   * returns a list channels of on a team for user.
   */
  @Override
  public ApiResponse<ChannelList> getChannelsForTeamForUser(String teamId, String userId,
      String etag) {
    return doApiGet(getUserRoute(userId) + getTeamRoute(teamId) + "/channels", etag,
        ChannelList.class);
  }

  /**
   * returns the channels on a team matching the provided search term.
   */
  @Override
  public ApiResponse<ChannelList> searchChannels(String teamId, ChannelSearch search) {
    return doApiPost(getChannelsForTeamRoute(teamId) + "/search", search, ChannelList.class);
  }

  /**
   * deletes channel based on the provided channel id string.
   */
  @Override
  public ApiResponse<Boolean> deleteChannel(String channelId) {
    return doApiDelete(getChannelRoute(channelId)).checkStatusOk();
  }

  /**
   * returns a channel based on the provided channel name and team id strings.
   */
  @Override
  public ApiResponse<Channel> getChannelByName(String channelName, String teamId, String etag) {
    return doApiGet(getChannelByNameRoute(channelName, teamId), etag, Channel.class);
  }

  /**
   * returns a channel based on the provided channel name and team name strings.
   */
  @Override
  public ApiResponse<Channel> getChannelByNameForTeamName(String channelName, String teamName,
      String etag) {
    return doApiGet(getChannelByNameForTeamNameRoute(channelName, teamName), etag, Channel.class);
  }

  /**
   * gets a page of channel members.
   */
  @Override
  public ApiResponse<ChannelMembers> getChannelMembers(String channelId, Pager pager, String etag) {
    return doApiGet(getChannelMembersRoute(channelId) + pager.toQuery(), etag,
        ChannelMembers.class);
  }

  /**
   * gets the channel members in a channel for a list of user ids.
   */
  @Override
  public ApiResponse<ChannelMembers> getChannelMembersByIds(String channelId, String... userIds) {
    return doApiPost(getChannelMembersRoute(channelId) + "/ids", userIds, ChannelMembers.class);
  }

  /**
   * gets a channel memner.
   */
  @Override
  public ApiResponse<ChannelMember> getChannelMember(String channelId, String userId, String etag) {
    return doApiGet(getChannelMemberRoute(channelId, userId), etag, ChannelMember.class);
  }

  /**
   * gets all the channel members for a user on a team.
   */
  @Override
  public ApiResponse<ChannelMembers> getChannelMembersForUser(String userId, String teamId,
      String etag) {
    return doApiGet(getUserRoute(userId) + String.format("/teams/%s/channels/members", teamId),
        etag, ChannelMembers.class);
  }

  /**
   * performs a view action for a user. synonymous with switching channels or marking channels as
   * read by a user.
   */
  @Override
  public ApiResponse<ChannelViewResponse> viewChannel(String userId, ChannelView view) {
    String url = String.format(getChannelsRoute() + "/members/%s/view", userId);
    return doApiPost(url, view, ChannelViewResponse.class);
  }

  /**
   * will return a ChannelUnread object that contains the number ofo unread messages and mentions
   * for a user.
   */
  @Override
  public ApiResponse<ChannelUnread> getChannelUnread(String channelId, String userId) {
    return doApiGet(getUserRoute(userId) + getChannelRoute(channelId) + "/unread", null,
        ChannelUnread.class);
  }

  /**
   * will update the roles on a channel for a user.
   */
  @Override
  public ApiResponse<Boolean> updateChannelRoles(String channelId, String userId, Role... roles) {
    UpdateRolesRequest request = new UpdateRolesRequest(roles);
    return doApiPut(getChannelMemberRoute(channelId, userId) + "/roles", request).checkStatusOk();
  }

  /**
   * will update the notification properties on a channel for a user.
   */
  @Override
  public ApiResponse<Boolean> updateChannelNotifyProps(String channelId, String userId,
      Map<String, String> props) {
    return doApiPut(getChannelMemberRoute(channelId, userId) + "/notify_props", props)
        .checkStatusOk();
  }

  /**
   * adds user to channel and return a channel memner.
   */
  @Override
  public ApiResponse<ChannelMember> addChannelMember(String channelId, String userId) {
    AddChannelMemberRequest request = AddChannelMemberRequest.builder().userId(userId).build();
    return doApiPost(getChannelMembersRoute(channelId), request, ChannelMember.class);
  }

  /**
   * will delete the channel member object for a user, effectively removing the user from a channel.
   */
  @Override
  public ApiResponse<Boolean> removeUserFromChannel(String channelId, String userId) {
    return doApiDelete(getChannelMemberRoute(channelId, userId)).checkStatusOk();
  }

  @Override
  public ApiResponse<Channel> restoreChannel(String channelId) {
    return doApiPost(getChannelRoute(channelId) + "/restore", null, Channel.class);
  }

  // Post Section

  /**
   * creates a post based on the provided post object.
   */
  @Override
  public ApiResponse<Post> createPost(Post post) {
    return doApiPost(getPostsRoute(), post, Post.class);
  }

  /**
   * updates a post based on the provided post object.
   */
  @Override
  public ApiResponse<Post> updatePost(String postId, Post post) {
    return doApiPut(getPostRoute(postId), post, Post.class);
  }

  /**
   * partially updates a post. Any missing fields are not updated.
   */
  @Override
  public ApiResponse<Post> patchPost(String postId, PostPatch patch) {
    return doApiPut(getPostRoute(postId) + "/patch", patch, Post.class);
  }

  /**
   * pin a post based on proviced post id string.
   */
  @Override
  public ApiResponse<Boolean> pinPost(String postId) {
    return doApiPost(getPostRoute(postId) + "/pin", null).checkStatusOk();
  }

  /**
   * unpin a post based on provided post id string.
   */
  @Override
  public ApiResponse<Boolean> unpinPost(String postId) {
    return doApiPost(getPostRoute(postId) + "/unpin", null).checkStatusOk();
  }

  /**
   * gets a single post.
   */
  @Override
  public ApiResponse<Post> getPost(String postId, String etag) {
    return doApiGet(getPostRoute(postId), etag, Post.class);
  }

  /**
   * deletes a post from the provided post id string.
   */
  @Override
  public ApiResponse<Boolean> deletePost(String postId) {
    return doApiDelete(getPostRoute(postId)).checkStatusOk();
  }

  /**
   * gets a post with all the other posts in the same thread.
   */
  @Override
  public ApiResponse<PostList> getPostThread(String postId, String etag) {
    return doApiGet(getPostRoute(postId) + "/thread", etag, PostList.class);
  }

  /**
   * gets a page of posts with an array for ordering for a channel.
   */
  @Override
  public ApiResponse<PostList> getPostsForChannel(String channelId, Pager pager, String etag) {
    return doApiGet(getChannelRoute(channelId) + "/posts" + pager.toQuery(), etag, PostList.class);
  }

  /**
   * returns flagges posts of a user based on user id string.
   */
  @Override
  public ApiResponse<PostList> getFlaggedPostsForUser(String userId, Pager pager) {
    return doApiGet(getUserRoute(userId) + "/posts/flagged" + pager.toQuery(), null,
        PostList.class);
  }

  /**
   * returns flagged posts in team of a user based on user id string.
   */
  @Override
  public ApiResponse<PostList> getFlaggedPostsForUserInTeam(String userId, String teamId,
      Pager pager) {
    // TODO teamId length validation

    return doApiGet(getUserRoute(userId) + "/posts/flagged" + pager.toQuery(), null,
        PostList.class);
  }

  /**
   * returns flagged posts in channel of a user based on user id string.
   */
  @Override
  public ApiResponse<PostList> getFlaggedPostsForUserInChannel(String userId, String channelId,
      Pager pager) {
    // TODO channelId length validation
    String query = new QueryBuilder().set("in_channel", channelId).toString();
    return doApiGet(getUserRoute(userId) + "/posts/flagged" + query + pager.toQuery(false), null,
        PostList.class);
  }

  /**
   * gets posts created after a specified time as Unix time in milliseconds.
   */
  @Override
  public ApiResponse<PostList> getPostsSince(String channelId, long time) {
    String query = String.format("?since=%d", time);
    return doApiGet(getChannelRoute(channelId) + "/posts" + query, null, PostList.class);
  }

  /**
   * gets a page of posts that were posted after the post provided.
   */
  @Override
  public ApiResponse<PostList> getPostsAfter(String channelId, String postId, Pager pager,
      String etag) {
    String query = new QueryBuilder().set("after", postId).toString();
    return doApiGet(getChannelRoute(channelId) + "/posts" + query + pager.toQuery(false), etag,
        PostList.class);
  }

  /**
   * gets a page of posts that were posted before the post provided.
   */
  @Override
  public ApiResponse<PostList> getPostsBefore(String channelId, String postId, Pager pager,
      String etag) {
    String query = new QueryBuilder().set("before", postId).toString();
    return doApiGet(getChannelRoute(channelId) + "/posts" + query + pager.toQuery(false), etag,
        PostList.class);
  }

  /**
   * returns any posts with matching term string.
   */
  @Override
  public ApiResponse<PostList> searchPosts(String teamId, String terms, boolean isOrSearch) {
    SearchPostsRequest request =
        SearchPostsRequest.builder().terms(terms).isOrSearch(isOrSearch).build();
    return doApiPost(getTeamRoute(teamId) + "/posts/search", request, PostList.class);
  }

  // TODO File Section

  // General Section

  /**
   * will ping the server and to see if it is up and running.
   * 
   * @return
   */
  @Override
  public ApiResponse<Boolean> getPing() {
    return doApiGet(getSystemRoute() + "/ping", null).checkStatusOk();
  }

  /**
   * will attempt to connect to the configured SMTP server.
   * 
   * @return
   */
  @Override
  public ApiResponse<Boolean> testEmail() {
    return doApiPost(getTestEmailRoute(), null).checkStatusOk();
  }

  /**
   * will retrieve the server config with some sanitized items.
   * 
   * @return
   */
  @Override
  public ApiResponse<Config> getConfig() {
    return doApiGet(getConfigRoute(), null, Config.class);
  }

  /**
   * will reload the server configuration.
   * 
   * @return
   */
  @Override
  public ApiResponse<Boolean> reloadConfig() {
    return doApiPost(getConfigRoute() + "/reload", null).checkStatusOk();
  }

  /**
   * will retrieve the parts of the server configuration needed by the client, formatted in the old
   * format.
   */
  @Override
  public ApiResponse<Map<String, String>> getOldClientConfig(String etag) {
    return doApiGet(getConfigRoute() + "/client?format=old", etag, stringMapType());
  }

  /**
   * will retrieve the parts of the server license needed by the client, formatted in the old
   * format.
   */
  @Override
  public ApiResponse<Map<String, String>> getOldClientLicense(String etag) {
    return doApiGet(getLicenseRoute() + "/client?format=old", etag, stringMapType());
  }

  /**
   * will recycle the connections. Discard current connection and get new one.
   * 
   * @return
   */
  @Override
  public ApiResponse<Boolean> databaseRecycle() {
    return doApiPost(getDatabaseRoute() + "/recycle", null).checkStatusOk();
  }

  /**
   * will purge the cache and can affect the performance while is cleaning.
   * 
   * @return
   */
  @Override
  public ApiResponse<Boolean> invalidateCaches() {
    return doApiPost(getCacheRoute() + "/invalidate", null).checkStatusOk();
  }

  /**
   * will update the server configuration.
   */
  @Override
  public ApiResponse<Config> updateConfig(Config config) {
    return doApiPut(getConfigRoute(), config, Config.class);
  }

  // Webhooks Section

  /**
   * creates an incoming webhook for a channel.
   */
  @Override
  public ApiResponse<IncomingWebhook> createIncomingWebhook(IncomingWebhook hook) {
    return doApiPost(getIncomingWebhooksRoute(), hook, IncomingWebhook.class);
  }

  /**
   * updates an incoming webhook for a channel.
   */
  @Override
  public ApiResponse<IncomingWebhook> updateIncomingWebhook(IncomingWebhook hook) {
    return doApiPut(getIncomingWebhookRoute(hook.getId()), hook, IncomingWebhook.class);
  }

  /**
   * returns a page of incoming webhooks on the system. Page counting starts at 0.
   */
  @Override
  public ApiResponse<IncomingWebhookList> getIncomingWebhooks(Pager pager, String etag) {
    return doApiGet(getIncomingWebhooksRoute() + pager.toQuery(), etag, IncomingWebhookList.class);
  }

  /**
   * returns a page of incoming webhooks for a team. Page counting starts at 0.
   */
  @Override
  public ApiResponse<IncomingWebhookList> getIncomingWebhooksForTeam(String teamId, Pager pager,
      String etag) {
    String query = new QueryBuilder().set("team_id", teamId).toString();
    return doApiGet(getIncomingWebhooksRoute() + query + pager.toQuery(false), etag,
        IncomingWebhookList.class);
  }

  /**
   * returns an Incoming webhook given the hook id.
   */
  @Override
  public ApiResponse<IncomingWebhook> getIncomingWebhook(String hookId, String etag) {
    return doApiGet(getIncomingWebhookRoute(hookId), etag, IncomingWebhook.class);
  }

  /**
   * deletes an Incoming Webhook given the hook id.
   */
  @Override
  public ApiResponse<Boolean> deleteIncomingWebhook(String hookId) {
    return doApiDelete(getIncomingWebhookRoute(hookId)).checkStatusOk();
  }

  /**
   * creates an outgoing webhook for a team or channel.
   */
  @Override
  public ApiResponse<OutgoingWebhook> createOutgoingWebhook(OutgoingWebhook hook) {
    return doApiPost(getOutgoingWebhooksRoute(), hook, OutgoingWebhook.class);
  }

  /**
   * updates an outgoing webhook.
   */
  @Override
  public ApiResponse<OutgoingWebhook> updateOutgoingWebhook(OutgoingWebhook hook) {
    return doApiPut(getOutgoingWebhookRoute(hook.getId()), hook, OutgoingWebhook.class);
  }

  /**
   * returns a page of outgoing webhooks ont eh system. Page counting starts at 0.
   */
  @Override
  public ApiResponse<OutgoingWebhookList> getOutgoingWebhooks(Pager pager, String etag) {
    return doApiGet(getOutgoingWebhooksRoute() + pager.toQuery(), etag, OutgoingWebhookList.class);
  }

  /**
   * outgoing webhooks on the system requested by hook id.
   */
  @Override
  public ApiResponse<OutgoingWebhook> getOutgoingWebhook(String hookId) {
    return doApiGet(getOutgoingWebhookRoute(hookId), null, OutgoingWebhook.class);
  }

  /**
   * returns a page of outgoing webhooks for a channel. Page counting starts at 0.
   */
  @Override
  public ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForChannel(String channelId,
      Pager pager, String etag) {
    String query = new QueryBuilder().set("channel_id", channelId).toString();
    return doApiGet(getOutgoingWebhooksRoute() + query + pager.toQuery(false), etag,
        OutgoingWebhookList.class);
  }

  /**
   * returns a page of outgoing webhooks for a team. Page counting starts at 0.
   */
  @Override
  public ApiResponse<OutgoingWebhookList> getOutgoingWebhooksForTeam(String teamId, Pager pager,
      String etag) {
    String query = new QueryBuilder().set("team_id", teamId).toString();
    return doApiGet(getOutgoingWebhooksRoute() + query + pager.toQuery(false), etag,
        OutgoingWebhookList.class);
  }

  /**
   * regenerate the outgoing webhook token.
   */
  @Override
  public ApiResponse<OutgoingWebhook> regenOutgoingHookToken(String hookId) {
    return doApiPost(getOutgoingWebhookRoute(hookId) + "/regen_token", null, OutgoingWebhook.class);
  }

  /**
   * delete the outgoing webhook on the system requested by hook id.
   */
  @Override
  public ApiResponse<Boolean> deleteOutgoingWebhook(String hookId) {
    return doApiDelete(getOutgoingWebhookRoute(hookId)).checkStatusOk();
  }

  // Preferences Section

  /**
   * returns the user's preferences.
   */
  @Override
  public ApiResponse<Preferences> getPreferences(String userId) {
    return doApiGet(getPreferencesRoute(userId), null, Preferences.class);
  }

  /**
   * saves the user's preferences.
   */
  @Override
  public ApiResponse<Boolean> updatePreferences(String userId, Preferences preferences) {
    return doApiPut(getPreferencesRoute(userId), preferences).checkStatusOk();
  }

  /**
   * deletes the user's preferences.
   */
  @Override
  public ApiResponse<Boolean> deletePreferences(String userId, Preferences preferences) {
    return doApiPost(getPreferencesRoute(userId) + "/delete", preferences).checkStatusOk();
  }

  /**
   * returns the user's preferences from the provided category string.
   */
  @Override
  public ApiResponse<Preferences> getPreferencesByCategory(String userId,
      PreferenceCategory category) {
    String url = String.format(getPreferencesRoute(userId) + "/%s", category.getCode());
    return doApiGet(url, null, Preferences.class);
  }

  /**
   * returns the user's preferences from the provided category and preference name string.
   */
  @Override
  public ApiResponse<Preference> getPreferenceByCategoryAndName(String userId,
      PreferenceCategory category, String preferenceName) {
    String url = String.format(getPreferencesRoute(userId) + "/%s/name/%s", category.getCode(),
        preferenceName);
    return doApiGet(url, null, Preference.class);
  }

  /**
   * returns metadata for the SAML configuration.
   * 
   * @return
   */
  @Override
  public ApiResponse<String> getSamlMetadata() {
    return doApiGet(getSamlRoute() + "/metadata", null, String.class);
  }

  protected Object samlFileToMultipart(Path dataFile, String dataFileName) {
    throw new UnsupportedOperationException("not impl"); // FIXME
  }

  /**
   * will upload an IDP certificate for SAML and set the config to use it.
   */
  @Override
  public boolean uploadSamlIdpCertificate(Path dataFile, String fileName) {
    throw new UnsupportedOperationException("not impl"); // FIXME
  }

  /**
   * will upload a public certificate for SAML and set the config to use it.
   */
  @Override
  public boolean uploadSamlPublicCertificate(Path dataFile, String fileName) {
    throw new UnsupportedOperationException("not impl"); // FIXME
  }

  /**
   * will upload a private key for SAML and set the config to use it.
   */
  @Override
  public boolean uploadSamlPrivateCertificate(Path dataFile, String fileName) {
    throw new UnsupportedOperationException("not impl"); // FIXME
  }

  /**
   * deletes the SAML IDP certificate from the server and updates the config to not use it and
   * disable SAML.
   * 
   * @return
   */
  @Override
  public ApiResponse<Boolean> deleteSamlIdpCertificate() {
    return doApiDelete(getSamlRoute() + "/certificate/idp").checkStatusOk();
  }

  /**
   * deletes the saml IDP certificate from the server and updates the config to not use it and
   * disable SAML.
   * 
   * @return
   */
  @Override
  public ApiResponse<Boolean> deleteSamlPublicCertificate() {
    return doApiDelete(getSamlRoute() + "/certificate/public").checkStatusOk();
  }

  /**
   * deletes the SAML IDP certificate from the server and updates the config to not use it and
   * disable SAML.
   * 
   * @return
   */
  @Override
  public ApiResponse<Boolean> deleteSamlPrivateCertificate() {
    return doApiDelete(getSamlRoute() + "/certificate/private").checkStatusOk();
  }

  /**
   * returns metadata for the SAML configuration.
   * 
   * @return
   */
  @Override
  public ApiResponse<SamlCertificateStatus> getSamlCertificateStatus() {
    return doApiGet(getSamlRoute() + "/certificate/status", null, SamlCertificateStatus.class);
  }

  // Compliance Section

  /**
   * creates a compliance report.
   */
  @Override
  public ApiResponse<Compliance> createComplianceReport(Compliance report) {
    return doApiPost(getComplianceReportsRoute(), report, Compliance.class);
  }

  /**
   * returns list of compliance reports.
   */
  @Override
  public ApiResponse<Compliances> getComplianceReports(Pager pager) {
    return doApiGet(getComplianceReportsRoute() + pager.toQuery(), null, Compliances.class);
  }

  /**
   * returns a compliance report.
   */
  @Override
  public ApiResponse<Compliance> getComplianceReport(String reportId) {
    return doApiGet(getComplianceReportRoute(reportId), null, Compliance.class);
  }

  /**
   * returns a full compliance report as a file.
   */
  @Override
  public ApiResponse<Object> downloadComplianceReport(String reportId) {
    throw new UnsupportedOperationException("not impl"); // FIXME
  }

  // Cluster Section

  @Override
  public ApiResponse<List<ClusterInfo>> getClusterStatus() {
    return doApiGet(getClusterRoute() + "/status", null, listType());
  }

  // LDAP Section

  /**
   * will force a sync with the configured LDAP server.
   * 
   * @return
   */
  @Override
  public ApiResponse<Boolean> syncLdap() {
    return doApiPost(getLdapRoute() + "/sync", null).checkStatusOk();
  }

  /**
   * will attempt to connect to the configured LDAP server and return OK if configured correctly.
   * 
   * @return
   */
  @Override
  public ApiResponse<Boolean> testLdap() {
    return doApiPost(getLdapRoute() + "/test", null).checkStatusOk();
  }

  // Audits Section

  /**
   * returns a list of audits for the whole system.
   */
  @Override
  public ApiResponse<Audits> getAudits(Pager pager, String etag) {
    return doApiGet("/audits" + pager.toQuery(), etag, Audits.class);
  }

  // Brand Section

  /**
   * retrieves the previously uploaded brand image.
   * 
   * @return
   */
  @Override
  public ApiResponse<Object> getBrandImage() {
    throw new UnsupportedOperationException("not impl"); // FIXME
  }

  /**
   * sets the brand image for the system.
   */
  @Override
  public boolean uploadBrandImage(Path dataFile) {
    throw new UnsupportedOperationException("not impl");// FIXME
  }

  // Logs Section

  /**
   * page of logs as a string list.
   */
  @Override
  public ApiResponse<List<String>> getLogs(Pager pager) {
    return doApiGet("/logs" + pager.toQuery(), null, listType());
  }

  /**
   * This method is a convenience Web Service call so clients can log messages into the server-side
   * logs. For example we typically log javascript error messages into the server-side. It returns
   * the log message if the logging was successful.
   */
  @Override
  public ApiResponse<Map<String, String>> postLog(Map<String, String> message) {
    return doApiPost("/logs", message, stringMapType());
  }

  // OAuth Section

  /**
   * will register a new OAuth 2.0 client application with Mattermost acting as an OAuth 2.0 service
   * provider.
   */
  @Override
  public ApiResponse<OAuthApp> createOAuthApp(OAuthApp app) {
    return doApiPost(getOAuthAppsRoute(), app, OAuthApp.class);
  }

  /**
   * gets a page of registered OAuth 2.0 client applications with Mattermost acting as an OAuth 2.0
   * service provider.
   */
  @Override
  public ApiResponse<List<OAuthApp>> getOAuthApps(Pager pager) {
    return doApiGet(getOAuthAppsRoute() + pager.toQuery(), null, listType());
  }

  /**
   * gets a registered OAuth 2.0 client application with Mattermost acting as an OAuth 2.0 service
   * provider.
   */
  @Override
  public ApiResponse<OAuthApp> getOAuthApp(String appId) {
    return doApiGet(getOAuthAppRoute(appId), null, OAuthApp.class);
  }

  /**
   * gets a sanitized version of a registered OAuth 2.0 client application with Mattermost acting as
   * an OAuth 2.0 service provider.
   */
  @Override
  public ApiResponse<OAuthApp> getOAuthAppInfo(String appId) {
    return doApiGet(getOAuthAppRoute(appId) + "/info", null, OAuthApp.class);
  }

  /**
   * deletes a registered OAuth 2.0 client application.
   */
  @Override
  public ApiResponse<Boolean> deleteOAuthApp(String appId) {
    return doApiDelete(getOAuthAppRoute(appId)).checkStatusOk();
  }

  /**
   * regenerates the client secret for a registered OAuth 2.0 client application.
   */
  @Override
  public ApiResponse<OAuthApp> regenerateOAuthAppSecret(String appId) {
    return doApiPost(getOAuthAppRoute(appId) + "/regen_secret", null, OAuthApp.class);
  }

  /**
   * gets a page of OAuth 2.0 client applications the user authorized to use access their account.
   */
  @Override
  public ApiResponse<List<OAuthApp>> getAuthorizedOAuthAppsForUser(String userId, Pager pager) {
    return doApiGet(getUserRoute(userId) + "/oauth/apps/authorized" + pager.toQuery(), null,
        listType());
  }

  /**
   * will authorize an OAuth 2.0 client application to access a user's account and provide a
   * redirect link to follow.
   */
  @Override
  public String authorizeOAuthApp(AuthorizeRequest authRequest) {
    return doApiRequest(HttpMethod.POST, url + "/oauth/authorize", authRequest, null,
        stringMapType()).readEntity().get("redirect");
  }

  /**
   * will deauthorize an OAuth 2.0 client application from accessing a user's account.
   */
  @Override
  public ApiResponse<Boolean> deauthorizeOAuthApp(String appId) {
    DeauthorizeOAuthAppRequest request =
        DeauthorizeOAuthAppRequest.builder().clientId(appId).build();
    return doApiRequest(HttpMethod.POST, url + "/oauth/deauthorize", request, null).checkStatusOk();
  }

  // Commands Section

  /**
   * will create a new command if the user have the right permissions.
   */
  @Override
  public ApiResponse<Command> createCommand(Command cmd) {
    return doApiPost(getCommandsRoute(), cmd, Command.class);
  }

  /**
   * updates a command based on the provided Command object.
   */
  @Override
  public ApiResponse<Command> updateCommand(Command cmd) {
    return doApiPut(getCommandRoute(cmd.getId()), cmd, Command.class);
  }

  /**
   * deletes a command based on the provided command id string.
   */
  @Override
  public ApiResponse<Boolean> deleteCommand(String commandId) {
    return doApiDelete(getCommandRoute(commandId)).checkStatusOk();
  }

  /**
   * will retrieve a list of commands available in the team.
   */
  @Override
  public ApiResponse<CommandList> listCommands(String teamId, boolean customOnly) {
    String query =
        new QueryBuilder().set("team_id", teamId).set("custom_only", customOnly).toString();
    return doApiGet(getCommandsRoute() + query, null, CommandList.class);
  }

  /**
   * executes a given command.
   */
  @Override
  public ApiResponse<CommandResponse> executeCommand(String channelId, String command) {
    CommandArgs args = new CommandArgs();
    args.setChannelId(channelId);
    args.setCommand(command);
    return doApiPost(getCommandsRoute() + "/execute", args, CommandResponse.class);
  }

  /**
   * will retrieve a list of commands available in the team.
   */
  @Override
  public ApiResponse<CommandList> listAutocompleteCommands(String teamId) {
    return doApiGet(getTeamAutoCompleteCommandsRoute(teamId), null, CommandList.class);
  }

  /**
   * will create a new token if the user have the right permissions.
   */
  @Override
  public ApiResponse<String> regenCommandToken(String commandId) {
    return doApiPut(getCommandRoute(commandId) + "/regen_token", null, String.class);
  }

  // Status Section

  /**
   * returns a user status based on the provided user id string.
   */
  @Override
  public ApiResponse<Status> getUserStatus(String userId, String etag) {
    return doApiGet(getUserStatusRoute(userId), etag, Status.class);
  }

  /**
   * returns a list of users status based on the provided user ids.
   */
  @Override
  public ApiResponse<List<Status>> getUsersStatusesByIds(String... userIds) {
    return doApiPost(getUserStatusesRoute() + "/ids", userIds, listType());
  }

  /**
   * sets a user's status based on the provided user id string.
   */
  @Override
  public ApiResponse<Status> updateUserStatus(String userId, Status userStatus) {
    return doApiPut(getUserStatusRoute(userId), userStatus, Status.class);
  }

  // Webrtc Section

  /**
   * returns a valid token, stun server and turn server with credentials to use with the Mattermost
   * WebRTC service.
   * 
   * @return
   */
  @Override
  public ApiResponse<WebrtcInfoResponse> getWebrtcToken() {
    return doApiGet("/webrtc/token", null, WebrtcInfoResponse.class);
  }

  // Emoji Section

  /**
   * will save an emoji to the server if the current user has permission to do so. If successful,
   * the provided emoji will be returned with its Id field filled in. Otherwise, an error will be
   * returned.
   */
  @Override
  public ApiResponse<Emoji> createEmoji(Emoji emoji, Path imageFile) {
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

    FileDataBodyPart body = new FileDataBodyPart("image", imageFile.toFile());
    multiPart.bodyPart(body);

    multiPart.field("emoji", emoji, MediaType.APPLICATION_JSON_TYPE);

    return doApiPostMultiPart(getEmojisRoute(), multiPart, Emoji.class);
  }

  /**
   * returns a list of custom emoji in the system.
   * 
   * @return
   */
  @Override
  public ApiResponse<EmojiList> getEmojiList(Pager pager) {
    return doApiGet(getEmojisRoute() + pager.toQuery(), null, EmojiList.class);
  }

  /**
   * delete an custom emoji on the provided emoji id string.
   */
  @Override
  public ApiResponse<Boolean> deleteEmoji(String emojiId) {
    return doApiDelete(getEmojiRoute(emojiId)).checkStatusOk();
  }

  /**
   * returns a custom emoji in the system on the provided emoji id string.
   */
  @Override
  public ApiResponse<Emoji> getEmoji(String emojiId) {
    return doApiGet(getEmojiRoute(emojiId), null, Emoji.class);
  }

  /**
   * returns the emoji image.
   */
  @Override
  public ApiResponse<Object> getEmojiImage(String emojiId) {
    throw new UnsupportedOperationException("not impl");
  }

  // Reaction Section

  /**
   * saves an emoji reaction for a post. Returns the saved reaction if successful, otherwise an
   * error will be returned.
   */
  @Override
  public ApiResponse<Reaction> saveReaction(Reaction reaction) {
    return doApiPost(getReactionsRoute(), reaction, Reaction.class);
  }

  /**
   * returns a list of reactions to a post.
   */
  @Override
  public ApiResponse<List<Reaction>> getReactions(String postId) {
    return doApiGet(getPostRoute(postId) + "/reactions", null, listType());
  }

  /**
   * deletes reaction of a user in a post.
   */
  @Override
  public ApiResponse<Boolean> deleteReaction(Reaction reaction) {
    return doApiDelete(getUserRoute(reaction.getUserId()) + getPostRoute(reaction.getPostId())
        + String.format("/reactions/%s", reaction.getEmojiName())).checkStatusOk();
  }
}
