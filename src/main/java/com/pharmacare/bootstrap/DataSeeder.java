package com.pharmacare.bootstrap;

import com.pharmacare.user.Role;
import com.pharmacare.user.User;
import com.pharmacare.user.UserRepository;
import com.pharmacare.permission.RolePermission;
import com.pharmacare.permission.RolePermissionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final RolePermissionRepository permissions;

    public DataSeeder(UserRepository users, PasswordEncoder passwordEncoder, RolePermissionRepository permissions) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.permissions = permissions;
    }

    @Override
    public void run(String... args) {
        if (!users.existsByUsername("admin")) {
            User u = new User();
            u.setUsername("admin");
            u.setPassword(passwordEncoder.encode("admin123"));
            u.setRole(Role.ADMIN);
            u.setActive(true);
            users.save(u);
        }

        seedDefaultPermissions();
    }

    private void seedDefaultPermissions() {
        List<String> keys = List.of(
                "module.dashboard",
                "module.sales",
                "module.inventory",
                "module.suppliers",
                "module.expenses",
                "module.profit",
                "module.prescriptions",
                "module.reports",
                "module.users",
                "module.settings.permissions",
                "inventory.categories",
                "inventory.medicines",
                "inventory.adjust_stock",
                "inventory.low_stock",
                "inventory.expiring",
                "action.inventory.category.create",
                "action.inventory.category.delete",
                "action.inventory.medicine.create",
                "action.inventory.medicine.update",
                "action.inventory.medicine.delete",
                "action.inventory.adjust_stock.apply",
                "action.sales.create",
                "action.suppliers.manage",
                "action.expenses.view",
                "action.expenses.create",
                "action.expenses.edit",
                "action.expenses.delete",
                "action.profit.view",
                "action.prescriptions.upload",
                "action.prescriptions.delete",
                "action.reports.view",
                "action.users.manage"
        );

        for (Role role : Role.values()) {
            for (String key : keys) {
                if (permissions.existsByRoleAndKey(role, key)) {
                    continue;
                }

                RolePermission p = new RolePermission();
                p.setRole(role);
                p.setKey(key);

                applyDefaultPermission(p, role, key);

                permissions.save(p);
            }
        }
    }

    private static void applyDefaultPermission(RolePermission p, Role role, String key) {
        boolean admin = role == Role.ADMIN;
        if (admin) {
            p.setCanView(true);
            p.setCanCreate(true);
            p.setCanUpdate(true);
            p.setCanDelete(true);
            return;
        }

        boolean canView = false;
        boolean canCreate = false;
        boolean canUpdate = false;
        boolean canDelete = false;

        // Everyone can see dashboard
        if ("module.dashboard".equals(key)) {
            canView = true;
        }

        if (role == Role.CASHIER) {
            if ("module.sales".equals(key)) canView = true;
            if ("module.inventory".equals(key)) canView = true;
            if ("module.expenses".equals(key)) canView = true;

            if ("inventory.medicines".equals(key)) canView = true;
            if ("inventory.low_stock".equals(key)) canView = true;
            if ("inventory.expiring".equals(key)) canView = true;

            if ("action.sales.create".equals(key)) {
                canView = true;
                canCreate = true;
            }
            if ("action.expenses.view".equals(key)) {
                canView = true;
            }
        }

        if (role == Role.PHARMACIST) {
            if ("module.inventory".equals(key)) canView = true;
            if ("module.prescriptions".equals(key)) canView = true;
            if ("module.reports".equals(key)) canView = true;
            if ("module.expenses".equals(key)) canView = true;
            if ("module.profit".equals(key)) canView = true;

            if (key.startsWith("inventory.")) canView = true;

            if ("action.inventory.category.create".equals(key)) {
                canView = true;
                canCreate = true;
            }
            if ("action.inventory.medicine.create".equals(key)) {
                canView = true;
                canCreate = true;
            }
            if ("action.inventory.medicine.update".equals(key)) {
                canView = true;
                canUpdate = true;
            }
            if ("action.expenses.view".equals(key)) {
                canView = true;
            }
            if ("action.expenses.create".equals(key)) {
                canView = true;
                canCreate = true;
            }
            if ("action.expenses.edit".equals(key)) {
                canView = true;
                canUpdate = true;
            }
            if ("action.expenses.delete".equals(key)) {
                canView = true;
                canDelete = true;
            }
            if ("action.profit.view".equals(key)) {
                canView = true;
            }
            if ("action.prescriptions.upload".equals(key)) {
                canView = true;
                canCreate = true;
            }
            if ("action.reports.view".equals(key)) {
                canView = true;
            }
        }

        if (role == Role.STORE_MANAGER) {
            if ("module.inventory".equals(key)) canView = true;
            if ("module.suppliers".equals(key)) canView = true;
            if ("module.reports".equals(key)) canView = true;
            if ("module.expenses".equals(key)) canView = true;
            if ("module.profit".equals(key)) canView = true;

            if (key.startsWith("inventory.")) canView = true;

            if ("action.inventory.medicine.create".equals(key)) {
                canView = true;
                canCreate = true;
            }
            if ("action.inventory.medicine.update".equals(key)) {
                canView = true;
                canUpdate = true;
            }
            if ("action.inventory.adjust_stock.apply".equals(key)) {
                canView = true;
                canCreate = true;
            }
            if ("action.suppliers.manage".equals(key)) {
                canView = true;
                canCreate = true;
                canUpdate = true;
            }
            if ("action.expenses.view".equals(key)) {
                canView = true;
            }
            if ("action.expenses.create".equals(key)) {
                canView = true;
                canCreate = true;
            }
            if ("action.expenses.edit".equals(key)) {
                canView = true;
                canUpdate = true;
            }
            if ("action.expenses.delete".equals(key)) {
                canView = true;
                canDelete = true;
            }
            if ("action.profit.view".equals(key)) {
                canView = true;
            }
            if ("action.reports.view".equals(key)) {
                canView = true;
            }
        }

        p.setCanView(canView);
        p.setCanCreate(canCreate);
        p.setCanUpdate(canUpdate);
        p.setCanDelete(canDelete);
    }
}
