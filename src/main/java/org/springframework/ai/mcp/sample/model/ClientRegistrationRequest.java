package org.springframework.ai.mcp.sample.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientRegistrationRequest {
    
    @NotBlank
    private String clientName;
    
    @NotEmpty
    private Set<String> redirectUris;
    
    private String clientUri;
    private String logoUri;
    private List<String> grantTypes;
    private List<String> responseTypes;
    private List<String> scopes;
    private String tokenEndpointAuthMethod;
    
    // Getters and Setters
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public Set<String> getRedirectUris() {
        return redirectUris;
    }
    
    public void setRedirectUris(Set<String> redirectUris) {
        this.redirectUris = redirectUris;
    }
    
    public String getClientUri() {
        return clientUri;
    }
    
    public void setClientUri(String clientUri) {
        this.clientUri = clientUri;
    }
    
    public String getLogoUri() {
        return logoUri;
    }
    
    public void setLogoUri(String logoUri) {
        this.logoUri = logoUri;
    }
    
    public List<String> getGrantTypes() {
        return grantTypes;
    }
    
    public void setGrantTypes(List<String> grantTypes) {
        this.grantTypes = grantTypes;
    }
    
    public List<String> getResponseTypes() {
        return responseTypes;
    }
    
    public void setResponseTypes(List<String> responseTypes) {
        this.responseTypes = responseTypes;
    }
    
    public List<String> getScopes() {
        return scopes;
    }
    
    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
    
    public String getTokenEndpointAuthMethod() {
        return tokenEndpointAuthMethod;
    }
    
    public void setTokenEndpointAuthMethod(String tokenEndpointAuthMethod) {
        this.tokenEndpointAuthMethod = tokenEndpointAuthMethod;
    }
} 