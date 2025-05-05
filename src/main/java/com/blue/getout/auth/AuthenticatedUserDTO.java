package com.blue.getout.auth;

import com.blue.getout.user.UserDTO;

public record AuthenticatedUserDTO(UserDTO user, String token) {
}
