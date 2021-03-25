package url.shortener.server.config.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import url.shortener.server.dto.ErrorDto;

@Operation(
    summary = "Endpoint to delete alias for stored URI",
    tags = {"urls"},
    security = @SecurityRequirement(name = "Bearer"),
    responses = {
        @ApiResponse(
            responseCode = "204",
            description = "Successfully delete alias for URI"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content = @Content(
                schema = @Schema(implementation = ErrorDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Alias does not exist",
            content = @Content(
                schema = @Schema(implementation = ErrorDto.class)
            )
        )
    }
)
public @interface DeleteUrl {

}
