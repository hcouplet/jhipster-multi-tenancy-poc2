package eu.creativeone.poc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

/**
 * Created by AdamS on 2015-03-31.
 */
@Configuration
@EnableTransactionManagement
public class EntityManagerConfiguration
{
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JpaVendorAdapter jpaVendorAdapter;

    @Bean(name = "entityManagerFactory")
    //@DependsOn("transactionManager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Throwable {

        HashMap<String, Object> properties = new HashMap<String, Object>();
        //properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
        //properties.put("javax.persistence.transactionType", "JTA");
        properties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
        properties.put("hibernate.cache.use_second_level_cache", "false");
        properties.put("hibernate.cache.use_query_cache", "false");
        properties.put("hibernate.generate_statistics", "true");

        properties.put("hibernate.tenant_identifier_resolver", "eu.creativeone.poc.tenancy.hibernate.MyCurrentTenantIdentifierResolver");
        /* MANY DATASOURCES. WORKING SOLUTION */
        //properties.put("hibernate.multi_tenant_connection_provider", "com.mycompany.myapp.tenancy.hibernate.MyMultiTenantConnectionProviderImpl");
        /*SCHEMA CONFIG THAT IS NOT WORKING*/
        properties.put("hibernate.multi_tenant_connection_provider", "eu.creativeone.poc.tenancy.hibernate.SchemaMultiTenantConnectionProviderImpl");
        properties.put("hibernate.multiTenancy", "SCHEMA");

        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource);
        entityManager.setJpaVendorAdapter(jpaVendorAdapter);
        //entityManager.setPackagesToScan("com.mycompany.myapp.domain");
        entityManager.setPackagesToScan(new String[] { "eu.creativeone.poc.domain","eu.creativeone.poc.tenancy.domain" });

        entityManager.setPersistenceUnitName("persistenceUnit");
        entityManager.setJpaPropertyMap(properties);
        return entityManager;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);

        return transactionManager;
    }
}
