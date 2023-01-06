package net.bis5.mattermost.resteasy.provider;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.MultiPartAdapter;

/**
 * {@link MultiPartAdapter} implementation for RESTEasy
 */
public class RestEasyMultiPartAdapter implements MultiPartAdapter {

  @Override
  public <T> ApiResponse<T> doApiPostMultiPart(Client httpClient, String url, String authority, FormMultiPart multiPart,
      Class<T> responseType) {

    var restEasyMultiPart = new MultipartFormDataOutput();

    multiPart.getBodyParts().forEach(p -> addPart(restEasyMultiPart, p));

    return ApiResponse.of(httpClient.target(url)
        .request(MediaType.APPLICATION_JSON)
        .header(MattermostClient.HEADER_AUTH, authority)
        .method(HttpMethod.POST, Entity.entity(restEasyMultiPart, multiPart.getMediaType())), responseType);
  }

  private void addPart(MultipartFormDataOutput restEasyMultiPart, Part part) {
    if (part instanceof FileBodyPart) {
      var fileBodyPart = (FileBodyPart) part;
      restEasyMultiPart.addFormData(fileBodyPart.getName(), fileBodyPart.getStream(),
          MediaType.MULTIPART_FORM_DATA_TYPE, fileBodyPart.getFileName());
    } else if (part instanceof FieldPart) {
      var fieldPart = (FieldPart) part;
      restEasyMultiPart.addFormData(fieldPart.getFieldName(), fieldPart.getValue(), MediaType.MULTIPART_FORM_DATA_TYPE);
    } else if (part instanceof EntityFieldPart) {
      var entityPart = (EntityFieldPart<?>) part;
      restEasyMultiPart.addFormData(entityPart.getName(), entityPart.getEntity(), entityPart.getMediaType());
    } else {
      throw new IllegalArgumentException("Unsupported body part: " + part);
    }
  }
}
