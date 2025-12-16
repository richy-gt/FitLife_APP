# FitLife App (Android – Kotlin)

## Integrantes:
* **Ricardo Saez**
* **Patricio Quilodran**

## 1. Caso elegido y alcance

* **Caso:** FitLife (Aplicación móvil para gimnasio)
* **Alcance EP3:** Diseño/UI, validaciones, navegación, gestión de estado, persistencia local, uso de recursos nativos (cámara/galería).

La app busca que los usuarios del gimnasio puedan registrarse, iniciar sesión y acceder a su información de perfil. También permite cambiar el avatar, ver secciones de entrenamiento y cerrar sesión fácilmente. El enfoque fue mantener todo simple y funcional, sin complicaciones innecesarias.

---

## 2. Requisitos y ejecución

### Stack principal

* **Frontend:** Android (Kotlin, ViewModel, DataStore, Retrofit)
* **Backend:** Node.js + Express
* **Base de datos:** MongoDB
* **Otras dependencias:** JWT 
<details>
   <summary>Obsoleto, preservado por evidencia</summary>
### Instalación y ejecución del backend

1. Clonar el repositorio y entrar en la carpeta del backend
2. Instalar dependencias:

   
   npm install
   
3. Crear un archivo `.env` con:

   
   MONGO_URI= credencial
   JWT_SECRET= credencial
   
4. Ejecutar el servidor:

   
   npm run dev
   

   La API queda disponible en:
   **[http://10.0.2.2:8080/api/](http://10.0.2.2:8080/api/)**
</details>

### Requisitos para la app Android

* Android Studio (cualquier versión reciente)
* JDK 11
* Android SDK 24 o superior

Compilar y ejecutar directamente desde Android Studio.
El proyecto usa DataStore para guardar el token y el email del usuario de forma segura.

---

## 3. Arquitectura y flujo

### Estructura de carpetas


app/
 data/
 local/
 remote/
 model/
 repository/
 ui/
 screens/
 components/
 navigation/
 theme/
 profile/
 viewmodel/


### Gestión de estado

Cada pantalla tiene su propio ViewModel que maneja los estados de carga, éxito o error.
El Repository se encarga de conectar la app con la API.
DataStore guarda los datos del usuario localmente.

### Navegación

La app usa **Navigation Component** con un stack simple:

* Splash -> Login -> Home -> Perfil
  Desde Home se accede a las demás secciones.
  Al cerrar sesión, se limpia todo y se vuelve al login.

---

## 4. Funcionalidades

* **Registro e inicio de sesión:**
  Formularios con validación de campos, manejo de errores y circulo de carga.
  El token se guarda localmente para mantener la sesión iniciada.

* **Navegación y backstack:**
  Flujo controlado con `Navigation Component`. La app recuerda el estado previo del usuario.

* **Gestión de estado:**
  Cada pantalla tiene estados diferenciados (loading, success, error) manejados desde los ViewModels.

* **Persistencia local:**
  Usa `DataStore` para token/email y almacenamiento interno para la imagen de perfil.

* **Recursos nativos:**
  Permite tomar una foto con la cámara o elegir una desde la galería.
  Se controlan los permisos y se muestran mensajes si algo falla.

* **Animaciones:**
  Transiciones suaves entre pantallas y pequeñas animaciones en botones

* **Consumo de API:**
  Se usa Retrofit con un interceptor para agregar el token.


## 5. Endpoints

**Base URL:** `http://10.0.2.2:8080/api/`

| Método | Ruta            | Body                            | Respuesta                       |
| ------ | --------------- | ------------------------------- | ------------------------------- |
| POST   | /users/register | { name, email, password }       | 201 { token, user }             |
| POST   | /users/login    | { email, password }             | 200 { token, user }             |
| GET    | /users/profile  | - (Authorization: Bearer token) | 200 { id, email, name, avatar } |
| GET    | /users/{id}     | -                               | 200 { id, name, email, avatar } |


## 6. User Flows

### Flujo principal

1. **Splash:** Revisa si existe sesión guardada.
2. Si hay token → va directo a **Home**.
   Si no → muestra **Login**.
3. Desde **Home**, el usuario puede ir al **Perfil**, **Plan de Entrenamiento**, **Nutrición**, o **Progreso**.
4. En **Perfil**, puede cambiar su foto (cámara o galería) o cerrar sesión.
5. Al cerrar sesión, se limpia el `DataStore` y vuelve al login.

### Casos de error

* **Sin conexión:** Muestra un mensaje de “Sin internet”.
* **Token inválido o expirado:** Redirige al login automáticamente.
* **Permisos denegados (cámara/galería):** Se muestra un aviso sin forzar el cierre de la app.

Firma APK 
<img width="1001" height="709" alt="image" src="https://github.com/user-attachments/assets/973018b5-4caf-4776-882b-38fac532e390" />

## 7. Codigo Fuente

### Repositorio del Backend

* https://github.com/richy-gt/Fitlife_BACK
* Tecnologías: Node.js, Express, MongoDB
* Puerto: 8080
* Deploy: Render

### Repositorio de la App

* https://github.com/richy-gt/FitLife_APP
* Tecnologías: Kotlin, Jetpack Compose
* Plataforma: Android SDK 24+

## 8. Evidencias de trabajo

### Patricio Quilodran (11 commits en App, 7 en Backend)

* Cambio completo de UI
* Agregado de caracteristicas
* Arreglo errores en backend
* Conectado de backend con frontend
* Creación de Backend
* Render

### Ricardo Saez (26 commits en app, 8 commits en Backend)

* Creacion de app y UI basica
* Arreglo errores de app
* Pulimiento de caracteristicas
* Pruebas Unitarias
* Agregado de caracteristicas QoL
