package com.eliasjavi.gestion.usuarios.infraestructura.controller;

import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. Mostrar formulario de Login
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Buscará login.html
    }

    // 2. Mostrar formulario de Registro
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new UserEntity());
        return "register"; // Buscará register.html
    }

    // 3. Procesar el Registro
    @PostMapping("/auth/register")
    public String processRegister(@ModelAttribute("usuario") UserEntity usuario, Model model) {

        // Validación: ¿Existe el email?
        if (userRepository.findByEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("error", "El email ya está registrado.");
            return "register";
        }

        // Encriptar contraseña antes de guardar
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);

        // Saldo inicial a 0
        usuario.setSaldo(BigDecimal.ZERO);

        userRepository.save(usuario);

        // Redirigir al login con mensaje de éxito
        return "redirect:/login?registered";
    }
}