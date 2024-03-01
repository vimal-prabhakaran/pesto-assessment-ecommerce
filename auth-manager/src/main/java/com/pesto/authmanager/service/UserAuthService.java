package com.pesto.authmanager.service;

import com.pesto.ecomm.common.lib.dto.auth.LoginRequestDTO;
import com.pesto.ecomm.common.lib.dto.auth.LoginResponseDTO;
import com.pesto.ecomm.common.lib.dto.auth.UserRegisterRequestDTO;
import com.pesto.ecomm.common.lib.dto.auth.UserResponseDTO;

public interface UserAuthService {

    UserResponseDTO registerUser(UserRegisterRequestDTO registerRequestDTO);

    LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO);

}
