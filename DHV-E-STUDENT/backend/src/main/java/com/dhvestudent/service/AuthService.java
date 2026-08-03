package com.dhvestudent.service;

import com.dhvestudent.dto.*;
import com.dhvestudent.entity.*;
import com.dhvestudent.repository.*;
import com.dhvestudent.security.JwtTokenProvider;
import com.dhvestudent.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtTokenProvider jwtProvider;
    @Autowired private EmailService emailService;

    @Transactional
    public ApiResponse<AuthResponse> register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            return ApiResponse.error("Email đã được sử dụng");
        }
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            return ApiResponse.error("Mật khẩu xác nhận không khớp");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));

        User user = User.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .studentCode(req.getStudentCode())
                .isActive(true)
                .isVerified(false)
                .build();
        user.getRoles().add(userRole);
        userRepository.save(user);

        return login(new LoginRequest(req.getEmail(), req.getPassword()));
    }

    @Transactional
    public ApiResponse<AuthResponse> login(LoginRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            String token = jwtProvider.generateToken(auth);
            UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

            User user = userRepository.findById(principal.getId()).orElseThrow();
            AuthResponse resp = AuthResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .initials(user.getInitials())
                    .avatarColor(user.getAvatarColor())
                    .role(user.getRoles().iterator().next().getName())
                    .build();
            return ApiResponse.success("Đăng nhập thành công", resp);
        } catch (Exception e) {
            return ApiResponse.error("Email hoặc mật khẩu không đúng");
        }
    }

    @Transactional
    public ApiResponse<String> forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ApiResponse.success("Nếu email tồn tại, bạn sẽ nhận được hướng dẫn đặt lại mật khẩu");

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
        tokenRepository.save(resetToken);

        String resetUrl = "http://localhost:8080/reset-password?token=" + token;
        emailService.sendPasswordReset(email, resetUrl);
        return ApiResponse.success("Email đặt lại mật khẩu đã được gửi");
    }

    @Transactional
    public ApiResponse<String> resetPassword(String token, String newPassword) {
        PasswordResetToken rt = tokenRepository.findByToken(token).orElse(null);
        if (rt == null || rt.getUsed() || rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ApiResponse.error("Token không hợp lệ hoặc đã hết hạn");
        }
        User user = rt.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        rt.setUsed(true);
        tokenRepository.save(rt);
        return ApiResponse.success("Đặt lại mật khẩu thành công");
    }

    @Transactional
    public ApiResponse<String> changePassword(Long userId, String oldPass, String newPass) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ApiResponse.error("Không tìm thấy người dùng");
        if (!passwordEncoder.matches(oldPass, user.getPasswordHash())) {
            return ApiResponse.error("Mật khẩu cũ không đúng");
        }
        user.setPasswordHash(passwordEncoder.encode(newPass));
        userRepository.save(user);
        return ApiResponse.success("Đổi mật khẩu thành công");
    }
}
