package com.shopEZ.ShopEazzy.controller;

import com.shopEZ.ShopEazzy.model.AppRole;
import com.shopEZ.ShopEazzy.model.Role;
import com.shopEZ.ShopEazzy.model.User;
import com.shopEZ.ShopEazzy.repository.RoleRepository;
import com.shopEZ.ShopEazzy.repository.UserRepository;
import com.shopEZ.ShopEazzy.security.jwt.JwtUtils;
import com.shopEZ.ShopEazzy.security.request.LoginRequest;
import com.shopEZ.ShopEazzy.security.request.SignUpRequest;
import com.shopEZ.ShopEazzy.security.response.MessageResponse;
import com.shopEZ.ShopEazzy.security.response.UserInfoResponse;
import com.shopEZ.ShopEazzy.security.service.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/sign_in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){

        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserName(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e){
            Map<String, Object> body = new HashMap<>();
            body.put("message", "BAD_CREDENTIALS");
            body.put("status", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        UserInfoResponse userInfoResponse = new UserInfoResponse(
                userDetails.getUserId(),
                userDetails.getUsername(),
                authorities);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(userInfoResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){

        if(userRepository.existsByUserName(signUpRequest.getUserName())){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: User name already exists!"));
        }
        if(userRepository.existsByEmail(signUpRequest.getEmail())){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: Email already exists!"));
        }

        User user = new User(signUpRequest.getUserName(),
                                signUpRequest.getEmail(),
                                passwordEncoder.encode(signUpRequest.getPassword()));

        Set<String> rolesFromRequest = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        if(rolesFromRequest == null){
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
        else{
           rolesFromRequest.forEach(role -> {
               switch(role){
                   case "admin":
                       Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                               .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                       roles.add(adminRole);
                       break;
                   case "seller":
                       Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                               .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                       roles.add(sellerRole);
                       break;
                   default:
                       Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                               .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                       roles.add(userRole);
               }
           });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/username")
    public String getUserName(Authentication authentication){
        return authentication != null ?
                authentication.getName() : "";
    }

    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> getUserDetails(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        UserInfoResponse userInfoResponse = new UserInfoResponse(
                userDetails.getUserId(),
                userDetails.getUsername(),
                authorities
        );
        return ResponseEntity.status(HttpStatus.OK).body(userInfoResponse);
    }

    @GetMapping("sign_out")
    public ResponseEntity<?> logoutUser(){

        ResponseCookie cookie = jwtUtils.cleanJwtCookie();
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out successfully."));
    }
}
