package com.pharmacare.supplier.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSupplierRequest {

    @NotBlank
    private String name;

    private String phone;
    private String email;
    private String address;
}
