package com.example.Authify.controller;


import java.io.Console;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.Authify.io.AuthRequest;
import com.example.Authify.io.AuthResponse;
import com.example.Authify.io.PasswordResetRequest;
import com.example.Authify.service.AppUserDetailService;
import com.example.Authify.service.ProfileService;
import com.example.Authify.util.JwtUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor

public class AuthController {
	
	@Autowired
	private AuthenticationManager authenticationmanager;
	@Autowired
	private AppUserDetailService appuserdetailservice;
	private final ProfileService profileService;
	
	@Autowired
	private  JwtUtil jwtutil;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest request){
		try {

			authenticate(request.getEmail(),request.getPassword());
			
			final UserDetails userdetails=appuserdetailservice.loadUserByUsername(request.getEmail());
			
				
			final String jwtToken=jwtutil.generatetoken(userdetails);
			ResponseCookie cookie=ResponseCookie.from("jwt",jwtToken)
					.httpOnly(true)
					.path("/")
					.maxAge(Duration.ofDays(1))
					.sameSite("STRICT")
					.build();
	
			return  ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new AuthResponse(request.getEmail(),jwtToken));
			
			
		}catch(BadCredentialsException e) {
			Map<String, Object>error=new HashMap<>();
			error.put("error", true);
			error.put("message", "Email or password is incorrect");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}catch(DisabledException e) {
			Map<String, Object>error=new HashMap<>();
			error.put("error", true);
			error.put("message", "Account disabled");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}catch(Exception e) {
			Map<String, Object>error=new HashMap<>();
			error.put("error",e.getMessage());
			error.put("message", "Authentication failed");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
			
		}
		
		
		
		
	}

	private void authenticate(String email, String password) {
		 authenticationmanager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		
	}
	
	@GetMapping("/is-authenticated")
	public ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(expression = "Authentication?.name") String email) {
		return ResponseEntity.ok(email!=null);
	}
	
	@PostMapping("/send-reset-otp")
	public void sendresetotp(@RequestParam String email) {
		try {
			profileService.sendPasswordRestOtp(email);
		}catch(Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage());
		}
	}
	
	@PostMapping("/reset-password")
	public void resetPassword(@Valid @RequestBody PasswordResetRequest passwordresetrequest){
		
		try {
			profileService.resetPassword(passwordresetrequest.getEmail(), passwordresetrequest.getOtp(), passwordresetrequest.getNewPassword());
			
		}catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
		}
	}
	
	@PostMapping("/send-verify-otp")
	public void sendOtp(@CurrentSecurityContext(expression = "Authentication?.name")String email) {
		try {
			profileService.sendvarificationOtp(email);
		}catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
		}
	}
	
	@PostMapping("/verfiy-otp")
	public void verifyOtp(@RequestBody Map<String,Object>request,@CurrentSecurityContext(expression = "Authentication?.name")String email) {
		if(request.get("otp").toString()==null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Missing details ");
		}
		try {
			profileService.verifyAccountVarifiactionOtp(email, request.get("otp").toString());
		}catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
		}
		
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(){
		ResponseCookie cookie=ResponseCookie.from("jwt","")
				.httpOnly(true)
				.secure(false)
				.path("/")
				.maxAge(0)
				.sameSite("strict")
				.build();
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("Logged out successfully!");
				}
	

}
	
