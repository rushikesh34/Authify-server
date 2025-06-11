package com.example.Authify.io;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProfileRequest {
	
	
	@NotBlank(message = "name should be not empty")
	private String name;
	@Email(message = "Enter valid email address")
	@NotNull(message = "Email should be not empty")
	private String email;
	@Size(min = 6,message = "minimum password should be atleast 6 characters")
	private String password;

}
