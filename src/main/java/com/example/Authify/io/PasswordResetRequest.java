package com.example.Authify.io;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PasswordResetRequest {
	
	@NotBlank(message = "password should not be empty")
	private String newPassword;
	@NotBlank(message = "email should not be empty")
	private String email;
	@NotBlank(message = "otp should not be empty")
	private String otp;

}
