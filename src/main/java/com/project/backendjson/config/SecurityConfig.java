package com.project.backendjson.config;//package com.example.backend.config;
//
//import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
//import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
//import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
//
//@Configuration
//public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {
//
//    @Override
//    @Bean
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
//
//        // Mise à jour pour utiliser `authorizeHttpRequests()` et éviter la méthode dépréciée
//        http
//                .authorizeHttpRequests()
//                .requestMatchers("/api/users/**").hasRole("USER")  // Accès autorisé aux utilisateurs avec le rôle USER
//                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Accès autorisé aux administrateurs
//                .anyRequest().authenticated()
//                .and()
//                .csrf().disable();  // Désactiver CSRF si ce n'est pas nécessaire
//    }
//
//    @Bean
//    public KeycloakRestTemplate keycloakRestTemplate() {
//        return new KeycloakRestTemplate();
//    }
//
//    // Configure l'authentification avec Keycloak
//    @Bean
//    public KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
//        KeycloakAuthenticationProvider provider = new KeycloakAuthenticationProvider();
//        provider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
//        return provider;
//    }
//}
