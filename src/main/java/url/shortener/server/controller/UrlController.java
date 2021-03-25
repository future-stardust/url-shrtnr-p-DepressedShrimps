package url.shortener.server.controller;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.net.URI;
import java.security.Principal;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import url.shortener.server.component.url.UrlComponent;
import url.shortener.server.config.openapi.CreateAlias;
import url.shortener.server.config.openapi.DeleteUrl;
import url.shortener.server.config.openapi.GetOriginalUrl;
import url.shortener.server.config.openapi.GetUserUrls;
import url.shortener.server.dto.UrlCreateDto;
import url.shortener.server.dto.UrlsListDto;
import url.shortener.server.service.UrlService;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UrlController {

  private final UrlComponent urlComponent;
  private final UrlService urlService;

  @CreateAlias
  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Post(value = "urls/shorten", consumes = APPLICATION_JSON)
  public HttpResponse<Void> createUrlAlias(
      Principal principal,
      @Valid @Body UrlCreateDto urlCreateDto
  ) {
    String alias = urlService.createUrl(principal.getName(), urlCreateDto);

    return HttpResponse.created(
        urlComponent.createLocationUri(alias)
    );
  }

  @GetUserUrls
  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Get(value = "urls", produces = APPLICATION_JSON)
  public UrlsListDto getAllUserUrls(Principal principal) {
    return urlService.getUserUrls(principal.getName());
  }

  @DeleteUrl
  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Delete("urls/{alias}")
  public HttpResponse<Void> deleteUrl(
      Principal principal,
      @PathVariable("alias") String alias
  ) {
    urlService.deleteUserUrl(principal.getName(), alias);
    return HttpResponse.status(HttpStatus.NO_CONTENT);
  }

  @GetOriginalUrl
  @PermitAll
  @Get("r/{alias}")
  public HttpResponse<Void> redirect(
      @PathVariable("alias") String alias
  ) {
    URI originalUrl = urlService.getOriginalUrl(alias);
    return HttpResponse.redirect(originalUrl);
  }
}
