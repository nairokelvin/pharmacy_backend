package com.pharmacare.permission.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertPermissionRequest {

    @NotBlank
    private String key;

    private boolean canView;
    private boolean canCreate;
    private boolean canUpdate;
    private boolean canDelete;
}
