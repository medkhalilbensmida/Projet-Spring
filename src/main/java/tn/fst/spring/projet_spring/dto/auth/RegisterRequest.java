package tn.fst.spring.projet_spring.dto.auth;

import lombok.Data;

    @Data
    public class RegisterRequest {
        private String username;
        private String email;
        private String password;
    }
