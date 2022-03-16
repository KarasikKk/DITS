package com.example.dits.mapper;

import com.example.dits.dto.RoleDTO;
import com.example.dits.dto.UserInfoDTO;
import com.example.dits.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInfoMapper {
    private final RoleMapper roleMapper;

    public UserInfoDTO convertToUserInfoDTO(User user){
        RoleDTO roleDTO = roleMapper.convertToRoleDTO(user.getRole());

        return UserInfoDTO.builder()
                        .userId(user.getUserId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .login(user.getLogin())
                        .role(roleDTO).build();
    }
}
