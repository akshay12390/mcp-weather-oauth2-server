package org.springframework.ai.mcp.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;

@RestController
public class AuthorizationServerMetadataController {

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @GetMapping("/.well-known/oauth-authorization-server")
    public Map<String, Object> getMetadata() {
        String baseUrl = String.format("http://localhost:%d%s", serverPort, contextPath);
        
        Map<String, Object> metadata = new HashMap<>();
        
        // Required fields
        metadata.put("issuer", baseUrl);
        metadata.put("authorization_endpoint", baseUrl + "/oauth2/authorize");
        metadata.put("token_endpoint", baseUrl + "/oauth2/token");
        metadata.put("registration_endpoint", baseUrl + "/oauth2/register");
        
        // Token endpoint authentication methods
        metadata.put("token_endpoint_auth_methods_supported", 
            new String[]{"client_secret_basic", "client_secret_post"});
        
        // Response types and grant types
        metadata.put("response_types_supported", 
            new String[]{"code", "token"});
        metadata.put("grant_types_supported", 
            new String[]{"authorization_code", "refresh_token", "client_credentials"});
        
        // Scopes
        metadata.put("scopes_supported", 
            new String[]{"openid", "profile", "email"});
        
        // PKCE
        metadata.put("code_challenge_methods_supported", 
            new String[]{"plain", "S256"});
        
        // Additional endpoints
        metadata.put("revocation_endpoint", baseUrl + "/oauth2/revoke");
        metadata.put("introspection_endpoint", baseUrl + "/oauth2/introspect");
        
        return metadata;
    }
} 