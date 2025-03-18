package com.aston.userservice.domain.mapper;

import com.aston.userservice.domain.dto.UserResponseDto;
import com.aston.userservice.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserResponseDto dto);
}
