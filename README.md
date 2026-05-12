# 🐾 AdoptaMe

AdoptaMe es una plataforma integral (Cliente-Servidor) diseñada para facilitar y gestionar el proceso de adopción de mascotas. El sistema conecta a posibles adoptantes con protectoras de animales a través de una interfaz de escritorio intuitiva y un backend robusto.

## 🚀 Tecnologías Utilizadas

Este proyecto se ha desarrollado siguiendo una arquitectura cliente-servidor, implementando buenas prácticas de POO y separación de responsabilidades (MVC).

### Backend (El Cerebro)
* **Java 23**
* **Spring Boot 3.3.0** - Para la autoconfiguración y despliegue rápido del servidor REST.
* **Spring Data JPA & Hibernate** - Para el mapeo objeto-relacional (ORM) y evitar consultas SQL manuales.
* **MySQL** - Base de datos relacional para garantizar la persistencia e integridad de la información (Usuarios, Perros, Solicitudes).
* **Maven** - Para la gestión centralizada de dependencias y ciclo de vida de construcción.

### Frontend (La Cara)
* **JavaFX 21** - Framework para la creación de interfaces de usuario modernas en aplicaciones de escritorio.
* **FXML & SceneBuilder** - Para el diseño declarativo de vistas (MVC) separando la lógica visual de los controladores Java.
* **Gson** - Para la serialización y deserialización eficiente de datos JSON al comunicarse con la API REST.

## 🏗️ Arquitectura del Sistema

El proyecto está dividido en dos módulos independientes:

1. **`backend-adoptame/`**: Expone una API RESTFul en el puerto `8080`. Se organiza en capas:
   * **Controllers**: Gestionan las peticiones HTTP (GET, POST, PUT, DELETE).
   * **Repositories**: Interfaces que heredan de `JpaRepository` para el acceso a datos.
   * **Entities**: Modelos de datos mapeados a tablas MySQL.
2. **`frontend-adoptame/`**: Aplicación de escritorio que consume la API del backend mediante `HttpClient` de forma asíncrona. Gestiona el enrutamiento visual mediante una ventana principal (tipo SPA).

## 👥 Tipos de Usuarios

El sistema contempla dos perfiles de usuario:
* **Adoptante**: Puede registrarse, explorar el catálogo de perros disponibles, ver sus detalles y enviar solicitudes de adopción. También puede hacer seguimiento del estado de sus solicitudes.
* **Administrador (Protectora)**: Tiene acceso a un panel de control desde donde puede registrar nuevos perros, actualizar sus datos, eliminarlos, y gestionar (Aprobar/Rechazar) las solicitudes recibidas.

## 🛠️ Instalación y Ejecución

### Prerrequisitos
* Java JDK 21 o superior
* MySQL Server ejecutándose localmente (Puerto 3306)
* Maven

### Paso 1: Configurar la Base de Datos
1. Abre MySQL y crea una base de datos vacía llamada `adoptame`:
   ```sql
   CREATE DATABASE adoptame;
   ```
   *(Hibernate se encargará de crear todas las tablas automáticamente gracias a la propiedad `ddl-auto=update`)*

### Paso 2: Levantar el Backend
1. Navega a la carpeta `backend-adoptame`.
2. Ejecuta el servidor de Spring Boot:
   ```bash
   ./mvnw spring-boot:run
   ```
   *(El servidor estará escuchando en `http://localhost:8080`)*

### Paso 3: Lanzar el Frontend
1. Navega a la carpeta `frontend-adoptame`.
2. Compila y ejecuta la aplicación JavaFX:
   ```bash
   mvn clean compile exec:java -Dexec.mainClass="com.adoptame.app.Main"
   ```


## 📸 Capturas de Pantalla

<img width="1134" height="805" alt="1  Login2" src="https://github.com/user-attachments/assets/57001dc6-a2d4-4f82-b56a-d6eed935a411" />

<img width="1831" height="1009" alt="image" src="https://github.com/user-attachments/assets/4fe0b05c-a08c-4c8d-9636-9816181a1ea3" />

<img width="1899" height="1015" alt="image" src="https://github.com/user-attachments/assets/a90e5868-bc82-421a-91ea-45681295e8c5" />


---
Desarrollado como Proyecto Final (DAM/DAW).
