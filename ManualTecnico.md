# 🛠️ Manual Técnico – CádizAccesible

**Aplicación móvil Android para la gestión de incidencias de accesibilidad urbana**

---

## 1. Introducción

Este manual técnico describe la arquitectura, tecnologías, estructura del proyecto y decisiones de diseño adoptadas en el desarrollo de **CádizAccesible**. Está dirigido a desarrolladores, evaluadores técnicos y personal encargado del mantenimiento o evolución de la aplicación.

El objetivo es facilitar la comprensión del funcionamiento interno de la aplicación y permitir su ampliación o adaptación futura.

---

## 2. Tecnologías y herramientas utilizadas

El proyecto ha sido desarrollado íntegramente en el ecosistema Android moderno.

- **Lenguaje:** Kotlin
- **Interfaz de usuario:** Jetpack Compose (Material 3)
- **Arquitectura:** MVVM (Model – View – ViewModel)
- **Persistencia local:** Room (SQLite)
- **Programación reactiva:** Kotlin Flow / StateFlow
- **Gestión de imágenes:** Coil
- **Localización:** Google Play Services (FusedLocationProviderClient)
- **Reconocimiento de voz:** RecognizerIntent
- **Gráficos personalizados:** Canvas (Jetpack Compose)
- **Testing:** JUnit, AndroidX Test, Room in-memory database
- **IDE:** Android Studio

---

## 3. Arquitectura del proyecto

### 3.1 Visión general de la arquitectura

```
UI (Jetpack Compose)
↓ observa
ViewModel (StateFlow)
↓ solicita
Repository
↓ accede
Room (DAO → SQLite)
```

---

### 3.2 Capa de Datos (Data)

Responsable del acceso, almacenamiento y gestión de la información persistente.

Incluye:
- **Room Database:** Define la estructura de la base de datos local.
- **Entities:** Representan las tablas de la base de datos.
- **DAO (Data Access Object):** Contienen las consultas SQL.
- **Repositorio:** Actúa como intermediario entre la base de datos y los ViewModels.

Esta capa permite modificar la fuente de datos (por ejemplo, añadir un backend remoto en el futuro) sin afectar a la interfaz.

---

### 3.3 Capa de Lógica (ViewModel)

Los ViewModels gestionan el estado de la aplicación y la lógica de negocio.

Funciones principales:
- Exponer datos a la UI mediante `StateFlow`.
- Aplicar filtros y transformaciones.
- Realizar cálculos de métricas e informes.
- Coordinar operaciones asíncronas.
- Evitar que la interfaz contenga lógica compleja.

Ejemplos de responsabilidades:
- Filtrado de incidencias por estado o gravedad.
- Cálculo de estadísticas e informes.
- Gestión coherente de estados de la aplicación.

---

### 3.4 Capa de Presentación (UI)

Implementada completamente con **Jetpack Compose**.

Características principales:
- Interfaz declarativa y reactiva.
- Reutilización de componentes.
- Observación de estados mediante `collectAsState()`.

La UI no accede directamente a la base de datos ni contiene lógica de negocio; se limita a representar el estado proporcionado por los ViewModels.

---

## 4. Estructura del proyecto

La siguiente estructura refleja la organización por capas del proyecto:

```
com.example.cadizaccesible
│
├── data
│ ├── db
│ │ ├── AppDatabase.kt
│ │ ├── dao
│ │ └── entity
│ ├── repository
│
├── ui
│ ├── screens
│ ├── components
│ ├── navigation
│ └── theme
│
├── viewmodel
│
└── test
```

Esta estructura favorece la escalabilidad y el mantenimiento.

---

## 5. Persistencia de datos (Room)

### 5.1 Base de datos

La base de datos local se define mediante `AppDatabase.kt` y utiliza Room como capa de abstracción sobre SQLite.

Características:
- Persistencia local incluso sin conexión.
- Validación de consultas en tiempo de compilación.
- Integración directa con `Flow` para datos reactivos.

Esta elección es especialmente relevante en un entorno urbano, donde la conectividad puede no ser constante.

---

### 5.2 Entidades

Las entidades representan los modelos persistentes del sistema, por ejemplo:

- `IncidenciaEntity`
- `UsuarioEntity`

Incluyen:
- Claves primarias.
- Tipos de datos adecuados.
- Conversión a modelos de dominio cuando es necesario.

---

### 5.3 DAO y consultas

Los DAO definen las consultas SQL, tanto CRUD como agregadas:

- Inserción y eliminación de incidencias.
- Consultas filtradas por estado o gravedad.
- Consultas agregadas (`COUNT`, `GROUP BY`) para informes.

Las consultas devuelven `Flow`, permitiendo reactividad automática en la interfaz.

---

## 6. Programación reactiva

La aplicación utiliza **Kotlin Flow** para la gestión de datos reactivos.

Ventajas:
- Actualización automática de la interfaz.
- Reducción de estados inconsistentes.
- Mejor rendimiento al evitar recargas completas.

Los ViewModels combinan múltiples flujos mediante operadores como:
- `map`
- `combine`
- `flatMapLatest`

---

## 7. Navegación

La navegación se gestiona mediante **Navigation Compose**.

Características:
- Grafo de navegación centralizado.
- Rutas diferenciadas según el rol del usuario.
- Protección de pantallas mediante control de sesión.
- Limpieza del back stack con `popUpTo`.

Esto evita accesos no válidos y mejora la coherencia de la experiencia de usuario.

---

## 8. Componentes reutilizables

El proyecto incluye un sistema de componentes reutilizables:

- Tarjetas personalizadas para incidencias.
- Chips semánticos para estados y gravedad.
- Campos de texto con entrada por voz.
- Botones reutilizables.
- Gráficos personalizados.

Estos componentes:
- Reciben datos por parámetros.
- Exponen eventos mediante callbacks.
- No contienen lógica de negocio.

---

## 9. Entrada natural de usuario (NUI)

La aplicación integra distintos mecanismos de interacción natural para mejorar la accesibilidad.

### 9.1 Entrada por voz
- Implementada mediante `RecognizerIntent`.
- Integrada en componentes reutilizables.
- Facilita el uso a personas con dificultades de escritura.

### 9.2 Gestos
- Uso de `SwipeToDismiss`.
- Acciones rápidas para la gestión de incidencias.

### 9.3 Sensores
- Uso de GPS para ubicación automática.
- Integración con mapas externos mediante Intents.

---

## 10. Gráficos e informes

Los informes se generan a partir de datos reales almacenados en Room.

- Cálculos realizados en ViewModels.
- Visualización mediante **Canvas**.
- Gráficos adaptativos según los valores.

El uso de Canvas permite:
- Control total del diseño.
- Integración con el tema visual.
- Evitar dependencias externas.

---

## 11. Gestión de permisos

Los permisos se solicitan únicamente cuando son necesarios:

- Ubicación: al crear incidencias.
- Cámara: al adjuntar imágenes.
- Micrófono: al utilizar entrada por voz.

Este enfoque mejora la confianza del usuario y cumple con las buenas prácticas recomendadas por Android.

---

## 12. Pruebas

Se han implementado pruebas para validar el correcto funcionamiento del sistema:

- Tests de DAO con base de datos en memoria.
- Tests de repositorio.
- Tests de ViewModels.

Estas pruebas verifican:
- Persistencia correcta de los datos.
- Cálculos de informes.
- Gestión coherente de estados.

No se han implementado pruebas automatizadas de interfaz, priorizando las pruebas de lógica y persistencia debido al alcance académico del proyecto.

---

## 13. Rendimiento y uso de recursos

Decisiones adoptadas:
- Carga asíncrona de imágenes mediante Coil.
- Acceso a base de datos fuera del hilo principal.
- UI reactiva sin bloqueos.

El resultado es una aplicación fluida, estable y eficiente en el uso de recursos.

---

## 14. Distribución (planteamiento)

Aunque la distribución se realiza en entorno de desarrollo, la aplicación está preparada para:

- Generación de App Bundle (.aab).
- Firma digital mediante KeyStore.
- Publicación en Google Play.
- Despliegue corporativo mediante MDM.

Estas medidas permiten que el proyecto pueda evolucionar hacia un entorno de producción real sin cambios estructurales significativos.

---

## 15. Posibles mejoras futuras

- Sincronización con backend remoto.
- Notificaciones push.
- Realidad aumentada para navegación urbana.
- Pruebas automatizadas de interfaz (UI Tests).
- Soporte multidioma.

---

## 16. Conclusión

El diseño técnico de **CádizAccesible** sigue principios modernos de desarrollo Android, priorizando la separación de responsabilidades, la reactividad y la accesibilidad.

Este manual técnico proporciona la información necesaria para comprender, mantener y ampliar la aplicación de forma estructurada, profesional y alineada con los criterios de calidad exigidos en el desarrollo de aplicaciones Android actuales.
