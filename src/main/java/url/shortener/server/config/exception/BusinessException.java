package url.shortener.server.config.exception;

import io.micronaut.http.HttpStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

  private HttpStatus httpStatus;

  public BusinessException(String message, HttpStatus httpStatus, Exception cause) {
    super(message, cause);
    this.httpStatus = httpStatus;
  }

  public BusinessException(String message, HttpStatus httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
  }

  public BusinessException(String message) {
    super(message);
    this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
  }
}

