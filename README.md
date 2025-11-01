# FitLifeApp

## 1. Caso elegido y alcance

* **Caso:** Gestión de usuarios y perfil (registro, login, perfil con avatar).
* **Alcance EP3:** Diseño/UI (Jetpack Compose + Material3), validaciones, navegación, estado, persistencia, recursos nativos, animaciones y consumo de API (`/me`).

## 2. Requisitos y ejecución

* **Stack:** Kotlin, Jetpack Compose, Navigation Compose, Retrofit/OkHttp, Gson, DataStore, Coil, Coroutines.
* **Instalación:**

```bash
./gradlew clean assembleDebug
# o en Windows
gradlew.bat clean assembleDebug
```

* **Ejecución:**

```bash
./gradlew installDebug
```

Perfiles:

* `debug` (por defecto)
* `release` (requiere firma)

## 3. Arquitectura y flujo

* **Estructura carpetas:**

```
app/
├─ data/
│  ├─ local/        → DataStore: preferencias de usuario, sesión, avatar
│  └─ remote/       → RetrofitClient y ApiService
├─ repository/      → AuthRepository, UserRepository
├─ ui/
│  ├─ screens/      → Login, Registro, Home, Perfil
│  ├─ navigation/   → AppNavigation.kt
│  ├─ components/   → diálogos, inputs, botones reutilizables
│  └─ profile/      → pantalla y ViewModel de perfil
├─ viewmodel/       → Lógica de estado (StateFlow)
├─ AvatarStorage.kt → Guardado local de imagen de perfil
└─ MainActivity.kt  → Punto de entrada y configuración de tema/nav
```

* **Gestión de estado:** ViewModel + MutableStateFlow (`loading`, `success`, `error`). Repositorios centralizan la lógica. Persistencia con DataStore.
* **Navegación:** NavHost con rutas `login`, `register`, `home`, `profile`. Backstack controlado con NavController.

## 4. Funcionalidades

* **Formulario validado:** Login y registro con validaciones básicas.
* **Navegación y backstack:** flujo completo entre pantallas.
* **Gestión de estado:** indicadores de carga y error.
* **Persistencia local (CRUD):** DataStore y almacenamiento interno de imagen de perfil.
* **Recursos nativos:** cámara/galería con permisos y fallback.
* **Animaciones:** feedback visual al cambiar avatar.
* **Consumo de API:** integrado con los endpoints definidos en el proyecto.

## 5. Endpoints (implementación actual en el proyecto)

**Base URL (actual en el proyecto):** `https://dummyjson.com/` (configurado en `RetrofitClient.kt`)

| Método | Ruta           | Body                                                | Respuesta (esperada)                              |
| ------ | -------------- | --------------------------------------------------- | ------------------------------------------------- |
| POST   | `user/login`   | `{ username, password }` (según DTO `LoginRequest`) | `LoginResponse` (token/usuario)                   |
| GET    | `auth/me`      | - (requiere header Authorization)                   | `UserDto` con `{ id, email?, name?, avatarUrl? }` |
| GET    | `users`        | -                                                   | `UsersResponse` (lista paginada)                  |
| GET    | `users/search` | `?q=texto`                                          | `UsersResponse` (resultados filtrados)            |
| GET    | `users/{id}`   | -                                                   | `UserDto` (usuario por id)                        |

> Nota: las rutas y DTOs están definidas en `ApiService.kt` y en `data/remote/dto`. Si deseas usar otro backend (por ejemplo Xano), cambia `RetrofitClient.BASE_URL` y adapta las rutas/DTOs.

## 6. User flows

* **Flujo principal:**

  1. Inicio → si `isLoggedIn`, ir a `Home`; si no, `Login`.
  2. Login/Register → validación → guardar sesión → navegar a `Home`.
  3. Home → opciones → `Perfil`.
  4. Perfil → ver/editar datos, cambiar avatar (cámara/galería).
  5. Logout → limpia DataStore y redirige a `Login`.
* **Casos de error:**

  * Sin conexión → mensaje adecuado.
  * Permisos cámara denegados → fallback a galería.
  * Error API o credenciales → mensaje visible y sin cierre forzado.
