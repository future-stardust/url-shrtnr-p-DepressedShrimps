package url.shortener.server.controller;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import javax.inject.Inject;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import url.shortener.server.config.openapi.CreateUser;
import url.shortener.server.config.openapi.LoginUser;
import url.shortener.server.dto.TokenDto;
import url.shortener.server.dto.UserCreateDto;
import url.shortener.server.service.UserService;

@Controller("/users")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UserController {

  private final UserService userService;

  @CreateUser
  @Secured(SecurityRule.IS_ANONYMOUS)
  @Post(value = "signup", consumes = APPLICATION_JSON)
  public void createUser(@Valid @Body UserCreateDto userCreateDto) {
    userService.createUser(userCreateDto);
  }

  @LoginUser
  @Secured(SecurityRule.IS_ANONYMOUS)
  @Post(value = "signin", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
  public TokenDto loginUser(
      @Valid @Body UserCreateDto userCreateDto
  ) {
    return userService.logInUser(userCreateDto);
  }
}
