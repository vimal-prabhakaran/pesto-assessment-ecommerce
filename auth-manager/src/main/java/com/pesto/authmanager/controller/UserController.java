package com.pesto.authmanager.controller;

import com.pesto.authmanager.service.UserAuthService;
import com.pesto.ecomm.common.lib.dto.auth.LoginResponseDTO;
import com.pesto.ecomm.common.lib.dto.auth.UserRegisterRequestDTO;
import com.pesto.ecomm.common.lib.dto.auth.UserResponseDTO;
import com.pesto.ecomm.common.lib.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/register")
    public ResponseEntity<String> adminRegister(@RequestBody UserRegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByUserName(registerRequestDTO.getUserName())) {
            return new ResponseEntity<String>("Username is already taken !", HttpStatus.BAD_REQUEST);
        }
        UserResponseDTO responseDTO = userAuthService.registerUser(registerRequestDTO);
        if (Objects.nonNull(responseDTO))
            return new ResponseEntity<String>("User Registration successful ! ", HttpStatus.CREATED);
        return new ResponseEntity<String>("Internal Server Error! Please try again later!", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody UserRegisterRequestDTO registerRequestDTO) throws LoginException {
        LoginResponseDTO loginResponseDTO = userAuthService.loginUser(registerRequestDTO);
        if (Objects.isNull(loginResponseDTO))
            throw new LoginException("Invalid Credentials");
        return new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);
    }

}