package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;
import ru.kata.spring.boot_security.demo.security.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class Services {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public Services(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public User getOne(Long id) {
        return userRepository.findById(id).get();
    }

    public void update(Long id, User user) {
        User oldUser = userRepository.findById(id).get();
        oldUser.setUsername(user.getUsername());
        oldUser.setLastName(user.getLastName());
        oldUser.setAge(user.getAge());
        oldUser.setPassword(user.getPassword());
        oldUser.setRoles(user.getRoles());
        userRepository.save(oldUser);
    }

    public Role getRole(String role) {
        return roleRepository.findByName(role);
    }

    public User oneUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserService userService = (UserService) authentication.getPrincipal();
        return userService.getUser();
    }

    public User createUser(User user, List<String> roles) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roleSet = new HashSet<>();
        for (String role : roles) {
            Role newRole = getRole(role);
            if (newRole != null) {
                roleSet.add(newRole);
            }
        }
        user.setRoles(roleSet);
        return user;
    }

    public User updateUser(User user, List<String> roles, Long id) {
        User oldUser = getOne(id);
        String oldPassword = oldUser.getPassword();
        String newPassword = user.getPassword();

        if (newPassword != null && !newPassword.isEmpty() && !passwordEncoder.matches(newPassword, oldPassword)) {

            user.setPassword(passwordEncoder.encode(newPassword));
        } else {
            user.setPassword(oldPassword);
        }

        Set<Role> roleSet = new HashSet<>();

        if (roles == null || roles.isEmpty()) {
            roleSet = oldUser.getRoles();
        } else {
            for (String role : roles) {
                Role newRole = getRole(role);
                if (newRole != null) {
                    roleSet.add(newRole);
                }
            }
        }

        user.setRoles(roleSet);
        return user;
    }
}
