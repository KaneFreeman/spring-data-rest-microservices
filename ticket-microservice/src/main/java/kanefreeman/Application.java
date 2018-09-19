package kanefreeman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.hypermedia.DiscoveredResource;
import org.springframework.cloud.client.hypermedia.DynamicServiceInstanceProvider;
import org.springframework.cloud.client.hypermedia.ServiceInstanceProvider;
import org.springframework.cloud.client.hypermedia.StaticServiceInstanceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * A remote {@link DiscoveredResource} that provides functionality to lookup projects.
     *
     * @param provider
     *
     * @return
     */
    @Bean
    public DiscoveredResource projectsResource(ServiceInstanceProvider provider) {
        return new DiscoveredResource(provider, traverson -> traverson.follow("projects"));
    }

    /**
     * A simple default {@link ServiceInstanceProvider} to use a hard-coded remote service to detect the store locations.
     *
     * @return
     */
    @Bean
    @Profile("default")
    public StaticServiceInstanceProvider staticServiceInstanceProvider() {
        return new StaticServiceInstanceProvider(new DefaultServiceInstance("projects", "localhost", 8051, false));
    }

    // Cloud configuration
    /**
     * Dedicated configuration to rather use a {@link ServiceInstanceProvider} to lookup the remote service via a service
     * registry.
     *
     * @author Oliver Gierke
     */
    @Profile({"cloud", "eureka-local"})
    @EnableDiscoveryClient
    static class CloudConfiguration {

        /**
         * A remote {@link DiscoveredResource} that provides functionality to lookup projects.
         *
         * @param provider
         *
         * @return
         */
        @Bean
        public DiscoveredResource projectsResource(ServiceInstanceProvider provider) {
            return new DiscoveredResource(provider, traverson -> traverson.follow("projects"));
        }

        @Bean
        public DynamicServiceInstanceProvider dynamicServiceProvider(DiscoveryClient client) {
            return new DynamicServiceInstanceProvider(client, "Projects");
        }
    }
}
