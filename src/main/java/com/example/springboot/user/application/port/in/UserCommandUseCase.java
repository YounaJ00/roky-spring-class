package com.example.springboot.user.application.port.in;

import com.example.springboot.user.application.port.in.dto.LoginCommand;
import com.example.springboot.user.application.port.in.dto.LoginResult;
import com.example.springboot.user.application.port.in.dto.SignupCommand;
import com.example.springboot.user.application.port.in.dto.UserResult;

public interface UserCommandUseCase {

    UserResult signup(SignupCommand command);

    LoginResult login(LoginCommand command);
}
