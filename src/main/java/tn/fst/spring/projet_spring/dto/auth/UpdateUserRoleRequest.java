package tn.fst.spring.projet_spring.dto.auth;

import lombok.Data;

@Data
public class UpdateUserRoleRequest {
    private Long userId;
    private String roleName;
}
