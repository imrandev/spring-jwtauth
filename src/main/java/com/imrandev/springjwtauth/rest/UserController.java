package com.imrandev.springjwtauth.rest;

import com.imrandev.springjwtauth.model.AuthRequest;
import com.imrandev.springjwtauth.model.AuthResponse;
import com.imrandev.springjwtauth.services.RestUserDetailsService;
import com.imrandev.springjwtauth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtTokenUtil;
    private final RestUserDetailsService userDetailsService;

    @Autowired
    public UserController(AuthenticationManager authenticationManager, JwtUtil jwtTokenUtil, RestUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @RequestMapping({ "/admin" })
    public ResponseEntity<?> getAdmin() {
        return ResponseEntity.ok(userDetailsService.loadUserByUsername("admin"));
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
