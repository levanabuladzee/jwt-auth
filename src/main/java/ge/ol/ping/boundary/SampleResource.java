package ge.ol.ping.boundary;

import ge.ol.ping.session.SampleSession;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("sample")
public class SampleResource {

    @Inject
    private SampleSession sampleSession;

    @GET
    @Path("read")
    @PermitAll
    public Response read() {
        return sampleSession.read();
    }

    @POST
    @Path("write")
//    @RolesAllowed({USER, ADMIN})
    public Response write() {
        return sampleSession.write();
    }

    @DELETE
    @Path("delete")
//    @RolesAllowed(ADMIN)
    public Response delete() {
        return sampleSession.delete();
    }

    @GET
    @Path("init")
    @PermitAll
    public String init() {
        sampleSession.init();
        return "Added!";
    }

}
