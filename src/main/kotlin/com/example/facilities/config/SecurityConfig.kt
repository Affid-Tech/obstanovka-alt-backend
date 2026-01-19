package com.example.facilities.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Value("\${ADMIN_USERNAME:}")
    private val adminUsername: String,
    @Value("\${ADMIN_PASSWORD:}")
    private val adminPassword: String
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService {
        if (adminUsername.isBlank() || adminPassword.isBlank()) {
            throw IllegalStateException("ADMIN_USERNAME and ADMIN_PASSWORD must be set for admin access.")
        }

        val user = User.withUsername(adminUsername)
            .password(passwordEncoder.encode(adminPassword))
            .roles("ADMIN")
            .build()

        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.ignoringRequestMatchers("/admin/v1/**") }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/admin/v1/**").hasRole("ADMIN")
                auth.requestMatchers("/v1/**").permitAll()
                auth.anyRequest().permitAll()
            }
            .httpBasic { }

        return http.build()
    }
}
