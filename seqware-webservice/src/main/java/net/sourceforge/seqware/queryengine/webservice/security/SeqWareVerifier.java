package net.sourceforge.seqware.queryengine.webservice.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.seqware.util.PasswordStorage;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Registration;
import org.apache.log4j.Logger;
import org.restlet.security.SecretVerifier;

/**
 * <p>
 * SeqWareVerifier class.
 * </p>
 *
 * @author morgantaschuk
 * @version $Id: $Id
 */
public class SeqWareVerifier extends SecretVerifier {

    private final LoadingCache<Credentials, Integer> credentials;

    public SeqWareVerifier() {
        //Cache credential requests for one minute to improve webservice performance
        credentials = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<Credentials, Integer>() {
                    @Override
                    public Integer load(Credentials key) {
                        RegistrationService registrationService = BeanFactory.getRegistrationServiceBean();
                        Registration registration = registrationService.findByEmailAddress(key.getIdentifier());
                        Logger.getLogger(SeqWareVerifier.class).debug(registration);
                        if (registration != null) {
                            String pass = new String(key.getSecret()).trim();
                            try {
                                final boolean b = PasswordStorage.verifyPassword(pass, registration.getPassword());
                                if (b) {
                                    return RESULT_VALID;
                                }
                            } catch (PasswordStorage.CannotPerformOperationException | PasswordStorage.InvalidHashException e) {
                                return RESULT_INVALID;
                            }
                        } else {
                            return RESULT_MISSING;
                        }
                        return RESULT_INVALID;
                    }
                });
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public int verify(String identifier, char[] secret) {
        return credentials.getUnchecked(new Credentials(identifier, secret));
    }

    private class Credentials {

        private final String identifier;
        private final char[] secret;

        public Credentials(String identifier, char[] secret) {
            this.identifier = identifier;
            this.secret = secret;
        }

        public String getIdentifier() {
            return identifier;
        }

        public char[] getSecret() {
            return secret;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + Objects.hashCode(this.identifier);
            hash = 37 * hash + Arrays.hashCode(this.secret);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Credentials other = (Credentials) obj;
            if (!Objects.equals(this.identifier, other.identifier)) {
                return false;
            }
            if (!Arrays.equals(this.secret, other.secret)) {
                return false;
            }
            return true;
        }

    }

}
