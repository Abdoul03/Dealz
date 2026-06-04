package com.doul.dealz.model.dto.mapper;

import com.doul.dealz.model.User;
import com.doul.dealz.model.dto.request.UserRequestDTO;
import com.doul.dealz.model.dto.response.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toUserEntity(UserRequestDTO userRequestDTO);

    UserResponseDTO toUserResponse(User user);

}
