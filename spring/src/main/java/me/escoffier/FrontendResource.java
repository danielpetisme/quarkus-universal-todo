package me.escoffier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/")
public class FrontendResource {

    //    @Inject
//    @ResourcePath("keycloak.js")
//    Template keycloak;
//
    @Value("${app.frontend.oidc-server}")
    String oicdServer;

    @Value("${app.frontend.oidc-app}")
    String appName;

    @Value("${app.frontend.oidc-realm}")
    String realm;

    @GetMapping(path = "/keycloak.js", produces = "text/javascript")
    public String get() {
        return "const keycloak_options = { \"realm\": \"{realm}\", \"url\": \"{oidcServer}\", \"clientId\": \"{appName}\"};"
                .replace("{realm}", realm)
                .replace("{oidcServer}", oicdServer)
                .replace("{appName}", appName);
    }

    @GetMapping
    public ResponseEntity redirect() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("todo.html"));
        return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);
    }

}
