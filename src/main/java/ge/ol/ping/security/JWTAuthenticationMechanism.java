package ge.ol.ping.security;

import ge.ol.ping.util.BodyReader;
import io.jsonwebtoken.ExpiredJwtException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.authentication.mechanism.http.RememberMe;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

import static ge.ol.ping.security.Constants.*;

@RememberMe(
        cookieMaxAgeSeconds = REMEMBERME_VALIDITY_SECONDS,
        cookieSecureOnly = false,
        isRememberMeExpression = "#{self.isRememberMe(httpMessageContext)}"
)
@RequestScoped
public class JWTAuthenticationMechanism implements HttpAuthenticationMechanism {

    private static final Logger LOGGER = Logger.getLogger(JWTAuthenticationMechanism.class.getName());

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Inject
    private TokenProvider tokenProvider;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
        LOGGER.log(Level.INFO, "validateRequest: {0}", request.getRequestURI());

        String name = request.getParameter("name");
        String password = request.getParameter("password");
        String token = extractToken(httpMessageContext);

        if (name != null && password != null) {
            LOGGER.log(Level.INFO, "credentials : {0}, {1}", new String[]{name, password});
            // validation of the credential using the identity store
            CredentialValidationResult result = identityStoreHandler.validate(new UsernamePasswordCredential(name, password));
            if (result.getStatus() == CredentialValidationResult.Status.VALID) {
                // Communicate the details of the authenticated user to the container and return SUCCESS.
                return createToken(result, httpMessageContext);
            }
            // if the authentication failed, we return the unauthorized status in the http response
            return httpMessageContext.responseUnauthorized();
        } else if (token != null) {
            // validation of the jwt credential
            return validateToken(token, httpMessageContext);
        } else if (httpMessageContext.isProtected()) {
            // A protected resource is a resource for which a constraint has been defined.
            // if there are no credentials and the resource is protected, we response with unauthorized status
            return httpMessageContext.responseUnauthorized();
        }
        // there are no credentials AND the resource is not protected,
        // SO Instructs the container to "do nothing"
        return httpMessageContext.doNothing();
    }

    // To validate the JWT token e.g Signature check, JWT claims check(expiration) etc
    private AuthenticationStatus validateToken(String token, HttpMessageContext httpMessageContext) {
        try {
            if (tokenProvider.validateToken(token)) {
                JWTCredential credential = tokenProvider.getCredential(token);
                return httpMessageContext.notifyContainerAboutLogin(credential.getPrincipal(), credential.getAuthorities());
            }
            // if token invalid, response with unauthorized status
            return httpMessageContext.responseUnauthorized();
        } catch (ExpiredJwtException eje) {
            LOGGER.log(Level.INFO, "Security exception for user {0} - {1}", new String[]{eje.getClaims().getSubject(), eje.getMessage()});
            return httpMessageContext.responseUnauthorized();
        }
    }

    // Create the JWT using CredentialValidationResult received from IdentityStoreHandler
    private AuthenticationStatus createToken(CredentialValidationResult result, HttpMessageContext httpMessageContext) {
        if (!isRememberMe(httpMessageContext)) {
            String jwt = tokenProvider.createToken(result.getCallerPrincipal().getName(), result.getCallerGroups(), false);
            httpMessageContext.getResponse().setHeader(AUTHORIZATION_HEADER, BEARER + jwt);
        }
        return httpMessageContext.notifyContainerAboutLogin(result.getCallerPrincipal(), result.getCallerGroups());
    }

    // To extract the JWT from Authorization HTTP header
    private String extractToken(HttpMessageContext httpMessageContext) {
        String authorizationHeader = httpMessageContext.getRequest().getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            return authorizationHeader.substring(BEARER.length(), authorizationHeader.length());
        }
        return null;
    }

    // this function invoked using RememberMe.isRememberMeExpression EL expression
    public Boolean isRememberMe(HttpMessageContext httpMessageContext) {
        return Boolean.valueOf(httpMessageContext.getRequest().getParameter("rememberme"));
    }
}
