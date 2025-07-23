package org.example.service;

import org.example.dto.AuthRequest;
import org.example.dto.AuthResponse;
import org.example.dto.RefreshTokenRequest;
import org.example.dto.UserRegistrationDto;
import org.example.model.RefreshToken;
import org.example.model.User;
import org.example.repository.RefreshTokenRepository;
import org.example.repository.UserRepository;
import org.example.security.JwtTokenUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtTokenUtil jwtTokenUtil,
                       PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public AuthResponse login(AuthRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        // Сохраняем refresh токен в БД
        saveRefreshToken(userDetails.getUsername(), refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }


    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Проверяем наличие токена в БД
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // Проверяем срок действия
        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        // Получаем данные пользователя
        UserDetails userDetails = userDetailsService.loadUserByUsername(
                jwtTokenUtil.extractUsername(refreshToken));

        // Генерируем новый access токен
        String newAccessToken = jwtTokenUtil.generateAccessToken(userDetails);

        return new AuthResponse(newAccessToken, refreshToken);
    }

    private void saveRefreshToken(String username, String refreshToken) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        User user = (User) userDetails;

        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setUserId(user.getId());
        newRefreshToken.setToken(refreshToken);
        newRefreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(newRefreshToken);
    }

    @Transactional
    public void register(UserRegistrationDto registrationDto) {
        if (userRepository.existsByLogin(registrationDto.getLogin())) {
            throw new DuplicateKeyException("Login already exists");
        }

        User user = new User();
        user.setLogin(registrationDto.getLogin());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFullName(registrationDto.getFullName());

        userRepository.save(user);
    }
}
