package ge.ol.ping.session;

import ge.ol.ping.boundary.AuthResource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class AuthSession {

    private static final Logger LOGGER = Logger.getLogger(AuthResource.class.getName());

    @PersistenceContext
    EntityManager entityManager;
    @Inject
    private SecurityContext securityContext;

    public Response login() {
        LOGGER.log(Level.INFO, "login");
        if (securityContext.getCallerPrincipal() != null) {
            JsonObject result = Json.createObjectBuilder()
                    .add("user", securityContext.getCallerPrincipal().getName())
                    .build();
            return Response.ok(result).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    public String expectedPassword(String caller) {
        return entityManager.createQuery("SELECT a.password FROM AppUser a WHERE a.username = :caller", String.class)
                .setParameter("caller", caller)
                .getSingleResult();
    }

    public Set<String> getRoles(String caller) {
        List<String> roles = entityManager.createQuery("SELECT r.name FROM Role r INNER JOIN r.appUsers a WHERE a.username = :username", String.class)
                .setParameter("username", caller)
                .getResultList();
        return new HashSet<>(roles);
    }
}
