package org.springframework.ai.mcp.sample.controller;

import jakarta.validation.Valid;
import org.springframework.ai.mcp.sample.model.ClientRegistrationRequest;
import org.springframework.ai.mcp.sample.model.ClientRegistrationResponse;
import org.springframework.ai.mcp.sample.service.ClientRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth2/register")
public class ClientRegistrationController {

    private final ClientRegistrationService clientRegistrationService;

    public ClientRegistrationController(ClientRegistrationService clientRegistrationService) {
        this.clientRegistrationService = clientRegistrationService;
    }

    @PostMapping
    public ResponseEntity<ClientRegistrationResponse> registerClient(@Valid @RequestBody ClientRegistrationRequest request) {
        ClientRegistrationResponse response = clientRegistrationService.registerClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientRegistrationResponse> getClient(
            @PathVariable String clientId,
            @RequestHeader("Authorization") String authorization) {
        
        String token = extractToken(authorization);
        if (!clientRegistrationService.validateRegistrationAccessToken(clientId, token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        RegisteredClient client = clientRegistrationService.getClient(clientId);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }

        ClientRegistrationResponse response = new ClientRegistrationResponse();
        response.setClientId(client.getClientId());
        response.setClientName(client.getClientName());
        response.setRedirectUris(client.getRedirectUris());
        response.setGrantTypes(client.getAuthorizationGrantTypes().stream()
                .map(grantType -> grantType.getValue())
                .toList());
        response.setClientIdIssuedAt(client.getClientIdIssuedAt());
        response.setClientUri(client.getClientSettings().getSetting("client_uri"));
        response.setLogoUri(client.getClientSettings().getSetting("logo_uri"));
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(
            @PathVariable String clientId,
            @RequestHeader("Authorization") String authorization) {
        
        String token = extractToken(authorization);
        if (!clientRegistrationService.validateRegistrationAccessToken(clientId, token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        clientRegistrationService.deleteClient(clientId);
        return ResponseEntity.noContent().build();
    }

    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
} 