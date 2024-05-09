package com.example.demo.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	
	
	@Autowired
	private UserDetailsService userdetailservice;
	
	@Bean
	AuthenticationProvider authenticationprovider() {
		
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userdetailservice);
		provider.setPasswordEncoder(new BCryptPasswordEncoder());
		
		return provider;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**");
		web.ignoring().antMatchers("/bootstrap/**");
		web.ignoring().antMatchers("/js/*");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests(requests -> requests
				.antMatchers("/index/**", "/index1/**", "/login", "/cart/**", "/img", "/images/**", "/resource/**",
						"/customer/**", "/aboutus", "/contactus", "/hello")
				.permitAll().antMatchers("/get*/**").hasAnyRole("ADMIN", "USER").antMatchers("/admin*/**")
				.hasRole("ADMIN").anyRequest().authenticated())
				.formLogin(login -> login.loginPage("/login").loginProcessingUrl("/login_form")
						.usernameParameter("j_username").passwordParameter("j_password").failureUrl("/login?error=true")
						.defaultSuccessUrl("/index"))
				.logout(logout -> logout.logoutUrl("/logout").deleteCookies("JSESSIONID")
						.logoutSuccessUrl("/login?logout=true"))
				.csrf(csrf -> csrf.disable());

	}
}
