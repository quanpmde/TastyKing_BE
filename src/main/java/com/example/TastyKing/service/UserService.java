package com.example.TastyKing.service;

import com.example.TastyKing.dto.request.*;
import com.example.TastyKing.dto.response.UserResponse;
import com.example.TastyKing.entity.RewardPoint;
import com.example.TastyKing.entity.User;
import com.example.TastyKing.enums.Role;
import com.example.TastyKing.exception.AppException;
import com.example.TastyKing.exception.ErrorCode;
import com.example.TastyKing.mapper.UserMapper;
import com.example.TastyKing.repository.RewardPointRepository;
import com.example.TastyKing.repository.UserRepository;
import com.example.TastyKing.util.EmailUtil;
import com.example.TastyKing.util.OtpUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class UserService {
    @Autowired
    private OtpUtil otpUtil;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RewardPointRepository rewardPointRepository;

    public String createNewUser(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.EMAIL_EXISTED);

        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(request.getEmail(), otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(Role.USER.name());
        user.setOtp(otp);
        user.setGenerateOtpTime(LocalDateTime.now());
        user.setUserName(request.getEmail());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        userRepository.save(user);
        return "To verify this is your email account, we will send a confirmation code to this email. Please check your email to receive the verification code to activate your account";

    }

    @PreAuthorize("hasRole('ADMIN')")
    public String createNewCustomerByAdmin(CreateNewCustomerRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserName(request.getEmail());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        if (request.getStatus().equalsIgnoreCase("Active")) {
            user.setActive(1);
            try {
                emailUtil.sendActiveAccount(request.getEmail(), request.getPassword());
            } catch (MessagingException e) {
                throw new RuntimeException("Unable to send activation email, please try again", e);
            }
            user.setGenerateOtpTime(LocalDateTime.now());
            user.setRole(Role.USER.name());
            userRepository.save(user);
            return "New account has been created and activated.";
        } else {
            user.setActive(0);
            user.setRole(Role.USER.name());
            userRepository.save(user);
            return "Account has been created. Please verify email to activate.";
        }
    }
    public String sendOTP(SendOTPRequest sendOTPRequest){
        User user = userRepository.findByEmail(sendOTPRequest.getEmail()).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(sendOTPRequest.getEmail(), otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        user.setOtp(otp);
        user.setGenerateOtpTime(LocalDateTime.now());
        userRepository.save(user);
        return "To verify this is your email account, we will send a confirmation code to this email. Please check your email to receive the verification code to change your password";
    }


    public boolean verifyOtp(String email, String otp) {

    User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
        if (user.getOtp().equals(otp) &&
                Duration.between(user.getGenerateOtpTime(), LocalDateTime.now()).getSeconds() < 60) {
            user.setActive(1);
            userRepository.save(user);
            RewardPoint rewardPoint = new RewardPoint();
            rewardPoint.setUser(user);
            rewardPoint.setBalance(0.0);
            rewardPointRepository.save(rewardPoint);
            return true;
        }
        return false;
    }
    public boolean verifyEmail(String email, String otp) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
        if (user.getOtp().equals(otp) &&
                Duration.between(user.getGenerateOtpTime(), LocalDateTime.now()).getSeconds() < 60) {
            user.setActive(1);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    public String regenerateOtp(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        user.setOtp(otp);
        user.setGenerateOtpTime(LocalDateTime.now());
        userRepository.save(user);
        return "Email sent...Please check email to verify account within 1 minutes";
    }


    public UserResponse getUserByEmail(String email){
        return userMapper.toUserResponse(userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.EMAIL_NOT_EXISTED)));
    }
    @PostAuthorize("returnObject.email == principal.claims['sub']")
    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name= context.getAuthentication().getName();
        User user = userRepository.findByEmail(name).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }
    private String getCurrentUserEmail() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getClaim("sub"); // Assuming the email is stored in the "sub" claim
    }

    @PostAuthorize("returnObject.email == principal.claims['sub'] or hasRole('ADMIN')")
    public UserResponse updateUser(String email, UpdateUserRequest request){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
        user.setUserName(request.getUserName());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        User updatedUser = userRepository.save(user);
        return userMapper.toUserResponse(updatedUser);

    }

    @PostAuthorize("returnObject.email == principal.claims['sub']")
    public UserResponse updatePassword(String email, UpdatePasswordRequest request){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
        boolean passwordValid = passwordEncoder.matches(request.getOldPass(), user.getPassword());
        if (!passwordValid)
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCHER);
        user.setPassword(passwordEncoder.encode(request.getNewPass()));
        User updatedPass = userRepository.save(user);
        return userMapper.toUserResponse(updatedPass);
    }

    public String changePassword(String email, ChangePasswordRequest request){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
        user.setPassword(passwordEncoder.encode(request.getNewPass()));
        userRepository.save(user);
        return "Change password successful. Now you can login with new password";
    }
    public boolean verifyOtpToChangePass(String email, String otp) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
        if (user.getOtp().equals(otp) &&
                Duration.between(user.getGenerateOtpTime(), LocalDateTime.now()).getSeconds() < 60) {
            user.setActive(1);
            userRepository.save(user);

            return true;
        }
        return false;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllCustomer(){
        String roles = Role.USER.name();
        List<User> users = userRepository.findByRole(roles);
        return users.stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserByUserID(int userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NO_EXIST));
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
   public UserResponse blockAccount(int userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NO_EXIST));
        if(user.getActive()==1){
            user.setActive(0);
            User blockUser = userRepository.save(user);
            return userMapper.toUserResponse(blockUser);
        }
        user.setActive(1);
        User unblockUser = userRepository.save(user);
        return userMapper.toUserResponse(unblockUser);
    }

}
