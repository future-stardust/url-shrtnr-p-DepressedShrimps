package url.shortener.server.config.openapi;

import io.micronaut.http.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import url.shortener.server.dto.ErrorDto;
import url.shortener.server.dto.UserCreateDto;

@Operation(
    summary = "Endpoint to create user",
    tags = {"users"},
    requestBody = @RequestBody(
        description = "User credentials",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = UserCreateDto.class)
        )
    ),
    responses = {
        @ApiResponse(
            responseCode = "200",
            description = "User successfully created"
        ),
        @ApiResponse(
            responseCode = "400.1",
            description = "Invalid data format",
            content = @Content(
                schema = @Schema(implementation = ErrorDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400.2",
            description = "User with such email already exists",
            content = @Content(
                schema = @Schema(implementation = ErrorDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "User already authorized",
            content = @Content(
                schema = @Schema(implementation = ErrorDto.class)
            )
        )
    }
)
public @interface CreateUser {

}
