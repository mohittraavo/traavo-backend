package com.travo.tcms.platform.dto;

public record AuthResponse(String token, AuthUserResponse user) {
}
