package com.travo.tcms.platform.dto;

import java.util.List;

public record AuthUserResponse(Long userId, Long tenantId, String fullName, String email, String role, List<String> permissions) {
}
