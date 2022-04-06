package net.bis5.mattermost.jersey.provider;

import java.text.ParseException;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.client4.MultiPartAdapter;

public class JerseyMultiPartAdapter implements MultiPartAdapter {

  @Override
  public String detectSuffix(String contentDispositionHeader) {
    try {
      ContentDisposition contentDisposition = new ContentDisposition(contentDispositionHeader);
      String fileName = contentDisposition.getFileName();
      return fileName.substring(fileName.lastIndexOf("."));
    } catch (ParseException e) {
      // If server returns illegal syntax, that is server bug.
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public <T> ApiResponse<T> doApiPostMultiPart(Client httpClient, String url, String authority, FormMultiPart multiPart, Class<T> responseType) {
  
    FormDataMultiPart jerseyMultiPart = new FormDataMultiPart();
    jerseyMultiPart.setMediaType(multiPart.getMediaType());
    
    multiPart.getBodyParts().stream()
      .map(this::toJerseyBodyPart)
      .forEach(jerseyMultiPart::bodyPart);

    return ApiResponse.of(httpClient.target(url)
      .request(MediaType.APPLICATION_JSON)
      .header(MattermostClient.HEADER_AUTH, authority)
      .method(HttpMethod.POST, Entity.entity(jerseyMultiPart, jerseyMultiPart.getMediaType()))
    , responseType);
  }
  
  private BodyPart toJerseyBodyPart(Part part) {
    if (part instanceof FileBodyPart) {
      return toJerseyBodyPart((FileBodyPart)part);
    }
    if (part instanceof FieldPart) {
      return toJerseyBodyPart((FieldPart)part);
    }
    if (part instanceof EntityFieldPart) {
      return toJerseyBodyPart((EntityFieldPart<?>)part);
    }
    throw new IllegalArgumentException("Unsupported body part: " + part);
  }

  private BodyPart toJerseyBodyPart(FileBodyPart filePart) {
    return new StreamDataBodyPart(filePart.getName(), filePart.getStream(), filePart.getFileName());
  }

  private BodyPart toJerseyBodyPart(FieldPart part) {
    return new FormDataBodyPart(part.getFieldName(), part.getValue());
  }

  private BodyPart toJerseyBodyPart(EntityFieldPart<?> part) {
    return new FormDataBodyPart(part.getName(), part.getEntity(), part.getMediaType());
  }
}
