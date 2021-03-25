package url.shortener.server.service;

import java.util.Optional;
import url.shortener.server.dto.TokenDto;
import url.shortener.server.dto.UserCreateDto;

public interface UserService {

  void createUser(UserCreateDto userCreateDto);

  TokenDto logInUser(UserCreateDto userCreateDto);

  void logOutUserByToken(String authToken);

  Optional<String> authorizeUser(String authToken);
}
