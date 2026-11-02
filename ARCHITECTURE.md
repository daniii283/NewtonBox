# NewtonBox — Architecture

## 1. Propósito

Este documento describe la arquitectura técnica de NewtonBox.

Su objetivo es separar claramente:

- la arquitectura que existe actualmente;
- los componentes y relaciones comprobados en el código;
- los problemas arquitectónicos detectados;
- la dirección de evolución acordada;
- las propuestas que todavía requieren una decisión formal.

Este documento no representa el backlog del proyecto ni sustituye a `ROADMAP.md`.

La visión funcional general se encuentra en `PROJECT_VISION.md` y el inventario técnico actual en `CURRENT_STATE.md`.

---

# 2. Principios arquitectónicos

La evolución técnica de NewtonBox seguirá los siguientes principios.

## 2.1 Evolución incremental

No se sustituirá una implementación existente únicamente porque exista una alternativa más moderna.

Antes de realizar un cambio arquitectónico se deberá:

1. comprender la implementación actual;
2. comprobar su funcionamiento;
3. evaluar si sigue siendo válida para la visión del proyecto;
4. identificar problemas concretos;
5. aplicar el cambio mínimo razonable.

---

## 2.2 Separación de responsabilidades

Cada componente deberá tener una responsabilidad clara.

En particular se intentará mantener diferenciadas las responsabilidades relacionadas con:

- transporte HTTP;
- reglas de negocio;
- autorización;
- persistencia;
- simulación;
- representación visual;
- integración con inteligencia artificial.

---

## 2.3 Backend como autoridad del estado

Las reglas relevantes del sistema y el estado persistente no deberán depender exclusivamente del cliente.

El frontend podrá representar e interactuar con el laboratorio, pero las operaciones que afecten al estado compartido, persistencia, autorización o simulación relevante deberán ser controladas por componentes del backend.

---

## 2.4 Rigor científico

Los resultados científicos no deberán depender exclusivamente de generación libre mediante modelos de inteligencia artificial.

Cuando existan reglas, fórmulas, modelos deterministas, datasets o fuentes científicas fiables, tendrán prioridad.

---

## 2.5 Decisiones explícitas

Los cambios arquitectónicos importantes deberán documentarse.

Cuando una decisión tenga impacto significativo o existan varias alternativas razonables, deberá considerarse la creación de un ADR dentro de:

```text
docs/decisions/
```

---

# 3. Arquitectura actual

NewtonBox utiliza actualmente una arquitectura monolítica por capas basada en Spring Boot.

De forma simplificada:

```text
Cliente HTTP
    │
    ▼
Spring Security
    │
    ▼
Controllers
    │
    ▼
Services / Validators
    │
    ▼
Repositories
    │
    ▼
JPA / Hibernate
    │
    ▼
MySQL
```

El backend expone una API REST y centraliza actualmente:

- autenticación;
- autorización;
- lógica de negocio;
- persistencia.

---

# 4. Aplicación backend

## 4.1 Tecnología

El backend utiliza actualmente:

- Java 17;
- Spring Boot 3.4.2;
- Maven;
- Spring Web;
- Spring Security;
- Spring Data JPA;
- Hibernate;
- MySQL;
- JWT.

El punto de entrada principal de la aplicación es `NewtonboxApplication`.

---

# 5. Capa HTTP

La API utiliza controllers REST.

Se han identificado los siguientes controladores principales:

- `AuthController`;
- `UserController`;
- `ExperimentController`;
- `ParticipantController`;
- `ResultController`;
- `CommentController`.

Su responsabilidad principal debe limitarse progresivamente a:

- recibir peticiones;
- validar datos de entrada;
- delegar la operación;
- devolver la respuesta HTTP adecuada.

Las reglas de negocio no deberían concentrarse en los controllers.

---

# 6. Capa de servicios

Actualmente existen servicios relacionados con los principales recursos:

- `UserService`;
- `ExperimentService`;
- `ParticipantService`;
- `ResultService`;
- `CommentService`;
- `UserDetailsServiceImpl`.

Los servicios contienen gran parte de la lógica de negocio actual.

También existen componentes de validación utilizados para comprobar acceso y determinadas reglas asociadas a los recursos.

Esta separación es válida como base y no existe actualmente una necesidad demostrada de sustituirla por una arquitectura diferente.

---

# 7. Persistencia

La persistencia utiliza Spring Data JPA y Hibernate.

Se han identificado los siguientes repositorios principales:

- `IUserRepository`;
- `IRoleRepository`;
- `IExperimentRepository`;
- `IParticipantRepository`;
- `IResultRepository`;
- `ICommentRepository`.

La base de datos utilizada actualmente es MySQL.

---

# 8. Modelo de dominio actual

Las principales entidades identificadas son:

```text
UserEntity
Experiment
Participant
Result
Comment
Role
Permission
```

De forma conceptual, las relaciones actuales pueden representarse así:

```text
User
 ├── Roles
 │    └── Permissions
 │
 ├── Experiments created
 ├── Participations
 ├── Results
 └── Comments

Experiment
 ├── Creator
 ├── Participants
 ├── Results
 └── Comments
```

Este esquema representa el modelo existente y no debe confundirse con el modelo de dominio futuro de NewtonBox.

---

# 9. DTOs y mappers

La API utiliza DTOs para separar parcialmente el modelo de persistencia del contrato HTTP.

Existen además mappers encargados de convertir entre:

```text
Entity ↔ DTO
```

Esta separación debe mantenerse.

Sin embargo, será necesario revisar progresivamente:

- DTOs utilizados tanto para creación como actualización;
- presencia de identificadores controlables por el cliente;
- profundidad de objetos anidados;
- referencias transitorias;
- tamaño de determinadas respuestas.

A medio plazo podrá resultar conveniente diferenciar explícitamente:

```text
CreateRequest
UpdateRequest
Response
```

cuando las necesidades de cada operación sean diferentes.

---

# 10. Seguridad

La seguridad actual está basada en Spring Security.

El flujo simplificado es:

```text
Petición
    │
    ▼
JwtAuthenticationFilter
    │
    ▼
Validación del JWT
    │
    ▼
Carga del usuario desde base de datos
    │
    ▼
SecurityContext
    │
    ▼
Reglas de autorización
    │
    ▼
Controller
```

---

# 11. Autenticación

## Registro

El registro:

1. recibe las credenciales;
2. comprueba la existencia del usuario;
3. cifra la contraseña mediante BCrypt;
4. obtiene el rol global `RESEARCHER`;
5. guarda el usuario.

Actualmente existe una dependencia arquitectónica importante:

```text
RESEARCHER debe existir previamente en base de datos.
```

La inicialización reproducible de datos base deberá resolverse durante la estabilización del proyecto.

---

## Login

El login utiliza:

```text
AuthenticationManager
        ↓
DAO Authentication Provider
        ↓
UserDetailsServiceImpl
        ↓
UserRepository
        ↓
MySQL
```

Tras una autenticación correcta se genera un JWT.

---

# 12. Autorización

Actualmente existen varias fuentes de autorización.

## Roles globales

Los usuarios pueden disponer de roles globales como:

```text
ADMIN
RESEARCHER
REVIEWER
```

Estos roles forman parte de las autoridades utilizadas por Spring Security.

---

## Permisos

Existe infraestructura para permisos asociados a roles.

Sin embargo, su utilización en reglas de autorización específicas todavía es limitada.

Por tanto, esta parte debe considerarse una capacidad existente pero incompleta.

---

## Propiedad y participación

Muchas decisiones actuales se basan también en:

- creador del recurso;
- creador del experimento;
- usuario autenticado;
- participación dentro de un experimento.

Estas reglas forman parte actualmente de la lógica de negocio y de validación.

---

# 13. Roles locales de experimento

`Participant` dispone de información relacionada con roles dentro de un experimento.

Estos roles deben diferenciarse de los roles globales.

Conceptualmente:

```text
Rol global
    → autoridad general dentro de NewtonBox

Rol local
    → responsabilidad dentro de un experimento concreto
```

Actualmente esta separación existe parcialmente en el modelo, pero los roles locales todavía no gobiernan de manera consistente las operaciones disponibles.

Antes de ampliar esta funcionalidad deberán definirse claramente sus reglas.

---

# 14. Gestión de errores

Existe un `GlobalExceptionHandler` para transformar determinadas excepciones en respuestas HTTP.

Actualmente conviven:

- excepciones específicas;
- excepciones genéricas;
- errores procedentes de Spring Security;
- errores de infraestructura.

La dirección arquitectónica será converger progresivamente hacia:

```text
Excepción de dominio / aplicación
        ↓
GlobalExceptionHandler
        ↓
Formato de error consistente
        ↓
Respuesta HTTP correcta
```

No se realizará una sustitución masiva hasta haber identificado todos los comportamientos existentes.

---

# 15. Configuración

Actualmente la aplicación utiliza:

```text
application.properties
```

para configurar:

- base de datos;
- JPA;
- servidor;
- JWT;
- Spring Security.

Existen valores sensibles versionados y no hay todavía una estrategia completa de configuración por entorno.

La dirección futura será separar:

```text
Configuración versionable
+
Configuración específica del entorno
+
Secretos externos
```

sin introducir herramientas adicionales hasta que exista una necesidad concreta.

---

# 16. Gestión del esquema de base de datos

Actualmente Hibernate utiliza:

```text
spring.jpa.hibernate.ddl-auto=update
```

Esto facilita el desarrollo inicial, pero no proporciona un historial controlado de evolución del esquema.

No existe actualmente un sistema de migraciones identificado.

La incorporación de una herramienta de migraciones deberá evaluarse durante la estabilización de persistencia.

Hasta entonces no se presupone una tecnología concreta.

---

# 17. Riesgos arquitectónicos actuales

El análisis del código ha identificado varias áreas que requieren revisión.

## Persistencia

- cascadas sobre relaciones compartidas;
- eliminación de usuarios;
- eliminación de experimentos;
- restricciones únicas;
- referencias creadas mediante mappers;
- dependencias entre entidades.

## Seguridad

- secretos versionados;
- JWT en logs;
- tratamiento de JWT inválidos;
- comportamiento del filtro sobre endpoints públicos;
- validación del estado del usuario;
- diferenciación entre roles globales y locales.

## Contratos HTTP

- validación incompleta;
- DTOs de creación con identificadores;
- respuestas potencialmente grandes;
- formato de errores inconsistente.

## Rendimiento

Se deberán revisar posibles problemas relacionados con:

- consultas N+1;
- serialización profunda;
- ausencia de paginación;
- carga de relaciones.

Estas cuestiones deben medirse antes de realizar optimizaciones generales.

---

# 18. Arquitectura objetivo conceptual

La visión futura de NewtonBox requiere capacidades que actualmente no existen.

A nivel conceptual, la plataforma podría evolucionar hacia:

```text
                 ┌─────────────────────────┐
                 │        Frontend         │
                 │                         │
                 │ Gestión + Laboratorio   │
                 │       interactivo       │
                 └────────────┬────────────┘
                              │
                           HTTP/API
                              │
                 ┌────────────▼────────────┐
                 │     Backend NewtonBox   │
                 │                         │
                 │ Usuarios                │
                 │ Seguridad               │
                 │ Experimentos            │
                 │ Entorno educativo       │
                 │ Sesiones                │
                 │ Resultados              │
                 │ Colaboración            │
                 └────────────┬────────────┘
                              │
              ┌───────────────┼───────────────┐
              │               │               │
              ▼               ▼               ▼
        Persistencia      Simulación       Servicios
                           científica       auxiliares
                              │
                    ┌─────────┴─────────┐
                    ▼                   ▼
             Modelos fiables      IA contextual
             reglas/fórmulas       secundaria
             datasets
```

Este esquema representa una dirección conceptual y no componentes que deban implementarse inmediatamente.

---

# 19. Frontend futuro

El repositorio actual no contiene un frontend funcional correspondiente a la visión completa de NewtonBox.

La visión contempla una aplicación cliente capaz de proporcionar:

- gestión de usuarios y entornos;
- gestión educativa;
- interacción con experimentos;
- representación del laboratorio;
- visualización de resultados.

La representación del laboratorio podrá utilizar tecnologías 3D.

Three.js forma parte de la visión histórica del proyecto, pero su incorporación concreta deberá evaluarse cuando comience el desarrollo del laboratorio virtual.

---

# 20. Laboratorio virtual

El laboratorio virtual deberá tratarse como una capa interactiva sobre un estado controlado.

Conceptualmente:

```text
Usuario realiza acción
        ↓
Frontend interpreta interacción
        ↓
Backend recibe acción
        ↓
Motor correspondiente evalúa resultado
        ↓
Estado del experimento cambia
        ↓
Backend devuelve nuevo estado
        ↓
Frontend representa el resultado
```

No todas las interacciones necesitarán necesariamente una llamada al servidor.

La frontera exacta entre simulación local y simulación del lado servidor deberá decidirse cuando existan casos de uso concretos.

---

# 21. Motor de simulación

Actualmente no existe un motor científico de simulación.

Su futura arquitectura deberá permitir utilizar diferentes mecanismos según el dominio.

Conceptualmente:

```text
Simulation Engine
       │
       ├── reglas deterministas
       ├── fórmulas
       ├── modelos matemáticos
       ├── propiedades científicas
       ├── datasets
       └── integraciones especializadas
```

No debe diseñarse inicialmente como un motor universal.

Cada capacidad científica deberá incorporarse de forma incremental.

---

# 22. Inteligencia artificial

Actualmente NewtonBox no contiene integración con inteligencia artificial.

Su posición arquitectónica futura será auxiliar.

Conceptualmente:

```text
Backend
   │
   ├── Motor científico principal
   │
   └── Servicio de IA
          │
          ├── ayuda contextual
          ├── explicaciones
          ├── asistencia educativa
          └── apoyo secundario cuando corresponda
```

La IA no sustituirá automáticamente el motor científico.

Las respuestas generadas deberán distinguirse de los resultados calculados mediante reglas o modelos científicos fiables.

---

# 23. Entorno educativo

Actualmente no existen entidades específicas para:

- centros educativos;
- clases;
- grupos académicos;
- prácticas asignadas;
- relaciones profesor-alumno.

Estas capacidades deberán incorporarse sobre el núcleo existente únicamente después de analizar qué conceptos actuales pueden reutilizarse.

Antes de crear nuevas entidades deberán estudiarse especialmente:

```text
User
Experiment
Participant
Role
Result
Comment
```

para evitar duplicar conceptos existentes.

---

# 24. Comunicación en tiempo real

Actualmente no existe comunicación en tiempo real mediante WebSockets u otra tecnología equivalente.

Esta capacidad podría ser necesaria en el futuro para casos como:

- experimentos colaborativos;
- sincronización de sesiones;
- cambios de estado compartidos;
- presencia de usuarios.

No se incorporará WebSocket únicamente por formar parte de la visión.

Primero deberá existir un caso funcional que justifique su necesidad.

---

# 25. Monolito modular como dirección actual

El backend actual es un monolito.

Esto no se considera por sí mismo un problema.

La dirección arquitectónica acordada para la evolución de NewtonBox será mantener inicialmente un **monolito modular**, evitando introducir microservicios antes de que exista una necesidad técnica real.

El objetivo será organizar progresivamente el backend alrededor de capacidades o dominios claramente diferenciados.

Conceptualmente:

```text
NewtonBox Backend
│
├── Identity / Security
├── Users
├── Experiments
├── Education
├── Laboratory
├── Simulation
└── AI
```

Esto no implica que todos estos módulos existan actualmente.

Los módulos futuros deberán incorporarse únicamente cuando exista funcionalidad real que los justifique.

La prioridad será conseguir límites internos claros antes de plantear límites entre procesos o servicios independientes.

---

# 26. Principios de arquitectura hexagonal

NewtonBox adoptará progresivamente principios de arquitectura hexagonal allí donde aporten valor.

Esto no implica rehacer el backend actual ni aplicar una estructura rígida de forma inmediata.

El objetivo principal será evitar que la lógica de negocio y los modelos centrales dependan directamente de tecnologías concretas.

Conceptualmente:

```text
Adaptadores de entrada
        │
        ▼
Casos de uso / Aplicación
        │
        ▼
Dominio
        │
        ▼
Puertos
   ┌────┴────┐
   ▼         ▼
Persistencia Servicios externos
```

Por ejemplo, una capacidad de simulación científica debería poder depender de abstracciones relacionadas con:

- almacenamiento;
- fuentes de datos científicos;
- servicios externos;
- inteligencia artificial;

sin acoplar directamente su lógica principal a una tecnología concreta.

La separación podrá introducirse progresivamente cuando se modifiquen o creen áreas relevantes.

No se realizará una refactorización global únicamente para adaptar el proyecto a un patrón arquitectónico.

---

## 26.1 Eventos internos de aplicación

NewtonBox podrá utilizar eventos internos para desacoplar acciones relacionadas dentro del mismo backend.

Esto no implica inicialmente utilizar:

- Kafka;
- RabbitMQ;
- brokers externos;
- microservicios.

Un evento interno representa un hecho ocurrido dentro de la aplicación.

Por ejemplo:

```text
Experimento completado
        │
        ▼
ExperimentCompleted
        │
        ├── guardar información derivada
        ├── actualizar métricas
        └── generar futuras notificaciones
```

La finalidad de estos eventos será evitar que un único servicio acumule responsabilidades que no forman parte directamente de su operación principal.

Los eventos internos deberán utilizarse únicamente cuando exista una necesidad clara de desacoplamiento.

No todas las operaciones necesitan convertirse en eventos.

Si en el futuro determinadas capacidades requieren procesos independientes, algunos de estos eventos podrán convertirse en mecanismos de comunicación entre servicios.

---

# 27. Posible evolución hacia servicios independientes

NewtonBox no adoptará microservicios como arquitectura inicial.

Sin embargo, el monolito modular deberá intentar mantener límites suficientemente claros para que determinados módulos puedan extraerse en el futuro si aparece una necesidad técnica real.

Algunos candidatos potenciales son:

## Motor de simulación

El motor de simulación podría requerir en el futuro:

- cargas computacionales diferentes;
- procesos especializados;
- tecnologías científicas específicas;
- escalado independiente.

En ese caso podría evolucionar hacia un servicio independiente.

## Inteligencia artificial

La integración con inteligencia artificial podría requerir:

- APIs externas;
- ejecución de modelos locales;
- procesamiento asíncrono;
- colas;
- GPUs;
- tiempos de respuesta diferentes.

Estas necesidades podrían justificar posteriormente un servicio específico.

Conceptualmente:

```text
                    Frontend
                       │
                       ▼
                 NewtonBox API
                       │
             ┌─────────┴─────────┐
             ▼                   ▼
     Simulation Service      AI Service
```

Este esquema representa únicamente una posible evolución futura.

Diseñar límites que permitan una separación posterior no implica realizar dicha separación ahora.

---

# 28. Criterios para extraer un microservicio

Un módulo solo deberá considerarse candidato a servicio independiente cuando exista una necesidad concreta.

Entre los motivos válidos podrían encontrarse:

- necesidad de escalarlo independientemente;
- cargas computacionales claramente diferentes;
- requisitos de disponibilidad distintos;
- necesidad de utilizar tecnologías incompatibles con el backend principal;
- despliegues independientes;
- aislamiento de fallos;
- procesos asíncronos de larga duración;
- límites de dominio suficientemente maduros.

La popularidad de una arquitectura o tecnología no será motivo suficiente para introducir microservicios.

---

# 29. Organización futura del código

Sin imponer todavía una refactorización, el crecimiento del proyecto deberá favorecer una organización progresiva por capacidades o dominios.

Una posible evolución conceptual sería:

```text
auth
users
experiments
participants
results
comments

education
laboratory
simulation
ai
```

Dentro de módulos complejos podrán separarse responsabilidades inspiradas en arquitectura hexagonal.

Por ejemplo:

```text
simulation/
├── application
├── domain
├── ports
└── infrastructure
```

Esta estructura es orientativa.

No se moverán paquetes existentes únicamente para adaptarlos a esta propuesta.

Cuando se trabaje sobre una funcionalidad concreta se evaluará si reorganizarla aporta un beneficio real.

---

# 30. Dirección arquitectónica

La dirección arquitectónica acordada para NewtonBox será:

```text
Backend Spring Boot actual
        ↓
Estabilización
        ↓
Monolito modular
        ↓
Límites claros por dominio/capacidad
        ↓
Principios hexagonales donde aporten valor
        ↓
Eventos internos cuando exista necesidad de desacoplamiento
        ↓
Extracción de servicios únicamente si aparece una necesidad real
```

La prioridad inmediata sigue siendo estabilizar el backend existente.

No se realizará una reescritura para adoptar esta arquitectura.

La evolución se realizará progresivamente mientras se trabajan las áreas existentes y se incorporan nuevas capacidades.

Este enfoque busca mantener la simplicidad operativa actual y, al mismo tiempo, preparar NewtonBox para áreas futuras con mayor complejidad como:

- entorno educativo;
- laboratorio virtual;
- motor de simulación científica;
- inteligencia artificial;
- colaboración en tiempo real.

La arquitectura deberá crecer con las necesidades reales del producto y no anticiparse innecesariamente a ellas.