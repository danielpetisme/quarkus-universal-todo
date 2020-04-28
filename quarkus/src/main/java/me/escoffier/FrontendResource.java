package me.escoffier;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

@Path("/")
public class FrontendResource {

    @Inject
    @ResourcePath("keycloak.js")
    Template keycloak;

    @ConfigProperty(name = "app.frontend.oidc-server")
    String oicdServer;

    @ConfigProperty(name = "app.frontend.oidc-app")
    String appName;

    @ConfigProperty(name = "app.frontend.oidc-realm")
    String realm;

    @GET
    @Path("/keycloak.js")
    @Produces(MediaType.TEXT_PLAIN)
    public TemplateInstance get() {
        return keycloak
                .data("oicd", oicdServer)
                .data("clientId", appName)
                .data("realm", realm);
    }

    @GET
    public Response index() {
        URI redirect = UriBuilder.fromUri("todo.html").build();
        return Response.temporaryRedirect(redirect).build();
    }

}
