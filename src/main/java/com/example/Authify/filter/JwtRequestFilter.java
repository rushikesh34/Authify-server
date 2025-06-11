package com.example.Authify.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.Authify.service.AppUserDetailService;
import com.example.Authify.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter  {
	

	private final JwtUtil jwtutil;
	private final AppUserDetailService appuserdetailservice;
	private static final List<String> PUBLIC_URLS=	List.of("/login","/register","/send-reset-otp","/reset-password","/logout");
	

	final 
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String path =request.getServletPath();
		
		if(PUBLIC_URLS.contains(path)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String jwt=null;
		String email=null;
		
//		check authorization header
		
		final String authorizationheader=request.getHeader("Authorization");
		
		if(authorizationheader != null && authorizationheader.startsWith("bearer ")) {
			jwt=authorizationheader.substring(7);
		}
		
//		if jwt token not found in header then check in the cookie
		
		if(jwt == null) {
			Cookie[] cookies=request.getCookies();
			if(cookies != null) {
				for(Cookie cookie : cookies) {
					if("jwt".equals(cookie.getName())) {
						jwt=cookie.getValue();
						break;
						
					}
				}
			}
			
		}
		
		
//		validate the token and set the security context
		
		if(jwt!=null) {
			email=jwtutil.extractEmaik(jwt);
			if(email != null && SecurityContextHolder.getContext().getAuthentication()== null) {
				
				UserDetails userdetail=appuserdetailservice.loadUserByUsername(email);
				if(jwtutil.validatetoken(jwt, userdetail)) {
					UsernamePasswordAuthenticationToken authenticationtoken=new UsernamePasswordAuthenticationToken(userdetail,null,userdetail.getAuthorities());
					authenticationtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationtoken);
				}
				
			}
		}
		
		
		filterChain.doFilter(request, response);
		
	
	}

}
