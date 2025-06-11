package com.example.Authify.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Authify.Entity.UserEntity;
import com.example.Authify.repository.UserRepository;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class AppUserDetailService implements UserDetailsService{
	
	@Autowired
	private UserRepository userreository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity existingUser= userreository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Email not found"+email));
		return new User(existingUser.getEmail(),existingUser.getPassword(),new ArrayList<>());
		
	}
	
}