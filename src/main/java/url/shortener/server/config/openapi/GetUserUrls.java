package url.shortener.server.config.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import url.shortener.server.dto.ErrorDto;
import url.shortener.server.dto.UrlsListDto;

@Operation(
    summary = "Endpoint to retrieve list of user urls",
    tags = {"urls"},
    security = @SecurityRequirement(name = "Token"),
    responses = {
        @ApiResponse(
            responseCode = "200",
            description = "List of pair of user urls",
            content = @Content(
                schema = @Schema(implementation = UrlsListDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content = @Content(
                schema = @Schema(implementation = ErrorDto.class)
            )
        )

    }
)
public @interface GetUserUrls {

}
