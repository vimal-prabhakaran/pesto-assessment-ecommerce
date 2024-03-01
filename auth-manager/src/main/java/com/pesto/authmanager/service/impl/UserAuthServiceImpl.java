package com.pesto.authmanager.service.impl;

import com.pesto.authmanager.security.JwtUtil;
import com.pesto.authmanager.service.UserAuthService;
import com.pesto.ecomm.common.lib.dto.auth.LoginRequestDTO;
import com.pesto.ecomm.common.lib.dto.auth.LoginResponseDTO;
import com.pesto.ecomm.common.lib.dto.auth.UserRegisterRequestDTO;
import com.pesto.ecomm.common.lib.dto.auth.UserResponseDTO;
import com.pesto.ecomm.common.lib.entity.User;
import com.pesto.ecomm.common.lib.enums.Role;
import com.pesto.ecomm.common.lib.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;


    @Override
    public UserResponseDTO registerUser(UserRegisterRequestDTO registerRequestDTO) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        User newUser = new User();
        newUser.setEmail(registerRequestDTO.getEmail());
        newUser.setUserName(registerRequestDTO.getUserName());
        newUser.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        if (Objects.isNull(registerRequestDTO.getIsSeller()) || !registerRequestDTO.getIsSeller())
            newUser.setRole(Role.BUYER);
        else
            newUser.setRole(Role.SELLER);
        User userEntity = userRepository.save(newUser);
        responseDTO.setEmail(userEntity.getEmail());
        responseDTO.setUserName(userEntity.getUserName());
        responseDTO.setUserId(userEntity.getUserId());
        responseDTO.setUserRole(userEntity.getRole());
        return responseDTO;
    }

    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUserName(), loginRequestDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User userEntity = userRepository.findByUserName(loginRequestDTO.getUserName());
        if (Objects.isNull(userEntity)) {
            return null;
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEntity.getUserName());
        String token = jwtUtil.generateToken(userDetails);
        LoginResponseDTO responseDto = new LoginResponseDTO();
        responseDto.setUserId(userEntity.getUserId());
        responseDto.setUserName(userEntity.getUserName());
        responseDto.setToken(token);
        responseDto.setEmail(userEntity.getEmail());
        responseDto.setUserRole(userEntity.getRole());
        return responseDto;
    }
}
