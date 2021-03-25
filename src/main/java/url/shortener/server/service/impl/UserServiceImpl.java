package url.shortener.server.service.impl;

import io.micronaut.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import url.shortener.server.component.encryption.HashingComponent;
import url.shortener.server.component.token.TokenComponent;
import url.shortener.server.config.exception.BusinessException;
import url.shortener.server.dto.TokenDto;
import url.shortener.server.dto.UserCreateDto;
import url.shortener.server.entity.User;
import url.shortener.server.mapper.UserMapper;
import url.shortener.server.repository.UserRepository;
import url.shortener.server.service.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final HashingComponent hashingComponent;
  private final TokenComponent tokenComponent;

  @Override
  public void createUser(UserCreateDto userCreateDto) {

    if (userRepository.existsById(userCreateDto.getEmail())) {
      throw new BusinessException("User with such email already exists", HttpStatus.BAD_REQUEST);
    }
    User userToSave = userMapper.to(userCreateDto)
        .setPassword(hashingComponent.hash(userCreateDto.getPassword()));

    log.info("Save user with id '{}'", userCreateDto.getEmail());
    userRepository.save(userToSave);
  }

  @Override
  public TokenDto logInUser(UserCreateDto userCreateDto) {
    return userRepository.findById(userCreateDto.getEmail())
        .filter(user -> hashingComponent.match(user.getPassword(), userCreateDto.getPassword()))
        .map(user -> tokenComponent.createToken(user.getEmail()))
        .map(token -> new TokenDto().setToken(token))
        .orElseThrow(() -> new BusinessException("User is unauthorized", HttpStatus.UNAUTHORIZED));
  }

  @Override
  public void logOutUserByToken(String authToken) {
    checkTokenNotBlank(authToken);
    tokenComponent.removeToken(authToken);
  }

  @Override
  public Optional<String> authorizeUser(String authToken) {
    checkTokenNotBlank(authToken);
    return tokenComponent.validateToken(authToken);
  }

  private void checkTokenNotBlank(String authToken) {
    if (StringUtils.isBlank(authToken)) {
      throw new BusinessException("Token is missing", HttpStatus.UNAUTHORIZED);
    }
  }
}
