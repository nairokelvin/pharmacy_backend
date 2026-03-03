package com.pharmacare.permission;

import com.pharmacare.permission.dto.PermissionDto;
import com.pharmacare.permission.dto.UpsertPermissionRequest;
import com.pharmacare.security.UserPrincipal;
import com.pharmacare.user.Role;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class RolePermissionController {

    private final RolePermissionService service;

    public RolePermissionController(RolePermissionService service) {
        this.service = service;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public List<PermissionDto> myPermissions(Authentication authentication) {
        Role role = resolveRole(authentication);
        return service.list(role);
    }

    @GetMapping("/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionDto> list(@PathVariable("role") Role role) {
        return service.list(role);
    }

    @PutMapping("/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionDto> replace(@PathVariable("role") Role role, @Valid @RequestBody List<UpsertPermissionRequest> items) {
        return service.replace(role, items);
    }

    @PostMapping("/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public PermissionDto upsert(@PathVariable("role") Role role, @Valid @RequestBody UpsertPermissionRequest item) {
        return service.upsert(role, item);
    }

    private static Role resolveRole(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal p) {
            return Role.valueOf(p.getRole());
        }
        throw new IllegalStateException("Unauthenticated");
    }
}
