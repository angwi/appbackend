package com.lauretteacademy.appbackend.authservice;

import com.lauretteacademy.appbackend.Repository.UserRepository;
import com.lauretteacademy.appbackend.authcontroller.AuthenticationRequest;
import com.lauretteacademy.appbackend.authcontroller.AuthenticationResponse;
import com.lauretteacademy.appbackend.authcontroller.RegisterRequest;
import com.lauretteacademy.appbackend.jwtutil.JwtService;
import com.lauretteacademy.appbackend.model.AppUser;
import com.lauretteacademy.appbackend.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {
        var appUser = AppUser.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(appUser);
        var jwtToken = jwtService.generateToken(appUser);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),
                request.getPassword()));
        var appUser = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(appUser);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
