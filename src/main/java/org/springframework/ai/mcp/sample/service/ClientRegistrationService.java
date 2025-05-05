package org.springframework.ai.mcp.sample.service;

import org.springframework.ai.mcp.sample.model.ClientRegistrationRequest;
import org.springframework.ai.mcp.sample.model.ClientRegistrationResponse;
import org.springframework.ai.mcp.sample.repository.JpaRegisteredClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientRegistrationService {

    private final JpaRegisteredClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientRegistrationService(JpaRegisteredClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ClientRegistrationResponse registerClient(ClientRegistrationRequest request) {
        String clientId = UUID.randomUUID().toString();
        String clientSecret = UUID.randomUUID().toString();
        String registrationAccessToken = UUID.randomUUID().toString();
        
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(passwordEncoder.encode(clientSecret))
                .clientIdIssuedAt(Instant.now())
                .clientName(request.getClientName())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUris(uris -> uris.addAll(request.getRedirectUris()))
                .scopes(scopes -> {
                    if (request.getScopes() != null) {
                        scopes.addAll(request.getScopes());
                    }
                })
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .requireProofKey(false)
                        .setting("client_uri", request.getClientUri())
                        .setting("logo_uri", request.getLogoUri())
                        .setting("registration_access_token", registrationAccessToken)
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))
                        .refreshTokenTimeToLive(Duration.ofDays(30))
                        .build())
                .build();

        clientRepository.save(registeredClient);

        ClientRegistrationResponse response = new ClientRegistrationResponse();
        response.setClientId(clientId);
        response.setClientSecret(clientSecret);
        response.setClientIdIssuedAt(Instant.now());
        response.setClientName(request.getClientName());
        response.setRedirectUris(request.getRedirectUris());
        response.setClientUri(request.getClientUri());
        response.setLogoUri(request.getLogoUri());
        response.setScopes(request.getScopes());
        response.setGrantTypes(registeredClient.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue)
                .collect(Collectors.toList()));
        
        response.setRegistrationAccessToken(registrationAccessToken);
        response.setRegistrationClientUri("/oauth2/register/" + clientId);

        return response;
    }

    public RegisteredClient getClient(String clientId) {
        return clientRepository.findByClientId(clientId);
    }

    public void deleteClient(String clientId) {
        clientRepository.deleteByClientId(clientId);
    }

    public boolean validateRegistrationAccessToken(String clientId, String token) {
        RegisteredClient client = getClient(clientId);
        if (client == null) {
            return false;
        }
        
        String storedToken = client.getClientSettings().getSetting("registration_access_token");
        return storedToken != null && storedToken.equals(token);
    }
} 