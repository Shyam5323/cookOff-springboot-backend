package com.uni.cookoff.controllers;

import com.uni.cookoff.Exception.UserException;
import com.uni.cookoff.config.JwtProvider;
import com.uni.cookoff.dto.request.LoginRequest;
import com.uni.cookoff.dto.response.AuthResponse;
import com.uni.cookoff.models.User;
import com.uni.cookoff.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;


@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException
    {
        String id = UuidCreator.getTimeOrdered().toString();
        String email = user.getEmail();
        String password = user.getPassword();
        String name = user.getName();
        String regNo = user.getRegNo();
        String role = "user" ;
        int roundQualified = 0;
        double score = 0.0;
        boolean isBanned = false;


        Optional<User> isEmailExist = userRepository.findByEmail(email);
        if(isEmailExist.isPresent())
        {
            throw new UserException("Email already used with another Account");
        }

        User createdUser = new User();

        createdUser.setId(id);
        createdUser.setEmail(email);
        createdUser.setPassword(passwordEncoder.encode(password));
        createdUser.setName(name);
        createdUser.setRegNo(regNo);
        createdUser.setRole(role);
        createdUser.setScore(score);
        createdUser.setBanned(isBanned);
        createdUser.setRoundQualified(roundQualified);

        User savedUser = userRepository.save(createdUser);


        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(),savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Signup Sucess...");

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUserHandler(@RequestBody LoginRequest loginRequest)
    {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(username , password);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Signin Sucess...");

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);

    }

    private Authentication authenticate(String username, String password) {
        Optional<User> userDetails = userRepository.findByEmail(username);

        if(userDetails == null)
        {
            throw new BadCredentialsException("invalid username");
        }

        User user = userDetails.get();

        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new BadCredentialsException("Invalid Password...");
        }

        return new UsernamePasswordAuthenticationToken(userDetails,null,user.getAuthorities());
    }

}
