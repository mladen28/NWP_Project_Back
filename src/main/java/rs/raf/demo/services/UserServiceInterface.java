package rs.raf.demo.services;

import rs.raf.demo.model.Role;
import rs.raf.demo.model.User;
import rs.raf.demo.model.UserInfo;

import java.util.List;
import java.util.Optional;

public interface UserServiceInterface {
    Optional<User> getUserById(Long id);
    User addUser(User user);
    User getUserByEmail(String mail);
    User updateUser(UserInfo user);
    void deleteUser(Long id);
    void saveRole(Role role);
    void addRoleToUser(String mail, String roleName);
    List<Role> rolesForUser(String mail);
    List<User> getUsers();
}
