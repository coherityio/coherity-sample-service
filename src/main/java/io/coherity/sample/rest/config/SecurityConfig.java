package io.coherity.sample.rest.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig 
{
    private final Environment environment;

    public SecurityConfig(Environment environment)
    {
        this.environment = environment;
    }
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
		HttpSecurity configuredHttp = http;
		if (environment.matchesProfiles("local"))
		{
			//Condition not currently used since both are same
			configuredHttp = SecurityConfig.configureNoAuthHttpSecurity(configuredHttp);
		}
		else
		{
			configuredHttp = SecurityConfig.configureOAuthHttpSecurity(configuredHttp);
		}
		return configuredHttp.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	//@Bean
	public UserDetailsService userDetailsService()
	{
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager
			.createUser(User.withUsername("user")
					.password(passwordEncoder().encode("password"))
					.roles("CUSTOMER").build());
		return manager;
	}
	
	
	public static HttpSecurity configureNoAuthHttpSecurity(HttpSecurity http) throws Exception
	{
		http
			.csrf().disable()  // Disable CSRF protection
			.authorizeHttpRequests((requests) -> 
    			requests.anyRequest().permitAll()
			);
		return http;
	}
	
	public static HttpSecurity configureOAuthHttpSecurity(HttpSecurity http) throws Exception
	{
        http
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/greeting/public", "/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(getJWTAuthenticationConverter())
                )
            );
			return http;
	}
	
	public static HttpSecurity configureBasicAuthHttpSecurity(HttpSecurity http) throws Exception
	{
		http
			.csrf().disable() // Disable CSRF protection
			.authorizeHttpRequests((requests) -> 
				requests
    				.requestMatchers("/actuator/**").permitAll()  // Allow access to actuator endpoints
    				.anyRequest().authenticated()  // Require authentication for all other endpoints
			)
			.httpBasic();  // Use basic authentication
		return http;
	}



    @Bean
    public static JwtAuthenticationConverter getJWTAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("");
        authoritiesConverter.setAuthoritiesClaimName("scope");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Get standard authorities
            Collection<String> authorities = authoritiesConverter.convert(jwt)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());

            // Extract Keycloak realm roles
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            Collection<String> realmRoles = realmAccess != null ? 
                (Collection<String>) realmAccess.get("roles") : List.of();

            // Extract Keycloak resource roles (client roles)
            Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
            Collection<String> resourceRoles = List.of();
            if (resourceAccess != null) {
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("coherity-sample-service");
                if (clientAccess != null) {
                    resourceRoles = (Collection<String>) clientAccess.get("roles");
                }
            }

            // Combine all authorities
            return Stream.concat(
                authorities.stream(),
                Stream.concat(
                    realmRoles.stream().map(role -> "ROLE_" + role),
                    resourceRoles.stream()
                )
            )
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        });

        return converter;
    }	
	
}
