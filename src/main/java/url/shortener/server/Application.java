package url.shortener.server;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(title = "Shorten url service"),
    servers = @Server(url = "http://localhost:8080")
)
@SecurityScheme(
    type = SecuritySchemeType.APIKEY,
    name = "Token",
    paramName = "Authorization",
    in = SecuritySchemeIn.HEADER
)
public class Application {

  public static void main(String[] args) {
    Micronaut.run(Application.class, args);
  }
}
