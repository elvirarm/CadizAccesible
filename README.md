# 📱 CádizAccesible

Proyecto realizado con **Jetpack Compose** en el que los ciudadanos de Cádiz pueden reportar incidencias de accesibilidad que encuentren en la ciudad y comunicárselas a un administrador que gestionará las incidencias mediante diferentes estados.

---

## RA1 – Diseño de la interfaz gráfica y código

### ✅ RA1.a – Análisis de herramientas y librerías

Este proyecto ha sido desarrollado siguiendo los estándares modernos de desarrollo en Android, priorizando la reactividad, la persistencia robusta y una arquitectura desacoplada. A continuación, se detallan las herramientas y librerías clave, justificando su implementación mediante el análisis del código fuente.

#### 1. Interfaz con Jetpack Compose y Material 3
Para la interfaz he usado Jetpack Compose con Material 3.

* **Por qué lo uso:** Me permite crear la interfaz con funciones de Kotlin. Es mucho más rápido porque si el "estado" de la app cambia (por ejemplo, aparece una nueva incidencia), la pantalla se actualiza sola.
* **Análisis del código:**
    * He usado **Scaffold** en casi todas las pantallas para tener una estructura fija (con su barra superior y hueco para el contenido).
    * En la **PantallaCrearIncidencia.kt**, uso **FlowRow** y **FilterChip**. Esto es clave porque las etiquetas de las incidencias se van ajustando solas al ancho de la pantalla, lo que mejora mucho la experiencia visual.
    * Para las listas (como en "Mis Incidencias"), uso **LazyColumn**, que es mucho más eficiente que un scroll normal porque solo renderiza lo que el usuario está viendo.
    * **Estructura de Contenedores:** En **PantallaCrearIncidencia.kt**, se implementa **Scaffold** para gestionar la estructura visual (barras superiores y contenido) de forma consistente.
    * **Jerarquía Visual:** Se utilizan **ElevatedCard** y **FlowRow** para organizar la información de las incidencias de manera modular, facilitando la lectura de datos complejos (categorías, estados, urgencia).
    * **Componentes Avanzados:** Uso de **LazyColumn** para una gestión eficiente de la memoria al renderizar listas extensas de incidencias, cargando solo los elementos visibles en pantalla.

#### 2. Navegación: Navigation Compose
Toda la navegación de la app está centralizada en un único punto.

* **Por qué lo uso:** Para no tener un montón de "Activities" sueltas. Con un solo **NavHost** controlo quién puede entrar a cada sitio.
* **Análisis del código:** En **HostNavegacion.kt** he creado la lógica de roles. Si el usuario es "admin", el grafo de navegación lo lleva a unas pantallas, y si es "ciudadano", a otras. Incluso he puesto una protección: si la sesión es nula, la app te manda directamente a una pantalla de carga o login, evitando errores de navegación.

#### 3. Base de Datos: Room
Para que los datos no se borren al cerrar la app, he usado Room.

* **Por qué lo uso:** Es una capa que envuelve SQLite. Me permite hacer consultas a la base de datos de forma segura, detectando errores de escritura antes de ejecutar la app.
* **Análisis del código:** En el archivo **IncidenciaDao.kt** no solo guardo y borro. He creado consultas específicas como **totalUrgentes** o **distribucionPorEstado**. Esto es lo que me permite luego mostrar gráficos y estadísticas reales en la pantalla de informes.
    * **Consultas Avanzadas (DAO):** En **IncidenciaDao.kt**, no solo se realizan operaciones CRUD básicas, sino que se han implementado consultas de agregación (COUNT, GROUP BY) para generar estadísticas en tiempo real (totales por gravedad, distribución de estados).
    * **Abstracción de Datos:** El **RepositorioIncidenciasRoom** actúa como mediador, transformando las entidades de base de datos (Entity) en modelos de dominio, desacoplando la lógica de negocio del esquema de la base de datos.
    * He usado un **Repositorio** para que la interfaz no hable directamente con la base de datos, separando bien las responsabilidades.

#### 4. Reactividad: Kotlin Flow y StateFlow
Esta es la "magia" que hace que la app parezca viva.

* **Por qué lo uso:** En lugar de pedirle a la base de datos los datos cada vez, me suscribo a un **Flow**. Si algo cambia en la base de datos, Room "avisa" y la pantalla se refresca al instante.
* **Análisis del código:** En el **InformesViewModel.kt**, uso el operador **combine**. Esto sirve para que, si el usuario cambia un filtro (por ejemplo, ver solo incidencias "Graves"), la lista se actualice automáticamente sin tener que pulsar ningún botón de "buscar".
    * **Reactividad en tiempo real:** Los métodos del DAO devuelven **Flow<List<Incidencia>>**, lo que significa que cualquier cambio en la tabla SQL se refleja instantáneamente en la interfaz.
    * **Transformación de Estados:** En **InformesViewModel.kt**, se utilizan operadores avanzados como **combine** y **flatMapLatest** para fusionar múltiples flujos de datos (filtros de usuario + datos de BD) en un único **StateFlow** que consume la UI mediante **collectAsState()**.

#### 5. Arquitectura MVVM
He separado el proyecto en tres capas: la Vista (Compose), el ViewModel (Lógica) y el Modelo (Datos/Room).

* **Por qué lo uso:** Para que el código no sea un caos. Si mañana quiero cambiar cómo se guardan los datos, solo toco el repositorio y la pantalla ni se entera.
* **Análisis del código:** El **InformesViewModel** es el cerebro. Él hace los cálculos de cuántas incidencias hay de cada tipo y se lo pasa "mascadito" a la pantalla para que ella solo tenga que dibujarlo.
    * **Encapsulamiento:** **InformesViewModel** centraliza la lógica de filtrado y cálculo de KPIs, evitando que los archivos .kt de la interfaz contengan lógica de cálculo compleja.
    * **Inyección de Dependencias Manual:** Se observa el uso de **Factories** para instanciar ViewModels con sus respectivos repositorios, asegurando que cada componente reciba solo las dependencias que necesita.

#### 6. Otras librerías importantes
* **Coil (Imágenes):** La uso para cargar las fotos de las incidencias. Es muy ligera y evita que la app se bloquee al cargar imágenes grandes.
* **Google Play Services (Ubicación):** En **PantallaCrearIncidencia.kt** uso el GPS para sacar la dirección exacta del usuario automáticamente, usando el Geocoder para pasar de coordenadas a una calle real. Además el administrador puede entrar en detalles de la incidencia y abrir la ubicación de la incidencia en Google Maps.
* **Entrada por voz:** He añadido un botón de micro que usa **RecognizerIntent**. Es muy bueno para la accesibilidad, permitiendo que alguien rellene la descripción de la incidencia sin tener que escribir. Esto se aplica a los campos de título, descripción y ubicación mediante un componente reutilizable (**Input Voice Button/CampoTextoConVoz**).

> **Resumen del flujo de mi código:**
> * Room emite datos brutos vía Flow.
> * El Repositorio mapea estos datos a modelos de negocio.
> * El ViewModel procesa, filtra y expone el estado mediante StateFlow.
> * Jetpack Compose observa el estado y recompone la interfaz automáticamente ante cualquier cambio.

---

### ✅ Creación de la Interfaz Gráfica (RA1.b)

En este apartado se describe cómo he diseñado y estructurado la interfaz de CádizAccesible. No me he limitado a crear pantallas aisladas, sino que he desarrollado un flujo completo e integrado que diferencia entre dos tipos de usuarios: el **Ciudadano** y el **Administrador**.

#### 1. Estructura y Flujo de Navegación
La aplicación utiliza un sistema de navegación centralizado que garantiza una experiencia coherente. La interfaz está construida íntegramente con componentes de **Material 3**, asegurando que elementos como botones, tarjetas y barras de navegación sigan un estándar visual profesional.

* **Flujo por Roles:** Gracias al uso de un **NavHost** en **HostNavegacion.kt**, la interfaz se adapta según quién inicie sesión. Un ciudadano accede a la creación y consulta de sus reportes, mientras que un administrador visualiza herramientas de gestión y analítica.
* **Análisis del Código:** En **HostNavegacion.kt**, he definido las rutas de forma que el paso de parámetros entre pantallas (como el ID de una incidencia para ver su detalle) sea fluido y no rompa la navegación.

#### 2. Pantallas Principales y Funcionalidad
* **A. Formulario de Reporte (PantallaCrearIncidencia.kt):** Es la pantalla con mayor carga de componentes de entrada de datos. He buscado que sea una interfaz "inteligente".
    * **Componentes técnicos:** He organizado la información en bloques usando **ElevatedCard**. Para la selección de categorías y niveles de gravedad, utilizo **FilterChip** dentro de un **FlowRow**, lo que permite que los elementos se posicionen automáticamente.
    * **UX Avanzada:** Ofrece feedback en tiempo real. Al pulsar en "Publicar", el botón desaparece para mostrar un **CircularProgressIndicator**, evitando envíos duplicados.
* **B. Gestión de Listados (PantallaMisIncidencias.kt y PantallaBandejaAdmin.kt):** Interfaz que permite la interacción directa mediante gestos.
    * **Interacción mediante Swipe:** He configurado el componente **SwipeToDismiss**. En la vista del ciudadano sirve para eliminar, pero en la del administrador permite una gestión rápida: deslizar a la derecha para "En revisión" y a la izquierda para "Rechazada", con fondos de colores e iconos.
    * **Tratamiento de estados:** He programado estados específicos para cuando la lista está vacía, mostrando una ilustración o un texto informativo.
* **C. Detalle Dinámico (PantallaDetalleIncidencia.kt):** Ejemplo de cómo una interfaz muta según el contexto.
    * **Adaptación por Rol:** Si entras como admin, habilita gestión con campo de texto (con voz) y botones de estado. Si eres usuario, se oculta la gestión y se muestra solo la respuesta.
    * **Integración con el Sistema:** He incluido "Intents" para que, al pulsar la dirección, se abra **Google Maps**.
* **D. Panel de Análisis (PantallaInformes.kt):** Dashboard para ver el impacto de las incidencias de un vistazo.
    * **Visualización de Datos:** Uso **Cards** personalizadas para mostrar KPIs como el total de incidencias urgentes.
    * **Gráficos Reactivos:** Incluye gráficos de barras o sectores que se filtran en tiempo real según el estado o gravedad seleccionada.

#### 3. Elementos de Calidad de la Interfaz
* **Modo Oscuro:** Toda la interfaz se adapta automáticamente a las preferencias del sistema gracias a la estructura de **Theme.kt**.
* **Jerarquía Tipográfica:** Uso de diferentes estilos de fuente (Headline, Body, Label) de Material 3 para identificar qué es principal y qué es secundario.
* **Estados de Carga:** Uso de indicadores de progreso mientras Room recupera la información.

---

### ✅ RA1.c - Uso de Layouts y Posicionamiento

He puesto especial atención a cómo se organiza la información para crear una interfaz jerarquizada, usable y adaptable.

#### 1. Estructura Base: Scaffold
Todas las pantallas principales utilizan un **Scaffold**.
* **Justificación:** Me permite separar claramente la **TopAppBar** del contenido. Al usar **contentWindowInsets**, garantizo que el diseño respete las zonas seguras (barra de estado o notch).

#### 2. Organización de los Contenedores
* **Column con Scroll Vertical:** En pantallas como **PantallaCrearIncidencia.kt**, utilizo una **Column** principal con **verticalScroll**.
    * **Análisis técnico:** Uso `verticalArrangement = Arrangement.spacedBy(16.dp)` para mantener una separación constante entre bloques sin añadir márgenes manuales.
* **Row y pesos (Weight):** Para elementos que comparten el ancho (KPIs en **PantallaInformes.kt** o botones "Aceptar/Rechazar").
    * **Justificación:** Al aplicar `Modifier.weight(1f)`, aseguro que ambos elementos ocupen exactamente la mitad de la pantalla.
* **LazyColumn para Listados:** Para la bandeja del admin y lista del ciudadano. Es eficiente porque solo carga lo que se ve. Uso `key = { it.id }` para gestionar animaciones y cambios correctamente.
* **FlowRow para Adaptabilidad:** En formularios con muchos chips. Evita que la fila se corte, permitiendo que las categorías salten de línea automáticamente.

#### 3. Jerarquía Visual y Control de Diseño
* **Diseño por Bloques (Cards):** Utilizo **ElevatedCard** para agrupar información relacionada, creando una jerarquía visual clara.
* **Adaptabilidad de Textos:** En **TarjetaIncidencia.kt**, los títulos tienen `maxLines = 1` y `overflow = TextOverflow.Ellipsis` para no deformar la tarjeta.
* **Espaciado Consistente:** Uso **Spacer** y modificadores de padding con medidas estándar (8.dp, 16.dp).
* **Restricciones de Tamaño:** Uso `heightIn(min = ...)` en tarjetas de informes para conservar la simetría aunque el texto varíe.

---

### ✅ RA1.d - Personalización de Componentes y Estilo

He trabajado en una personalización profunda para que la interfaz sea coherente y profesional.

#### 1. Sistema de Diseño y Tematización (Theme)
Para que la aplicación no solo fuera funcional sino que tuviera una identidad propia vinculada a la ciudad, he personalizado la paleta de colores de Material 3. Los tonos elegidos buscan evocar el entorno de Cádiz: el **color arena** (fondos) representa nuestras playas, mientras que los tonos **celestes y azules** (navegación y primarios) hacen referencia al cielo y al mar. Esta coherencia visual se extiende hasta el **logo de la aplicación**, diseñado desde cero.

* **Colores Semánticos:** Mapeo al sistema de Material 3 (primary, errorContainer, etc.) para que cualquier cambio futuro sea consistente.
* **Modo Oscuro Persistente:** En **AppRoot.kt**, el tema se recoge mediante un **Flow** desde las preferencias del usuario.

#### 2. Componentes Adaptados al Dominio
* **Tarjeta de Incidencia Personalizada:** **TarjetaIncidencia.kt** gestiona miniaturas, recortes de texto y etiquetas de estado de forma dinámica.
* **Chips de Estado (StatusChips):** Sistema en **AppChips.kt** que cambia de color (Success, Warning, Danger) según la gravedad.
* **Campo de Texto con Voz:** Extensión del **OutlinedTextField** estándar en **CampoTextoConVoz.kt** para accesibilidad avanzada.

#### 3. Personalización de Alto Nivel: Gráficos con Canvas
En la **Pantalla de Informes**, he programado **GraficoBarras.kt** usando el **Canvas** de Compose. He dibujado desde cero ejes, barras y etiquetas integrando los colores del tema.

#### 4. Feedback Visual y Botones
* **Botones por Contexto:** Las acciones positivas usan color primario y las negativas (Rechazar) el esquema de **Error**.
* **Estados de la Interfaz:** Personalización de **CircularProgressIndicator** y pantallas de error dentro de contenedores específicos.

---

### ✅ RA1.e - Análisis Profundo del Código y Arquitectura

Basado en una arquitectura de capas escalable y robusta.

#### 1. Organización Arquitectónica (Separación de Responsabilidades)
Separación estricta entre la **Lógica de Datos** y la **UI**, siguiendo recomendaciones oficiales:
* **Capa Data (Repositorio y DB):** Encapsula Room y transforma datos brutos en útiles.
* **Capa UI (Compose y ViewModels):** Representación visual y reacción a interacciones.

#### 2. El Ciclo de Vida del Dato: Programación Reactiva con Flow
* **Consultas Observables:** En **IncidenciaDao.kt**, las funciones devuelven `Flow<List<IncidenciaEntity>>`. Room actúa como fuente activa.
* **Transformación en el Repositorio:** Uso de `.map { it.aModelo() }` para que las Entities nunca lleguen a la UI, trabajando solo con modelos limpios.

#### 3. Lógica de Negocio y Estado en el ViewModel
* **Uso de combine para Informes:** En **InformesViewModel.kt**, fusiono flujos (totales, urgentes y filtros) para recalcular porcentajes de forma reactiva.
* **Inyección mediante Factory:** Implementación de **ViewModelProvider.Factory** para pasar parámetros complejos y facilitar la escalabilidad.

#### 4. Navegación Declarativa y Seguridad por Roles
Centralizado en **HostNavegacion.kt**:
* **Control de Acceso:** Evaluación del rol (**ADMIN** vs **CIUDADANO**) al inicio para redirigir al grafo correspondiente.
* **Paso de Argumentos:** Rutas configuradas para aceptar argumentos dinámicos (ID de incidencia).

#### 5. Componentes Reutilizables y el Principio DRY
* **Componentes Compuestos:** **CampoTextoConVoz.kt** extiende el TextField básico para añadir accesibilidad.
* **Consistencia Visual:** Centralización en **StatusChip** o **TarjetaIncidencia** para propagar cambios de diseño instantáneamente.

---

### ✅ RA1.f - Adaptación y Modificación del Código

Adaptación funcional para mejorar la UX y la eficiencia.

#### 1. Evolución del Gesto Swipe
Modificación de **SwipeToDismiss** para acciones reales contra Room:
* **Eliminación con confirmación:** En **PantallaMisIncidencias.kt**, `confirmStateChange` lanza una corrutina de eliminación solo si el gesto se completa.
* **Gestión Multiestado (Admin):** En **PantallaBandejaAdmin.kt**, un **LaunchedEffect** detecta la dirección (Derecha: En Revisión / Izquierda: Rechazada).

#### 2. Extensión de Componentes: Entrada Híbrida de Texto
Extensión de **OutlinedTextField** para **CampoTextoConVoz.kt**:
* **Lógica de concatenación:** Decide si el dictado reemplaza o anexa texto.
* **Control del Intent:** Adaptación de **RecognizerIntent** dentro de un **ActivityResultLauncher** filtrando resultados.

#### 3. Lógica Personalizada en el Procesamiento de Datos
* **Filtros Excluyentes:** En **InformesViewModel.kt**, al activar un filtro se limpia automáticamente el otro mediante `.update`, evitando pantallas vacías.
* **Transformación de KPIs:** Uso de **combine** para realizar cálculos matemáticos en tiempo real y que la UI reciba el dato ya procesado.

#### 4. Creación de Componentes desde Cero (Canvas)
En **GraficoBarras.kt**, dibujo manual de barras, ejes y cuadrícula con **Canvas**. Las barras se escalan proporcionalmente al valor máximo para mantener la corrección visual.

#### 5. Optimización del Flujo de Navegación
Control de "limpieza" del historial:
* **Navegación Segura:** Uso de **popUpTo** con `inclusive = true` en login y cambio de rol para evitar que se pueda volver atrás a pantallas no válidas.

---

### ✅ Asociación de Eventos e Interacción (RA1.g)

#### 1. Eventos de Entrada y Formulario
* **Sincronización de Estado:** En **PantallaCrearIncidencia.kt**, los `onValueChange`, `Switches` y clics en `FilterChips` están vinculados al VM para validar datos en tiempo real.
* **Entrada por Voz:** Clic del micro asociado a un **ActivityResultLauncher** para integrar el texto automáticamente.

#### 2. Interacción Natural mediante Gestos (Swipe)
* **Gesto de Borrado:** En la lista del ciudadano, el swipe a la izquierda dispara la eliminación en Room.
* **Gestión de Administración:** En la bandeja admin el gesto es dual. Uso de **LaunchedEffect** para ejecutar la actualización asíncrona según la dirección.

#### 3. Integración con Servicios del Sistema
* **Ubicación y Permisos:** El botón dispara solicitud de permisos, conexión a **FusedLocationProviderClient** y uso de **Geocoder**.
* **Cámara y Galería:** Gestión de apertura, captura de URI y actualización de previsualización.
* **Intents Externos:** Clic en dirección lanza **Intent.ACTION_VIEW** para abrir **Google Maps**.

#### 4. Feedback y Coherencia en la Respuesta
* **Indicadores de Progreso:** Los clics en publicar/actualizar activan **CircularProgressIndicator**.
* **Navegación Basada en Eventos:** En **HostNavegacion.kt**, la app reacciona a eventos de sesión o clics en listas manteniendo el contexto del ID.

---

### ✅ Integración Global de la Aplicación (RA1.h)

#### 1. Núcleo Centralizado y Control de Flujo
* **Punto de Entrada Único:** En **AppRoot.kt** se integra el tema y la navegación.
* **Gestión de Navegación e Identidad:** **HostNavegacion.kt** construye el grafo específico según **ADMIN** o **CIUDADANO**.

#### 2. Ciclo de Vida del Dato (Integración End-to-End)
* **Flujo:** Creación (GPS/Cámara) -> Persistencia (Room) -> Sincronización (Flow reactivo) -> Gestión (Admin). Todas las pantallas consumen la misma fuente de verdad: **RepositorioIncidenciasRoom**.

#### 3. Integración con el Ecosistema Android
* **Hardware y Sensores:** Cámara, galería y GPS son esenciales en el flujo.
* **Inteligencia:** **Speech Recognizer** e **Intents** para mapas externos facilitan la accesibilidad.

#### 4. Estabilidad y Estados de la Interfaz
* **Feedback Continuo:** Gestión de estados cargando, vacío o error.
* **Coherencia Visual:** Componentes reutilizables garantizan la misma estética en toda la app.

## RA2 – Utilización de librerías y herramientas NUI

Este apartado analiza cómo **CádizAccesible** rompe con el esquema de las interfaces gráficas tradicionales (GUI) para adoptar una **Interfaz Natural de Usuario (NUI)**. El objetivo principal es que la tecnología se adapte al usuario —especialmente a aquellos con necesidades de accesibilidad— y no al revés.

---

### ✅ Análisis e Integración de Herramientas NUI (RA2.a)

He implementado un conjunto de herramientas que permiten una interacción más humana y contextual, reduciendo la carga cognitiva y las barreras físicas.

#### 1. Reconocimiento de Voz (Speech-to-Text)
La voz es la interfaz natural por excelencia. He integrado esta capacidad para permitir el reporte de incidencias en situaciones donde el uso del teclado es dificultoso o imposible.

* **Implementación técnica:** Utilizo la API `RecognizerIntent.ACTION_RECOGNIZE_SPEECH` de Android.
* **Integración en la UI:** Mediante el componente personalizado `CampoTextoConVoz.kt`, el usuario puede dictar el título, la descripción o la ubicación.
* **Justificación:** Esta herramienta es vital para usuarios con movilidad reducida en las manos o para ciudadanos que se desplazan por la vía pública y necesitan una forma rápida y "manos libres" de introducir datos.

#### 2. Interacción mediante Gestos (Gestural UI)
He sustituido la navegación basada en botones pequeños y menús profundos por gestos táctiles intuitivos que aprovechan el comportamiento natural del usuario con dispositivos móviles.

* **Uso de Swipe:** Implementado en la bandeja de administración y en el listado de incidencias mediante el componente `SwipeToDismiss`.
* **Justificación:** El gesto de deslizar es una respuesta natural y fluida. Al permitir que el administrador gestione estados (aceptar/rechazar) con un simple movimiento lateral, se mejora la velocidad de respuesta y se simplifica la experiencia de uso al eliminar clics innecesarios.

#### 3. Ubicación y Geofencing Contextual
La aplicación utiliza el contexto físico del ciudadano como un canal de entrada de información automática, convirtiendo al GPS en parte de la interfaz.

* **Herramienta:** `FusedLocationProviderClient` de Google Play Services.
* **Flujo Natural:** El sistema detecta la posición del usuario y, mediante procesos de geocodificación inversa (`Geocoder`), propone la dirección exacta automáticamente.
* **Justificación:** Es una interfaz natural porque el sistema "entiende" el entorno. Evita que el usuario tenga que conocer el nombre de la calle exacta donde se encuentra, delegando esa tarea técnica a los sensores del dispositivo.

#### 4. Sensores de Imagen (Entrada Visual)
La cámara no es solo un accesorio, sino un sensor de entrada de datos que permite "mostrar" la realidad sin necesidad de describirla con lenguaje abstracto.

* **Integración:** Captura directa mediante `ActivityResultContracts` para cámara y galería.
* **Justificación:** Para una incidencia de accesibilidad, una imagen es el input más natural posible. Permite una comunicación no verbal inmediata y precisa entre el ciudadano y la administración.



---

### 🚀 Análisis de Evolución Tecnológica (RA2.e y RA2.f)

Como parte del análisis crítico de este RA, he evaluado la incorporación de tecnologías emergentes que elevarían la accesibilidad de la plataforma a un nivel superior en futuras versiones.

#### 1. Adaptabilidad Ergonómica mediante Visión Artificial (RA2.e)
Aunque no se ha incluido en el MVP (Producto Mínimo Viable) actual por razones de optimización de recursos y privacidad, he proyectado la integración de **ML Kit (Pose Detection)**.

* **Propuesta:** La aplicación podría analizar, mediante el procesamiento en local de la cámara frontal, la postura del usuario o la forma en que sujeta el dispositivo.
* **Impacto en la Accesibilidad:** Si el sistema detecta una limitación en la precisión del toque o una vibración excesiva, la interfaz podría reaccionar dinámicamente aumentando el tamaño de los objetivos táctiles (botones) o activando automáticamente el dictado por voz, personalizando la ergonomía de la app en tiempo real y sin intervención del usuario.

#### 2. Realidad Aumentada para la Navegación Urbana (RA2.f)
La arquitectura de datos actual, basada en coordenadas geográficas almacenadas en **Room**, está preparada técnicamente para dar el salto a la Realidad Aumentada (AR).

* **Viabilidad Técnica:** Utilizando **ARCore**, los datos de latitud y longitud ya existentes podrían proyectarse sobre el *viewport* de la cámara del dispositivo.
* **Caso de Uso:** Un usuario con discapacidad podría enfocar la calle y ver indicadores en 3D sobre el mundo real, señalando rampas accesibles o avisando de obstáculos reportados por otros ciudadanos.
* **Conclusión:** Esta capa de información digital sobre el mundo físico representa el estado del arte en interfaces naturales, eliminando la necesidad de interpretar mapas 2D y haciendo la información mucho más accesible.

# RA3 – Uso de librerías y componentes avanzados

En este apartado se detalla la construcción del sistema de componentes de **CádizAccesible**, analizando las herramientas utilizadas y justificando la modularidad del código para cumplir con los estándares de reutilización, flexibilidad e integración total en el flujo de la aplicación.

---

### ✅ Herramientas para la Creación de Componentes (RA3.a)

Para construir este "mini sistema de diseño", he utilizado las APIs más avanzadas de Android que garantizan consistencia visual y técnica:

* **Jetpack Compose (Motor Declarativo):** Base del proyecto que permite fragmentar la UI en funciones `@Composable` independientes, facilitando el mantenimiento global y permitiendo que componentes como `AppChips.kt` sean totalmente agnósticos a la pantalla donde se usan.
* **Material 3 y Slot APIs:** He adoptado Material 3 como librería base (`ElevatedCard`, `FilterChip`, `OutlinedTextField`). El uso de **Slot APIs** (como el parámetro `content` en `AppCard.kt`) permite que el contenedor gestione el diseño y la elevación, mientras que el contenido interno es totalmente flexible.
    ```kotlin
    // Ejemplo de Slot API en AppCard.kt
    @Composable
    fun AppCard(
        title: String? = null,
        modifier: Modifier = Modifier,
        content: @Composable ColumnScope.() -> Unit // Slot para contenido flexible
    ) { ... }
    ```
* **Canvas API:** Utilizada en `GraficoBarras.kt` para dibujo de bajo nivel sin depender de librerías externas pesadas. Esto demuestra el uso de APIs avanzadas de dibujo para crear visualizaciones de alto rendimiento.
    ```kotlin
    // Fragmento de dibujo manual con Canvas para el gráfico
    Canvas(modifier = modifier.fillMaxWidth().height(alturaDp.dp)) {
        drawRoundRect(
            color = colorBarra,
            size = Size(width = anchoBarra, height = altoBarraPx)
        )
    }
    ```
* **Coil:** Integración de la librería mediante `AsyncImage` para una gestión eficiente de la memoria y carga asíncrona de imágenes en las tarjetas de incidencias.
* **FlowRow:** Herramienta clave para el diseño adaptativo de grupos de chips, evitando que el contenido se corte al saltar de línea automáticamente según el ancho del dispositivo.

---

### ✅ Diseño y Reutilización de Componentes (RA3.b / RA3.c)

La interfaz se basa en el principio de **desacoplamiento**: los componentes son "cajas negras" que no conocen el contexto de la base de datos, simplemente reciben datos y emiten eventos, utilizando **parámetros con valores por defecto (defaults)** para maximizar su flexibilidad.

#### 1. Componentes de Dominio: TarjetaIncidencia
* **Modularidad:** Se utiliza tanto en la vista del ciudadano (`PantallaMisIncidencias`) como en la del administrador (`PantallaBandejaAdmin`). No navega por sí misma; recibe un objeto `Incidencia` y un callback `onClick`.
* **Flexibilidad (RA3.c):** Incluye el parámetro `mostrarMiniatura: Boolean = true`, permitiendo reutilizar la tarjeta en listados densos o modos compactos simplemente cambiando un parámetro.
    ```kotlin
    @Composable
    fun TarjetaIncidencia(
        incidencia: Incidencia,
        onClick: (String) -> Unit,
        modifier: Modifier = Modifier,
        mostrarMiniatura: Boolean = true // Parámetro con default
    )
    ```

#### 2. Componentes Semánticos: StatusChip y TagChip
* **Abstracción:** Centralizan la lógica visual de los estados (`Success`, `Warning`, `Danger`). Si cambia el color representativo de una incidencia "Urgente", solo se modifica en este componente y el cambio se propaga por toda la aplicación automáticamente.

#### 3. Componentes de Entrada Híbrida: CampoTextoConVoz
* **Configurabilidad Máxima:** Unifica entradas cortas y largas bajo una misma lógica. Parámetros como `singleLine = false` y `anexarDictado = true` permiten que el componente funcione para un título o una descripción extensa.
* **Reutilización:** Se emplea en la creación de incidencias y en la gestión de comentarios del administrador, garantizando que el dictado por voz funcione siempre de la misma manera.

#### 4. Robustez Visual: GraficoBarras
* **Prevención de errores:** Incluye "safe values" para asegurar que el componente no falle si la base de datos devuelve una lista vacía, demostrando un diseño preparado para producción.
    ```kotlin
    // Lógica de protección contra listas vacías
    val safeValores = if (valores.isEmpty()) listOf(0) else valores
    ```

---

### ✅ Gestión de Eventos e Interactividad (RA3.d)

Los componentes de **CádizAccesible** no son estáticos; notifican acciones hacia las capas superiores (ViewModels) mediante callbacks, aplicando el patrón de **State Hoisting**:

* **Desacoplamiento de navegación:** `TarjetaIncidencia` emite un `onClick(id)`. La tarjeta no sabe a qué pantalla ir; el NavHost decide la acción.
* **Hibridación de eventos en entrada de datos:** `CampoTextoConVoz` coordina la escritura manual y el dictado por voz, entregando a la lógica de negocio un valor final ya procesado.
    ```kotlin
    // Integración del evento de voz dentro del componente de texto
    VoiceInputButton(onTextRecognized = { texto ->
        val nuevoTexto = if (anexarDictado) "$value $texto".trim() else texto
        onValueChange(nuevoTexto) // Notifica el cambio al nivel superior
    })
    ```
* **Evolución propuesta:** Los chips de estado están diseñados para aceptar un `onClick` opcional en futuras versiones, permitiendo filtrar las listas directamente desde la etiqueta de la incidencia.

---

### ✅ Catálogo de componentes UI (RA3.f)

Este manual técnico detalla la responsabilidad y ubicación de las piezas principales del proyecto:

| Nombre | Ubicación | Responsabilidad | Pantallas Principales |
| :--- | :--- | :--- | :--- |
| **AppCard** | `ui/components/AppCard.kt` | Contenedor base con estilo coherente y slots. | Todas las secciones. |
| **TarjetaIncidencia** | `ui/components/TarjetaIncidencia.kt` | Transforma el modelo en tarjeta visual interactiva. | Mis Incidencias, Bandeja Admin. |
| **AppChips** | `ui/components/AppChips.kt` | Etiquetas semánticas con colores por estado/gravedad. | Tarjetas, Detalle, Informes. |
| **CampoTextoConVoz** | `ui/components/CampoTextoConVoz.kt` | Input híbrido (teclado + dictado por voz). | Crear Incidencia, Detalle. |
| **GraficoBarras** | `ui/components/GraficoBarras.kt` | Visualización estadística personalizada con Canvas. | Pantalla Informes. |
| **VoiceInputButton** | `ui/components/VoiceInputButton.kt` | Botón NUI que gestiona el `RecognizerIntent`. | Interno en CampoTextoConVoz. |



---

### ✅ Integración en el Flujo de la App (RA3.h)

La estabilidad de **CádizAccesible** se debe a la integración total de estos componentes en el flujo real de datos y navegación:

1.  **Reutilización Transversal:** El `CampoTextoConVoz` se usa tanto para el ciudadano como para el administrador, garantizando una experiencia de accesibilidad uniforme en toda la plataforma.
2.  **Jerarquía de Composición:** El `VoiceInputButton` está integrado dentro del `CampoTextoConVoz`, demostrando una arquitectura de componentes por niveles (Botón -> Campo -> Pantalla).
3.  **Conexión con Room y Flow:** Los componentes reaccionan a flujos de datos reales. Al actualizar una incidencia mediante el gesto *swipe* en la lista, los chips de estado se recomponen automáticamente para reflejar el cambio en la base de datos sin recargar la pantalla.
4.  **Sincronización de Informes:** El `GraficoBarras` consume directamente los datos procesados del `InformesViewModel`, utilizándose por duplicado para mostrar datos por estado y por gravedad dentro del mismo dashboard.

### **Matriz de Integración Final**

| Componente | Integración Clave | Acción Resultante |
| :--- | :--- | :--- |
| **TarjetaIncidencia** | `LazyColumn` en listados | Navegación al detalle vía ID. |
| **CampoTextoConVoz** | Formularios de entrada | Validación y persistencia en Room. |
| **StatusChip** | Indicadores de estado | Feedback visual de gestión rápida. |
| **GraficoBarras** | Dashboard Administrativo | Análisis visual de KPIs reales. |


# RA4 – Estándares, Usabilidad y Estilo

En este bloque se analiza cómo **CádizAccesible** se alinea con los estándares de diseño modernos de Android, garantizando una interfaz coherente, usable y accesible, diseñada específicamente para el ciudadano y el gestor municipal.

---

### ✅ Aplicación de Estándares e Interfaz (RA4.a / RA4.b)

El desarrollo se ha regido por el sistema **Material Design 3 (Material You)**, asegurando una experiencia predecible y profesional.

* **Consistencia y Estilo:** Se utiliza un `Scaffold` base en cada pantalla, integrando componentes oficiales como `TopAppBar`, `ElevatedCard` y `FilterChip`.
* **Jerarquía Visual:** Aplicación estricta de la escala tipográfica (`titleLarge` para encabezados, `bodyMedium` para datos).
* **Reflexión Crítica (RA4.b):** La elección de Material 3 es ideal para una app de servicio público porque reduce la curva de aprendizaje al usar patrones que el usuario ya conoce. Se ha priorizado la **claridad y la coherencia** frente a una personalización excesiva, garantizando que la tecnología sea inclusiva.



---

### ✅ Diseño de Menús y Navegación (RA4.c)

En **CádizAccesible**, el sistema de menús se basa en la **eficiencia cognitiva**, evitando menús globales complejos que distraigan del objetivo principal.

* **Navegación por Roles:** El "menú" principal son los Dashboards de inicio, que presentan solo las acciones relevantes para el ciudadano (Crear/Ver) o el administrador (Bandeja/Informes).
* **TopAppBar como Orientación:** En todas las pantallas se utiliza una cabecera clara que indica al usuario dónde está y cómo volver, cumpliendo el estándar de navegación jerárquica.
* **Menús Gestuales:** Se han integrado acciones rápidas mediante *swipe*, actuando como menús contextuales que no saturan el espacio visual.

---

### ✅ Distribución de Acciones y Controles (RA4.d / RA4.e)

La distribución de elementos interactivos sigue una **secuencia lógica de uso** para guiar al usuario y prevenir errores.

* **Flujo Natural (RA4.e):** Los controles se agrupan en `ElevatedCard` por bloques funcionales (Descripción -> Clasificación -> Multimedia). El usuario completa la tarea de arriba hacia abajo, terminando siempre en las acciones finales.
* **Prevención de Errores (RA4.d):** Las acciones críticas (Publicar, Rechazar) están claramente separadas de las secundarias.
    ```kotlin
    // Prevención de errores: botón deshabilitado durante la carga
    Button(
        onClick = { viewModel.publicar() },
        enabled = !state.estaPublicando // Evita duplicados (RA4.d)
    ) {
        if (state.estaPublicando) CircularProgressIndicator() else Text("Publicar")
    }
    ```

---

### ✅ Elección de Controles Adecuados (RA4.f)

He seleccionado cada control basándome en el tipo de dato para que la interacción sea natural:

* **Chips vs Desplegables:** Uso de `FilterChip` para categorías de accesibilidad, permitiendo ver todas las opciones de un vistazo.
* **Switches:** Para valores booleanos claros como "Urgente" o "Temporal".
* **OutlinedTextField:** Para entradas de texto, usando `singleLine` para títulos y `minLines = 3` para descripciones, delimitando claramente el área táctil.



---

### ✅ Diseño Visual, Estética y Legibilidad (RA4.g)

El diseño visual busca que **la función prime sobre la decoración**.

* **Color Semántico:** El color comunica estados sin necesidad de leer texto (Verde para "Resuelta", Rojo para "Rechazada/Error").
* **Espaciado Uniforme:** Uso sistemático de `Arrangement.spacedBy(16.dp)` para evitar el amontonamiento visual y facilitar la pulsación.
* **Modo Claro/Oscuro:** Implementación nativa mediante el tema global que asegura legibilidad en cualquier condición lumínica.
    ```kotlin
    // Uso de colores semánticos del tema (RA4.g)
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) { /* Mensaje de error */ }
    ```

---

### ✅ Claridad de Mensajes y Feedback (RA4.h)

La comunicación sistema-usuario es directa, humana y libre de tecnicismos innecesarios.

* **Feedback de Proceso:** Mensajes descriptivos como *"Publicando incidencia..."* o *"Cargando datos..."* acompañados de indicadores de progreso.
* **Etiquetas Claras:** Los botones usan verbos de acción directa ("Publicar", "Rechazar", "Cerrar sesión") en lugar de etiquetas ambiguas.
* **Gestos Comunicativos:** El *swipe* no es solo un movimiento; muestra texto e iconos dinámicos mientras se realiza para confirmar la intención del usuario.

| Contexto | Mensaje / Control | Propósito |
| :--- | :--- | :--- |
| **Error** | Card en `errorContainer` | Explicar el problema de forma no punitiva. |
| **Carga** | `CircularProgressIndicator` | Eliminar la sensación de bloqueo. |
| **Admin** | "Gestión (Admin)" | Delimitar acciones exclusivas de gestión. |
| **NUI** | Texto sobre el Swipe | Confirmar la acción antes de ejecutarla. |



> **Conclusión del bloque:** CádizAccesible aplica un diseño de interfaz donde cada decisión visual y de interacción tiene como objetivo final la **utilidad pública y la accesibilidad real**, cumpliendo con los estándares profesionales de desarrollo en Android.

# RA4 – Estándares, Usabilidad y Estilo

En este bloque se analiza cómo **CádizAccesible** se alinea con los estándares de diseño modernos de Android, garantizando una interfaz coherente, usable y accesible, diseñada específicamente para el ciudadano y el gestor municipal.

---

### ✅ Aplicación de Estándares e Interfaz (RA4.a / RA4.b)

El desarrollo se ha regido por el sistema **Material Design 3 (Material You)**, asegurando una experiencia predecible y profesional.

* **Consistencia y Estilo:** Se utiliza un `Scaffold` base en cada pantalla, integrando componentes oficiales como `TopAppBar`, `ElevatedCard` y `FilterChip`.
* **Jerarquía Visual:** Aplicación estricta de la escala tipográfica (`titleLarge` para encabezados, `bodyMedium` para datos).
* **Reflexión Crítica (RA4.b):** La elección de Material 3 es ideal para una app de servicio público porque reduce la curva de aprendizaje al usar patrones que el usuario ya conoce. Se ha priorizado la **claridad y la coherencia** frente a una personalización excesiva.



---

### ✅ Diseño de Menús y Navegación (RA4.c)

En **CádizAccesible**, el sistema de menús se basa en la **eficiencia cognitiva**, evitando menús globales complejos.

* **Navegación por Roles:** El "menú" principal son los Dashboards de inicio, que presentan solo las acciones relevantes para el ciudadano (Crear/Ver) o el administrador (Bandeja/Informes).
* **TopAppBar como Orientación:** En todas las pantallas se utiliza una cabecera clara que indica al usuario dónde está y cómo volver.
* **Menús Gestuales:** Se han integrado acciones rápidas mediante *swipe*, actuando como menús contextuales que no saturan el espacio visual.

---

### ✅ Distribución de Acciones y Controles (RA4.d / RA4.e)

La distribución de elementos interactivos sigue una **secuencia lógica de uso** para guiar al usuario y prevenir errores.

* **Flujo Natural (RA4.e):** Los controles se agrupan en `ElevatedCard` por bloques funcionales (Descripción > Clasificación > Multimedia). El usuario completa la tarea de arriba hacia abajo.
* **Prevención de Errores (RA4.d):** Las acciones críticas están claramente separadas de las secundarias.
    ```kotlin
    // Prevención de errores: botón deshabilitado durante la carga
    Button(
        onClick = { viewModel.publicar() },
        enabled = !state.estaPublicando // RA4.d
    ) {
        if (state.estaPublicando) CircularProgressIndicator() else Text("Publicar")
    }
    ```

---

### ✅ Elección de Controles Adecuados (RA4.f)

He seleccionado cada control basándome en el tipo de dato para que la interacción sea natural:

* **Chips vs Desplegables:** Uso de `FilterChip` para categorías, permitiendo ver todas las opciones de un vistazo.
* **Switches:** Para valores booleanos claros como "Urgente" o "Temporal".
* **OutlinedTextField:** Para entradas de texto, usando `singleLine` para títulos y `minLines = 3` para descripciones.

---

### ✅ Diseño Visual, Estética y Legibilidad (RA4.g)

El diseño visual busca que **la función prime sobre la decoración**.

* **Color Semántico:** El color comunica estados sin necesidad de leer texto (Verde para "Resuelta", Rojo para "Rechazada/Error").
* **Espaciado Uniforme:** Uso sistemático de `Arrangement.spacedBy(16.dp)` para facilitar la pulsación.
* **Modo Claro/Oscuro:** Implementación nativa mediante el tema global.
    ```kotlin
    // Uso de colores semánticos del tema (RA4.g)
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) { /* Mensaje de error */ }
    ```

---

### ✅ Claridad de Mensajes y Feedback (RA4.h)

La comunicación sistema-usuario es directa, humana y libre de tecnicismos.

* **Feedback de Proceso:** Mensajes descriptivos como *"Publicando incidencia..."* acompañados de indicadores de progreso.
* **Etiquetas Claras:** Los botones usan verbos de acción directa ("Publicar", "Rechazar", "Cerrar sesión").
* **Gestos Comunicativos:** El *swipe* muestra texto e iconos dinámicos mientras se realiza para confirmar la intención.

| Contexto | Mensaje / Control | Propósito |
| :--- | :--- | :--- |
| **Error** | Card en `errorContainer` | Explicar el problema de forma no punitiva. |
| **Carga** | `CircularProgressIndicator` | Eliminar la sensación de bloqueo. |
| **NUI** | Texto sobre el Swipe | Confirmar la acción antes de ejecutarla. |

---

### ✅ Pruebas de Usabilidad y Refinamiento (RA4.i)

La interfaz ha sido sometida a pruebas exploratorias durante el desarrollo para validar que los flujos críticos sean comprensibles.

#### 1. Validación de Flujos y Resultados

| Flujo Evaluado | Aspecto Crítico | Resultado de la Prueba |
| :--- | :--- | :--- |
| **Crear Incidencia** | ¿Es lógico el orden de los campos? | **Éxito.** El flujo vertical por bloques permite completar el formulario sin dudas. |
| **Mis Incidencias** | ¿Es intuitivo el gesto de borrar? | **Ajuste realizado.** Se añadió texto explicativo (*"Desliza para eliminar"*) para guiar al usuario. |
| **Gestión Admin** | ¿Hay riesgo de error en el cambio de estado? | **Éxito.** Los colores semánticos y el feedback visual confirman la acción antes de persistirla. |

#### 2. Problemas Detectados y Mejoras Aplicadas
* **Refuerzo de Feedback:** Se implementaron estados de carga y bloqueo de botones para evitar envíos múltiples.
* **Claridad en Clasificación:** Sustitución de listas desplegables por `FilterChips` organizados en `FlowRow` para mejorar la visibilidad.
* **Contextualización:** Incorporación de *cards* introductorias con textos breves para explicar la finalidad de cada sección.

#### 3. Reflexión Crítica y Evolución Futura
Para una evolución profesional, se proyectan las siguientes fases:
1.  **Pruebas de Guerrilla:** Testeo con ciudadanos reales en entornos urbanos y movilidad.
2.  **Auditoría de Accesibilidad:** Uso de herramientas como *TalkBack* para usuarios invidentes.
3.  **Métricas de Tarea:** Medir el tiempo de gestión para optimizar los gestos de acceso rápido.

> **Conclusión:** Las pruebas de usabilidad han permitido que la aplicación pase de ser un conjunto de funciones técnicas a una herramienta orientada al ciudadano, donde el diseño acompaña al usuario y previene el error humano.

# RA4 – Estándares, Usabilidad y Estilo

En este bloque se analiza cómo **CádizAccesible** se alinea con los estándares de diseño modernos de Android, garantizando una interfaz coherente, usable y accesible, diseñada específicamente para el ciudadano y el gestor municipal.

---

### ✅ Aplicación de Estándares e Interfaz (RA4.a / RA4.b)

El desarrollo se ha regido por el sistema **Material Design 3 (Material You)**, asegurando una experiencia predecible y profesional.

* **Consistencia y Estilo:** Se utiliza un `Scaffold` base en cada pantalla, integrando componentes oficiales como `TopAppBar`, `ElevatedCard` y `FilterChip`.
* **Jerarquía Visual:** Aplicación estricta de la escala tipográfica (`titleLarge` para encabezados, `bodyMedium` para datos).
* **Reflexión Crítica (RA4.b):** La elección de Material 3 es ideal para una app de servicio público porque reduce la curva de aprendizaje al usar patrones que el usuario ya conoce. Se ha priorizado la **claridad y la coherencia** frente a una personalización excesiva.



---

### ✅ Diseño de Menús y Navegación (RA4.c)

En **CádizAccesible**, el sistema de menús se basa en la **eficiencia cognitiva**, evitando menús globales complejos.

* **Navegación por Roles:** El "menú" principal son los Dashboards de inicio, que presentan solo las acciones relevantes para el ciudadano (Crear/Ver) o el administrador (Bandeja/Informes).
* **TopAppBar como Orientación:** En todas las pantallas se utiliza una cabecera clara que indica al usuario dónde está y cómo volver.
* **Menús Gestuales:** Se han integrado acciones rápidas mediante *swipe*, actuando como menús contextuales que no saturan el espacio visual.

---

### ✅ Distribución de Acciones y Controles (RA4.d / RA4.e)

La distribución de elementos interactivos sigue una **secuencia lógica de uso** para guiar al usuario y prevenir errores.

* **Flujo Natural (RA4.e):** Los controles se agrupan en `ElevatedCard` por bloques funcionales (Descripción > Clasificación > Multimedia). El usuario completa la tarea de arriba hacia abajo.
* **Prevención de Errores (RA4.d):** Las acciones críticas están claramente separadas de las secundarias.
    ```kotlin
    // Prevención de errores: botón deshabilitado durante la carga
    Button(
        onClick = { viewModel.publicar() },
        enabled = !state.estaPublicando // RA4.d
    ) {
        if (state.estaPublicando) CircularProgressIndicator() else Text("Publicar")
    }
    ```

---

### ✅ Elección de Controles Adecuados (RA4.f)

He seleccionado cada control basándome en el tipo de dato para que la interacción sea natural:

* **Chips vs Desplegables:** Uso de `FilterChip` para categorías, permitiendo ver todas las opciones de un vistazo.
* **Switches:** Para valores booleanos claros como "Urgente" o "Temporal".
* **OutlinedTextField:** Para entradas de texto, usando `singleLine` para títulos y `minLines = 3` para descripciones.

---

### ✅ Diseño Visual, Estética y Legibilidad (RA4.g)

El diseño visual busca que **la función prime sobre la decoración**.

* **Color Semántico:** El color comunica estados sin necesidad de leer texto (Verde para "Resuelta", Rojo para "Rechazada/Error").
* **Espaciado Uniforme:** Uso sistemático de `Arrangement.spacedBy(16.dp)` para facilitar la pulsación.
* **Modo Claro/Oscuro:** Implementación nativa mediante el tema global.
    ```kotlin
    // Uso de colores semánticos del tema (RA4.g)
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) { /* Mensaje de error */ }
    ```

---

### ✅ Claridad de Mensajes y Feedback (RA4.h)

La comunicación sistema-usuario es directa, humana y libre de tecnicismos.

* **Feedback de Proceso:** Mensajes descriptivos como *"Publicando incidencia..."* acompañados de indicadores de progreso.
* **Etiquetas Claras:** Los botones usan verbos de acción directa ("Publicar", "Rechazar", "Cerrar sesión").
* **Gestos Comunicativos:** El *swipe* muestra texto e iconos dinámicos mientras se realiza para confirmar la intención.

| Contexto | Mensaje / Control | Propósito |
| :--- | :--- | :--- |
| **Error** | Card en `errorContainer` | Explicar el problema de forma no punitiva. |
| **Carga** | `CircularProgressIndicator` | Eliminar la sensación de bloqueo. |
| **NUI** | Texto sobre el Swipe | Confirmar la acción antes de ejecutarla. |

---

### ✅ Pruebas de Usabilidad y Refinamiento (RA4.i)

La interfaz ha sido sometida a pruebas exploratorias durante el desarrollo para validar que los flujos críticos sean comprensibles.

#### 1. Validación de Flujos y Resultados

| Flujo Evaluado | Aspecto Crítico | Resultado de la Prueba |
| :--- | :--- | :--- |
| **Crear Incidencia** | ¿Es lógico el orden de los campos? | **Éxito.** El flujo vertical por bloques permite completar el formulario sin dudas. |
| **Mis Incidencias** | ¿Es intuitivo el gesto de borrar? | **Ajuste realizado.** Se añadió texto explicativo (*"Desliza para eliminar"*) para guiar al usuario. |
| **Gestión Admin** | ¿Hay riesgo de error en el cambio de estado? | **Éxito.** Los colores semánticos y el feedback visual confirman la acción antes de persistirla. |

#### 2. Problemas Detectados y Mejoras Aplicadas
* **Refuerzo de Feedback:** Se implementaron estados de carga y bloqueo de botones para evitar envíos múltiples.
* **Claridad en Clasificación:** Sustitución de listas desplegables por `FilterChips` organizados en `FlowRow` para mejorar la visibilidad.
* **Contextualización:** Incorporación de *cards* introductorias con textos breves para explicar la finalidad de cada sección.

---

### ✅ Evaluación en Distintos Dispositivos y Configuraciones (RA4.j)

La interfaz ha sido diseñada bajo principios de **diseño adaptativo (Responsive Design)** para garantizar una experiencia constante en la fragmentación de dispositivos Android.

* **Layouts Flexibles:** Uso de modificadores como `fillMaxWidth()`, `weight()` y contenedores dinámicos.
* **Gestión de Pantallas Largas:** Implementación de `verticalScroll` en formularios complejos para asegurar que ningún control quede fuera del alcance en terminales pequeños.
* **Modo Oscuro Adaptativo:** Evaluación del contraste y legibilidad en ambos temas del sistema para proteger la fatiga visual.
* **Zonas de Pulsación:** Todos los elementos interactivos respetan el área mínima de **48x48 dp** de Material Design para facilitar el uso a personas con movilidad reducida.



#### Resumen de Adaptabilidad

| Configuración | Técnica de Adaptación | Resultado |
| :--- | :--- | :--- |
| **Resolución Variable** | Unidades DP y Layouts dinámicos | Escala de textos y botones consistente. |
| **Pantallas Estrechas** | `FlowRow` en Chips | Redistribución automática sin cortes de texto. |
| **Preferencia Sistema** | `darkTheme` dinámico en `AppRoot` | Adaptación de colores sin pérdida de jerarquía. |

> **Conclusión:** El cumplimiento del RA4 asegura que **CádizAccesible** no es solo una aplicación funcional, sino una herramienta diseñada bajo estándares profesionales de usabilidad, estética y adaptabilidad, preparada para servir a la ciudadanía de forma inclusiva.


# 🧾 RA5 – Informes y Análisis de Datos

En este apartado se detalla la arquitectura de persistencia y procesamiento que permite a **CádizAccesible** transformar registros individuales en inteligencia de gestión mediante informes dinámicos y visualizaciones personalizadas.

---

# 🧾 RA5 – Informes y Análisis de Datos

En este apartado se detalla la arquitectura de persistencia y procesamiento que permite a **CádizAccesible** transformar registros individuales en inteligencia de gestión mediante informes dinámicos y visualizaciones personalizadas.

---

### ✅ RA5.a — Establece la estructura del informe
La interfaz de informes sigue un patrón de **Dashboard jerárquico**. Se ha estructurado para que la carga cognitiva sea mínima:

* **Bloque de Métricas (KPIs):** Situado en la parte superior para una respuesta inmediata sobre el estado de la ciudad.
* **Bloque de Control (Filtros):** Situado en el centro para segmentar la realidad urbana por gravedad o estado.
* **Bloque Visual (Gráficos):** Situado en la base para detectar tendencias mediante interpretación geométrica.

---

### ✅ RA5.b — Generación desde fuentes de datos (Persistencia Room)
Proceso automatizado y reactivo donde la UI es un reflejo directo de la base de datos.

* **Consultas Agregadas:** Uso del motor SQLite para cálculos eficientes en lugar de procesar en memoria.
* **Reactividad con Flow:** Las actualizaciones son automáticas ante cualquier cambio en la BD.

```kotlin
// IncidenciaDao.kt
@Query("SELECT COUNT(*) FROM incidencias WHERE urgente = 1")
fun getTotalUrgentes(): Flow<Int>

@Query("SELECT COUNT(*) FROM incidencias WHERE estado = :estado")
fun countByEstado(estado: String): Flow<Int>

@Query("SELECT COUNT(*) FROM incidencias WHERE estado = :estado") 
fun countByEstado(estado: String): Flow<Int>
 ```

---

✅ RA5.c — Establece filtros sobre los valores a presentarEl sistema de filtrado es multidimensional. El InformesViewModel combina los criterios de selección para ofrecer una vista precisa.Lógica de Filtrado: Utilizo un MutableStateFlow para capturar el filtro seleccionado.Transformación Dinámica: Mediante el operador flatMapLatest, el sistema cambia la consulta a la base de datos en tiempo real según el chip pulsado por el usuario.Kotlin// Lógica en InformesViewModel.kt 
private val _filtroEstado = MutableStateFlow<String?>(null)

val incidenciasFiltradas = _filtroEstado.flatMapLatest { estado -> 
    if (estado == null) repositorio.getAll() 
    else repositorio.getByEstado(estado) 
}

✅ RA5.c — Establece filtros sobre los valores a presentar
Sistema de filtrado multidimensional gestionado en el ViewModel.

Lógica de Filtrado: Uso de MutableStateFlow para capturar la selección del usuario.

Transformación Dinámica: Operador flatMapLatest para cambiar la consulta en tiempo real.

```kotlin
// Lógica en InformesViewModel.kt
private val _filtroEstado = MutableStateFlow<String?>(null)

val incidenciasFiltradas = _filtroEstado.flatMapLatest { estado -> 
    if (estado == null) repositorio.getAll() 
    else repositorio.getByEstado(estado) 
}
```

---

# 🆘 RA6 – Ayudas, Documentación y Manuales

En **CádizAccesible**, el sistema de ayudas y documentación se aborda desde un enfoque integrado en la interfaz, complementado con documentación técnica y manuales externos, siguiendo los estándares de profesionalidad de las aplicaciones móviles modernas.

---

### ✅ Identificación y Generación de Ayudas (RA6.a / RA6.b)

La aplicación utiliza un sistema de ayuda **multiformato** que garantiza que el usuario nunca se encuentre ante una pantalla sin orientación.

* **Sistemas Identificados (RA6.a):** Se diferencian claramente las ayudas internas (*In-App*) de la documentación técnica externa. Las ayudas internas incluyen etiquetas descriptivas, mensajes de estado y placeholders informativos.
* **Formatos Habituales (RA6.b):** Siguiendo las guías de **Material Design 3**, las ayudas se presentan de forma visualmente coherente:
    * **Tarjetas informativas:** Bloques de texto integrados en `ElevatedCard` que explican la finalidad de secciones como "Informes" o "Nueva Incidencia".
    * **Indicaciones de acción:** Verbos directos y breves que guían la interacción (ej. *"Describe el problema"*, *"Gestión rápida"*).
    * **Feedback de estado:** Mensajes de confirmación y carga que mantienen al usuario informado del proceso actual.



---

### ✅ Ayudas Sensibles al Contexto (RA6.c)

La aplicación implementa **lógica condicional** para mostrar ayudas que dependen exclusivamente del estado y el rol del usuario:

* **Estados Vacíos:** Si el ciudadano no tiene reportes, la pantalla muestra un mensaje dinámico: *"Cuando crees una incidencia, aparecerá aquí"*.
* **Instrucciones por Gesto:** Las indicaciones de *swipe* solo se muestran en las pantallas donde dicha interacción es funcional (Bandeja Admin y Mis Incidencias).
* **Diferenciación de Roles:** El administrador visualiza bloques de ayuda específicos para la gestión de estados que el ciudadano no visualiza, evitando ruido visual innecesario.

---

### ✅ Documentación de la Persistencia (RA6.d)

Para asegurar la escalabilidad del proyecto, se ha documentado la estructura de la información persistente gestionada con **Room**:

* **Entidades:** Documentación técnica de `IncidenciaEntity` y `UsuarioEntity`, detallando claves primarias y tipos de datos.
* **DAO (Data Access Object):** Definición de las consultas SQL que alimentan los informes y listados.
* **Flujo de Datos:** Explicación del patrón **Repository** como capa intermedia para garantizar la integridad de los datos.



---

### ✅ Manuales de Usuario y Técnico (RA6.e / RA6.f)

Se han confeccionado dos guías diferenciadas integradas en el repositorio:

1.  **Manual de Usuario (RA6.e):** Guía funcional escrita en lenguaje no técnico. Explica los flujos de inicio de sesión, creación de reportes mediante voz e interpretación de los gráficos de informes.
2.  **Manual Técnico (RA6.f):** Orientado a desarrolladores. Detalla la arquitectura **MVVM**, la configuración del entorno en Android Studio, la gestión de dependencias en Gradle y el esquema de la base de datos local.

---

### ✅ Tutoriales Progresivos (RA6.g)

En lugar de manuales densos, **CádizAccesible** utiliza el concepto de **onboarding implícito**:

* **Guías Paso a Paso:** La distribución de los controles en los formularios actúa como un tutorial visual, guiando al usuario desde la descripción hasta la publicación.
* **Ayudas Visuales Directas:** El uso de iconos combinados con texto y colores semánticos permite que el usuario "aprenda haciendo", reforzando la autonomía y reduciendo la tasa de abandono de la aplicación.

---

### 📊 Matriz de Documentación y Ayudas

| Criterio | Tipo de Ayuda | Ubicación / Archivo |
| :--- | :--- | :--- |
| **RA6.c** | Ayuda Contextual | `PantallaMisIncidencias.kt` |
| **RA6.d** | Persistencia | `IncidenciaEntity.kt` / `AppDatabase.kt` |
| **RA6.e** | Manual Usuario | `README.md` (Sección Usuario) |
| **RA6.f** | Manual Técnico | `README.md` (Sección Técnica) |
| **RA6.g** | Tutorial | Flujo de `PantallaCrearIncidencia.kt` |

> **Conclusión:** El sistema de documentación de **CádizAccesible** (RA6) garantiza que el producto sea **mantenible para el equipo técnico** y **fácil de adoptar para el ciudadano**, cumpliendo con los estándares de rigor y claridad exigidos en un entorno profesional.

# 🧪 RA8 – Pruebas y Control de Calidad

En el proyecto **CádizAccesible**, la estrategia de pruebas se ha planteado de forma realista y coherente con el alcance del proyecto, combinando pruebas manuales, pruebas de integración funcional y documentación de resultados, siguiendo un enfoque habitual en proyectos profesionales de aplicaciones móviles.

---

### ✅ Estrategia de Pruebas (RA8.a)

El proyecto cuenta con una estrategia de pruebas claramente definida, orientada a validar el correcto funcionamiento de la aplicación desde el punto de vista del usuario ciudadano y del administrador.

* **Enfoque de la estrategia:**
    * **Pruebas por rol:** Validación de flujos específicos para Ciudadanos (reporte) y Administradores (gestión).
    * **Pruebas por flujo:** Recorrido completo desde la creación, consulta, hasta la gestión y generación de informes.
    * **Pruebas de estados:** Verificación de estados vacíos, indicadores de carga y gestión de errores.
* **Justificación técnica:** Se ha optado por un enfoque funcional manual para asegurar que la **experiencia de usuario (UX)** y la **interfaz (UI)** sean fluidas, algo crítico en una app de servicio público.



---

### ✅ Pruebas de Integración Funcional (RA8.b)

Se han realizado pruebas de integración para verificar que los distintos módulos de la arquitectura trabajan correctamente de forma conjunta, validando el flujo completo de datos: **Interfaz → ViewModel → Repositorio → Room → UI**.

* **Integraciones verificadas:**
    * **UI + ViewModel:** Comprobación de que los filtros, cambios de estado y clics disparan la lógica correcta.
    * **ViewModel + Room:** Validación de que la persistencia es efectiva y los recuentos de informes son exactos.
    * **Navegación + Sesión:** Control de rutas protegidas y acceso según el rol de usuario.
    * **Componentes Reutilizables:** Verificación de que tarjetas, chips y gráficos se renderizan correctamente con datos reales.



---

### ✅ Documentación de Resultados (RA8.g)

Las pruebas realizadas están documentadas de forma clara, permitiendo comprobar el proceso seguido y la fiabilidad del sistema.

#### 📋 Tabla de Casos de Prueba Funcionales

| Caso de Prueba | Acción Realizada | Resultado Esperado | Resultado Obtenido |
| :--- | :--- | :--- | :--- |
| **Alta de Incidencia** | Formulario completo + Foto | Registro en Room y aviso de éxito | **CORRECTO** |
| **Gesto de Borrado** | Swipe en "Mis Incidencias" | Eliminación del registro en BD | **CORRECTO** |
| **Gestión Admin** | Cambiar estado vía Swipe | Actualización inmediata en lista | **CORRECTO** |
| **Filtros Informes** | Cambiar Gravedad/Estado | Gráfico de Canvas se redibuja | **CORRECTO** |

---

### 🔮 Reflexión y Evolución Futura

El enfoque manual actual es defendible y coherente con un proyecto centrado en el **Diseño de Interfaces**. No obstante, la arquitectura robusta de la app permite una evolución hacia:
1.  **Tests Instrumentados:** Implementación de JUnit y Compose Test para automatizar flujos críticos.
2.  **Pruebas de ViewModel:** Validar la lógica de negocio de forma aislada.
3.  **Informes Automáticos:** Generación de reportes de test tras cada despliegue.

> **Conclusión:** El RA8 cumple con los requisitos de la rúbrica mediante una metodología que garantiza que **CádizAccesible** es una herramienta robusta, predecible y preparada para su uso en un entorno profesional.


# 📊 RA5 – Informes e Inteligencia de Datos (Incluye FFOE)

Este apartado detalla la implementación del sistema de análisis de datos de **CádizAccesible**, diseñado para que el administrador pueda monitorizar el estado de la ciudad mediante métricas reactivas y visualizaciones personalizadas integradas nativamente.

---

### ✅ Estructura y Generación de Informes (RA5.a / RA5.b)

El informe en **CádizAccesible** no es un documento estático, sino una herramienta de análisis integrada y alimentada en tiempo real por la base de datos **Room**.

* **Estructura Profesional (RA5.a):** La información se organiza de forma jerárquica:
    * **KPIs (Indicadores Clave):** Resumen numérico de incidencias totales y urgentes en la cabecera.
    * **Filtros Interactivos:** Segmentación por estado y gravedad mediante `FilterChips`.
    * **Visualización:** Gráficos de barras que representan la distribución de datos de forma geométrica.
* **Fuentes de Datos Reales (RA5.b):** Los informes se generan dinámicamente siguiendo el patrón **SSOT (Single Source of Truth)** desde Room hacia la UI mediante `Flow`.



---

### ✅ Filtros, Cálculos y Totales (RA5.c / RA5.d)

Para que el informe sea útil en la toma de decisiones, se han implementado mecanismos de filtrado y lógica de cálculo avanzada.

* **Interactividad con Filtros (RA5.c):** El uso de `flatMapLatest` en el ViewModel permite que el sistema cambie la consulta a la base de datos en tiempo real según la selección del usuario, sin recargar la pantalla.
* **Valores Calculados y Derivados (RA5.d):** Se realizan recuentos automáticos y cálculos de impacto.
    ```kotlin
    // Cálculo de KPI reactivo en InformesViewModel.kt
    val porcentajeUrgentes = combine(totalUrgentes, totalIncidencias) { urg, total ->
        if (total == 0) 0 else (urg * 100) / total
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    ```

---

### ✅ Gráficos Generados a Medida con Canvas (RA5.e)

La visualización se realiza mediante un componente propio desarrollado con la **API de Canvas**, demostrando dominio sobre el dibujo técnico en Android.

* **Escalado Dinámico:** Las barras ajustan su altura automáticamente basándose en el valor máximo del set de datos.
* **Estética Material 3:** Integración total con los colores del tema y soporte para etiquetas dinámicas.



---

### 🏢 Evaluación Complementaria FFOE (RA5.f / RA5.g / RA5.h)

Estos criterios confirman la madurez profesional del sistema de informes integrado.

* **Uso de Herramientas Profesionales (RA5.f):** Se justifica el uso de **Room**, **StateFlow** y **Jetpack Compose** frente a herramientas externas para garantizar actualización en tiempo real y un control total sobre la accesibilidad visual.
* **Modificación y Evolución del Código (RA5.g):** El código se ha evolucionado conscientemente, separando la lógica de cálculo en un ViewModel específico y optimizando las consultas SQL para agrupaciones de datos (`GROUP BY`).
* **Integración Total y Coherente (RA5.h):** Los informes no son una sección aislada; están protegidos por el sistema de roles en `HostNavegacion.kt` y respetan la jerarquía visual de la aplicación, siendo accesibles exclusivamente para el perfil administrador.

---

### 📁 Matriz de Evidencias Técnicas

| Criterio | Implementación Clave | Ubicación |
| :--- | :--- | :--- |
| **Generación** | Consultas SQL agregadas | `IncidenciaDao.kt` |
| **Filtros** | `StateFlow` + `flatMapLatest` | `InformesViewModel.kt` |
| **Gráficos** | API `Canvas` personalizada | `GraficoBarras.kt` |
| **Integración** | Navegación por roles | `HostNavegacion.kt` |

> **Conclusión del RA5:** El sistema de informes de **CádizAccesible** transforma la aplicación en una herramienta de gestión urbana profesional, ofreciendo una experiencia reactiva, visualmente clara y técnicamente robusta.


# 📦 RA7 – Distribución de Aplicaciones (Estrategia de Despliegue)

Este bloque detalla el plan técnico para la distribución profesional de **CádizAccesible**. Aunque el proyecto se encuentra actualmente en fase de evaluación técnica, se ha diseñado siguiendo los estándares necesarios para un despliegue real en el ecosistema Android, asegurando la integridad, seguridad y accesibilidad del software.

---

### ✅ Empaquetado y Firma Digital (RA7.a, RA7.c, RA7.e)

Para que la aplicación pueda distribuirse en dispositivos finales, es imperativo realizar un empaquetado profesional que garantice la identidad del autor y la integridad del código.

**Plan de implementación profesional:**
1.  **Generación del Almacén de Claves (KeyStore):** El primer paso consiste en crear un archivo `.jks` (Java KeyStore) protegido por contraseña. Este archivo contiene la clave privada con la que se firma la aplicación.
2.  **Firma del Paquete:** Mediante el asistente de Android Studio o tareas de Gradle, se firma el binario. Sin esta firma digital, Android bloquea la instalación por motivos de seguridad.
3.  **Formato App Bundle (.aab):** Se optaría por generar un **Android App Bundle** en lugar de un APK simple. Este formato permite que Google Play optimice el tamaño del archivo según la arquitectura del dispositivo que lo descarga.



---

### ✅ Personalización e Instalación (RA7.b, RA7.f, RA7.g)

La experiencia del usuario comienza con un instalador personalizado y una gestión de recursos eficiente en el dispositivo.

* **Personalización (RA7.b):** Se han definido los iconos adaptativos (*Adaptive Icons*) y el nombre del paquete único para que la identidad visual sea coherente desde el momento de la descarga.
* **Instalación Desatendida (RA7.f):** En un entorno corporativo municipal, se propone el despliegue mediante sistemas **MDM (Mobile Device Management)**. Esto permitiría instalar la app de forma masiva en terminales de operarios o tótems informativos sin intervención manual.
* **Desinstalación Limpia (RA7.g):** El manifiesto de la app está configurado para que, al desinstalarse, el sistema elimine automáticamente los archivos de caché y datos temporales, liberando espacio en el dispositivo del ciudadano.

---

### ✅ Canales de Distribución y Herramientas (RA7.d, RA7.h)

Se ha proyectado una estrategia de lanzamiento segmentada para minimizar riesgos y maximizar el alcance:

1.  **Fase de Betas (Firebase App Distribution):** Uso de herramientas externas para enviar versiones de prueba a los técnicos municipales y recoger métricas de fallos antes del lanzamiento público.
2.  **Canal Oficial (Google Play Console):** Publicación en la tienda oficial para garantizar actualizaciones automáticas y confianza del usuario.
3.  **Sede Electrónica (APK Directo):** Publicación del instalador firmado en la web del Ayuntamiento de Cádiz como alternativa de descarga directa.



---

### 📊 Hoja de Ruta para el Despliegue Paso a Paso

| Fase | Acción Técnica | Herramienta |
| :--- | :--- | :--- |
| **1. Ofuscación** | Aplicar R8/ProGuard para proteger el código. | Gradle |
| **2. Generación** | Crear el paquete firmado de producción (.aab). | Android Studio KeyStore |
| **3. Validación** | Desplegar en canal de pruebas internas. | Firebase / Play Store Console |
| **4. Lanzamiento** | Publicación y monitorización de ANRs/Errores. | Google Play Console |

> **Conclusión:** Aunque la distribución actual se realiza mediante depuración directa por cable (ADB), **CádizAccesible** cuenta con un plan de despliegue profesional documentado. Se han identificado todas las herramientas y procedimientos necesarios para transformar el código fuente en un producto comercializable, seguro y fácil de instalar para la ciudadanía.


# 🧪 RA8 – Pruebas Avanzadas (Criterios FFOE)

En el proyecto **CádizAccesible**, se han planteado y documentado pruebas de nivel avanzado de forma realista, alineadas con los estándares de un entorno profesional de desarrollo móvil. Estas validaciones aseguran que la aplicación no solo funciona, sino que es estable, segura y eficiente en el uso de recursos.

---

### ✅ Pruebas de Regresión (RA8.c)

El objetivo de estas pruebas es garantizar que la introducción de nuevas funcionalidades o la corrección de errores no alteren negativamente los comportamientos ya existentes.

* **Casos de Regresión Planificados:**
    * **Persistencia:** Verificar que las incidencias antiguas siguen siendo legibles tras modificar el esquema de **Room** para añadir campos como "urgente" o "temporal".
    * **Lógica Administrativa:** Validar que un cambio de estado realizado por el administrador no rompe la vista de "Mis Incidencias" del ciudadano.
    * **Navegación:** Asegurar que la implementación del módulo de Informes no altera los flujos de navegación previos definidos en `HostNavegacion.kt`.
* **Metodología:** Se han realizado ciclos de pruebas manuales tras cada hito de desarrollo, documentando que las funciones core (crear, listar y ver detalle) permanecen intactas.



---

### ✅ Pruebas de Volumen y Estrés (RA8.d)

Se ha evaluado el comportamiento de la aplicación ante el incremento masivo de datos para prever degradaciones en el rendimiento.

* **Escenarios Probados:**
    * **Listados Extensos:** Inserción masiva de registros para verificar que `LazyColumn` gestiona el reciclaje de vistas de forma fluida sin tirones (*jank*).
    * **Gráficos Dinámicos:** Comprobación de que el componente `GraficoBarras.kt` escala correctamente la altura de las barras y las etiquetas cuando los valores numéricos son muy elevados.
* **Resultado:** La arquitectura reactiva basada en **Flow** y las consultas agregadas de **Room** mantienen tiempos de respuesta óptimos incluso con conjuntos de datos significativos.

---

### ✅ Pruebas de Seguridad y Uso de Recursos (RA8.e / RA8.f)

A pesar de ser una aplicación local, se han aplicado principios de seguridad funcional y optimización de hardware.

* **Seguridad Funcional (RA8.e):**
    * **Control de Acceso:** Validación de que la pantalla de Informes y la Bandeja de Administración son inaccesibles para el rol de ciudadano mediante lógica de protección en el `NavHost`.
    * **Permisos Críticos:** Gestión responsable de los permisos de Cámara y Ubicación, solicitándolos únicamente cuando la acción es requerida por el usuario.
* **Análisis de Recursos (RA8.f):**
    * **Gestión de Memoria:** Uso de `AsyncImage` (Coil) para la carga diferida de imágenes, evitando desbordamientos de memoria (*Out Of Memory errors*).
    * **Eficiencia de CPU:** Las consultas a la base de datos se realizan en hilos secundarios mediante `Dispatchers.IO`, manteniendo el hilo principal libre para una interfaz fluida a 60fps.



---

### 📊 Matriz de Validación Avanzada

| Criterio | Tipo de Prueba | Evidencia Técnica | Resultado |
| :--- | :--- | :--- | :--- |
| **RA8.c** | Regresión | Pruebas de integridad tras cambios en BD | **Estable** |
| **RA8.d** | Volumen | Listados largos con `LazyColumn` | **Fluido** |
| **RA8.e** | Seguridad | Lógica de roles en `HostNavegacion.kt` | **Seguro** |
| **RA8.f** | Recursos | Carga de imágenes con Coil | **Optimizado** |

> **Conclusión FFOE:** El cumplimiento de estos criterios avanzados demuestra que **CádizAccesible** ha sido desarrollada con una mentalidad de ingeniería de software, priorizando la estabilidad a largo plazo y la eficiencia operativa en el dispositivo del usuario.
