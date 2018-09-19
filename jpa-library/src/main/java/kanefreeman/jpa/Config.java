package kanefreeman.jpa;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "dbEntityManagerFactory",
        transactionManagerRef = "dbTransactionManager",
        basePackages = {"kanefreeman.jpa.repositories"}
)
public class Config {

    @Bean(name = "dbDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dbDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public Flyway flyway() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dbDataSource());
        flyway.setBaselineOnMigrate(false);
        flyway.setLocations("classpath:db/migration");
        flyway.migrate();
        return flyway;
    }

    // We need to run flyway before the datasource is linked to JPA, so we have a @DependsOn tag here.
    @Bean
    @DependsOn("flyway")
    public LocalContainerEntityManagerFactoryBean dbEntityManagerFactory(EntityManagerFactoryBuilder builder,
            @Qualifier("dbDataSource") DataSource dataSource) {

        return builder.dataSource(dataSource)
                .packages("kanefreeman.jpa.model")
                .persistenceUnit("db")
                .build();
    }

    @Bean
    public PlatformTransactionManager dbTransactionManager(@Qualifier("dbEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
