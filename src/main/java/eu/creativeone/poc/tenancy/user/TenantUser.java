package eu.creativeone.poc.tenancy.user;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by AdamS on 2015-03-31.
 */
public class TenantUser  extends org.springframework.security.core.userdetails.User {
    private String tenantId;

    public TenantUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }


    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
