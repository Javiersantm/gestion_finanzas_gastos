# 💰 Gestión de Finanzas Personales

Aplicación web completa para la gestión de economía doméstica, control de ingresos, gastos y simulación de envío de dinero (Bizum). Desarrollada con Spring Boot y Thymeleaf.

![Dashboard Preview](ruta/a/una/captura_de_pantalla.png)
*(Opcional: Si subes una captura a la carpeta del proyecto, pon la ruta aquí)*

## 🚀 Características

* **Dashboard Interactivo:** Vista general con balance total, ingresos y gastos del mes actual.
* **Gráficos Visuales:** Gráfico de rosquilla (Chart.js) para visualizar la distribución de gastos por categoría en tiempo real.
* **Gestión de Transacciones:** CRUD completo para Ingresos y Gastos.
* **Categorización:** Clasificación de movimientos (Comida, Vivienda, Ocio, etc.).
* **Sistema Bizum:** Simulación de envío de dinero entre usuarios registrados mediante email, con notificaciones automáticas por correo.
* **Seguridad:** Login y Registro de usuarios con contraseñas encriptadas.
* **Modo Oscuro/Claro:** Interfaz adaptable con persistencia de preferencias.
* **Diseño Responsive:** Adaptado a móviles y escritorio usando Bootstrap 5.

## 🛠️ Tecnologías Utilizadas

* **Backend:** Java 17, Spring Boot 3, Spring Security, Spring Data JPA.
* **Frontend:** Thymeleaf, HTML5, CSS3, JavaScript.
* **Estilos y UI:** Bootstrap 5, FontAwesome, SweetAlert2.
* **Gráficos:** Chart.js.
* **Base de Datos:** MySQL.
* **Notificaciones:** JavaMailSender (Gmail SMTP).

## ⚙️ Instalación y Configuración

Sigue estos pasos para ejecutar el proyecto en local:

### 1. Base de Datos
Crea una base de datos vacía en MySQL llamada `ingresos`:
```sql
CREATE DATABASE ingresos;
2. Configuración del Entorno
Por seguridad, el archivo de configuración con las claves no se incluye en el repositorio.

Navega a src/main/resources/.

Busca el archivo application.properties.example.

Haz una copia de ese archivo y renómbralo a application.properties.

Edita el nuevo archivo con tus credenciales:

Properties

# Base de Datos
spring.datasource.username=TU_USUARIO_ROOT
spring.datasource.password=TU_CONTRASEÑA_MYSQL

# Configuración de Email (Necesario para Bizum)
# Debes generar una "Contraseña de Aplicación" en tu cuenta de Google
spring.mail.username=tu_email@gmail.com
spring.mail.password=tu_contraseña_de_aplicacion_generada
3. Ejecución
Ejecuta la aplicación desde tu IDE (IntelliJ/Eclipse) o mediante terminal:

Bash

./mvnw spring-boot:run
La aplicación estará disponible en: http://localhost:8080

👤 Usuarios de Prueba
Puedes registrar un nuevo usuario desde el formulario de registro o usar la base de datos para inspeccionar los usuarios creados.

Desarrollado por Elías Javi - 2025
