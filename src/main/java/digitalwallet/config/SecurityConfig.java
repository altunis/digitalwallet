package digitalwallet.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	
	
	@Value("${security.username.admin}")
	public String adminUserName;
	
	@Value("${security.password.admin}")
	public String password;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.formLogin(formLogin ->
		formLogin.permitAll().defaultSuccessUrl("/swagger-ui/index.html", true)
        .permitAll());
		http
        .headers(headers -> headers
            .frameOptions(frameOptions -> 
                frameOptions.sameOrigin()
            )
        );
		http.authorizeHttpRequests(auth -> auth
				  .requestMatchers("/api/wallet").authenticated()).csrf(AbstractHttpConfigurer::disable);;
		http.authorizeHttpRequests(auth -> auth
				  .requestMatchers("/api/transaction").authenticated()).csrf(AbstractHttpConfigurer::disable);;
		http.authorizeHttpRequests(auth -> auth
				  .requestMatchers("/").permitAll()).csrf(AbstractHttpConfigurer::disable);;
					http.authorizeHttpRequests(h2 -> h2.requestMatchers(toH2Console()).permitAll()).csrf().ignoringRequestMatchers(toH2Console());

		http.authorizeHttpRequests(auth -> auth
				  .requestMatchers("/**").authenticated()).csrf(AbstractHttpConfigurer::disable);;
		return http.build();
	}
	
	public void addViewControllers(ViewControllerRegistry registry) {
	    registry.addViewController("/").setViewName("forward:/login");
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}

	@Bean
	public InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) {
	    InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
	    UserDetails userAdmin = User.withUsername(adminUserName)
	        .password(passwordEncoder.encode(password))
	        .roles("ADMIN")
	        .build();
	    userDetailsManager.createUser(userAdmin);
	    UserDetails sampleUser = User.withUsername("EMPLOYEE")
	        .password(passwordEncoder.encode("1234"))
	        .roles("EMPLOYEE")
	        .build();
	    userDetailsManager.createUser(sampleUser);
	    return userDetailsManager;
	}


}