package kanefreeman.gateway;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableZuulProxy
@SpringBootApplication
@EnableCircuitBreaker
@EnableDiscoveryClient
@ComponentScan
@RefreshScope
@Controller
public class Application {

    // allowed origins seperated by comma   abc.com,123.com,test.com
    @Value("${gateway.allowedOrigins}")
    private String originStr;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();

        List<String> origins = Arrays.asList(originStr.split(","));

        config.setAllowCredentials(true);
        config.setAllowedOrigins(origins);
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @GetMapping("/")
    @ResponseBody()
    public Map profile(HttpServletRequest request) {
        String url = request.getRequestURL().toString();

        // Needs for link discovering via Eureka. TODO: Query eureka for these URLs
        return new HashMap<String, Object>() {
            {
                put("_links", new HashMap<String, Object>() {
                    {
                        put("projects", new HashMap<String, Object>() {
                            {
                                put("href", url + "projects");
                            }
                        });
                        put("tickets", new HashMap<String, Object>() {
                            {
                                put("href", url + "tickets");
                            }
                        });
                    }
                });
            }
        };
    }
}
