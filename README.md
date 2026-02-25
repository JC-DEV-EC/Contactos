# ContactHub

Aplicacion movil Android para la gestion de contactos personales. Desarrollada con Kotlin, Jetpack Compose y arquitectura limpia (Clean Architecture), permite crear, editar, eliminar y visualizar contactos de forma local con persistencia en base de datos Room.

## Tabla de contenidos

- [Descripcion general](#descripcion-general)
- [Capturas de pantalla](#capturas-de-pantalla)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Requisitos previos](#requisitos-previos)
- [Instalacion y ejecucion](#instalacion-y-ejecucion)
- [Funcionalidades](#funcionalidades)
- [Validacion y seguridad](#validacion-y-seguridad)
- [Configuracion del proyecto](#configuracion-del-proyecto)

---

## Descripcion general

ContactHub es una aplicacion nativa de Android que gestiona contactos almacenados localmente en el dispositivo. El usuario puede registrar nombre, correo electronico, telefono y notas para cada contacto. La aplicacion sigue el patron MVVM con inyeccion de dependencias mediante Hilt y navegacion declarativa con Navigation Compose.

## Capturas de pantalla

> Agregar capturas de pantalla de la aplicacion en la carpeta `screenshots/` y referenciarlas aqui.

## Arquitectura

El proyecto implementa **Clean Architecture** dividida en tres capas principales:

```
Presentation --> Domain --> Data
```

- **Presentation**: Pantallas Compose (`@Composable`), ViewModels con `StateFlow` y manejo de estado reactivo.
- **Domain**: Modelo de datos (`Contact`), interfaz del repositorio (`ContactRepository`) y mappers entre capas.
- **Data**: Implementacion del repositorio (`ContactRepositoryImpl`), entidad Room (`ContactEntity`), DAO y base de datos.

La inyeccion de dependencias conecta las capas a traves de modulos Hilt (`DatabaseModule`, `RepositoryModule`), respetando la inversion de dependencias.

## Estructura del proyecto

```
app/src/main/java/app/aplication/appproductos/
|
|-- ContactHubApp.kt                  # Application class (@HiltAndroidApp)
|-- MainActivity.kt                   # Activity principal (@AndroidEntryPoint)
|
|-- contact/
|   |-- data/
|   |   |-- ContactDao.kt            # DAO de Room (queries SQL)
|   |   |-- ContactDatabase.kt       # Base de datos Room (v1)
|   |   |-- ContactEntity.kt         # Entidad de tabla "contacts"
|   |   |-- ContactRepositoryImpl.kt # Implementacion del repositorio
|   |
|   |-- domain/
|   |   |-- Contact.kt               # Modelo de dominio + mappers
|   |   |-- ContactRepository.kt     # Interfaz del repositorio
|   |
|   |-- presentation/
|       |-- detail/
|       |   |-- ContactDetailScreen.kt     # Formulario de crear/editar contacto
|       |   |-- ContactDetailViewModel.kt  # ViewModel con validacion y filtrado
|       |
|       |-- list/
|           |-- ContactListScreen.kt       # Lista de contactos con eliminacion
|           |-- ContactListViewModel.kt    # ViewModel de la lista
|
|-- core/
    |-- di/
    |   |-- AppModule.kt             # Modulos Hilt (DB + Repository)
    |
    |-- navigation/
    |   |-- NavGraph.kt              # Grafo de navegacion (sealed class Screen)
    |
    |-- theme/
        |-- Color.kt                 # Paleta de colores (Blue professional)
        |-- Theme.kt                 # Tema Material3 (light/dark + dynamic)
        |-- Type.kt                  # Tipografia
```

## Tecnologias utilizadas

| Componente | Tecnologia | Version |
|---|---|---|
| Lenguaje | Kotlin | 2.0.21 |
| UI | Jetpack Compose (Material 3) | BOM 2024.10.01 |
| Base de datos | Room | 2.6.1 |
| Inyeccion de dependencias | Hilt (Dagger) | 2.52 |
| Navegacion | Navigation Compose | 2.8.3 |
| Procesamiento de anotaciones | KSP | 2.0.21-1.0.27 |
| Coroutines | Kotlinx Coroutines | 1.9.0 |
| Build system | Gradle (Kotlin DSL) | AGP 8.13.2 |
| Min SDK | Android 8.0 (API 26) | - |
| Target SDK | Android 15 (API 35) | - |
| JVM Target | Java 17 | - |

## Requisitos previos

- **Android Studio** Hedgehog (2023.1.1) o superior.
- **JDK 17** configurado en el proyecto.
- **Dispositivo o emulador** con Android 8.0 (API 26) o superior.

## Instalacion y ejecucion

1. Clonar el repositorio:

```bash
git clone <url-del-repositorio>
cd appProductos
```

2. Abrir el proyecto en Android Studio.

3. Esperar a que Gradle sincronice las dependencias.

4. Ejecutar la aplicacion seleccionando un dispositivo/emulador y presionando **Run** o ejecutando desde terminal:

```bash
./gradlew installDebug
```

## Funcionalidades

### Lista de contactos
- Visualizacion de todos los contactos ordenados alfabeticamente.
- Contador de contactos totales en la barra superior.
- Avatar con la inicial del nombre de cada contacto.
- Vista de email y telefono en cada tarjeta.
- Estado vacio con indicaciones visuales cuando no hay contactos.

### Crear contacto
- Formulario con campos: nombre, correo electronico, telefono y notas.
- Validacion en tiempo real con mensajes de error por campo.
- Contadores de caracteres visibles en cada campo.
- Teclado adaptado segun el tipo de campo (email, telefono).

### Editar contacto
- Carga automatica de los datos existentes al abrir el formulario.
- Mismas validaciones que en la creacion.
- Navegacion de retorno automatica tras guardar.

### Eliminar contacto
- Dialogo de confirmacion antes de eliminar.
- Eliminacion inmediata con actualizacion reactiva de la lista.

### Tema visual
- Soporte para modo claro y oscuro (automatico segun sistema).
- Compatible con Dynamic Color en Android 12+.
- Paleta profesional basada en tonos azules.
- Diseño edge-to-edge.

## Validacion y seguridad

Todos los campos de entrada implementan validacion tanto a nivel de UI como en la capa de logica de negocio (ViewModel). Se aplica un filtrado doble: al momento de escribir (`onValueChange`) y al momento de guardar (`saveContact`).

### Reglas por campo

**Nombre (obligatorio)**
- Caracteres permitidos: letras, espacios, guion (`-`) y apostrofe (`'`).
- Longitud: minimo 2, maximo 50 caracteres.
- Espacios multiples se colapsan automaticamente a uno.

**Correo electronico (opcional)**
- Caracteres permitidos: letras, digitos, `@`, `.`, `_`, `+`, `-`.
- Longitud maxima: 100 caracteres.
- Validacion de formato con expresion regular al guardar.

**Telefono (opcional)**
- Caracteres permitidos: solo digitos (`0-9`).
- Longitud: minimo 7, maximo 10 digitos.
- Teclado numerico forzado.

**Notas (opcional)**
- Longitud maxima: 300 caracteres.
- Caracteres de control y null bytes bloqueados.
- Caracteres `<` y `>` bloqueados como medida anti-inyeccion (XSS/HTML).

### Proteccion contra ataques

- **SQL Injection**: Room utiliza queries parametrizadas en todos los accesos a la base de datos, lo que previene inyeccion SQL por defecto.
- **XSS/HTML Injection**: Los campos de texto filtran caracteres `<` y `>` para prevenir inyeccion de markup.
- **Null byte injection**: Se eliminan caracteres nulos (`\u0000`) de todas las entradas.
- **Overflow de entrada**: Cada campo tiene un limite maximo de caracteres aplicado antes de escribir en el estado.
- **Doble sanitizacion**: Los datos se filtran tanto en la entrada del usuario como antes de persistir en la base de datos.

## Configuracion del proyecto

### Identificadores

- **Application ID**: `com.example.productapp`
- **Namespace**: `app.aplication.appproductos`
- **Root project name**: `ContactHub`

### Base de datos

- **Nombre**: `contact_db`
- **Tabla**: `contacts`
- **Columnas**: `id` (PK autoincremental), `name`, `email`, `phone`, `notes`
- **Version del schema**: 1
- **Export schema**: deshabilitado

### Dependencias principales

Las versiones estan centralizadas en `gradle/libs.versions.toml` y se referencian mediante el catalogo de versiones de Gradle (version catalog).

---

**Autor**: *Completar con nombre del autor*
**Licencia**: *Completar con tipo de licencia*
