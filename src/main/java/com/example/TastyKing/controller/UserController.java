package com.example.TastyKing.controller;


import com.example.TastyKing.dto.request.*;
import com.example.TastyKing.dto.response.ApiResponse;
import com.example.TastyKing.dto.response.UserResponse;
import com.example.TastyKing.exception.AppException;
import com.example.TastyKing.exception.ErrorCode;
import com.example.TastyKing.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody @Valid RegisterRequest request){
            return ApiResponse.<String>builder()
                    .result(userService.createNewUser(request))
                    .build();
    }
    @PostMapping("/account")
    public ApiResponse<String> createCustomerAccount(@RequestBody @Valid CreateNewCustomerRequest request){
        return ApiResponse.<String>builder()
                .result(userService.createNewCustomerByAdmin(request))
                .build();
    }
    @PostMapping("/account-staff")
    public ApiResponse<String> createStaffAccount(@RequestBody @Valid CreateNewStaffRequest request){
        return ApiResponse.<String>builder()
                .result(userService.createNewStaffByAdmin(request))
                .build();
    }

    @PostMapping("/sendOTP")
    public ApiResponse<String> sendOTP(@RequestBody SendOTPRequest sendOTPRequest){
        return ApiResponse.<String>builder()
                .result(userService.sendOTP(sendOTPRequest))
                .build();
    }
    @PutMapping("/verify-account")
    public ApiResponse<String> verifyAccount(@RequestBody OtpRequest otpRequest){
        boolean isVerified = userService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp());
        if (isVerified) {
//            return new ResponseEntity<>("OTP verified successfully.", HttpStatus.OK);
            return ApiResponse.<String>builder()
                    .result("OTP verified successfully")
                    .build();
        } else {
            throw new AppException(ErrorCode.OTP_INVALID);  // This should return a 400 or appropriate error status
        }
    }
    @PutMapping("/regenerate-otp")
    public ApiResponse<String> regenerateOtp(@RequestParam String email) {
        return ApiResponse.<String>builder()
                .result(userService.regenerateOtp(email))
                .build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{email}")
    public ApiResponse<UserResponse> getUserByEmail(@PathVariable("email") String email){
            return ApiResponse.<UserResponse>builder()
                    .result(userService.getUserByEmail(email))
                    .build();
    }

    @GetMapping("/myInfo")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo()).build();
    }

    @PutMapping("/update/{email}")
    public ApiResponse<UserResponse> updateUser(@PathVariable("email") String email, @Valid @RequestBody UpdateUserRequest updateUserRequest){
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(email, updateUserRequest))
                .build();
    }
    @PutMapping("/update-pass/{email}")
    public ApiResponse<UserResponse> updatePass(@PathVariable("email") String email, @Valid @RequestBody UpdatePasswordRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.updatePassword(email, request))
                .build();
    }
    @PutMapping("/change-pass/{email}")
    public ApiResponse<String> changePass(@PathVariable("email") String email, @Valid @RequestBody ChangePasswordRequest request){
        return ApiResponse.<String>builder()
                .result(userService.changePassword(email, request))
                .build();
    }
    @PutMapping("/verify-email")
    public ApiResponse<String> verifyAccountToChangePass(@RequestBody OtpRequest otpRequest){
        boolean isVerified = userService.verifyEmail(otpRequest.getEmail(), otpRequest.getOtp());
        if (isVerified) {
//            return new ResponseEntity<>("OTP verified successfully.", HttpStatus.OK);
            return ApiResponse.<String>builder()
                    .result("OTP verified successfully")
                    .build();
        } else {
            throw new AppException(ErrorCode.OTP_INVALID);  // This should return a 400 or appropriate error status
        }
    }

    @GetMapping("/getCustomer")
    public ApiResponse<List<UserResponse>> getAllCustomer(){
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllCustomer())
                .build();
    }

    @GetMapping("getUserByID/{userId}")
    public ApiResponse<UserResponse> getUserByID(@PathVariable("userId") int userId){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserByUserID(userId))
                .build();
    }

    @PutMapping("/account-controll/{userId}")
    public ApiResponse<UserResponse> accountControll(@PathVariable("userId") int userId){
        return ApiResponse.<UserResponse>builder()
                .result(userService.blockAccount(userId))
                .build();
    }

    @GetMapping("/getStaff")
    public ApiResponse<List<UserResponse>> getAllStaff(){
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllStaff())
                .build();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteByUserID(@PathVariable("userId") int userId){
        return ApiResponse.<String>builder()
                .result(userService.deleteUerByID(userId))
                .build();
    }

        @PutMapping("/updateStaff/{userId}")
    public ApiResponse<UserResponse> updateStaff(@PathVariable("userId") int userId, @Valid @RequestBody UpdateStaffRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateStaff(userId, request))
                .build();
        }


}
