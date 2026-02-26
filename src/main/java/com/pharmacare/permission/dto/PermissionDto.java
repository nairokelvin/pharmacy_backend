package com.pharmacare.permission.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PermissionDto {
    private String key;
    private boolean canView;
    private boolean canCreate;
    private boolean canUpdate;
    private boolean canDelete;
}
