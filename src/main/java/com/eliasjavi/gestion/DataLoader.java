package com.eliasjavi.gestion; // Asegúrate que sea tu paquete raíz

import com.eliasjavi.gestion.usuarios.domain.entity.UserEntity;
import com.eliasjavi.gestion.usuarios.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal; // Importación necesaria para BigDecimal

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    // Inyectamos el repositorio de usuarios que ya creamos
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        String emailDePrueba = "mi.email@prueba.com";

        if (userRepository.findByEmail(emailDePrueba).isEmpty()) {

            System.out.println("--- CREANDO USUARIO DE PRUEBA ---");

            UserEntity usuarioDePrueba = new UserEntity();
            usuarioDePrueba.setEmail(emailDePrueba);
            usuarioDePrueba.setNombre("Usuario de Prueba");

            // CORRECCIÓN: Inicializar el saldo a 0.00
            usuarioDePrueba.setSaldo(BigDecimal.ZERO); // Usar BigDecimal.ZERO para 0.00

            userRepository.save(usuarioDePrueba);

            System.out.println("--- USUARIO 'mi.email@prueba.com' CREADO CON SALDO 0.00 ---");
        } else {
            System.out.println("--- EL USUARIO DE PRUEBA YA EXISTE ---");
        }
    }
}
