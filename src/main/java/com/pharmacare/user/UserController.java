package com.pharmacare.user;

import com.pharmacare.user.dto.CreateUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> list() {
        return users.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public User create(@Valid @RequestBody CreateUserRequest request) {
        if (users.existsByUsername(request.getUsername().trim())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User u = new User();
        u.setUsername(request.getUsername().trim());
        u.setPassword(passwordEncoder.encode(request.getPassword()));
        u.setRole(request.getRole());
        u.setActive(request.getActive() == null || request.getActive());
        return users.save(u);
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public User setActive(@PathVariable("id") Long id, @RequestParam("active") boolean active) {
        User u = users.findById(id).orElseThrow();
        u.setActive(active);
        return users.save(u);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable("id") Long id) {
        users.deleteById(id);
    }
}
