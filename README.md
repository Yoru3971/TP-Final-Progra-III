# 🍽️ MiViandita

**MiViandita** es un sistema de gestión de pedidos de viandas que conecta a clientes con emprendimientos gastronómicos. La plataforma permite a los dueños gestionar sus viandas y emprendimientos, y a los clientes realizar pedidos de manera rápida y organizada a través de una interfaz web.

## 📌 Descripción

El sistema está pensado para digitalizar el flujo de pedidos entre clientes y pequeños emprendimientos de viandas. El objetivo es que el emprendimiento reciba los pedidos y luego se comuniquen cliente y emprendimiento para coordinar la entrega y el pago.

Cuenta con tres tipos de usuarios:
- **Cliente**: puede ver y filtrar emprendimientos, realizar pedidos y gestionarlos (ABM).
- **Dueño**: puede gestionar sus emprendimientos y viandas (ABM), y aceptar o rechazar pedidos recibidos.
- **Admin**: usuario con acceso completo para soporte técnico. Se crea automáticamente si no hay usuarios en el sistema y puede crear otros administradores.

## ⚙️ Tecnologías utilizadas

- Java 21
- Spring Boot
- MySQL
- Maven
- Swagger
- Basic Auth

## 🚀 Instalación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Yoru3971/TP-Final-Progra-III.git
   cd TP-Final-Progra-III
   ```

2. Crear una base de datos MySQL (en localhost):
   ```sql
   CREATE DATABASE miViandita_db;
   ```

3. Configurar el archivo `application.properties` (con tus credenciales de MySQL):
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/miViandita_db
   spring.datasource.username=TU_USUARIO
   spring.datasource.password=TU_CONTRASEÑA
   ```

4. Ejecutar la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```

## 📚 Documentación de la API

El proyecto incluye Swagger para la documentación interactiva de la API.

- Accedé a Swagger desde:  
  `http://localhost:8080/swagger-ui/index.html`

## 🔐 Autenticación

El sistema utiliza Basic Auth para autenticar a los usuarios. Según las credenciales ingresadas, se asigna uno de los siguientes roles:

- `CLIENTE`
- `DUENO`
- `ADMIN`

Cuando se inicia el sistema por primera vez y no existen usuarios en la base de datos, se crea automáticamente un administrador con las siguientes credenciales:

- Usuario: `admin@viandas.com`
- Contraseña: `admin123`

> ⚠️ Se recomienda cambiar esta contraseña apenas se accede por primera vez.

## 📬 Notificaciones

- Los **dueños** reciben notificación al llegar un nuevo pedido.
- Los **clientes** reciben notificación cuando su pedido es aceptado o rechazado.

> Las notificaciones se implementan a nivel lógico; pueden adaptarse a diferentes mecanismos (email, sistema interno, etc.) en versiones futuras.

## 🛣️ Funcionalidades principales

### Cliente
- Ver y filtrar emprendimientos disponibles.
- ABM de pedidos.

### Dueño
- ABM de sus emprendimientos.
- ABM de viandas asociadas.
- Aceptar o rechazar pedidos recibidos.

### Admin
- Acceso total para auditoría o soporte técnico (puede crear otros administradores).

## 🛠️ Funcionalidades futuras

- Agregar frontend web.
- Reemplazar Basic Auth por JWT.
- Integración con APIs externas (ej. Mercado Pago).

## 🤝 Contribuciones

Este proyecto está en desarrollo. Por el momento no se aceptan contribuciones externas, pero podrían habilitarse en versiones futuras.

## 📄 Licencia

Este proyecto no posee una licencia pública aún. Su uso está limitado a fines educativos o privados.
