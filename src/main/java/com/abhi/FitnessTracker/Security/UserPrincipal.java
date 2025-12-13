package com.abhi.FitnessTracker.Security;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User principal for authentication context.
 */
@Data
@AllArgsConstructor
public class UserPrincipal {
    private String userId;
    private String email;
    private boolean admin;
}
