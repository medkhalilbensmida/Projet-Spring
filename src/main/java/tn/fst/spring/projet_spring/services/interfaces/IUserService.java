package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.auth.UserAdminUpdateDto;
import tn.fst.spring.projet_spring.dto.auth.UserDto;
import tn.fst.spring.projet_spring.dto.auth.UserProfileUpdateDto;

import java.util.List;

public interface IUserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto updateUserByAdmin(Long id, UserAdminUpdateDto adminDto);

    void deleteUser(Long id);

    UserDto updateCurrentUserProfile(String email, UserProfileUpdateDto profileDto);

    UserDto getUserByEmail(String email);

    void updateUserRole(Long userId, String roleName);
}
