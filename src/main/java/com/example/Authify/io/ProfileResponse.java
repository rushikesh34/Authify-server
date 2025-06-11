package com.example.Authify.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder

public class ProfileResponse {
	
	private String userid;
	private String name;
	private String email;
	private boolean accountVerified;
	


}
