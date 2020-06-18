package ge.ol;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import static ge.ol.ping.security.Constants.ADMIN;
import static ge.ol.ping.security.Constants.USER;

@DeclareRoles({ADMIN, USER})
@ApplicationPath("api")
public class JAXRSConfiguration extends Application {

}
