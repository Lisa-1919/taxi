package com.modsen.account.client;

import com.modsen.account.config.RetrieveMessageErrorDecoder;
import jakarta.ws.rs.core.MediaType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(
        name = "keycloak",
        url = "http://keycloak:8080",
        configuration = RetrieveMessageErrorDecoder.class
)
public interface KeycloakClient {

    @PostMapping(value = "/realms/taxi/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED
    )
    Map<String, Object> getToken(@RequestBody Map<String, ?> data);

}
