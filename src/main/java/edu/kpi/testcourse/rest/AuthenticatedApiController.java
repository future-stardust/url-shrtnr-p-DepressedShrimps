package edu.kpi.testcourse.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.kpi.testcourse.entities.UrlAlias;
import edu.kpi.testcourse.logic.Logic;
import edu.kpi.testcourse.rest.models.ErrorResponse;
import edu.kpi.testcourse.rest.models.UrlShortenRequest;
import edu.kpi.testcourse.rest.models.UrlShortenResponse;
import edu.kpi.testcourse.serialization.JsonTool;
import edu.kpi.testcourse.storage.UrlRepository.AliasAlreadyExist;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.security.Principal;
import java.util.List;
import javax.inject.Inject;

/**
 * API controller for all REST API endpoints that require authentication.
 */
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
public class AuthenticatedApiController {

  private final Logic logic;
  private final JsonTool json;
  private final HttpHostResolver httpHostResolver;

  /**
   * Main constructor.
   *
   * @param logic the business logic module
   * @param json JSON serialization tool
   * @param httpHostResolver micronaut httpHostResolver
   */
  @Inject
  public AuthenticatedApiController(
      Logic logic,
      JsonTool json,
      HttpHostResolver httpHostResolver
  ) {
    this.logic = logic;
    this.json = json;
    this.httpHostResolver = httpHostResolver;
  }

  /**
   * Create URL alias.
   */
  @Post(value = "/urls/shorten", processes = MediaType.APPLICATION_JSON)
  public HttpResponse<String> shorten(
      @Body UrlShortenRequest request,
      Principal principal,
      HttpRequest<?> httpRequest
  ) throws JsonProcessingException {
    String email = principal.getName();
    try {
      String baseUrl = httpHostResolver.resolve(httpRequest);
      var shortenedUrl = baseUrl + "/r/"
          + logic.createNewAlias(email, request.url(), request.alias());
      return HttpResponse.created(
        json.toJson(new UrlShortenResponse(shortenedUrl)));
    } catch (AliasAlreadyExist e) {
      return HttpResponse.serverError(
        json.toJson(new ErrorResponse(1, "Alias is already taken"))
      );
    }
  }

  /**
   * Delete URL alias.
   */
  @Delete(value = "/urls/{alias}", processes = MediaType.APPLICATION_JSON)
  public HttpResponse<?> delete(
      @Header String alias,
      @Body UrlShortenRequest request,
      Principal principal,
      HttpRequest<?> httpRequest
  ) throws JsonProcessingException {
    String email = principal.getName();
    try {
      logic.deleteAlias(email, alias);
      return HttpResponse.ok();
    } catch (AliasAlreadyExist e) {
      return HttpResponse.serverError(
        json.toJson(new ErrorResponse(1, "Alias not found."))
      );
    }
  }

  /**
   * Get user URL alias.
   */
  @Get(value = "/urls", processes = MediaType.APPLICATION_JSON)
  public HttpResponse<?> showUserAlias(
      @Body UrlShortenRequest request,
      Principal principal,
      HttpRequest<?> httpRequest
  ) throws JsonProcessingException {
    String email = principal.getName();
    try {
      List<UrlAlias> al = logic.showUserAlias(email);
      return HttpResponse.ok(
        json.toJson(al.toArray())
      );
    } catch (AliasAlreadyExist e) {
      return HttpResponse.serverError(
        json.toJson(new ErrorResponse(1, "User not found."))
      );
    }
  }
}
