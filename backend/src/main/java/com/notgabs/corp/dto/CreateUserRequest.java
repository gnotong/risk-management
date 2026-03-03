package com.notgabs.corp.dto;

import com.notgabs.corp.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateUserRequest {
    
    @NotBlank(message = "Username is required")
    public String username;
    
    @NotBlank(message = "Password is required")
    public String password;
    
    @NotBlank(message = "First name is required")
    public String firstName;
    
    @NotBlank(message = "Last name is required")
    public String lastName;
    
    @Email(message = "Email should be valid")
    public String email;
    
    public Role role;
}
