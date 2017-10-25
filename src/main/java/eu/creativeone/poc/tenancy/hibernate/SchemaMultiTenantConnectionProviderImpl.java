package eu.creativeone.poc.tenancy.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by AdamS on 2015-04-02.
 */
public class SchemaMultiTenantConnectionProviderImpl  implements MultiTenantConnectionProvider, ServiceRegistryAwareService
{

    private final Logger log = LoggerFactory.getLogger(SchemaMultiTenantConnectionProviderImpl.class);
    DataSource dataSource;

    @Override
    public Connection getAnyConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        try {
            connection.createStatement().execute("USE jhipster;");
        }
        catch (SQLException e) {
            throw new HibernateException("Could not alter JDBC connection to specified schema [public]", e);
        }
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        log.warn("Get Connection for Tenatd is:"+tenantIdentifier);
        final Connection connection = getAnyConnection();
        try {
            connection.setCatalog(tenantIdentifier);
//            connection.createStatement().execute("USE " + tenantIdentifier + ";");
//            connection.setCatalog(tenantIdentifier);
//            connection.setSchema(tenantIdentifier);
        }
        catch (SQLException e) {
            throw new HibernateException("Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]", e);
        }
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
//        try {
//            connection.createStatement().execute("USE "+tenantIdentifier+";");
//        }
//        catch (SQLException e) {
//            throw new HibernateException("Could not alter JDBC connection to specified schema [public]", e);
//        }
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }

    @Override
    public void injectServices(ServiceRegistryImplementor serviceRegistry) {
        Map lSettings = serviceRegistry.getService(ConfigurationService.class).getSettings();
        DataSource localDs =  (DataSource) lSettings.get("hibernate.connection.datasource");
        dataSource = localDs;
    }
}
