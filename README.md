# FitLifeApp

## 1. Caso Elegido y Alcance

**Caso elegido:** FitLife — aplicación móvil de gestión de entrenamiento y bienestar personal.

**Alcance EP4:** Se amplía la app con nuevas secciones: Entrenador, Plan de Entrenamiento, Plan Nutricional y Progreso. Cada módulo incorpora navegación propia, persistencia de datos, componentes reutilizables y control de estado con ViewModel.

## 2. Requisitos y Ejecución

**Stack Tecnológico:**
- Kotlin
- Jetpack Compose
- Navigation Compose
- Retrofit/OkHttp
- Gson
- DataStore
- Coil
- Coroutines

**Instalación:**
```bash
./gradlew clean assembleDebug
# o en Windows
gradlew.bat clean assembleDebug
```

**Ejecución:**
```bash
./gradlew installDebug
```

**Perfiles:**
- `debug` (por defecto)
- `release` (requiere firma)

## 3. Arquitectura y Flujo

**Estructura de Carpetas:**
```
app/
├─ data/
│  ├─ local/        → DataStore: preferencias de usuario, sesión, avatar, progreso
│  └─ remote/       → RetrofitClient y ApiService
├─ repository/      → AuthRepository, UserRepository, EntrenamientoRepository, NutricionRepository, ProgresoRepository
├─ ui/
│  ├─ screens/      
│  │  ├─ login/               → pantalla de inicio de sesión
│  │  ├─ register/            → pantalla de registro
│  │  ├─ home/                → pantalla principal y menú general
│  │  ├─ profile/             → perfil de usuario y edición de datos
│  │  ├─ entrenador/          → listado y detalle de entrenadores
│  │  ├─ planEntrenamiento/   → rutinas, ejercicios y calendarización
│  │  ├─ planNutricional/     → menú diario y seguimiento alimenticio
│  │  └─ progreso/            → métricas, estadísticas y avances
│  ├─ navigation/   → AppNavigation.kt (rutas y NavHost)
│  ├─ components/   → diálogos, inputs, botones, tarjetas reutilizables
│  └─ theme/        → colores, tipografía y estilos Material3
├─ viewmodel/       → Lógica de estado (StateFlow) para cada módulo
├─ AvatarStorage.kt → Guardado local de imagen de perfil
└─ MainActivity.kt  → Punto de entrada y configuración de tema/nav
```

**Gestión de Estado:**
- ViewModel + MutableStateFlow (loading, success, error)
- Los repositorios centralizan la lógica y la persistencia con DataStore

**Navegación:**
- NavHost con rutas: login, register, home, profile, entrenador, planEntrenamiento, planNutricional, progreso
- El backstack es controlado con NavController

## 4. Funcionalidades

- **Formulario validado:** Login y registro con validaciones básicas
- **Navegación y backstack:** Flujo completo entre pantallas
- **Gestión de estado:** Indicadores de carga y error
- **Persistencia local (CRUD):** DataStore y almacenamiento interno de imagen y progreso
- **Recursos nativos:** Cámara/galería con permisos y fallback
- **Animaciones:** Feedback visual al cambiar avatar o registrar progreso
- **Consumo de API:** Integrado con los endpoints definidos en el proyecto

## 5. Endpoints (Implementación Actual)

**Base URL:** `https://dummyjson.com/` (configurado en RetrofitClient.kt)

| Método | Ruta | Body | Respuesta Esperada |
|--------|------|------|-------------------|
| POST | `user/login` | `{ username, password }` (según DTO LoginRequest) | LoginResponse (token/usuario) |
| GET | `auth/me` | - (requiere header Authorization) | UserDto con `{ id, email?, name?, avatarUrl? }` |
| GET | `users` | - | UsersResponse (lista paginada) |
| GET | `users/search` | `?q=texto` | UsersResponse (resultados filtrados) |
| GET | `users/{id}` | - | UserDto (usuario por id) |

**Nota:** Las rutas y DTOs están definidas en ApiService.kt y en data/remote/dto. Si se desea usar otro backend (por ejemplo Xano), cambia RetrofitClient.BASE_URL y adapta las rutas/DTOs.

## 6. User Flows

**Flujo Principal:**
- Inicio → si isLoggedIn, ir a Home; si no, Login
- Login/Register → validación → guardar sesión → navegar a Home
- Home → opciones → Perfil, Entrenador, Plan de Entrenamiento, Plan Nutricional, Progreso
- Perfil → ver/editar datos, cambiar avatar (cámara/galería)
- Entrenador → mostrar lista y detalle de entrenadores disponibles
- Plan de Entrenamiento → ver rutinas asignadas o crear nuevas
- Plan Nutricional → mostrar menús, recomendaciones y calorías
- Progreso → registrar peso, IMC y evolución visual
- Logout → limpia DataStore y redirige a Login

**Casos de Error:**
- Sin conexión → mensaje adecuado
- Permisos cámara denegados → fallback a galería
- Error API o credenciales → mensaje visible y sin cierre forzado

## 7. Nuevas Secciones (EP4)

**Entrenador:**
- Gestión de entrenadores, información de contacto y especialidad
- Interfaz simple con cards, búsqueda y detalles individuales

**Plan de Entrenamiento:**
- Asignación de rutinas personalizadas al usuario
- Incluye listado de ejercicios, duración, tipo y días de entrenamiento

**Plan Nutricional:**
- Planificación alimenticia según objetivos (pérdida, mantenimiento, ganancia)
- Permite registrar alimentos consumidos y calcular calorías

**Progreso:**
- Seguimiento de métricas (peso, IMC, fotos, gráficos)
- Se guarda localmente y se visualiza con Compose Charts
