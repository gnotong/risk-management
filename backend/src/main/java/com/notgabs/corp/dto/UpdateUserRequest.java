package com.notgabs.corp.dto;

import com.notgabs.corp.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateUserRequest {
    
    @NotBlank(message = "Username is required")
    public String username;
    
    public String password; // Nullable to keep current password
    
    @NotBlank(message = "First name is required")
    public String firstName;
    
    @NotBlank(message = "Last name is required")
    public String lastName;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    public String email;
    
    @NotNull(message = "Role is required")
    public Role role;
    
    public Boolean isActive; // Allow toggling user status
}
