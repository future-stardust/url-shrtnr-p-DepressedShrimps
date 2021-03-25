package url.shortener.server.mapper;

import org.mapstruct.Mapper;
import url.shortener.server.dto.UserCreateDto;
import url.shortener.server.entity.User;

@Mapper(config = CommonMappingConfig.class)
public interface UserMapper {

  User to(UserCreateDto source);

}
