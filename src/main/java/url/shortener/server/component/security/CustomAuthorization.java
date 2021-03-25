package url.shortener.server.component.security;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.DefaultAuthentication;
import io.micronaut.security.filters.AuthenticationFetcher;
import io.reactivex.Flowable;
import java.util.Collections;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import url.shortener.server.service.UserService;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CustomAuthorization implements AuthenticationFetcher {

  private final UserService userService;

  @Override
  public Publisher<Authentication> fetchAuthentication(HttpRequest<?> request) {
    log.debug("In authentication fetcher");
    HttpHeaders headers = request.getHeaders();
    Optional<String> authorization = headers.getAuthorization();

    if (authorization.isEmpty()) {
      log.debug("Auth token is empty");
      return Flowable.empty();
    }
    return userService.authorizeUser(authorization.map(s -> s.replaceAll("Bearer ", "")).get())
        .map(this::getAuthentication)
        .map(Flowable::just)
        .orElse(Flowable.empty());
  }

  private Authentication getAuthentication(String username) {
    return new DefaultAuthentication(username, Collections.emptyMap());
  }
}
