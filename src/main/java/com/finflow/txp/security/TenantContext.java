package com.finflow.txp.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class TenantContext {

    public String tenantId(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Object tenant = jwtAuthenticationToken.getTokenAttributes().get("tenant_id");
            if (tenant != null) {
                return tenant.toString();
            }
        }
        return "demo-tenant";
    }
}
