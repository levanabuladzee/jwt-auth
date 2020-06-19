package ge.ol.ping.security;

import ge.ol.ping.session.AuthSession;

import javax.inject.Inject;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.PROVIDE_GROUPS;

public class AuthorizationIdentityStore implements IdentityStore {

    @Inject
    private AuthSession session;

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        Set<String> result = session.getRoles(validationResult.getCallerPrincipal().getName());
        if (result == null) {
            result = emptySet();
        }
        return result;
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return singleton(PROVIDE_GROUPS);
    }

}
