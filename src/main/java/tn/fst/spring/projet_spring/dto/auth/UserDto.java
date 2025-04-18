package tn.fst.spring.projet_spring.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String role;
}
