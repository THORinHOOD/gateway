package fi.lipp.greatheart.gateway.service.mapper;

import fi.lipp.greatheart.gateway.domain.User;
import fi.lipp.greatheart.gateway.service.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto convert(User user);
    User convert(UserDto user);
}
