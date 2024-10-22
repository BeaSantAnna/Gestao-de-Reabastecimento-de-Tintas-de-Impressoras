package com.projeto.gerenciamento.config;

import com.projeto.gerenciamento.entity.Usuario;
import com.projeto.gerenciamento.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeData(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                Usuario usuario = Usuario.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))  
                    .role("ADMIN")
                    .build();
                

                usuarioRepository.save(usuario);
                System.out.println("Usuário padrão criado: admin/admin123");
            } else {
                System.out.println("Administrador padrão já existe.");
            }
        };
    }
}
