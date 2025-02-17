package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.Permission;
import rs.raf.demo.model.Role;
import rs.raf.demo.model.UserInfo;
import rs.raf.demo.model.User;
import rs.raf.demo.repositories.RoleRepository;
import rs.raf.demo.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserServiceInterface {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User addUser(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        Collection<Role> roles = new ArrayList<>();
        for (Role role: user.getRoles()) {
            roles.add(role);
        }
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public void saveRole(Role role) {
        roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String mail, String roleName) {
        Role role = roleRepository.findByName(roleName);
        User user = userRepository.findByMail(mail);
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public User updateUser(UserInfo user) {
        User newUser = userRepository.getById(user.getId());
        newUser.setName(user.getName());
        newUser.setLastName(user.getLastName());
        newUser.setMail(user.getMail());
        Collection<Role> roles = new ArrayList<>();
        for (Role role: user.getRoles()) {
            roles.add(role);
        }
        newUser.setRoles(roles);
        return userRepository.save(newUser);
    }

    @Override
    public void deleteUser(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Role> rolesForUser(String mail) {
        System.out.println("mail");
        System.out.println(mail);
        return new ArrayList<>(userRepository.findByMail(mail).getRoles());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User getUserByEmail(String mail) {
        return userRepository.findByMail(mail);
    }

    public List<Role> getRoles(){
        return roleRepository.findAll();
    }
}
