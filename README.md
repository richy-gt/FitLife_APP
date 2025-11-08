FitLife App
Aplicación móvil Android de gestión de entrenamiento y bienestar personal desarrollada con Kotlin y Jetpack Compose.
Tabla de Contenidos

Descripción
Requisitos Previos
Instalación
Configuración del Backend
Compilación y Ejecución
Arquitectura
Funcionalidades
API Endpoints
Tecnologías Utilizadas

Descripción
FitLife es una aplicación móvil que permite a los usuarios gestionar su entrenamiento físico y bienestar personal. Incluye funcionalidades de registro, autenticación, gestión de perfil, planes de entrenamiento, nutrición y seguimiento de progreso.
Requisitos Previos

Android Studio Hedgehog o superior
JDK 11 o superior
Android SDK API 24 o superior
Emulador Android o dispositivo físico
Node.js 16+ (para el backend)
MongoDB Atlas o instancia local

Instalación
Backend

Navegar al directorio del backend:

bashcd backend

Instalar dependencias:

bashnpm install

Configurar variables de entorno en archivo .env:

envPORT=8080
MONGO_URI=tu_string_conexion_mongodb
JWT_SECRET=tu_secreto_jwt

Iniciar el servidor:

bashnpm run dev
El servidor estará disponible en http://localhost:8080
Aplicación Android

Abrir el proyecto en Android Studio
Sincronizar dependencias Gradle:

bash./gradlew build

Compilar la aplicación:

bash./gradlew clean assembleDebug
Configuración del Backend
La aplicación se conecta al backend mediante RetrofitClient.kt. La URL base está configurada para usar el emulador Android:
kotlinprivate const val BASE_URL = "http://10.0.2.2:8080/api/"
Para dispositivo físico, cambiar a la IP local de tu máquina:
kotlinprivate const val BASE_URL = "http://192.168.x.x:8080/api/"
Compilación y Ejecución
Modo Debug
bash./gradlew installDebug
O desde Android Studio: Run > Run 'app'
Modo Release
bash./gradlew assembleRelease
```

Nota: Requiere configuración de firma en `build.gradle.kts`

## Arquitectura

### Estructura del Proyecto
```
app/
├── data/
│   ├── local/              # DataStore para sesión y preferencias
│   ├── remote/             # Retrofit y servicios API
│   └── model/              # Modelos de datos
├── repository/             # Repositorios de datos
├── ui/
│   ├── screens/            # Pantallas principales
│   ├── navigation/         # Navegación de la app
│   ├── components/         # Componentes reutilizables
│   ├── theme/              # Tema y estilos Material3
│   └── profile/            # Perfil de usuario
├── viewmodel/              # ViewModels con StateFlow
└── MainActivity.kt         # Actividad principal
```

### Patrón de Arquitectura

- MVVM (Model-View-ViewModel)
- Repository Pattern
- Gestión de estado con StateFlow
- Inyección manual de dependencias

## Funcionalidades

### Autenticación

- Registro de nuevos usuarios
- Inicio de sesión con JWT
- Gestión de sesión persistente con DataStore
- Cierre de sesión

### Perfil de Usuario

- Visualización de datos del usuario
- Edición de perfil
- Cambio de avatar con cámara o galería
- Almacenamiento persistente de imagen

### Módulos Principales

- **Entrenador**: Listado de entrenadores disponibles con especialidades
- **Plan de Entrenamiento**: Gestión de rutinas y ejercicios personalizados
- **Plan Nutricional**: Planificación alimenticia y seguimiento calórico
- **Progreso**: Registro y visualización de métricas de avance

### Características Técnicas

- Navegación con Navigation Compose
- Manejo de permisos (cámara, almacenamiento)
- Validación de formularios
- Indicadores de carga y manejo de errores
- Almacenamiento local con DataStore
- Consumo de API REST con Retrofit

## API Endpoints

Base URL: `http://10.0.2.2:8080/api/`

### Autenticación

#### Registro
```
POST /users/register
Body: { "name": "string", "email": "string", "password": "string" }
Response: { "message": "string", "user": {...}, "token": "string" }
```

#### Login
```
POST /users/login
Body: { "email": "string", "password": "string" }
Response: { "message": "string", "user": {...}, "token": "string" }
```

#### Perfil
```
GET /users/profile
Headers: { "Authorization": "Bearer {token}" }
Response: { "id": "string", "name": "string", "email": "string" }
```

#### Usuario por ID
```
GET /users/{id}
Headers: { "Authorization": "Bearer {token}" }
Response: { "id": "string", "name": "string", "email": "string" }
Tecnologías Utilizadas
Android

Kotlin 2.0.21
Jetpack Compose
Material Design 3
Navigation Compose 2.7.7
Lifecycle ViewModel Compose 2.8.0
DataStore Preferences 1.0.0
Coil 2.6.0 (carga de imágenes)
Accompanist Permissions 0.32.0

Networking

Retrofit 2.11.0
OkHttp 4.12.0
Gson Converter 2.11.0
Coroutines Android 1.9.0

Backend

Node.js
Express 4.21.2
MongoDB 7.0.0
Mongoose 8.19.3
JWT (jsonwebtoken 9.0.2)
bcryptjs 3.0.3

Flujo de Usuario

Inicio: La app verifica si hay sesión activa
Login/Registro: Autenticación contra el backend
Home: Pantalla principal con acceso a todos los módulos
Navegación: Acceso a Entrenador, Planes, Progreso y Perfil
Perfil: Gestión de datos personales y avatar
Logout: Limpieza de sesión y retorno al login

Manejo de Errores

Validación de campos vacíos
Mensajes claros de error de red
Manejo de errores HTTP (400, 401, 500)
Fallback cuando no hay conexión
Estados de carga visibles

Seguridad

Tokens JWT con expiración
Contraseñas hasheadas en el backend
Comunicación HTTPS (producción)
Permisos de Android solicitados en runtime
DataStore para almacenamiento seguro local

Notas de Desarrollo

El emulador Android usa 10.0.2.2 para acceder a localhost del PC
Los tokens JWT expiran en 1 hora
Las imágenes de avatar se guardan en almacenamiento interno
Se requiere permiso de cámara y almacenamiento para cambiar avatar
