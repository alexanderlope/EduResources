# EduResources (Android nativo)

Aplicación Android para **explorar, calificar y gestionar recursos educativos** (libros, videos, artículos, tutoriales y cursos) de Ingeniería de Sistemas.

Hay dos perfiles de usuario, con una experiencia distinta cada uno:

- **Estudiante:** explora el catálogo, guarda favoritos y califica recursos.
- **Docente:** administra el catálogo (crear, editar y eliminar recursos).

---
Creado por Enrique Alexander Solano Lopez 
SL223188

Video Demostrativo: https://drive.google.com/file/d/1S3Hppp3GoKxBC8gq85AdPHcMTGCg05dX/view?usp=sharing

Enlace APK: https://drive.google.com/file/d/1fHo8wDogdzuL0I1LucVe6IKuakXTgRGl/view?usp=sharing

---

## Índice
1. [Funcionalidades](#funcionalidades)
2. [Roles y permisos](#roles-y-permisos)
3. [Arquitectura](#arquitectura)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Configurar la API (MockAPI)](#configurar-la-api-mockapi)
6. [Cómo compilar y ejecutar](#cómo-compilar-y-ejecutar)
7. [Datos que se guardan en el dispositivo](#datos-que-se-guardan-en-el-dispositivo)
8. [Limitaciones conocidas](#limitaciones-conocidas)
9. [Solución de problemas](#solución-de-problemas)

---

## Funcionalidades

### Autenticación
**Registro**
- Campos: nombre completo, correo, rol (Estudiante o Docente), contraseña y confirmación.
- La contraseña debe cumplir **todas** estas reglas; mientras se escribe, la pantalla muestra en rojo las que faltan y, cuando está completa, un aviso "Contraseña válida":
  - mínimo 12 caracteres
  - al menos una mayúscula
  - al menos una minúscula
  - al menos un número
  - al menos un carácter especial de `!@#$%^&*`
- Se valida que no haya campos vacíos, que las contraseñas coincidan y que el correo no esté ya registrado.
- El correo se guarda en minúsculas. Al terminar, un mensaje de éxito devuelve al inicio de sesión.

**Inicio de sesión**
- Correo y contraseña, con botón para mostrar u ocultar la contraseña.
- Si las credenciales no coinciden se muestra "Credenciales incorrectas.".
- La **sesión se mantiene**: al volver a abrir la app se entra directamente sin pedir los datos otra vez, hasta que se cierre sesión.

### Explorar recursos (todos los usuarios)
- Lista de tarjetas con imagen, insignia del tipo (con color e ícono propios), título, descripción, estrellas y promedio.
- **Búsqueda** por título, por tipo (coincidencia parcial) o por **ID exacto**.
- **Filtros por tipo**: Todos, libro, video, artículo, tutorial y curso. Se combinan con la búsqueda.
- Contador de recursos disponibles según el filtro activo.
- **Jalar hacia abajo para refrescar** los datos desde la API.
- **Botones flotantes** para subir al inicio o bajar al final de la lista (aparecen al desplazarse).
- Si no hay resultados se muestra un estado vacío.
- Solo los estudiantes ven el corazón de favorito en cada tarjeta.

### Detalle de un recurso
- Imagen grande, tipo, título, calificación con estrellas, descripción e ID del recurso.
- Botón **Acceder al recurso** que abre el enlace en el navegador del teléfono.
- **Solo estudiantes:**
  - Corazón para agregar o quitar de favoritos.
  - Calificación de 1 a 5 estrellas. Cada calificación se envía a la API y el promedio se recalcula con un decimal.

### Favoritos (solo estudiantes)
- Lista de los recursos guardados, con imagen, tipo, título y descripción.
- Tocar una fila abre el detalle; el corazón de la derecha lo quita de la lista.
- Los favoritos son **independientes por usuario**: cada cuenta tiene los suyos en el mismo dispositivo.
- Estado vacío con instrucciones cuando aún no hay ninguno.

### Gestión (solo docentes)
- Lista de todos los recursos con buscador, contador y refresco por arrastre.
- Cada fila muestra título, tipo, ID y dos acciones: **editar** y **eliminar**.
- **Eliminar** pide confirmación antes de borrar el recurso en la API.
- El botón **+** abre el formulario de nuevo recurso.

### Formulario de recurso (crear / editar)
| Campo | Regla |
|---|---|
| Título | obligatorio |
| Descripción | obligatoria |
| Tipo | obligatorio: libro, video, artículo, tutorial o curso |
| Enlace | obligatorio, debe empezar por `http` |
| Imagen | opcional; si se escribe, debe empezar por `http` |

- Los errores se muestran bajo cada campo.
- Al **editar**, los campos llegan precargados con los datos actuales.
- Al **crear**, el recurso nace sin calificaciones (`ratings` vacío y `promedio` 0).
- Al guardar con éxito aparece un mensaje y se vuelve a la lista.

### Perfil
- Inicial del nombre, nombre, rol, correo e ID de usuario.
- Lista de **permisos de la cuenta**, con marca de verde o gris según lo que el rol permite.
- **Cerrar sesión**, con confirmación.

### Navegación
- Barra inferior que cambia según el rol:
  - Estudiante: **Explorar · Favoritos · Perfil**
  - Docente: **Explorar · Gestión · Perfil**
- El detalle y el formulario se abren a pantalla completa, sin la barra inferior.
- Tema oscuro, pensado con la paleta de la app original (fondo `#0F172A`, acento verde `#6EE7B7`).

---

## Roles y permisos

| Acción | Estudiante | Docente |
|---|:---:|:---:|
| Ver y explorar recursos | ✅ | ✅ |
| Buscar y filtrar | ✅ | ✅ |
| Guardar en favoritos | ✅ | ❌ |
| Calificar recursos | ✅ | ❌ |
| Crear recursos | ❌ | ✅ |
| Editar recursos | ❌ | ✅ |
| Eliminar recursos | ❌ | ✅ |

---

## Arquitectura

Aplicación de una sola actividad con interfaz declarativa y patrón **MVVM**:

```
UI (Compose)  ──observa──▶  ViewModels (StateFlow)  ──usa──▶  Datos
 ui/*                        SessionViewModel                  Api (Retrofit → MockAPI)
                             ResourcesViewModel                LocalStore (SharedPreferences)
```

- **`SessionViewModel`**: usuario en sesión, registro, login, cierre de sesión y favoritos del usuario actual.
- **`ResourcesViewModel`**: lista de recursos compartida entre Explorar, Detalle y Gestión; carga, refresco, crear, editar, eliminar y calificar. Cada cambio exitoso actualiza la lista en memoria, por lo que las pantallas se ven al día sin volver a pedir todo a la red.
- **`AppNav`**: decide entre el flujo de autenticación y el principal según haya o no sesión, y arma las pestañas según el rol.
- **Red**: Retrofit con conversor Gson contra MockAPI.
- **Imágenes**: Coil.

**Tecnologías:** Kotlin 2.0 · Jetpack Compose (Material 3) · Navigation Compose · Lifecycle ViewModel · Retrofit 2 + Gson · Coil · SharedPreferences. `minSdk 26` (Android 8.0), `compileSdk/targetSdk 35`.

---

## Estructura del proyecto

```
app/src/main/java/com/edu/resources/
├── MainActivity.kt                  Punto de entrada (pantalla completa, tema)
├── data/
│   ├── Models.kt                    Recurso, User, StoredUser, RecursoInput, RecursoNuevo…
│   ├── Api.kt                       Interfaz Retrofit y URL base de MockAPI
│   ├── LocalStore.kt                Persistencia local (usuarios, sesión, favoritos)
│   ├── SessionViewModel.kt          Autenticación y favoritos
│   └── ResourcesViewModel.kt        Operaciones sobre los recursos
├── util/
│   ├── Validation.kt                Reglas de contraseña
│   └── PasswordHasher.kt            Hash PBKDF2 de contraseñas
└── ui/
    ├── AppNav.kt                    Navegación y barra inferior
    ├── theme/Theme.kt               Colores y tema
    ├── components/Common.kt         Campos, botones, insignias, diálogos…
    ├── auth/                        LoginScreen, RegisterScreen
    ├── resources/                   ResourcesScreen, DetailScreen, FavoritesScreen
    ├── teacher/                     TeacherScreen, ResourceFormScreen
    └── profile/ProfileScreen.kt
mockapi/
├── recursos.json                    10 recursos de ejemplo
└── seed.py                          Script para cargarlos en tu MockAPI
```

---

## Configurar la API (MockAPI)

La app consume una API REST creada en [mockapi.io](https://mockapi.io).

### 1. Crear el proyecto y el recurso
1. En mockapi.io crea un proyecto con **API prefix** `recursos`.
2. Dentro, crea un recurso llamado `recursos` con estos campos (el `id` lo genera MockAPI):

| Campo | Tipo |
|---|---|
| `titulo` | String |
| `descripcion` | String |
| `tipo` | String |
| `enlace` | String |
| `imagen` | String |
| `ratings` | Array |
| `promedio` | Number |

### 2. Cargar datos de ejemplo (opcional)
Desde la carpeta `mockapi/`:

```
python3 seed.py https://TU_ID.mockapi.io/recursos/recursos
```

(En Windows suele ser `python` o `py` en lugar de `python3`.) Úsalo **una sola vez**: si se repite, los recursos quedan duplicados.

### 3. Apuntar la app a tu API
En `app/src/main/java/com/edu/resources/data/Api.kt` ajusta la URL del proyecto, **sin** el último `recursos`:

```kotlin
private const val BASE_URL = "https://TU_ID.mockapi.io/recursos/"
```

### Endpoints que usa la app
| Operación | Método y ruta |
|---|---|
| Listar | `GET /recursos` |
| Crear | `POST /recursos` |
| Editar | `PUT /recursos/{id}` |
| Calificar | `PUT /recursos/{id}` (envía `ratings` y `promedio`) |
| Eliminar | `DELETE /recursos/{id}` |

Ejemplo de un recurso:

```json
{
  "id": "2",
  "titulo": "Curso Completo de Redes de Computadoras",
  "descripcion": "…",
  "tipo": "video",
  "enlace": "https://…",
  "imagen": "https://…",
  "ratings": [4, 5, 5],
  "promedio": 4.7
}
```

---

## Cómo compilar y ejecutar
1. Abre la carpeta del proyecto en **Android Studio** (Ladybug 2024.2 o superior, JDK 17).
2. Espera el *Gradle Sync*; descarga Gradle 8.9 y las dependencias.
3. Revisa que `BASE_URL` apunte a tu MockAPI (ver arriba).
4. Pulsa ▶ en un emulador o dispositivo con Android 8.0 (API 26) o superior. Se necesita conexión a internet.

APK de depuración por consola: `./gradlew assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`
(si no existe `gradlew`, ábrelo una vez en Android Studio o ejecuta `gradle wrapper`).

---

## Datos que se guardan en el dispositivo

Usuarios, sesión y favoritos **no están en la API**: viven en el teléfono (`SharedPreferences`, archivo `edu_resources`).

| Dato | Contenido |
|---|---|
| Usuarios | id, nombre, correo, rol y la contraseña como **hash PBKDF2-HMAC-SHA256 con sal** (120 000 iteraciones). Nunca en texto plano |
| Sesión | usuario actual (sin credenciales) |
| Favoritos | una lista por usuario (`favorites_<id>`) con una copia de cada recurso |

Los recursos (catálogo y calificaciones) sí se guardan en MockAPI y se comparten entre todos los dispositivos.

---

## Limitaciones conocidas
- **La cuenta es local.** Los usuarios existen solo en el dispositivo donde se registraron; no hay servidor de autenticación ni recuperación de contraseña. Si se borran los datos de la app, se pierden cuentas, sesión y favoritos.
- **Calificaciones sin control por usuario.** La app no recuerda qué recursos calificó cada persona: cada vez que se toca una estrella se añade una calificación nueva a la API.
- **Favoritos como copia.** Se guarda una copia del recurso; si un docente lo edita o lo elimina, la copia del favorito puede quedar desactualizada hasta que se vuelva a calificar o se quite.
- **Seguridad de la API.** MockAPI es un servicio de pruebas sin autenticación: cualquiera con la URL puede leer o modificar los recursos. Es adecuado para desarrollo y demostración, no para producción.
- La app está bloqueada en orientación vertical.

## Solución de problemas
| Síntoma | Causa probable | Qué hacer |
|---|---|---|
| Error al cargar: **HTTP 404** | `BASE_URL` incorrecta, o prefijo / nombre del recurso distintos | Comprueba que `BASE_URL + "recursos"` abra una lista JSON en el navegador |
| Error: formato de datos inesperado | El campo `ratings` no es de tipo Array en MockAPI | Cámbialo a Array |
| Error de conexión | Sin internet en el emulador o dispositivo | Revisa la red |
| Lista vacía sin error | La API no tiene datos | Ejecuta `seed.py` una vez |
| Los recursos aparecen duplicados | `seed.py` se ejecutó más de una vez | Borra los duplicados en MockAPI |

