package url.shortener.server.component.security;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.security.handlers.LogoutHandler;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import url.shortener.server.service.UserService;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CustomLogoutHandler implements LogoutHandler {

  private final UserService userService;

  @Override
  public MutableHttpResponse<?> logout(HttpRequest<?> request) {
    log.debug("In logout handler");
    HttpHeaders headers = request.getHeaders();
    Optional<String> authorization = headers.getAuthorization();

    if (authorization.isEmpty()) {
      return HttpResponse.badRequest();
    }
    userService.logOutUserByToken(authorization.get());
    return HttpResponse.noContent();
  }
}
