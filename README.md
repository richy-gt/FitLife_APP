# FitLife APP

Aplicacion de android para la empresa FitLife con Kotlin


## Caso elegido y alcance 

FitLife es una empresa de gyms que se enfoca en la salud y el bienestar de sus clientes,
lo cual se ve reflejado en su aplicacion, que revisa y da datos de salud del usuario.

Esta aplicacion tiene validaciones para el inicio de sesion, registro, persistencia.

## Requisitos y ejecucion

-Android Studio (version mas actualizada)
-JDK 11
-ANDROID SDK 24
-NODE.JS
-MONGODB

## Instalacion

-Backend: npm install -> configurar .env con credenciales en MONGO_URI Y JWT_SECRET e iniciar con npm run o npm run dev


## Arquitectura y flujo

app/
data/
local/
remote/
model/
repository/
ui/
screens/
navigation/
components/
theme/
profile/
viewmodel/
mainactivity.kt

Gestion Estado:
Local: StateFlow en ViewModels individuales por pantalla
Global: SessionManager (DataStore) para token y email del usuario

Flujo:
UI (Composable) 
   ↓ eventos
ViewModel 
   ↓ llamadas
Repository 
   ↓ requests
ApiService/DataStore
   ↓ respuestas
ViewModel (actualiza StateFlow)
   ↓ recomposición
UI actualizada

Rutas principales:

splash → Verificación de sesión
login / register → Autenticación
home → Pantalla principal (hub)
personalizacion → Perfil de usuario
camera_avatar → Captura de avatar
entrenador / plan_entrenamiento / plan_nutricional / progreso

## Funcionalidades:

Formulario validado
Registro (RegisterScreen.kt):

Nombre: Solo letras, mínimo 3 caracteres
Email: Formato válido (validación con Patterns.EMAIL_ADDRESS)
Contraseña: Mínimo 8 caracteres, 1 mayúscula, 1 número, 1 carácter especial

Login (LoginScreen.kt):

Email válido
Contraseña no vacía
Mensajes de error en tiempo real

Navegación y backstack

Stack navigation con NavHost
Decisión automática en splash: si hay sesión activa → home, sino → login
Back button respetado en todas las pantallas
PopUpTo para evitar volver a login después de autenticarse

Gestión de estado (carga/éxito/error)
Estados manejados:
kotlinwhen {
    state.isLoading -> CircularProgressIndicator()
    state.error != null -> Text(state.error)
    else -> /* UI normal */
}
Indicadores:

Spinners durante llamadas API
Cards de error con mensaje descriptivo
Deshabilitación de botones durante carga

Persistencia local
DataStore (SessionManager.kt):

Token JWT
Email de usuario
Estado de login

Almacenamiento de archivo (AvatarStorage.kt):

Copia la imagen desde URI temporal a almacenamiento interno
Ruta persistente: app_filesDir/avatar/avatar.jpg
Limpieza al cerrar sesión

Almacenamiento de imagen de perfil
Flujo completo:

Usuario selecciona cámara o galería
Se solicitan permisos (Accompanist)
Imagen capturada → AvatarStorage.persistFromUri()
Se guarda URI en AvatarPreferences
Carga con Coil en ProfileScreen
Animación de escala al actualizar

Recursos nativos: cámara/galería
Implementación (CameraAvatarScreen.kt):
Permisos solicitados:

CAMERA
READ_MEDIA_IMAGES (Android 13+)
READ_EXTERNAL_STORAGE (Android 12-)

Launchers:
kotlinval tomarFotoLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.TakePicture()
) { ok -> /* guardar */ }

val elegirImagenLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.PickVisualMedia()
) { uri -> /* guardar */ }
Fallback:

Si no se conceden permisos, se muestra mensaje informativo
Si la cámara no está disponible, solo se habilita galería

Animaciones con propósito
Avatar con escala (ProfileScreen.kt):
kotlinval scale by animateFloatAsState(
    targetValue = if (localAvatarUri != null) 1.05f else 1f,
    animationSpec = tween(durationMillis = 600)
)

Box(modifier = Modifier.scale(scale)) {
    Image(...)
}
Propósito: Feedback visual al usuario cuando la imagen se actualiza exitosamente.
Consumo de API
Endpoints consumidos:

POST /users/register - Registro de usuario
POST /users/login - Autenticación
GET /users/profile - Obtener datos del usuario autenticado (incluye /me)
GET /users/{id} - Obtener usuario por ID

Autenticación:

Header: Authorization: Bearer <token>
Interceptor automático (AuthInterceptor.kt)

Manejo de errores:
kotlincatch (e: HttpException) {
    when (e.code()) {
        401 -> "Usuario o contraseña incorrectos"
        404 -> "Servicio no encontrado"
        500 -> "Error en el servidor"
    }
}
catch (e: IOException) {
    "Sin conexión a Internet"
}

---

## 5. Endpoints

**Base URL:** `http://10.0.2.2:8080/api/`

| Método | Ruta              | Body                                      | Respuesta                                                              |
|--------|-------------------|-------------------------------------------|------------------------------------------------------------------------|
| POST   | `/users/register` | `{ "email": "user@mail.com", "password": "Pass123!", "name": "Juan" }` | `201 { "message": "...", "user": { "id", "email", "name" }, "token" }` |
| POST   | `/users/login`    | `{ "email": "user@mail.com", "password": "Pass123!" }`                 | `200 { "message": "...", "user": { "id", "email" }, "token" }`         |
| GET    | `/users/profile`  | - (requiere header `Authorization: Bearer <token>`)                    | `200 { "id", "name", "email" }`                                        |
| GET    | `/users/{id}`     | - (requiere header `Authorization: Bearer <token>`)                    | `200 { "id", "name", "email" }`                                        |
| GET    | `/users`          | - (requiere header `Authorization: Bearer <token>`)                    | `200 { "users": [{ "id", "name", "email" }] }`                         |

---

## 6. Flujo principal: Registro y Login

Inicio de la aplicación:

La app verifica en SplashDecider si existe una sesión activa (token y email guardados en DataStore)
Si hay sesión → Navega directamente a HomeScreen
Si no hay sesión → Navega a LoginScreen


Registro de nuevo usuario:

Usuario accede a RegisterScreen desde el link "¿No tienes cuenta?"
Completa el formulario: nombre, email y contraseña
Las validaciones se ejecutan en tiempo real:

Nombre: solo letras, mínimo 3 caracteres
Email: formato válido
Contraseña: mínimo 8 caracteres, 1 mayúscula, 1 número, 1 símbolo


Al presionar "Registrarse", el RegisterViewModel ejecuta POST /users/register
Si es exitoso: guarda token y email en DataStore, muestra mensaje de éxito
Usuario presiona "Ir al login" y es redirigido a LoginScreen


Inicio de sesión:

Usuario ingresa email y contraseña en LoginScreen
Al presionar "Ingresar", el LoginViewModel ejecuta POST /users/login
Si las credenciales son correctas:

Guarda token JWT en SessionManager
Guarda email en UserPreferences
Marca isLoggedIn = true
Navega a HomeScreen eliminando el login del backstack


Si hay error: muestra mensaje descriptivo (credenciales incorrectas, sin internet, etc.)



Flujo de navegación en la app

Pantalla principal (Home):

Muestra el email del usuario autenticado
Botones para navegar a:

Perfil → ProfileScreen
Entrenador → Lista de entrenadores disponibles
Plan de Entrenamiento → Lista de rutinas
Plan Nutricional → Lista de planes alimenticios
Progreso → Métricas del usuario


Botón "Cerrar sesión" que limpia el token y vuelve a login


Visualización del perfil:

Usuario navega a ProfileScreen
El ProfileViewModel ejecuta GET /users/profile con el token JWT
Muestra:

Avatar del usuario (imagen guardada localmente o icono por defecto)
Nombre completo
Email
Botón "Cambiar foto"
Botón "Refrescar datos"
Botón "Cerrar sesión"





Flujo de cambio de avatar

Captura/Selección de imagen:

Usuario presiona "Cambiar foto" en el perfil
Navega a CameraAvatarScreen
Opciones disponibles:

Tomar foto: solicita permiso de cámara, abre la cámara nativa
Elegir de galería: solicita permiso de almacenamiento, abre el selector de imágenes


Si el usuario deniega permisos: muestra mensaje informativo pero no bloquea la app
Una vez seleccionada/capturada la imagen:

Se guarda en almacenamiento interno persistente con AvatarStorage.persistFromUri()
Se guarda la URI en AvatarPreferences
Vuelve automáticamente a ProfileScreen
La imagen se actualiza con animación de escala

Casos de error

Error de autenticación:

Usuario ingresa credenciales incorrectas → Se muestra "Usuario o contraseña incorrectos"
Token expirado al consultar perfil → Se muestra "Sesión expirada" y debe hacer login nuevamente


Error de conexión:

Sin internet al intentar login/registro → "Sin conexión a Internet. Verifica tu red"
Backend no disponible → "Error en el servidor. Intenta más tarde"


Error de permisos:

Permisos de cámara denegados → No se abre la cámara, se muestra Snackbar informativo
Permisos de galería denegados → No se abre el selector, usuario debe configurar permisos manualmente


Cierre de sesión:

Usuario presiona "Cerrar sesión" desde Home o Perfil
Se ejecuta SessionManager.logout() que limpia todo el DataStore
Se ejecuta AvatarStorage.clear() que elimina la imagen del avatar
Navega a LoginScreen eliminando todo el historial del backstack
Usuario debe autenticarse nuevamente para acceder a la app
