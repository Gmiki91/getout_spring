package com.blue.getout.auth;

public record ResetPasswordRequest(String token,String password) {
}
