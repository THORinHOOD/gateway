package fi.lipp.greatheart.gateway.service.mapper;

import fi.lipp.greatheart.gateway.domain.UserEntity;
import fi.lipp.greatheart.gateway.service.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto convert(UserEntity user);
    UserEntity convert(UserDto user);
}
