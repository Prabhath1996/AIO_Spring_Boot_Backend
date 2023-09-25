package com.prabhath.AIO_Backend.Controller.Auth;

import com.prabhath.AIO_Backend.auth.AuthenticationRequest;
import com.prabhath.AIO_Backend.auth.AuthenticationResponse;
import com.prabhath.AIO_Backend.auth.AuthenticationService;
import com.prabhath.AIO_Backend.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        System.out.println(request);
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/test")
    public String test(){
        return "test api";
    }


}
