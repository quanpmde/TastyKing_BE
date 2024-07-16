package com.example.TastyKing.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateStaffRequest {

    private String fullName;
    @Size(min = 10, message = "PHONE_INVALID")
    private String phone;
    @Size(min = 8, message = "PASSWORD_INVALID")
    private String password;

}
