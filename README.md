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
