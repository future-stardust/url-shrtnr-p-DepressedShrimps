package url.shortener.server.config.openapi;

import io.micronaut.http.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import url.shortener.server.dto.ErrorDto;
import url.shortener.server.dto.UrlCreateDto;

@Operation(
    summary = "Endpoint to create alias for given URI. Can be proposed own alias",
    tags = {"urls"},
    security = @SecurityRequirement(name = "Token"),
    requestBody = @RequestBody(
        description = "URI which is needed to store and optional alias",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = UrlCreateDto.class)
        )
    ),
    responses = {
        @ApiResponse(
            responseCode = "201",
            description = "Successfully create alias for given URI"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content = @Content(
                schema = @Schema(implementation = ErrorDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Provided alias is already taken",
            content = @Content(
                //TODO change error body if needed
                schema = @Schema(implementation = ErrorDto.class)
            )
        )
    }
)
public @interface CreateAlias {

}
