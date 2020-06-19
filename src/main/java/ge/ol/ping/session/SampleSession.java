package ge.ol.ping.session;

import ge.ol.ping.boundary.SampleResource;
import ge.ol.ping.entity.AppUser;
import ge.ol.ping.entity.Role;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ge.ol.ping.security.Constants.ADMIN;
import static ge.ol.ping.security.Constants.USER;

@Stateless
public class SampleSession {

    private static final Logger LOGGER = Logger.getLogger(SampleResource.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private SecurityContext securityContext;

    public Response read() {
        LOGGER.log(Level.INFO, "read");
        JsonObject result = Json.createObjectBuilder()
                .add("user", securityContext.getCallerPrincipal() != null ?
                        securityContext.getCallerPrincipal().getName() : "Anonymous")
                .add("message", "Read resource")
                .build();
        return Response.ok(result).build();
    }

    public Response write() {
        LOGGER.log(Level.INFO, "write");
        JsonObject result = Json.createObjectBuilder()
                .add("user", securityContext.getCallerPrincipal().getName())
                .add("message", "Write resource")
                .build();
        return Response.ok(result).build();
    }

    public Response delete() {
        LOGGER.log(Level.INFO, "delete");
        JsonObject result = Json.createObjectBuilder()
                .add("user", securityContext.getCallerPrincipal().getName())
                .add("message", "Delete resource")
                .build();
        return Response.ok(result).build();
    }

    public void init() {
        AppUser user1 = new AppUser();
        AppUser user2 = new AppUser();
        Role role1 = new Role();
        Role role2 = new Role();
        role1.setName(USER);
        role2.setName(ADMIN);
        user1.setUsername("ol");
        user1.setPassword("12345");
        user2.setUsername("java");
        user2.setPassword("secret");
        user1.addRole(role1);
        user1.addRole(role2);
        user2.addRole(role1);

        entityManager.persist(user1);
        entityManager.persist(user2);
    }
}
