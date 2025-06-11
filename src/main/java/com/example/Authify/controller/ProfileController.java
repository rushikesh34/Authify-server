package com.example.Authify.controller;


import org.springframework.web.bind.annotation.RestController;

import com.example.Authify.io.ProfileRequest;
import com.example.Authify.io.ProfileResponse;
import com.example.Authify.service.EmailService;
import com.example.Authify.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequiredArgsConstructor
public class ProfileController {
	
	@Autowired
	private ProfileService profileservice;
	private final EmailService emailservice;	
	
	@PostMapping("/register")
//	@ResponseStatus()
	public ProfileResponse register(@Valid @RequestBody ProfileRequest request ) {
		
		ProfileResponse response= profileservice.CreateProfile(request);
		emailservice.sendWelcomeEmail(response.getEmail(), response.getName());
		return response;
		
	}
	
	@GetMapping("/profile")
	public ProfileResponse getuserdetail(@CurrentSecurityContext(expression = "authentication?.name") String email) {
		
		return profileservice.getProfile(email);
	}
	

}
