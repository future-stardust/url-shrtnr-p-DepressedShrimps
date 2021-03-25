package url.shortener.server.config.handler;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import url.shortener.server.config.exception.NotUniqueAliasException;
import url.shortener.server.dto.NotUniqueAliasErrorDto;

@Slf4j
@Singleton
@Produces(MediaType.APPLICATION_JSON)
public class NotUniqueAliasExceptionHandler
    implements ExceptionHandler<NotUniqueAliasException, HttpResponse<NotUniqueAliasErrorDto>> {

  @Override
  public HttpResponse<NotUniqueAliasErrorDto> handle(
      HttpRequest request, NotUniqueAliasException exception
  ) {
    var error = new NotUniqueAliasErrorDto()
        .setMessage("Suggested alias in not unique")
        .setSuggestions(exception.getSuggestion());
    return HttpResponse.status(HttpStatus.CONFLICT)
        .body(error);
  }
}
