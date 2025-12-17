package com.eliasjavi.gestion.usuarios.infraestructura.security;

import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        // IMPORTANTE: Convertimos el string "ADMIN" a "ROLE_ADMIN" para Spring
        String rolSpring = "ROLE_" + usuario.getRol();

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(rolSpring)))
                .build();
    }
}