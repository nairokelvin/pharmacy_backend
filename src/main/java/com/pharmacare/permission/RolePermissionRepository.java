package com.pharmacare.permission;

import com.pharmacare.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRole(Role role);

    Optional<RolePermission> findByRoleAndKey(Role role, String key);

    boolean existsByRoleAndKey(Role role, String key);
}
