package com.pharmacare.permission;

import com.pharmacare.user.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "role_permissions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_role_permission_role_key", columnNames = {"role", "permission_key"})
        }
)
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "permission_key", nullable = false)
    private String key;

    @Column(nullable = false)
    private boolean canView;

    @Column(nullable = false)
    private boolean canCreate;

    @Column(nullable = false)
    private boolean canUpdate;

    @Column(nullable = false)
    private boolean canDelete;
}
