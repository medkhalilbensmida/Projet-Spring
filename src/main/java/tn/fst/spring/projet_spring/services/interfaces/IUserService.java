package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.auth.UserDto;

import java.util.List;

public interface IUserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
}