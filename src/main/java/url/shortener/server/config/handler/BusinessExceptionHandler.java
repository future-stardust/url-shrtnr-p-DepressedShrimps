package url.shortener.server.config.handler;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import url.shortener.server.config.exception.BusinessException;
import url.shortener.server.dto.ErrorDto;

import javax.inject.Singleton;

@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class BusinessExceptionHandler implements
    ExceptionHandler<BusinessException, HttpResponse<ErrorDto>> {

  @Override
  public HttpResponse<ErrorDto> handle(HttpRequest request, BusinessException exception) {
    log.error("Handle '{}'", exception.getClass(), exception);

    ErrorDto errorDto = new ErrorDto()
        .setMessage(exception.getMessage());
    return HttpResponse
        .status(exception.getHttpStatus())
        .body(errorDto);
  }
}
