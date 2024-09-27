package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.ApprovalRequest;
import com.example.demo.domain.User;
import com.example.demo.domain.Usershow;
import com.example.demo.service.ApprovalRequestService;
import com.example.demo.service.LoginService;
import com.example.demo.service.UserService;
import com.example.demo.util.AuthRequest;
import com.example.demo.util.JwtUtil;

@RestController
@RequestMapping("/admin")
@CrossOrigin("http://localhost:4200")
public class AdminController {

		
		@Autowired
		private UserService service;
		
		@Autowired
		private LoginService lservice;
		
	    @Autowired
	    private AuthenticationManager authenticationManager;

	    @Autowired
	    private JwtUtil jwtUtil;
	    
	    @Autowired
	    private UserDetailsService userDetailsService;
		
	    @Autowired
	    private ApprovalRequestService appRequestService;
		
	    @PreAuthorize("hasRole('ADMIN')")
		//shows every request made to Admin regarding user account 
		@GetMapping(value = "/showRequests")
		public List<ApprovalRequest> showUnapproved() {
		    return appRequestService.requests();
		}
		
		//Approval, Rejection and Closing of user account
	    @PreAuthorize("hasRole('ADMIN')")
	    @PutMapping("/approve-user-account/{requestId}")
	    public String approveAccount(@PathVariable int requestId) {
	    	return appRequestService.approveUserAccounts(requestId);
	    }
	    
	    @PreAuthorize("hasRole('ADMIN')")
	    @PutMapping("/reject-user-account/{requestId}")
	    public String RejectAccount(@PathVariable int requestId) {
	    	return appRequestService.RejectUserAccount(requestId);
	    }
	    
	    @PreAuthorize("hasRole('ADMIN')")
	    @PutMapping("/close-user-account/{requestId}")
	    public String closeuserAccount(@PathVariable int requestId) {
	    	return appRequestService.CloseUserAccounts(requestId);
	    }
	  
		//standardmethods
	    @PreAuthorize("hasRole('ADMIN')")
		@GetMapping(value = "/showAll")
		public List<Usershow> showAllUsers() {
		    return service.showAllUsers();
		}
		
	    @PreAuthorize("hasRole('ADMIN')")
		@GetMapping(value="/searchUser/{username}")
		public Usershow searchuser(@PathVariable String username) {
			return service.searchUser(username);
		}
		
	    @PreAuthorize("hasRole('ADMIN')")
		 @DeleteMapping("/delete/{username}")
		public String deleteUser(@PathVariable String username) {
			 return service.deleteUser(username);
		 }
		 
	    @PreAuthorize("hasRole('ADMIN')")
		 @GetMapping(value="/adminauth/{username}/{password}")
			public String adminAuth(@PathVariable String username,@PathVariable String password) {
				return lservice.adminauthenticate(username, password);
			}
	    
	    @CrossOrigin("http://localhost:4200")
	    @PostMapping("/generatetoken")
	    public String createToken(@RequestBody AuthRequest authRequest) throws Exception {
	        authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
	        );

	        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

	        boolean isAdmin = userDetails.getAuthorities().stream()
	            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

	        if (!isAdmin) {
	            throw new Exception("User is not authorized to generate the token");
	        }

	        return jwtUtil.generateToken(userDetails.getUsername());
	    }
		 
	}

