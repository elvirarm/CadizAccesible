## 📱 CádizAccesible – Aplicación móvil de incidencias de accesibilidad

**CádizAccesible** es una aplicación móvil Android orientada a la detección, comunicación y gestión de incidencias relacionadas con la accesibilidad urbana en la ciudad de Cádiz. El objetivo principal del proyecto es facilitar que cualquier ciudadano pueda reportar de forma sencilla problemas de accesibilidad (rampas en mal estado, aceras intransitables, barreras arquitectónicas, etc.) y que estos avisos puedan ser gestionados de forma eficiente desde un perfil administrativo.

La aplicación está pensada desde un enfoque práctico y realista, priorizando la facilidad de uso, la accesibilidad y la claridad de la información. Para ello, se han implementado distintos mecanismos que reducen barreras de uso, como la entrada por voz, el uso de gestos y una interfaz visual clara basada en estándares modernos de Android.

CádizAccesible distingue entre dos tipos de usuarios:

- **Ciudadano**, que puede crear incidencias, adjuntar información contextual (texto, imágenes y ubicación) y consultar el estado de sus reportes.
- **Administrador**, que puede gestionar las incidencias recibidas, cambiar su estado, añadir comentarios y consultar informes con métricas agregadas sobre la situación de la ciudad.

Desde el punto de vista técnico, el proyecto se ha desarrollado íntegramente con **Jetpack Compose**, siguiendo una arquitectura **MVVM**, utilizando **Room** como sistema de persistencia local y **Kotlin Flow / StateFlow** para garantizar una experiencia reactiva y coherente. La aplicación no se limita a mostrar datos, sino que los transforma en información útil mediante informes y visualizaciones personalizadas.

Este proyecto no solo aborda aspectos funcionales, sino que también pone especial atención en la usabilidad, la organización del código, la documentación y la calidad del desarrollo, alineándose con un contexto real de aplicación municipal.

---

### ✅ RA1.a – Análisis de herramientas y librerías

Desde el inicio del proyecto tuve claro que CádizAccesible debía cumplir dos requisitos clave:  
1) una interfaz dinámica, con muchos cambios de estado según el rol del usuario, y  
2) persistencia local fiable para poder generar informes reales sin depender de un backend.

Por este motivo, la elección de herramientas no ha sido arbitraria, sino directamente condicionada por las necesidades funcionales de la aplicación.

**Jetpack Compose y Material 3** son la base de toda la interfaz. En una app donde las incidencias cambian de estado (creada, en revisión, rechazada, resuelta) y los informes se recalculan constantemente, trabajar por estados es fundamental. Gracias a Compose, cuando cambia el estado expuesto por el ViewModel, la pantalla se recompone automáticamente sin tener que refrescar manualmente la UI. Esto se aprecia especialmente en la pantalla de Informes y en las listas de incidencias.

La navegación se gestiona con **Navigation Compose** desde un único punto (`HostNavegacion.kt`). Esta decisión es clave porque la app tiene dos roles claramente diferenciados (CIUDADANO y ADMIN). Centralizando el `NavHost` puedo controlar desde el inicio qué pantallas son accesibles según el rol y evitar que un usuario sin sesión acceda a pantallas protegidas.

Para la persistencia local he utilizado **Room**, ya que la aplicación necesita mantener las incidencias incluso al cerrar la app y generar estadísticas reales. En `IncidenciaDao.kt` no solo se realizan operaciones CRUD, sino consultas de agregación (`COUNT` y `GROUP BY`) que permiten calcular totales y distribuciones directamente desde la base de datos, lo que resulta más eficiente que procesar los datos en memoria.

La reactividad se gestiona mediante **Kotlin Flow y StateFlow**. Room emite los datos como `Flow` y el ViewModel los combina con los filtros seleccionados por el usuario. Por ejemplo, en `InformesViewModel.kt` uso `combine` y `flatMapLatest` para que, al cambiar un chip de estado o gravedad, los listados y gráficos se actualicen automáticamente sin botones de búsqueda adicionales.

Otras librerías complementan funcionalidades clave del proyecto:
- **Coil** para la carga eficiente de imágenes de incidencias.
- **Google Play Services (ubicación)** para obtener la dirección automática mediante `Geocoder`.
- **RecognizerIntent** para entrada por voz, integrada en un componente reutilizable (`CampoTextoConVoz.kt`) que mejora la accesibilidad del formulario.

En conjunto, estas herramientas forman un flujo coherente: Room emite datos, el ViewModel los procesa de forma reactiva y Compose representa el estado actualizado en pantalla.


---

### ✅ RA1.b – Creación de la interfaz gráfica

La interfaz de CádizAccesible no se ha diseñado como un conjunto de pantallas aisladas, sino como un flujo completo que se adapta al tipo de usuario que inicia sesión: **Ciudadano** o **Administrador**.

La navegación está centralizada y mantiene siempre una estructura coherente mediante Material 3, lo que garantiza que botones, tarjetas y barras de navegación sigan el mismo criterio visual en toda la aplicación. El usuario siempre sabe en qué pantalla se encuentra y cómo volver atrás.

El formulario de creación de incidencias (`PantallaCrearIncidencia.kt`) es la pantalla más compleja a nivel de interfaz. Para evitar una experiencia caótica, la información se organiza en bloques claros mediante `ElevatedCard`. Las categorías y niveles de gravedad se seleccionan mediante `FilterChip` dentro de un `FlowRow`, permitiendo que la interfaz se adapte automáticamente al ancho del dispositivo.

Las pantallas de listado (`PantallaMisIncidencias.kt` y `PantallaBandejaAdmin.kt`) están pensadas para la gestión rápida. El ciudadano puede eliminar incidencias mediante gestos, mientras que el administrador puede cambiar estados directamente con un swipe, evitando menús adicionales.

La pantalla de detalle (`PantallaDetalleIncidencia.kt`) se adapta dinámicamente al rol: el administrador ve opciones de gestión y respuesta, mientras que el ciudadano solo visualiza la información relevante, evitando confusión.

Por último, la pantalla de Informes actúa como un panel de control, mostrando métricas y gráficos de forma clara y ordenada, reforzando el carácter profesional de la interfaz.

---

### ✅ RA1.c – Uso de layouts y posicionamiento

La organización visual de la aplicación se basa en una jerarquía clara que facilita la lectura y el uso en pantallas de distinto tamaño.

Todas las pantallas principales utilizan `Scaffold`, lo que permite separar correctamente la barra superior del contenido y respetar las zonas seguras del sistema. En formularios largos, como la creación de incidencias, se utiliza una `Column` con `verticalScroll` y separación constante entre bloques para evitar saturar visualmente al usuario.

En los listados se emplea `LazyColumn`, asegurando un rendimiento fluido incluso con un número elevado de incidencias. Para los formularios con múltiples opciones, `FlowRow` permite que los chips se redistribuyan automáticamente sin cortes ni solapamientos.

El uso de `Row` con `Modifier.weight` garantiza una distribución equilibrada de elementos como KPIs o botones de acción, mientras que las tarjetas (`ElevatedCard`) agrupan información relacionada y refuerzan la jerarquía visual de la interfaz.


---

### ✅ RA1.e – Análisis del código y arquitectura

La arquitectura del proyecto sigue el patrón **MVVM**, separando claramente la lógica de datos, la lógica de presentación y la interfaz.

Room actúa como única fuente de verdad, emitiendo datos reactivos mediante `Flow`. El repositorio transforma las entidades de base de datos en modelos de dominio, evitando que la UI dependa directamente del esquema de persistencia.

El ViewModel centraliza toda la lógica de negocio. En el caso de Informes, se combinan múltiples flujos para calcular KPIs y aplicar filtros, entregando a la interfaz datos ya procesados. De esta forma, las pantallas se limitan a representar el estado sin contener lógica compleja.

La navegación y el control de acceso por roles están centralizados en `HostNavegacion.kt`, lo que mejora la mantenibilidad y evita errores de acceso.


---

### ✅ RA1.f – Adaptación y modificación del código

Durante el desarrollo de CádizAccesible no me he limitado a reutilizar componentes o ejemplos estándar, sino que he modificado y extendido el código para adaptarlo a las necesidades reales de la aplicación y mejorar la experiencia de usuario.

Uno de los casos más claros es la evolución del gesto `SwipeToDismiss`. Inicialmente el gesto solo eliminaba elementos visualmente, pero se adaptó para ejecutar acciones reales sobre la base de datos. En `PantallaMisIncidencias.kt`, el gesto de swipe lanza una corrutina que elimina la incidencia en Room únicamente cuando el gesto se completa correctamente, evitando eliminaciones accidentales.

En la vista del administrador (`PantallaBandejaAdmin.kt`), el gesto se amplía a una gestión multiestado. Dependiendo de la dirección del deslizamiento, la incidencia pasa a estado **En revisión** o **Rechazada**. Esta lógica se implementa mediante un `LaunchedEffect` que detecta el sentido del gesto y ejecuta la actualización correspondiente en la base de datos.

Otro ejemplo de modificación significativa es la creación del componente `CampoTextoConVoz.kt`, que extiende el comportamiento estándar de `OutlinedTextField`. Este componente combina escritura manual y dictado por voz, permitiendo decidir si el texto reconocido sustituye o se añade al contenido existente.

También se ha modificado la lógica de filtrado en `InformesViewModel.kt` para evitar estados incoherentes. Los filtros de estado y gravedad son excluyentes: al activar uno, el otro se limpia automáticamente, evitando combinaciones incompatibles.

Por último, el componente `GraficoBarras.kt` se ha creado desde cero usando `Canvas`, adaptando el escalado de las barras en función del valor máximo del conjunto de datos.

Estas modificaciones no son estéticas, sino funcionales, y responden a problemas reales detectados durante el uso de la aplicación.


---

### ✅ RA1.g – Asociación de eventos e interacción

La aplicación presenta una interacción fluida y natural gracias a la correcta asociación de eventos entre la interfaz, el ViewModel y los servicios del sistema.

En los formularios, especialmente en `PantallaCrearIncidencia.kt`, todos los eventos de entrada (`onValueChange`, selección de chips, switches y botones) están vinculados directamente al estado gestionado por el ViewModel.

La entrada por voz se integra como un evento más dentro del flujo normal de la interfaz. El clic sobre el icono de micrófono lanza un `ActivityResultLauncher` que recoge el texto reconocido y lo incorpora automáticamente al campo correspondiente.

La interacción mediante gestos es uno de los elementos clave de la aplicación. En las listas del ciudadano, el swipe ejecuta la eliminación de una incidencia, mientras que en la bandeja del administrador el mismo gesto se reutiliza para la gestión de estados, reforzado por efectos visuales previos a la acción.

Además, la aplicación se integra con servicios del sistema como la ubicación, la cámara y los intents externos, abriendo directamente Google Maps mediante `Intent.ACTION_VIEW` cuando el usuario interactúa con una dirección.


---

### ✅ RA1.h – Integración global de la aplicación

CádizAccesible funciona como una aplicación integrada y estable, donde todas las pantallas y componentes forman parte de un único flujo coherente.

El punto de entrada se encuentra en `AppRoot.kt`, donde se integran el sistema de tematización y la navegación. Desde ahí, `HostNavegacion.kt` construye el grafo de navegación en función del rol del usuario (ADMIN o CIUDADANO).

El ciclo de vida del dato está completamente integrado de extremo a extremo: una incidencia se crea utilizando sensores del dispositivo, se persiste en Room y se propaga automáticamente a todas las pantallas mediante Flow.

La aplicación integra permisos, sensores, intents externos y reconocimiento de voz dentro de un flujo natural y accesible. Además, se contemplan estados de carga, estados vacíos y mensajes de error, garantizando la estabilidad y coherencia visual del proyecto.


## 🧠 RA2 – Utilización de librerías y herramientas NUI

En este apartado se analiza cómo CádizAccesible va más allá de una interfaz gráfica tradicional (GUI) para incorporar principios de **Interfaz Natural de Usuario (NUI)**. El objetivo no es que el usuario se adapte a la tecnología, sino que la aplicación aproveche los sensores y capacidades del dispositivo para reducir esfuerzo físico, cognitivo y barreras de accesibilidad, especialmente en un contexto urbano real.

---

### ✅ RA2.a – Herramientas NUI utilizadas

La aplicación integra varias herramientas NUI que permiten una interacción más natural y contextual, aprovechando voz, gestos, ubicación e imagen como canales de entrada de información.

En primer lugar, se ha integrado **reconocimiento de voz** mediante la API `RecognizerIntent.ACTION_RECOGNIZE_SPEECH`. Esta herramienta permite introducir texto sin necesidad de teclado, algo fundamental en una app pensada para usarse en la calle o por personas con dificultades de movilidad en las manos.

La **interacción por gestos** se implementa mediante deslizamientos (*swipe*) usando `SwipeToDismiss`. En lugar de depender únicamente de botones pequeños, el usuario puede ejecutar acciones directas mediante movimientos naturales sobre la pantalla.

La **ubicación** se utiliza como entrada contextual mediante `FusedLocationProviderClient`, convirtiendo el GPS en parte activa de la interfaz. La aplicación obtiene la posición del usuario y la traduce automáticamente a una dirección legible usando `Geocoder`.

Por último, la **entrada visual** se realiza a través de la cámara y la galería del dispositivo, integradas mediante `ActivityResultContracts`. En este caso, la imagen actúa como un input directo que comunica la incidencia sin necesidad de descripciones textuales complejas.

Estas herramientas no se usan de forma aislada, sino integradas en los flujos principales de la aplicación, justificando plenamente su uso como NUI.

---

### ✅ RA2.b – Diseño conceptual de la interfaz NUI

El diseño conceptual de CádizAccesible parte de una idea clara: el ciudadano no debería tener que *rellenar formularios complejos* para informar de un problema urbano.

Por ello, la interfaz se concibe como un proceso guiado donde gran parte de la información se obtiene de forma automática o natural:

- La ubicación se propone automáticamente.  
- La descripción puede dictarse por voz.  
- El estado de las incidencias se gestiona mediante gestos.  
- La imagen sustituye a explicaciones técnicas largas.

Este enfoque reduce la carga cognitiva y permite que la aplicación sea usable en situaciones reales: caminando, con una sola mano o en contextos de urgencia. El diseño NUI no sustituye completamente a la interfaz tradicional, sino que la complementa, permitiendo que cada usuario elija la forma de interacción que le resulte más cómoda.

---

### ✅ RA2.c – Interacción por voz

La interacción por voz está integrada de forma clara y realista en la aplicación, no como una funcionalidad experimental.

Se utiliza la API estándar de Android `RecognizerIntent`, encapsulada dentro de un componente reutilizable llamado `CampoTextoConVoz.kt`. Este componente permite dictar texto en campos clave como:

- Título de la incidencia  
- Descripción del problema  
- Ubicación  
- Respuesta del administrador  

La voz se integra como una alternativa natural al teclado, manteniendo coherencia con la entrada manual. El texto reconocido se procesa antes de enviarse al ViewModel, permitiendo decidir si sustituye o se añade al contenido existente.

Esta funcionalidad resulta especialmente útil para personas con movilidad reducida, usuarios que se desplazan por la vía pública o situaciones donde escribir resulta incómodo. La integración es realista porque se apoya en APIs oficiales, no requiere hardware adicional y funciona dentro del flujo normal de la aplicación.

---

### ✅ RA2.d – Interacción por gesto

La interacción por gestos se implementa como un mecanismo principal de acción, no como un añadido superficial.

En los listados de incidencias, el gesto de deslizamiento lateral (*swipe*) permite ejecutar acciones directas:

- En la vista del ciudadano, el swipe elimina una incidencia.  
- En la vista del administrador, el mismo gesto se reutiliza para cambiar el estado de la incidencia (**En revisión / Rechazada**).

Esta interacción se apoya en señales visuales claras (colores e iconos) que indican al usuario la acción que se va a ejecutar antes de completarla, evitando errores. El gesto resulta natural porque imita comportamientos ya asumidos en aplicaciones móviles modernas y reduce el número de pasos necesarios para gestionar incidencias.

La elección del gesto está justificada por el contexto de uso: gestión rápida, listas extensas y necesidad de minimizar clics y menús intermedios.

---

### ✅ RA2.e – Detección facial o corporal (visión artificial)

Aunque la aplicación no incorpora actualmente detección facial o corporal, se ha realizado una reflexión razonada sobre su posible integración futura.

Se plantea el uso de **ML Kit (Pose Detection)** para analizar, de forma local y sin enviar datos a servidores externos, la postura del usuario o la estabilidad del dispositivo. Esta información podría utilizarse para adaptar dinámicamente la interfaz.

Por ejemplo, si el sistema detecta movimientos imprecisos o vibración excesiva, la aplicación podría:

- Aumentar el tamaño de botones y áreas táctiles.  
- Activar automáticamente el dictado por voz.  
- Simplificar la interfaz visible.

Esta propuesta está pensada desde un enfoque de accesibilidad y privacidad, ya que el procesamiento se realizaría en el propio dispositivo. Aunque no se implementa en el MVP por razones de complejidad y alcance, la reflexión es coherente con los principios NUI y con la evolución natural del proyecto.

---

### ✅ RA2.f – Realidad aumentada

La aplicación plantea una propuesta clara y útil de **Realidad Aumentada** como evolución futura del sistema.

Actualmente, las incidencias se almacenan con coordenadas geográficas en la base de datos local. Esta estructura permite, a nivel conceptual, integrar **ARCore** para superponer información digital sobre el entorno real.

En un escenario de uso, un usuario podría enfocar una calle con la cámara y visualizar:

- Indicadores sobre rampas accesibles.  
- Alertas de obstáculos reportados.  
- Señalización virtual de incidencias cercanas.

Esta propuesta es coherente y realista porque reutiliza datos ya existentes y responde a un problema concreto: la dificultad de interpretar mapas 2D para personas con discapacidad. La Realidad Aumentada permitiría una interacción más directa con el entorno urbano, alineándose plenamente con el concepto de Interfaz Natural de Usuario.


## 🧩 RA3 – Uso de librerías y componentes avanzados

En este apartado se analiza cómo se ha diseñado y construido el sistema de componentes de CádizAccesible, justificando las herramientas empleadas, la reutilización de componentes, la definición de parámetros, la gestión de eventos y su integración total dentro del flujo real de la aplicación.

El objetivo principal ha sido evitar código duplicado, mejorar la mantenibilidad y asegurar que los componentes sean reutilizables, coherentes y desacoplados de la lógica de negocio.

---

### ✅ RA3.a – Herramientas para la creación de componentes

Para el desarrollo de componentes reutilizables se han utilizado herramientas modernas de Android que facilitan un diseño modular y consistente.

La base del sistema es **Jetpack Compose**, que permite construir la interfaz a partir de funciones `@Composable` independientes. Gracias a este enfoque declarativo, cada componente puede diseñarse como una unidad aislada, sin depender del contexto de la pantalla en la que se utiliza.

Como librería visual se ha utilizado **Material 3**, aprovechando componentes oficiales como `ElevatedCard`, `FilterChip` y `OutlinedTextField`. Estos componentes se han extendido y adaptado cuando ha sido necesario, manteniendo siempre la coherencia visual del sistema.

Para contenedores flexibles se han utilizado **Slot APIs**, como en el componente `AppCard.kt`, donde el contenedor gestiona el estilo y la estructura mientras que el contenido interno se define desde la pantalla que lo consume. Esta técnica permite reutilizar el mismo componente en contextos muy distintos sin duplicar código.

En el caso de los informes, se ha utilizado la API `Canvas` de Compose para crear gráficos personalizados (`GraficoBarras.kt`), evitando dependencias externas y permitiendo un control total sobre el dibujo, los colores y el escalado.

Además, se ha integrado **Coil** para la carga asíncrona de imágenes y `FlowRow` para el diseño adaptativo de chips, facilitando interfaces que se ajustan automáticamente al tamaño de pantalla.

Estas herramientas están correctamente identificadas y justificadas en función de las necesidades reales del proyecto.

---

### ✅ RA3.b – Componentes reutilizables

La aplicación se apoya en un conjunto de componentes reutilizables diseñados bajo el principio de desacoplamiento. Los componentes no conocen la base de datos ni la navegación; únicamente reciben datos y emiten eventos.

Un ejemplo central es `TarjetaIncidencia`, utilizada tanto en la vista del ciudadano como en la del administrador. Este componente recibe un objeto `Incidencia` y un callback `onClick`, lo que permite reutilizarlo en distintas pantallas sin modificar su lógica interna.

Otros componentes reutilizables relevantes son:

- `AppCard`, como contenedor base para secciones.
- `StatusChip` / `AppChips`, que encapsulan la representación visual de estados y gravedades.
- `CampoTextoConVoz`, que unifica la entrada de texto manual y por voz.
- `GraficoBarras`, utilizado para distintos conjuntos de datos dentro de la pantalla de informes.

Esta reutilización evita duplicaciones, mejora la coherencia visual y facilita la evolución del proyecto.

---

### ✅ RA3.c – Uso de parámetros y valores por defecto

Los componentes han sido diseñados con parámetros bien definidos y valores por defecto coherentes, lo que permite adaptar su comportamiento sin necesidad de crear versiones duplicadas.

En `TarjetaIncidencia`, el parámetro `mostrarMiniatura: Boolean = true` permite reutilizar el componente tanto en listados visuales como en modos más compactos simplemente cambiando un valor.

En `CampoTextoConVoz`, parámetros como `singleLine`, `minLines` o `anexarDictado` permiten usar el mismo componente para títulos cortos, descripciones largas o campos de respuesta del administrador.

El uso consistente de valores por defecto facilita la lectura del código, reduce errores y permite que los componentes sean flexibles sin perder claridad.

---

### ✅ RA3.d – Gestión de eventos en componentes

Los componentes no gestionan directamente la lógica de negocio, sino que notifican eventos hacia capas superiores mediante callbacks, siguiendo el patrón de **State Hoisting**.

Por ejemplo, `TarjetaIncidencia` emite un evento `onClick(id)` cuando el usuario pulsa sobre ella, pero no decide la navegación. Es la pantalla o el `NavHost` quien interpreta ese evento y ejecuta la acción correspondiente.

En `CampoTextoConVoz`, el componente gestiona internamente la coordinación entre escritura manual y dictado por voz, pero siempre comunica el resultado final mediante `onValueChange`, manteniendo una interacción fluida y coherente.

Esta gestión de eventos permite que los componentes sean reutilizables, fáciles de probar y completamente desacoplados del contexto en el que se usan.

---

### ✅ RA3.f – Documentación de componentes

El proyecto incluye una documentación clara y estructurada de los componentes principales, detallando su responsabilidad y ubicación dentro del código.

Se ha elaborado un catálogo de componentes que identifica:

- El nombre del componente.
- El archivo donde se encuentra.
- Su función dentro de la aplicación.
- Las pantallas donde se utiliza.

Esta documentación facilita el mantenimiento del proyecto y permite que otros desarrolladores comprendan rápidamente la estructura del sistema de componentes.

---

### ✅ RA3.h – Integración de los componentes en la aplicación

Los componentes no se utilizan de forma aislada, sino que están completamente integrados en el flujo real de la aplicación.

El `CampoTextoConVoz` se utiliza tanto en la creación de incidencias como en la respuesta del administrador, garantizando una experiencia de accesibilidad uniforme. El `VoiceInputButton` se integra dentro del propio campo de texto, formando una jerarquía clara de composición.

Los componentes reaccionan a datos reales provenientes de Room mediante `Flow`. Cuando una incidencia cambia de estado, los chips y tarjetas se recomponen automáticamente sin necesidad de recargar la pantalla.

El `GraficoBarras` consume directamente los datos procesados por el `InformesViewModel` y se reutiliza para representar distintas distribuciones dentro del mismo dashboard.

Esta integración transversal demuestra que el sistema de componentes forma parte activa del funcionamiento global de la aplicación.


## 🎨 RA4 – Estándares, Usabilidad y Estilo

En este apartado se analiza cómo CádizAccesible aplica estándares de diseño reconocidos en Android y cómo estas decisiones influyen directamente en la usabilidad, la claridad visual y la accesibilidad de la aplicación, tanto para el ciudadano como para el administrador.

El objetivo principal ha sido construir una interfaz coherente, predecible y fácil de usar, evitando diseños experimentales que puedan dificultar el uso en un contexto de servicio público.

---

### ✅ RA4.a – Aplicación de estándares

La aplicación se ha desarrollado siguiendo de forma consistente los principios de **Material Design 3 (Material You)**, que es el estándar actual recomendado por Android.

Todas las pantallas utilizan una estructura base común mediante `Scaffold`, integrando componentes oficiales como `TopAppBar`, `ElevatedCard`, `FilterChip`, `OutlinedTextField` y `Button`. Esta consistencia garantiza que el usuario perciba la aplicación como un todo uniforme y no como un conjunto de pantallas inconexas.

La jerarquía visual se apoya en la escala tipográfica oficial de Material 3, utilizando estilos como `titleLarge` para encabezados y `bodyMedium` para información secundaria. Esto facilita que el usuario identifique rápidamente qué información es principal y cuál es complementaria.

La aplicación de estos estándares es completa y homogénea en toda la app, lo que permite alcanzar una experiencia profesional y alineada con el ecosistema Android.

---

### ✅ RA4.b – Valoración y reflexión sobre los estándares

La elección de Material Design 3 no se ha realizado solo por conveniencia técnica, sino por una reflexión consciente sobre el tipo de aplicación desarrollada.

CádizAccesible es una app de servicio público, por lo que se ha priorizado la claridad, previsibilidad y accesibilidad frente a una personalización excesiva. Utilizar patrones visuales ya conocidos reduce la curva de aprendizaje y evita que el usuario tenga que *aprender* a usar la aplicación.

Además, Material 3 ofrece soporte nativo para modo claro y oscuro, escalas tipográficas accesibles y áreas táctiles adecuadas, lo que refuerza el carácter inclusivo de la aplicación. En este contexto, seguir el estándar no limita el diseño, sino que mejora la experiencia global del usuario.

---

### ✅ RA4.c – Diseño de menús y navegación

La aplicación no utiliza menús tradicionales complejos, sino un sistema de navegación basado en roles y contexto, orientado a la eficiencia cognitiva.

El punto de entrada actúa como un **dashboard** que funciona como menú principal. El ciudadano solo ve acciones relacionadas con la creación y consulta de incidencias, mientras que el administrador accede directamente a la bandeja de gestión y a los informes. De este modo, se evita mostrar opciones irrelevantes según el perfil del usuario.

La `TopAppBar` está presente en todas las pantallas y cumple una función de orientación clara: indica dónde se encuentra el usuario y permite volver atrás siguiendo el patrón de navegación jerárquica recomendado por Android.

Además, se integran menús gestuales mediante *swipe*, que actúan como menús contextuales invisibles, permitiendo acciones rápidas sin sobrecargar la interfaz con botones adicionales.

---

### ✅ RA4.d – Distribución de acciones

Las acciones dentro de la aplicación están distribuidas de forma clara y predecible, evitando errores y confusión.

Las acciones principales, como **Publicar**, **Rechazar** o **Actualizar estado**, se sitúan siempre al final del flujo de la pantalla y se diferencian visualmente de las acciones secundarias. Esto evita pulsaciones accidentales y refuerza la intención del usuario.

Además, se implementan mecanismos de prevención de errores, como la desactivación de botones durante procesos en curso. Por ejemplo, mientras una incidencia se está publicando, el botón se deshabilita y se muestra un indicador de progreso, impidiendo envíos duplicados.

Esta distribución hace que la interacción sea segura y fácil de entender incluso para usuarios poco habituados a aplicaciones móviles.

---

### ✅ RA4.e – Distribución de controles

Los controles están organizados siguiendo una jerarquía lógica de uso, guiando al usuario paso a paso.

En formularios complejos, como la creación de incidencias, los controles se agrupan en bloques funcionales dentro de `ElevatedCard`: primero la descripción del problema, después la clasificación y finalmente los elementos multimedia. Este orden natural permite completar la tarea de arriba hacia abajo sin saltos cognitivos.

El uso consistente de espaciado (`Arrangement.spacedBy(16.dp)`) y *padding* estándar garantiza que los controles no se amontonen y que las zonas táctiles sean cómodas, reforzando la usabilidad y la accesibilidad.

---

### ✅ RA4.f – Elección de controles

Cada control se ha elegido en función del tipo de dato y del contexto de uso, justificando su elección desde el punto de vista de la interacción natural.

Se utilizan `FilterChip` en lugar de desplegables para categorías y gravedades, permitiendo ver todas las opciones de un vistazo y reduciendo el número de pulsaciones necesarias.

Los `Switch` se emplean para valores booleanos claros, como **Urgente** o **Temporal**, ya que representan visualmente un estado activado/desactivado.

Para la entrada de texto se usan `OutlinedTextField`, diferenciando entre campos de una sola línea (títulos) y campos multilínea (descripciones), delimitando correctamente el área táctil y facilitando la escritura o el dictado por voz.

La elección de controles es coherente, consistente y perfectamente justificada para el tipo de información gestionada.

---

### ✅ RA4.g – Diseño visual, estética y legibilidad

El diseño visual prioriza la función sobre la decoración, garantizando una interfaz limpia y legible.

El uso de color semántico permite identificar estados sin necesidad de leer texto: verde para estados resueltos, rojo para errores o rechazos y colores neutros para información informativa. Esto mejora la comprensión rápida, especialmente en listados largos.

El espaciado uniforme y la alineación consistente evitan la saturación visual y facilitan la interacción táctil. Además, la implementación completa de modo claro y oscuro asegura una buena legibilidad en distintas condiciones lumínicas, reduciendo la fatiga visual.

---

### ✅ RA4.h – Claridad de mensajes y feedback

La comunicación entre el sistema y el usuario es clara, directa y adaptada a un lenguaje no técnico.

Los mensajes de estado informan siempre de lo que está ocurriendo, por ejemplo: *“Publicando incidencia…”* o *“Cargando datos…”*, acompañados de indicadores visuales que evitan la sensación de bloqueo.

Los botones utilizan verbos de acción claros y directos, evitando ambigüedades. Además, los gestos de *swipe* se acompañan de iconos y textos dinámicos que confirman la acción antes de ejecutarla, reduciendo errores y reforzando la confianza del usuario.

---

### ✅ RA4.i – Pruebas de usabilidad

Se han realizado pruebas de usabilidad de forma práctica durante el desarrollo de la aplicación, verificando los flujos principales desde el punto de vista del usuario ciudadano y del administrador.

Estas pruebas han incluido:

- Comprobación de la claridad del flujo de creación de incidencias.
- Verificación de que los gestos y botones resultan comprensibles sin instrucciones previas.
- Revisión de estados vacíos, mensajes de error y *feedback* visual.

Aunque no se han realizado pruebas formales con usuarios externos, el análisis ha sido completo dentro del alcance del proyecto y coherente con un entorno académico.

---

### ✅ RA4.j – Evaluación en distintos dispositivos y configuraciones

Durante el desarrollo de CádizAccesible, la evaluación en distintos dispositivos se ha abordado desde un enfoque de diseño adaptativo, teniendo en cuenta las limitaciones de medios disponibles.

La aplicación no ha podido ser probada físicamente en dispositivos como tablets debido a la falta de acceso a este tipo de hardware. No obstante, la interfaz ha sido diseñada desde el inicio siguiendo principios de *responsive design*, con el objetivo de garantizar su correcta adaptación a diferentes tamaños de pantalla.

Para ello, se utilizan layouts flexibles como `fillMaxWidth()`, `Modifier.weight()` y `FlowRow`, que permiten que los elementos se redistribuyan automáticamente según la resolución y orientación del dispositivo. En formularios largos se emplea `verticalScroll`, asegurando que ningún control quede fuera del alcance incluso en pantallas más pequeñas.

Asimismo, se ha comprobado el comportamiento de la aplicación en modo claro y modo oscuro, evaluando contraste y legibilidad para mantener una experiencia consistente en distintas condiciones de uso. Todos los elementos interactivos respetan el área mínima recomendada de **48×48 dp**, lo que facilita la interacción táctil y mejora la accesibilidad.

En conclusión, aunque no se ha realizado una validación física en múltiples dispositivos, la aplicación está diseñada y preparada para adaptarse correctamente a la fragmentación del ecosistema Android, siguiendo las recomendaciones oficiales de diseño.


## 📊 RA5 – Informes y Análisis de Datos

En este apartado se describe cómo CádizAccesible transforma los datos almacenados en la aplicación en información útil para la gestión, permitiendo al administrador analizar el estado de las incidencias de forma clara, estructurada y reactiva.

El objetivo del sistema de informes no es únicamente mostrar datos, sino ayudar a interpretar la situación real de accesibilidad de la ciudad.

---

### ✅ RA5.a – Estructura del informe

La pantalla de informes se ha diseñado siguiendo un modelo de **dashboard jerárquico**, similar al utilizado en herramientas profesionales de análisis de datos.

La información se organiza en tres bloques claramente diferenciados:

- **Bloque de métricas (KPIs):** situado en la parte superior, muestra valores clave como el total de incidencias o el número de incidencias urgentes. Permite obtener una visión inmediata del estado general.
- **Bloque de control (filtros):** ubicado en la zona central, permite segmentar los datos por estado o gravedad mediante chips interactivos.
- **Bloque visual (gráficos):** situado en la parte inferior, representa la distribución de las incidencias de forma gráfica, facilitando la detección de patrones y tendencias.

Esta estructura reduce la carga cognitiva y permite al administrador pasar de una visión general a un análisis más detallado de forma natural.

---

### ✅ RA5.b – Generación de informes a partir de fuentes de datos

Los informes se generan directamente a partir de los datos persistidos en **Room**, sin utilizar datos simulados ni cálculos manuales en la interfaz.

La base de datos actúa como **fuente única de verdad**, y las consultas necesarias para los informes se realizan mediante SQL agregado en el DAO. Esto permite obtener recuentos y totales de forma eficiente y coherente.

Ejemplos de consultas utilizadas en `IncidenciaDao.kt`:

```kotlin
@Query("SELECT COUNT(*) FROM incidencias WHERE urgente = 1")
fun getTotalUrgentes(): Flow<Int>

@Query("SELECT COUNT(*) FROM incidencias WHERE estado = :estado")
fun countByEstado(estado: String): Flow<Int>
```

Estas consultas devuelven datos reactivos mediante `Flow`, de modo que cualquier cambio en la base de datos (creación, eliminación o cambio de estado de una incidencia) se refleja automáticamente en el informe sin necesidad de recargar la pantalla.

---

### ✅ RA5.c – Filtros sobre los valores a presentar

El sistema de informes incorpora filtros interactivos claros y bien justificados, que permiten al administrador segmentar la información según sus necesidades.

Los filtros se gestionan desde el `InformesViewModel` mediante `MutableStateFlow`, capturando la selección del usuario (estado o gravedad). A partir de ese estado, se modifica dinámicamente la consulta a la base de datos usando el operador `flatMapLatest`.

Ejemplo de lógica de filtrado:

```kotlin
private val _filtroEstado = MutableStateFlow<String?>(null)

val incidenciasFiltradas = _filtroEstado.flatMapLatest { estado ->
    if (estado == null) repositorio.getAll()
    else repositorio.getByEstado(estado)
}
```

## 🏢 Evaluación complementaria del sistema de informes (RA5.f / RA5.g / RA5.h)

En este apartado se describe la implementación avanzada del sistema de informes de CádizAccesible, centrada en el uso de herramientas adecuadas, la evolución del código y su integración real dentro de la aplicación.

---

### ✅ RA5.f – Uso de herramientas para generar informes

El sistema de informes se ha desarrollado utilizando herramientas propias del ecosistema Android, integradas directamente en la arquitectura de la aplicación.

La obtención de datos se realiza mediante **Room**, utilizando consultas SQL agregadas para recuentos y distribuciones. Estos resultados se exponen como flujos reactivos mediante `Flow` y `StateFlow`, permitiendo que la información se actualice automáticamente ante cualquier cambio en la base de datos.

Para la generación y representación visual de los informes, se ha utilizado la API `Canvas` de **Jetpack Compose**, desarrollando componentes gráficos personalizados desde cero. Esta elección permite dibujar directamente barras, ejes y etiquetas, adaptando dinámicamente los gráficos a los valores recibidos y manteniendo coherencia con el tema visual de la aplicación.

El uso de `Canvas`, junto con Jetpack Compose, proporciona un control total sobre el diseño, la accesibilidad visual y el comportamiento de los gráficos, sin depender de librerías externas, lo que facilita la integración y el mantenimiento del sistema de informes.

---

### ✅ RA5.g – Modificación y evolución del código del informe

El código del sistema de informes ha sido modificado y refinado progresivamente conforme se han incorporado nuevas necesidades de análisis.

La lógica de filtrado y cálculo se ha centralizado en un **ViewModel** específico, separando claramente la obtención de datos, el procesamiento de la información y la representación visual. Además, se han optimizado las consultas SQL para realizar recuentos y agrupaciones directamente en la base de datos, reduciendo la carga de trabajo en la interfaz.

Estas modificaciones han permitido añadir filtros dinámicos, cálculos derivados y gráficos reactivos manteniendo un código claro, mantenible y coherente con la arquitectura general del proyecto.

---

### ✅ RA5.h – Integración de los informes en la aplicación

Los informes están completamente integrados en el flujo normal de la aplicación y no funcionan como un módulo independiente.

El acceso a esta funcionalidad está controlado por el sistema de roles, de forma que solo el perfil **administrador** puede visualizar los informes. A nivel visual, la pantalla de informes utiliza los mismos componentes, estilos y jerarquía que el resto de la aplicación, garantizando una experiencia consistente.

Los datos mostrados en los informes provienen de la misma fuente que las pantallas de creación y gestión de incidencias, asegurando coherencia y actualización en tiempo real en toda la aplicación.



## 🆘 RA6 – Ayudas, documentación y manuales

En CádizAccesible, el sistema de ayudas y documentación se ha planteado como un elemento integrado en la experiencia de uso, no como un añadido externo. El objetivo es que el usuario pueda comprender y utilizar la aplicación de forma autónoma, y que el proyecto sea fácilmente mantenible desde el punto de vista técnico.

La documentación combina ayudas visibles en la interfaz con explicaciones funcionales y técnicas incluidas en el propio repositorio.

---

### ✅ RA6.a – Identificación de sistemas de generación de ayudas

La aplicación diferencia claramente entre dos tipos de sistemas de ayuda:

- **Ayudas integradas en la interfaz (In-App):** orientadas al usuario final, visibles durante el uso normal de la aplicación.
- **Documentación externa en el repositorio:** dirigida tanto a usuarios como a desarrolladores, accesible desde el README.

Las ayudas internas se generan mediante componentes de interfaz estándar (textos, tarjetas informativas, mensajes de estado) y se adaptan al contexto de uso y al rol del usuario.

---

### ✅ RA6.b – Generación de ayudas en formatos habituales

Las ayudas mostradas en la aplicación siguen formatos habituales en aplicaciones móviles modernas y respetan las guías de Material Design.

Entre los formatos utilizados se incluyen:

- **Tarjetas informativas:** bloques de texto integrados en `ElevatedCard` que explican la finalidad de determinadas pantallas, como la sección de informes o la creación de incidencias.
- **Indicaciones de acción:** textos breves y directos que guían al usuario sobre qué debe hacer en cada campo o sección.
- **Mensajes de estado:** información visible durante procesos como carga de datos, publicación de incidencias o actualización de estados.

Estos formatos permiten ofrecer ayuda sin interrumpir el flujo de uso ni saturar visualmente la interfaz.

---

### ✅ RA6.c – Ayudas sensibles al contexto

Las ayudas mostradas por la aplicación se adaptan al estado de la interfaz y al rol del usuario, evitando mostrar información innecesaria.

Algunos ejemplos de ayudas contextuales son:

- **Estados vacíos:** cuando un ciudadano no tiene incidencias registradas, se muestra un mensaje explicativo indicando que los reportes aparecerán en esa pantalla una vez creados.
- **Ayudas por gesto:** las instrucciones relacionadas con el uso del gesto *swipe* solo se muestran en aquellas pantallas donde dicha interacción está disponible.
- **Diferenciación por rol:** el administrador visualiza ayudas específicas relacionadas con la gestión de incidencias que no aparecen en la vista del ciudadano.

Este enfoque reduce la carga cognitiva y hace que la ayuda sea relevante en cada momento.

---

### ✅ RA6.d – Documentación de la estructura de la información persistente

La estructura de la información persistente gestionada mediante Room está documentada dentro del proyecto para facilitar su comprensión y mantenimiento.

La documentación incluye:

- **Entidades:** descripción de las principales entidades de la base de datos, como `IncidenciaEntity` y `UsuarioEntity`, indicando sus campos y claves primarias.
- **DAO:** explicación de las consultas utilizadas para alimentar listados e informes, incluyendo recuentos y filtros.
- **Flujo de datos:** descripción del patrón **Repository** como capa intermedia entre la base de datos y la interfaz, garantizando la integridad y consistencia de la información.

Esta documentación permite entender rápidamente cómo se almacena y recupera la información en la aplicación.

---

### ✅ RA6.e – Manual de usuario y guía de referencia

El proyecto incluye un **manual de usuario** integrado en el propio repositorio, accesible desde el README, redactado en un lenguaje claro y no técnico.

Este manual explica:

- El funcionamiento general de la aplicación.
- El flujo de uso para ciudadanos y administradores.
- El proceso de creación de incidencias, incluyendo el uso del dictado por voz.
- La interpretación básica de los informes y métricas mostradas en la aplicación.

El objetivo del manual es que cualquier usuario pueda entender cómo utilizar la aplicación sin necesidad de conocimientos técnicos ni asistencia externa.

---

### ✅ RA6.f – Manual técnico de instalación y configuración

Junto al manual de usuario, el proyecto incorpora un **manual técnico**, también incluido en el repositorio, orientado a desarrolladores o personal técnico.

Este documento describe:

- La arquitectura general del proyecto (MVVM).
- La estructura de paquetes y componentes.
- La configuración del entorno de desarrollo en Android Studio.
- La gestión de dependencias mediante Gradle.
- La estructura y funcionamiento de la base de datos local gestionada con Room.

Este manual permite instalar, comprender y mantener el proyecto de forma ordenada y eficiente.

---

### ✅ RA6.g – Tutoriales y aprendizaje progresivo

Como apoyo adicional a los manuales, se ha creado un **vídeo explicativo** en el que se muestra el funcionamiento general de la aplicación y sus principales flujos de uso.

Este vídeo sirve como introducción visual para comprender rápidamente:

- Cómo se crea una incidencia.
- Cómo se gestionan las incidencias desde el perfil administrador.
- Cómo se interpretan los informes y gráficos.

Además, se contempla la creación de vídeos tutoriales más específicos en versiones posteriores, centrados en acciones concretas como la creación de incidencias, la gestión mediante gestos o el uso de los informes.

Este enfoque combina documentación escrita con apoyo audiovisual, facilitando el aprendizaje progresivo y adaptándose a distintos perfiles de usuario.


## 📦 RA7 – Distribución de aplicaciones

Este bloque describe cómo se prepararía CádizAccesible para una distribución real en Android. Actualmente la aplicación se ejecuta en entorno de desarrollo (Android Studio / ADB), por lo que este RA se plantea como plan técnico de empaquetado y despliegue, documentando los pasos y decisiones necesarios para publicar una versión instalable y mantenible.

---

### ✅ RA7.a – Empaquetado de la aplicación

Para distribuir la aplicación fuera del entorno de desarrollo sería necesario generar un paquete de *release*. El proceso consistiría en configurar el proyecto en modo *release*, revisar dependencias y permisos, y generar el artefacto final desde Android Studio o Gradle.

La opción recomendada para un lanzamiento en tienda sería generar un **Android App Bundle (`.aab`)**, ya que permite que la tienda entregue a cada dispositivo solo los recursos necesarios, reduciendo el tamaño final de descarga. Para instalaciones internas o pruebas también se podría generar un **APK firmado**.

---

### ✅ RA7.b – Personalización del instalador

La personalización se centra en la identidad visual y en cómo aparece la aplicación al instalarse.

En este proyecto ya se ha realizado una personalización visible: se ha cambiado el **icono de la aplicación**, utilizando recursos adaptativos para garantizar su correcta visualización en distintos *launchers* y tamaños de pantalla.

Además, para una entrega final se definirían de forma consistente:

- Nombre visible de la aplicación (*label*).
- Icono adaptativo completo (foreground / background si aplica).
- Identidad de paquete estable para que futuras actualizaciones se instalen encima sin conflictos.

---

### ✅ RA7.c – Generación del paquete desde el entorno

La generación del paquete se realizaría directamente desde **Android Studio** (`Build > Generate Signed Bundle / APK`) o mediante tareas de **Gradle**.

El flujo sería:

1. Selección del tipo de salida (APK o AAB).
2. Selección del *build variant* `release`.
3. Generación del archivo final con configuración optimizada.

En este punto también se revisaría que la aplicación funciona correctamente en *release* (por ejemplo, que no se producen errores por minificación u optimizaciones).

---

### ✅ RA7.d – Uso de herramientas externas

Para distribuir versiones de prueba sin depender de instalaciones manuales, se utilizarían herramientas externas como:

- **Firebase App Distribution** (o un canal de pruebas interno) para enviar versiones a un grupo reducido de usuarios, como técnicos municipales o colaboradores.
- Alternativamente, un repositorio privado o canal interno de distribución, según el contexto.

Estas herramientas permiten centralizar descargas, controlar versiones y recoger *feedback* sin publicar directamente en tienda.

---

### ✅ RA7.e – Firma digital

Para publicar o distribuir una aplicación fuera del entorno de depuración es imprescindible firmarla digitalmente.

El proceso consistiría en:

- Crear un **keystore (`.jks`)** protegido por contraseña.
- Asociar la firma al *build* de `release` desde Android Studio o Gradle.
- Guardar y documentar las credenciales de forma segura, sin incluirlas en el repositorio.

Esta firma actúa como la identidad de la aplicación: sin ella no se podrían publicar actualizaciones ni garantizar la integridad del paquete.

---

### ✅ RA7.f – Instalación desatendida

En un entorno real (por ejemplo, uso municipal en dispositivos corporativos), una forma de instalación sin intervención manual sería utilizar sistemas **MDM (Mobile Device Management)**.

El planteamiento sería:

- Distribuir la aplicación a dispositivos gestionados (móviles de operarios, tablets de puntos de información, etc.).
- Instalar y actualizar automáticamente sin que el usuario tenga que aceptar cada paso.

Este enfoque es especialmente útil cuando la aplicación forma parte de un servicio institucional y se requiere control centralizado.

---

### ✅ RA7.g – Desinstalación

En Android, la desinstalación elimina automáticamente los datos privados de la aplicación (Room, preferencias y caché interno). Para una salida limpia, la preparación consistiría en:

- Evitar guardar datos en ubicaciones externas innecesarias.
- Mantener todo el almacenamiento en áreas internas de la aplicación.
- Gestionar ficheros temporales (por ejemplo, imágenes) mediante limpieza periódica o almacenamiento controlado.

De este modo, al desinstalar la aplicación no quedarían residuos ni archivos externos.

---

### ✅ RA7.h – Canales de distribución

Para una estrategia realista de publicación se plantean tres canales diferenciados:

- **Canal de pruebas:** distribución a *testers* mediante Firebase App Distribution para validar estabilidad y detectar fallos.
- **Canal oficial:** publicación en Google Play Console para ofrecer la aplicación a la ciudadanía con actualizaciones automáticas.
- **Distribución alternativa:** entrega de APK firmado desde un canal institucional (por ejemplo, la web del ayuntamiento) si se requiere una vía fuera de tienda.

Este enfoque permite separar pruebas, lanzamiento estable y distribución alternativa según el público objetivo.


## 🧪 RA8 – Pruebas avanzadas

En este apartado se describen las pruebas realizadas sobre CádizAccesible con el objetivo de validar la estabilidad de la aplicación, la corrección de la lógica de negocio y el correcto funcionamiento del acceso a datos y ViewModels. Las pruebas se han ejecutado utilizando el sistema de testing integrado de Android Studio, combinando **tests instrumentados** y **tests unitarios**.

---

### ✅ RA8.c – Pruebas de regresión

Se han implementado pruebas de regresión reales para asegurar que los cambios introducidos en la aplicación no afectan negativamente a funcionalidades ya existentes.

Estas pruebas se centran principalmente en:

- Operaciones CRUD sobre incidencias.
- Actualización de estados y comentarios.
- Cálculo de métricas e informes.
- Comportamiento de los ViewModels ante cambios en los datos.

Las pruebas se han ejecutado tras modificaciones en la base de datos y en la lógica de negocio, verificando que los resultados siguen siendo correctos y coherentes.

**Evidencias:**

- `RepositorioIncidenciasRoomTest`
- `IncidenciaDaoTest`
- `InformesViewModelTest`
- `DetalleIncidenciaViewModelTest`

Todos los tests se ejecutan correctamente, confirmando que las funcionalidades principales permanecen estables tras los cambios realizados.

---

### ✅ RA8.d – Pruebas de volumen y estrés

Para evaluar el comportamiento de la aplicación con conjuntos de datos más amplios, se han realizado pruebas que simulan escenarios con múltiples incidencias registradas en la base de datos.

Estas pruebas permiten comprobar:

- Que los listados devuelven correctamente todos los elementos esperados.
- Que los recuentos y distribuciones por estado o gravedad se calculan correctamente incluso con varios registros.
- Que los ViewModels gestionan correctamente los flujos de datos sin errores ni bloqueos.

El uso de `LazyColumn`, consultas agregadas en Room y flujos reactivos garantiza que el rendimiento se mantiene estable incluso cuando el número de incidencias aumenta.

---

### ✅ RA8.e – Pruebas de seguridad funcional

Aunque se trata de una aplicación sin backend remoto, se han validado aspectos clave de **seguridad funcional** mediante pruebas indirectas sobre la lógica de acceso y gestión de datos.

Entre las comprobaciones realizadas destacan:

- Verificación de que las operaciones de actualización y borrado solo afectan a las incidencias esperadas.
- Comprobación de que los ViewModels gestionan correctamente los datos según el contexto (por ejemplo, detalle de incidencia frente a listados).
- Validación de que las pantallas de gestión e informes dependen de la lógica de rol definida en la aplicación.

Estas pruebas aseguran que la aplicación mantiene un comportamiento coherente y predecible, evitando accesos indebidos o estados inconsistentes.

---

### ✅ RA8.f – Uso de recursos y eficiencia

Las pruebas realizadas también permiten evaluar indirectamente el uso de recursos de la aplicación.

Se ha verificado que:

- El acceso a base de datos se realiza mediante Room y `Flow`, evitando bloqueos del hilo principal.
- Las operaciones de cálculo (totales, distribuciones, porcentajes) se ejecutan en los ViewModels, manteniendo la UI ligera.
- La carga de imágenes se gestiona de forma asíncrona mediante Coil, reduciendo el consumo de memoria.

La ejecución completa de las baterías de pruebas sin errores ni bloqueos confirma que la aplicación hace un uso eficiente de CPU y memoria dentro de su alcance funcional.

---

### 📊 Resumen de pruebas ejecutadas

| Tipo de prueba | Clase                              | Objetivo                                               |
|---------------|------------------------------------|--------------------------------------------------------|
| DAO           | `IncidenciaDaoTest`                | Validación de consultas SQL y persistencia              |
| Repositorio   | `RepositorioIncidenciasRoomTest`   | Integridad de la lógica de acceso a datos               |
| ViewModel     | `InformesViewModelTest`            | Cálculo correcto de métricas e informes                 |
| ViewModel     | `DetalleIncidenciaViewModelTest`   | Gestión del estado y detalle de incidencias             |

---

En conclusión las pruebas implementadas en CádizAccesible demuestran que la aplicación ha sido validada más allá de una simple comprobación visual. La combinación de pruebas sobre base de datos, repositorio y ViewModels garantiza un comportamiento **estable, coherente y mantenible**.

Este enfoque permite detectar errores de forma temprana, facilita la evolución del proyecto y sienta las bases para futuras ampliaciones con pruebas automatizadas adicionales.
