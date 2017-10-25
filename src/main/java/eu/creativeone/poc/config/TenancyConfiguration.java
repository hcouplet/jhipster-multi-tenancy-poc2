package eu.creativeone.poc.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by AdamS on 2015-03-30.
 */
@Configuration
@EnableJpaRepositories(basePackages  = "eu.creativeone.poc.tenancy.repository", entityManagerFactoryRef = "tenancyEntityManager",transactionManagerRef = "transactionManager")
//@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class TenancyConfiguration implements EnvironmentAware
{

    private final Logger log = LoggerFactory.getLogger(TenancyConfiguration.class);

    private Environment env;
    private RelaxedPropertyResolver propertyResolver;

    @Autowired
    private JpaVendorAdapter jpaVendorAdapter;

    private DataSource tenancyDataSource;

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
        this.propertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
    }

    @Bean(name = "tenancyEntityManager")
    public LocalContainerEntityManagerFactoryBean tenancyEntityManager() throws Throwable {

        HashMap<String, Object> properties = new HashMap<String, Object>();
        //properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        //properties.put("javax.persistence.transactionType", "JTA");
        properties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
        properties.put("hibernate.cache.use_second_level_cache", "false");
        properties.put("hibernate.cache.use_query_cache", "false");
        properties.put("hibernate.generate_statistics", "true");

        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(tenancyDataSource());
        entityManager.setJpaVendorAdapter(jpaVendorAdapter);
        entityManager.setPackagesToScan("eu.creativeone.poc.tenancy.domain");
        entityManager.setPersistenceUnitName("tenanctPersistenceUnit");
        entityManager.setJpaPropertyMap(properties);
        return entityManager;
    }

    //@Bean(destroyMethod = "shutdown")
    //@ConditionalOnMissingClass(name = "com.mycompany.myapp.config.HerokuDatabaseConfiguration")
    //@Profile("!" + Constants.SPRING_PROFILE_CLOUD)
    public DataSource tenancyDataSource() {
        log.debug("Configuring Datasource");
        if (propertyResolver.getProperty("url") == null && propertyResolver.getProperty("databaseName") == null) {
            log.error("Your database connection pool configuration is incorrect! The application" +
                    "cannot start. Please check your Spring profile, current profiles are: {}",
                Arrays.toString(env.getActiveProfiles()));

            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(propertyResolver.getProperty("dataSourceClassName"));
        if (propertyResolver.getProperty("url") == null || "".equals(propertyResolver.getProperty("url"))) {
            config.addDataSourceProperty("databaseName", propertyResolver.getProperty("databaseName"));
            config.addDataSourceProperty("serverName", propertyResolver.getProperty("serverName"));
        } else {
            config.setJdbcUrl("jdbc:mariadb://localhost:3306/jhipster");
        }
        config.addDataSourceProperty("user", propertyResolver.getProperty("username"));
        config.addDataSourceProperty("password", propertyResolver.getProperty("password"));

        //MySQL optimizations, see https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        if ("com.mysql.jdbc.jdbc2.optional.MysqlDataSource".equals(propertyResolver.getProperty("dataSourceClassName"))) {
            config.addDataSourceProperty("cachePrepStmts", propertyResolver.getProperty("cachePrepStmts", "true"));
            config.addDataSourceProperty("prepStmtCacheSize", propertyResolver.getProperty("prepStmtCacheSize", "250"));
            config.addDataSourceProperty("prepStmtCacheSqlLimit", propertyResolver.getProperty("prepStmtCacheSqlLimit", "2048"));
            config.addDataSourceProperty("useServerPrepStmts", propertyResolver.getProperty("useServerPrepStmts", "true"));
        }
        tenancyDataSource = new HikariDataSource(config);
        return tenancyDataSource;
    }

//    @Bean(name = "tenancyTransactionManager")
//    public PlatformTransactionManager transactionManager(EntityManagerFactory tenancyEntityManager){
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(tenancyEntityManager);
//
//        return transactionManager;
//    }
}
