package url.shortener.server.component.url;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import url.shortener.server.config.properties.ServiceProperties;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UrlComponent {

  private final ServiceProperties serviceProperties;

  @SneakyThrows
  public URI createLocationUri(String alias) {
    return new URIBuilder()
        .setScheme(serviceProperties.getSchema())
        .setHost(serviceProperties.getHost())
        .setPathSegments("r", alias)
        .build();
  }
}
