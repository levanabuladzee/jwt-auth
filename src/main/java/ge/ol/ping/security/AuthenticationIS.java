package ge.ol.ping.security;

import ge.ol.ping.session.AuthSession;

import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Set;

import static java.util.Collections.singleton;
import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.VALIDATE;

public class AuthenticationIS implements IdentityStore {

    @Inject
    AuthSession session;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        CredentialValidationResult result;

        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential usernamePassword = (UsernamePasswordCredential) credential;
            String expectedPW = session.expectedPassword(usernamePassword.getCaller());
            if (expectedPW != null && expectedPW.equals(usernamePassword.getPasswordAsString())) {
                result = new CredentialValidationResult(usernamePassword.getCaller());
            } else {
                result = CredentialValidationResult.INVALID_RESULT;
            }
        } else {
            result = CredentialValidationResult.NOT_VALIDATED_RESULT;
        }
        return result;
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return singleton(VALIDATE);
    }

}
