package com.example.Authify.service;

import com.example.Authify.io.ProfileRequest;
import com.example.Authify.io.ProfileResponse;

public interface  ProfileService {
	
	ProfileResponse CreateProfile(ProfileRequest request);
	
	ProfileResponse getProfile(String email);
	
	void sendPasswordRestOtp(String email);
	
	void resetPassword(String email,String otp,String password);
	
	void sendvarificationOtp(String email);
	 
	void verifyAccountVarifiactionOtp(String email,String otp);

}
