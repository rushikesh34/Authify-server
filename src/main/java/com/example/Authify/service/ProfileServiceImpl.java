package com.example.Authify.service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.Authify.Entity.UserEntity;
import com.example.Authify.io.ProfileRequest;
import com.example.Authify.io.ProfileResponse;
import com.example.Authify.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
	
	@Autowired
	private UserRepository userrepository;
	
	private final PasswordEncoder passwordencoder;
	private final EmailService emailservice;
	

	@Override
	public ProfileResponse CreateProfile(ProfileRequest request) {
		UserEntity NewProfile=ConvertToUserEntity(request);
		if(!userrepository.existsByEmail(request.getEmail())) {
			NewProfile=userrepository.save(NewProfile);
			return ConverToProfileResponse(NewProfile) ;
		}
		
		throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exits");
		
		
	}
	
	
	private ProfileResponse ConverToProfileResponse(UserEntity newprofile) {
		return ProfileResponse.builder()
				.email(newprofile.getEmail())
				.name(newprofile.getName())
				.userid(newprofile.getUserid())
				.accountVerified(newprofile.getAccountVerified())
				.build();
	}
	
	private UserEntity ConvertToUserEntity(ProfileRequest request) {
		return UserEntity.builder()
		.email(request.getEmail())
		.userid(UUID.randomUUID().toString())
		.name(request.getName())
		.password(passwordencoder.encode(request.getPassword()))
		.accountVerified(false)
		.resetOtpExpiredAt(0L)
		.verifyOtp(null)
		.verifyOtpExpiredAt(0L)
		.resetOtp(null)
		.build();
	}


	@Override
	public ProfileResponse getProfile(String email) {
		UserEntity existingprofile=userrepository.findByEmail(email)
				.orElseThrow(()->new UsernameNotFoundException("user not found"+email));
		return ConverToProfileResponse(existingprofile);
	}


	@Override
	public void sendPasswordRestOtp(String email) {
		UserEntity existingUser=userrepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found exception"+email));
		
//		create otp
		
		String otp=String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
		
//		calculate expiry time (currenttime + 15 min in milliseconds)
		
		long expiryTime=System.currentTimeMillis()+(15*60*1000);
		
		existingUser.setResetOtp(otp);
		existingUser.setResetOtpExpiredAt(expiryTime);
		userrepository.save(existingUser);
		
		try {
			emailservice.sendResetOtpEmail(existingUser.getEmail(), otp);
//			emailservice.sendOtpEmail(existingUser.getEmail(),otp, "reset");
			
		}catch(Exception ex) {
			throw new RuntimeException("unable to send email");
		}
		
	}


	@Override
	public void resetPassword(String email, String otp, String password) {
		UserEntity user=userrepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found exception"+email));
		if(!user.getResetOtp().equals(otp) || user.getResetOtp()==null) {
			throw new RuntimeException("Invalid otp");
			
		}
		
		 if(user.getResetOtpExpiredAt()< System.currentTimeMillis()) {
			throw new RuntimeException("otp expired");
		}
		 
		 user.setPassword(passwordencoder.encode(password));
		 user.setResetOtp(null);
		 user.setResetOtpExpiredAt(0L);
		 userrepository.save(user);

	}


	@Override
	public void sendvarificationOtp(String email) {
		UserEntity existingUser=userrepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found with email"+email));
		
		if(existingUser.getAccountVerified()!=null && existingUser.getAccountVerified() ) {
			throw new RuntimeException("Account Already verified...");
			
		}
		String otp=String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
		
		long expiryTime=System.currentTimeMillis()+(24*60*60*1000);
		
		existingUser.setVerifyOtp(otp);
		existingUser.setVerifyOtpExpiredAt(expiryTime);
		userrepository.save(existingUser);
		
		try {
			emailservice.sendVerifyOtpToEmail(email, otp);
//			emailservice.sendOtpEmail(email, otp, "verify"); 
		}catch(Exception e) {
			throw new RuntimeException("Enabled to send Otp");
		}
	}


	@Override
	public void verifyAccountVarifiactionOtp(String email, String otp) {
		UserEntity existingUser=userrepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found with email"+email));
		
		if(existingUser.getVerifyOtp()== null && !existingUser.getVerifyOtp().equals(otp)) {
			throw new RuntimeException("Otp not valid");
		}
		
		if(existingUser.getVerifyOtpExpiredAt()<System.currentTimeMillis()) {
			throw new RuntimeException("Otp expired..");
		}
		
		
		
		existingUser.setAccountVerified(true);
		existingUser.setVerifyOtp(null);
		existingUser.setVerifyOtpExpiredAt(0L);
		
		userrepository.save(existingUser);
		
	}
	
	

}
