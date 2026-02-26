package com.pharmacare.permission;

import com.pharmacare.permission.dto.PermissionDto;
import com.pharmacare.permission.dto.UpsertPermissionRequest;
import com.pharmacare.user.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RolePermissionService {

    private final RolePermissionRepository repo;

    public RolePermissionService(RolePermissionRepository repo) {
        this.repo = repo;
    }

    public List<PermissionDto> list(Role role) {
        return repo.findByRole(role).stream().map(RolePermissionService::toDto).toList();
    }

    @Transactional
    public List<PermissionDto> replace(Role role, List<UpsertPermissionRequest> items) {
        List<RolePermission> existing = repo.findByRole(role);
        repo.deleteAllInBatch(existing);

        List<RolePermission> saved = repo.saveAll(items.stream().map(req -> {
            RolePermission p = new RolePermission();
            p.setRole(role);
            p.setKey(req.getKey().trim());
            p.setCanView(req.isCanView());
            p.setCanCreate(req.isCanCreate());
            p.setCanUpdate(req.isCanUpdate());
            p.setCanDelete(req.isCanDelete());
            return p;
        }).toList());

        return saved.stream().map(RolePermissionService::toDto).toList();
    }

    @Transactional
    public PermissionDto upsert(Role role, UpsertPermissionRequest req) {
        String key = req.getKey().trim();

        RolePermission p = repo.findByRoleAndKey(role, key).orElseGet(() -> {
            RolePermission np = new RolePermission();
            np.setRole(role);
            np.setKey(key);
            return np;
        });

        p.setCanView(req.isCanView());
        p.setCanCreate(req.isCanCreate());
        p.setCanUpdate(req.isCanUpdate());
        p.setCanDelete(req.isCanDelete());

        return toDto(repo.save(p));
    }

    private static PermissionDto toDto(RolePermission p) {
        return new PermissionDto(p.getKey(), p.isCanView(), p.isCanCreate(), p.isCanUpdate(), p.isCanDelete());
    }
}
