package net.bis5.mattermost.resteasy.provider;

import java.util.logging.Level;
import java.util.logging.Logger;


import jakarta.ws.rs.client.ClientBuilder;
import net.bis5.mattermost.client4.MultiPartAdapter;
import net.bis5.mattermost.client4.spi.MattermostClientProvider;
import net.bis5.mattermost.provider.MattermostModelMapperProvider;

public class MattermostRestEasyClientProvider implements MattermostClientProvider {

  @Override
  public ClientBuilder createClientBuilder(boolean ignoreUnknownProperties, Level clientLogLevel) {
    return ClientBuilder.newBuilder()
        .register(new MattermostModelMapperProvider(ignoreUnknownProperties));
  }

  @Override
  public MultiPartAdapter createMultiPartAdapter() {
    return new RestEasyMultiPartAdapter();
  }
  
}
