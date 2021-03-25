package url.shortener.server.config.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import url.shortener.server.dto.ErrorDto;

@Operation(
    summary = "Endpoint to retrieve original URI for given alias",
    tags = {"urls"},
    responses = {
        @ApiResponse(
            responseCode = "302",
            description = "Successfully retrieve original URI for given alias"
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
public @interface GetOriginalUrl {

}
