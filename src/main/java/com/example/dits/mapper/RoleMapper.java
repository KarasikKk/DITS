package com.example.dits.mapper;

import com.example.dits.dto.RoleDTO;
import com.example.dits.entity.Role;
import com.example.dits.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Component
public class RoleMapper {

    private final RoleService roleService;
    private static final int INDEX_OF_BEGIN_ROLE_NAME = 5;


    public RoleDTO convertToRoleDTO(Role role){
        List<String> listRoles = roleService.findAll().stream().map(r-> r.getRoleName().toLowerCase(Locale.ROOT)
            .substring(INDEX_OF_BEGIN_ROLE_NAME)).collect(Collectors.toList());

      return  RoleDTO.builder()
                .rolesList(listRoles)
                .currentRole(role.getRoleName().substring(INDEX_OF_BEGIN_ROLE_NAME).toLowerCase(Locale.ROOT))
                .build();
    }
}
