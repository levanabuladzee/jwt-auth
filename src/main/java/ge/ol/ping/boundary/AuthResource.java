package ge.ol.ping.boundary;

import ge.ol.ping.session.AuthSession;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("auth")
public class AuthResource {

    @Inject
    private AuthSession authSession;

    @GET
    @Path("login")
    public Response login() {
        return authSession.login();
    }

}
