package net.bis5.mattermost.provider.test;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import net.bis5.mattermost.client4.MultiPartAdapter;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.spi.MattermostClientProvider;
import net.bis5.mattermost.provider.MattermostModelMapperProvider;

public class SimpleClientProvider implements MattermostClientProvider {

  @Override
  public ClientBuilder createClientBuilder(boolean ignoreUnknownProperties) {
    return ClientBuilder.newBuilder();
  }

  @Override
  public MultiPartAdapter createMultiPartAdapter() {
    return new MultiPartAdapter() {

      @Override
      public <T> ApiResponse<T> doApiPostMultiPart(Client httpClient, String url, String authority,  FormMultiPart multiPart, Class<T> responseType) {
        throw new UnsupportedOperationException();
      }
    };
  }
}
