package  org.ngbw.cipresrest.auth;

@SuppressWarnings("serial")
public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String message, String realm) {
        super(message);
        this.realm = realm;
    }

    private String realm = null;

    public String getRealm() {
        return this.realm;
    }

}
