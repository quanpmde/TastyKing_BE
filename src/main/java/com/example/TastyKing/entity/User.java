package com.example.TastyKing.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)

@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "userid")
 int userId;

 @Column(name = "fullname", length = 255, unique = true)
 String fullName;

 @Column(name = "username", length = 50)
String userName;

 @Column(name = "email", length = 100)
String email;

 @Column(name = "phone", length = 15)
 String phone;

 @Column(name = "password", length = 255)
 String password;

 @Column(name = "role", length = 50)
 String role;

 @Column(name = "active")
 boolean active;

 @Column(name = "otp", length = 10)
 String otp;

 @Column(name = "generateotptime")
 LocalDateTime generateOtpTime;
 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
 private List<VoucherExchange> voucherExchanges;

}
