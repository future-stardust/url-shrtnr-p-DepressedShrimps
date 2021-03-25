package url.shortener.server.config.handler;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import url.shortener.server.dto.ErrorDto;

@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class CustomExceptionHandler implements ExceptionHandler<Exception, HttpResponse<ErrorDto>> {

  @Override
  public HttpResponse<ErrorDto> handle(HttpRequest request, Exception exception) {
    log.error("Handle '{}'", exception.getClass(), exception);

    ErrorDto errorDto = new ErrorDto()
        .setMessage(exception.getMessage());
    return HttpResponse
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorDto);
  }
}
