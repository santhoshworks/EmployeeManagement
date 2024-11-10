package com.sas.hr.employee_management_api.dto;


import jakarta.validation.constraints.NotNull;

public record EmployeeInputDTO(
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        String city,
        String state,
        String location,
        @NotNull
        String birthDate   ) {
}