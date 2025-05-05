package org.springframework.ai.mcp.sample.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.mcp.sample.entity.RegisteredClientEntity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JpaRegisteredClientRepository implements RegisteredClientRepository {
    private final RegisteredClientJpaRepository clientRepository;
    private final ObjectMapper objectMapper;

    public JpaRegisteredClientRepository(RegisteredClientJpaRepository clientRepository, ObjectMapper objectMapper) {
        Assert.notNull(clientRepository, "clientRepository cannot be null");
        Assert.notNull(objectMapper, "objectMapper cannot be null");
        this.clientRepository = clientRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        
        RegisteredClientEntity entity = toEntity(registeredClient);
        clientRepository.save(entity);
    }

    @Override
    public RegisteredClient findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        return clientRepository.findById(id)
                .map(this::toObject)
                .orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Assert.hasText(clientId, "clientId cannot be empty");
        return clientRepository.findByClientId(clientId)
                .map(this::toObject)
                .orElse(null);
    }

    public void deleteByClientId(String clientId) {
        Assert.hasText(clientId, "clientId cannot be empty");
        clientRepository.deleteByClientId(clientId);
    }

    private RegisteredClientEntity toEntity(RegisteredClient registeredClient) {
        RegisteredClientEntity entity = new RegisteredClientEntity();
        entity.setId(registeredClient.getId());
        entity.setClientId(registeredClient.getClientId());
        entity.setClientIdIssuedAt(registeredClient.getClientIdIssuedAt());
        entity.setClientSecret(registeredClient.getClientSecret());
        entity.setClientSecretExpiresAt(registeredClient.getClientSecretExpiresAt());
        entity.setClientName(registeredClient.getClientName());
        
        entity.setClientAuthenticationMethods(
                StringUtils.collectionToCommaDelimitedString(
                        registeredClient.getClientAuthenticationMethods().stream()
                                .map(ClientAuthenticationMethod::getValue)
                                .collect(Collectors.toSet())));
        
        entity.setAuthorizationGrantTypes(
                StringUtils.collectionToCommaDelimitedString(
                        registeredClient.getAuthorizationGrantTypes().stream()
                                .map(AuthorizationGrantType::getValue)
                                .collect(Collectors.toSet())));
        
        entity.setRedirectUris(
                StringUtils.collectionToCommaDelimitedString(registeredClient.getRedirectUris()));
        
        entity.setScopes(
                StringUtils.collectionToCommaDelimitedString(registeredClient.getScopes()));

        try {
            entity.setClientSettings(objectMapper.writeValueAsString(registeredClient.getClientSettings().getSettings()));
            entity.setTokenSettings(objectMapper.writeValueAsString(registeredClient.getTokenSettings().getSettings()));
        } catch (Exception e) {
            throw new RuntimeException("Error serializing client settings", e);
        }

        return entity;
    }

    private RegisteredClient toObject(RegisteredClientEntity entity) {
        Set<ClientAuthenticationMethod> clientAuthenticationMethods = Arrays.stream(
                StringUtils.commaDelimitedListToStringArray(entity.getClientAuthenticationMethods()))
                .map(ClientAuthenticationMethod::new)
                .collect(Collectors.toSet());

        Set<AuthorizationGrantType> authorizationGrantTypes = Arrays.stream(
                StringUtils.commaDelimitedListToStringArray(entity.getAuthorizationGrantTypes()))
                .map(AuthorizationGrantType::new)
                .collect(Collectors.toSet());

        RegisteredClient.Builder builder = RegisteredClient.withId(entity.getId())
                .clientId(entity.getClientId())
                .clientIdIssuedAt(entity.getClientIdIssuedAt())
                .clientSecret(entity.getClientSecret())
                .clientSecretExpiresAt(entity.getClientSecretExpiresAt())
                .clientName(entity.getClientName())
                .clientAuthenticationMethods(methods -> methods.addAll(clientAuthenticationMethods))
                .authorizationGrantTypes(types -> types.addAll(authorizationGrantTypes))
                .redirectUris(uris -> uris.addAll(
                        Arrays.asList(StringUtils.commaDelimitedListToStringArray(entity.getRedirectUris()))))
                .scopes(scopes -> scopes.addAll(
                        Arrays.asList(StringUtils.commaDelimitedListToStringArray(entity.getScopes()))));

        try {
            Map<String, Object> clientSettings = objectMapper.readValue(
                    entity.getClientSettings(), new TypeReference<Map<String, Object>>() {});
            builder.clientSettings(ClientSettings.withSettings(clientSettings).build());

            Map<String, Object> tokenSettings = objectMapper.readValue(
                    entity.getTokenSettings(), new TypeReference<Map<String, Object>>() {});
            builder.tokenSettings(TokenSettings.withSettings(tokenSettings).build());
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing settings", e);
        }

        return builder.build();
    }
} 