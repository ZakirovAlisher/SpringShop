package kz.springboot.springbootdemo.services;

import kz.springboot.springbootdemo.entities.Roles;
import kz.springboot.springbootdemo.entities.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    Users getUserByEmail(String email);
    List<Users> getAllUsers( );
    Users addUser(Users b);
    Users getUser(Long id);
    Roles getRole(Long id);
    Users saveUser(Users item);


    List<Roles> getAllRoles( );
    Roles addRole(Roles b);

    Roles saveRole(Roles item);


    void deleteUser(Users user);


}
