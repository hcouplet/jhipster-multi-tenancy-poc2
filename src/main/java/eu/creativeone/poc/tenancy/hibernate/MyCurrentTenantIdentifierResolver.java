package eu.creativeone.poc.tenancy.hibernate;

import eu.creativeone.poc.tenancy.user.TenantUser;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by AdamS on 2015-03-12.
 */
public class MyCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver
{

    private final Logger log = LoggerFactory.getLogger(MyCurrentTenantIdentifierResolver.class);
    /**
     *
     * If the current principal is not a TenantUserDetails Object
     * classify him as anonymous user and only give him access to the
     * public database.
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        if(SecurityContextHolder.getContext().getAuthentication() != null
            && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null){
            Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(userDetails instanceof TenantUser){
                TenantUser tenantUsr = ((TenantUser) userDetails);
                log.info("We have: " + tenantUsr.getTenantId());
                return tenantUsr.getTenantId();
            }
        }
        log.info("Normalnie zwracam jhipster");
        return "jhipster";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
