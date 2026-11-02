# NewtonBox — Current State

## 1. Propósito de este documento

Este documento describe el estado técnico y funcional actual de NewtonBox a partir del código existente en el repositorio.

Su objetivo es distinguir claramente entre:

- funcionalidad presente;
- funcionalidad presente pero pendiente de revisión;
- funcionalidad incompleta;
- problemas técnicos detectados;
- funcionalidad todavía no implementada.

Este documento no representa la visión futura del producto. Para ello debe consultarse `PROJECT_VISION.md`.

> **Nota:** gran parte del inventario actual ha sido comprobado mediante análisis estático del código. Salvo cuando se indique lo contrario, la presencia de una funcionalidad en el código no implica que su comportamiento haya sido validado mediante ejecución o pruebas end-to-end.

---

# 2. Estado general

NewtonBox dispone actualmente de un backend desarrollado con Spring Boot que implementa una primera base para gestionar usuarios, autenticación, experimentos y colaboración entre participantes.

El proyecto se encuentra en un estado intermedio de desarrollo.

Existe una cantidad significativa de funcionalidad implementada, pero todavía requiere trabajo de estabilización, seguridad, pruebas, documentación y definición de determinadas reglas de negocio antes de ampliar el alcance del sistema.

Actualmente no existe un laboratorio virtual funcional ni un motor de simulación científica.

---

# 3. Stack técnico actual

## Backend

- Java 17 declarado en el proyecto.
- Spring Boot 3.4.2.
- Maven.
- API REST.
- Spring Security.
- JWT.
- Spring Data JPA / Hibernate.

## Persistencia

- MySQL.
- JPA/Hibernate.
- Configuración actual mediante `application.properties`.
- Actualización automática del esquema mediante `ddl-auto=update`.

## Seguridad

- autenticación mediante usuario y contraseña;
- contraseñas almacenadas con BCrypt;
- autenticación stateless mediante JWT;
- roles globales;
- permisos asociados a roles;
- autorización mediante Spring Security y `@PreAuthorize`.

---

# 4. Estructura funcional existente

## Usuarios

### Estado

🟡 **Implementado en código — pendiente de validación completa**

Existe gestión de usuarios mediante:

- `UserEntity`;
- `UserController`;
- `UserService`;
- `IUserRepository`;
- DTOs y mappers relacionados.

Actualmente existen operaciones relacionadas con:

- registro;
- consulta de usuarios;
- modificación de determinados datos;
- cambio de contraseña;
- eliminación.

### Observaciones

La actualización de usuarios existe en la capa de servicio, aunque no todas las capacidades parecen estar expuestas mediante controlador.

La eliminación de usuarios requiere revisión debido a sus relaciones con otras entidades.

---

# 5. Autenticación

## Registro

### Estado

🟡 **Implementado en código — pendiente de validación completa**

El flujo actual incluye:

1. recepción de nombre y contraseña;
2. comprobación de usuario existente;
3. cifrado de contraseña mediante BCrypt;
4. asignación del rol global `RESEARCHER`;
5. persistencia del usuario.

### Problema conocido

El registro depende de que el rol `RESEARCHER` exista previamente en la base de datos.

Actualmente no se ha identificado un mecanismo reproducible de inicialización de roles.

Por tanto, una instalación nueva podría requerir preparación manual de la base de datos.

---

## Login

### Estado

🟡 **Implementado en código — pendiente de validación completa**

El sistema utiliza:

- `AuthenticationManager`;
- `UserDetailsServiceImpl`;
- `CustomUserDetails`;
- BCrypt;
- JWT.

Una autenticación correcta genera un token JWT.

---

# 6. JWT

### Estado

🟡 **Implementado, pero requiere revisión de seguridad**

El token contiene actualmente:

- nombre de usuario como `subject`;
- fecha de creación;
- fecha de expiración.

La duración configurada es de aproximadamente 24 horas.

Los roles y permisos no se almacenan en el token. El usuario y sus autoridades se vuelven a consultar en base de datos durante las peticiones autenticadas.

### Problemas detectados

- existen secretos JWT almacenados en configuración versionada;
- se han detectado logs que pueden mostrar tokens completos;
- debe revisarse el tratamiento de tokens con firma incorrecta;
- debe revisarse el comportamiento del filtro JWT sobre rutas públicas;
- los tokens existentes no se invalidan automáticamente tras un cambio de contraseña.

---

# 7. Roles y permisos

## Roles globales

### Estado

🟡 **Implementados parcialmente**

Existen los roles:

- `ADMIN`;
- `RESEARCHER`;
- `REVIEWER`.

Los roles globales forman parte de las autoridades cargadas por Spring Security.

### Problemas pendientes

No se ha identificado todavía un mecanismo de inicialización de estos roles en una instalación nueva.

Las diferencias funcionales entre `RESEARCHER` y `REVIEWER` no están completamente definidas.

---

## Permisos

### Estado

🔴 **Infraestructura existente, uso funcional incompleto**

Existen entidades y estructuras relacionadas con permisos.

Los permisos pueden formar parte de las autoridades del usuario.

Sin embargo, no se ha identificado un uso significativo de autorización mediante permisos específicos como:

```text
hasAuthority(...)
```

La autorización actual depende principalmente de:

- rol global;
- propietario del recurso;
- creador del experimento;
- participación en el experimento.

Por tanto, el sistema de permisos existe pero todavía no parece formar parte plenamente de las reglas de negocio.

---

# 8. Experimentos

### Estado

🟡 **Implementados en código — requieren revisión**

Existe:

- `Experiment`;
- `ExperimentController`;
- `ExperimentService`;
- `IExperimentRepository`;
- DTOs;
- mappers;
- validadores.

El sistema permite actualmente operaciones relacionadas con:

- creación;
- consulta;
- modificación;
- eliminación;
- filtrado;
- comprobación de acceso.

Los experimentos disponen de un creador y pueden contener participantes, resultados y comentarios.

### Aspectos pendientes

- revisar reglas de autorización;
- revisar creación de recursos anidados;
- revisar tamaño de las respuestas;
- revisar consultas y posibles problemas N+1;
- validar comportamiento real mediante pruebas.

---

# 9. Participantes

### Estado

🟡 **Implementados parcialmente**

Existe una entidad `Participant` que relaciona usuarios con experimentos.

Permite representar la participación de un usuario dentro de un experimento.

Existe además un rol local asociado a la participación.

### Problemas pendientes

Los roles locales se almacenan, pero actualmente no parecen controlar de forma consistente las operaciones principales del experimento.

Un usuario con un determinado rol local no obtiene necesariamente permisos diferentes en las acciones disponibles.

También debe reforzarse la prevención de participaciones duplicadas a nivel de base de datos.

---

# 10. Auto-registro en experimentos

### Estado

🔴 **Funcionalidad incompleta**

Existe lógica relacionada con la posibilidad de permitir que usuarios se registren en determinados experimentos.

Sin embargo:

- existe un indicador de apertura/cierre;
- se realizan determinadas comprobaciones;
- no se ha identificado una API completa para configurar esta funcionalidad;
- los experimentos parecen crearse inicialmente cerrados;
- algunas reglas de autorización requieren revisión.

Por tanto, la funcionalidad existe parcialmente pero actualmente no puede considerarse completa.

---

# 11. Resultados

### Estado

🟡 **Implementados en código — pendientes de validación**

Existe:

- `Result`;
- `ResultController`;
- `ResultService`;
- `IResultRepository`;
- DTOs y mappers relacionados.

Los participantes autorizados pueden almacenar resultados asociados a experimentos.

### Aspectos pendientes

- definir con mayor precisión el modelo de resultado científico;
- revisar permisos de creación y modificación;
- revisar la creación de resultados anidados;
- validar comportamiento mediante pruebas.

---

# 12. Comentarios

### Estado

🟡 **Implementados en código — requieren revisión**

Existe:

- `Comment`;
- `CommentController`;
- `CommentService`;
- `ICommentRepository`.

Los usuarios autorizados pueden crear y consultar comentarios relacionados con experimentos.

### Problemas pendientes

Se han detectado posibles inconsistencias entre:

- reglas de lectura;
- reglas de escritura;
- datos incluidos indirectamente dentro de los DTO de experimentos.

También debe revisarse la resolución de autores durante determinadas operaciones de mapeo.

---

# 13. Gestión de errores

### Estado

🟡 **Implementada parcialmente**

Existe un:

```text
GlobalExceptionHandler
```

y varias excepciones específicas relacionadas con:

- recursos inexistentes;
- credenciales;
- conflictos;
- roles;
- permisos;
- contraseñas;
- campos requeridos.

### Problemas detectados

Todavía existen operaciones que utilizan excepciones genéricas.

Además:

- algunas excepciones de recurso inexistente pueden terminar devolviendo `400` en lugar de `404`;
- el formato de error no es completamente uniforme;
- errores de seguridad pueden ser tratados de forma diferente al resto;
- un manejador genérico de `RuntimeException` puede ocultar errores más específicos.

---

# 14. Validación de entrada

### Estado

🔴 **Incompleta**

Existen anotaciones de validación en algunos DTO.

Sin embargo, se ha detectado que algunos puntos de entrada no utilizan `@Valid`.

También existen DTOs con validación limitada.

Debe revisarse:

- validación de campos obligatorios;
- formato;
- longitud;
- valores nulos;
- IDs proporcionados por el cliente;
- contraseñas.

---

# 15. Creación mediante DTO

### Estado

🔴 **Requiere revisión de seguridad y consistencia**

Algunos DTO utilizados para crear recursos aceptan identificadores.

Algunos mappers conservan esos identificadores al construir las entidades.

Esto debe revisarse porque podría permitir que el cliente influya en identificadores que deberían ser gestionados exclusivamente por persistencia.

Deberá diferenciarse claramente entre:

- DTO de creación;
- DTO de actualización;
- DTO de respuesta.

---

# 16. Persistencia y relaciones

### Estado

🟡 **Funcional, pero con riesgos detectados**

Se utilizan relaciones JPA entre las entidades principales.

### Riesgos identificados

Debe revisarse especialmente:

- uso de `CascadeType.ALL` en relaciones compartidas;
- relación usuario → roles;
- relación roles → permisos;
- eliminación de usuarios referenciados;
- eliminación de experimentos;
- referencias transitorias creadas por algunos mappers;
- restricciones únicas en base de datos.

Las cascadas actuales podrían provocar efectos no deseados al eliminar entidades compartidas.

---

# 17. Configuración de base de datos

### Estado

🔴 **No reproducible de forma segura todavía**

La configuración actual utiliza MySQL local.

Actualmente se ha detectado:

- URL de conexión almacenada en configuración;
- credenciales almacenadas directamente en archivos versionados;
- `ddl-auto=update`;
- ausencia de perfiles de entorno;
- ausencia de migraciones;
- ausencia de scripts claros de inicialización.

Antes de ampliar el proyecto deberá hacerse reproducible la preparación del entorno de base de datos.

---

# 18. Gestión de secretos

### Estado

🔴 **Problema crítico pendiente**

Se han detectado valores sensibles dentro de archivos versionados.

Entre ellos existen configuraciones relacionadas con:

- base de datos;
- JWT;
- credenciales de Spring Security.

Los valores concretos no deben reproducirse en documentación.

Será necesario:

- retirar secretos del repositorio;
- utilizar configuración externa;
- revisar el historial Git cuando corresponda;
- definir configuración segura para desarrollo.

---

# 19. Logging

### Estado

🔴 **Requiere revisión**

Se han detectado logs que pueden incluir tokens JWT completos.

Los tokens de autenticación no deben escribirse completos en los logs.

También deberá revisarse el nivel y contenido general del logging antes de utilizar NewtonBox fuera de un entorno de desarrollo.

---

# 20. Tests

### Estado

🔴 **Cobertura prácticamente inexistente**

Actualmente se ha identificado únicamente una prueba básica de contexto:

```text
NewtonboxApplicationTests.contextLoads()
```

No existen pruebas suficientes para validar:

- autenticación;
- autorización;
- experimentos;
- participantes;
- resultados;
- comentarios;
- persistencia;
- reglas de negocio;
- errores.

El comportamiento actual del sistema no debe considerarse suficientemente verificado hasta incorporar pruebas relevantes.

---

# 21. Documentación de API

### Estado

⚪ **No implementada**

Actualmente no se ha identificado:

- OpenAPI;
- Swagger UI;
- especificación formal de endpoints.

La API deberá documentarse una vez estabilizado su contrato básico.

---

# 22. Documentación del proyecto

### Estado

🔴 **En proceso de recuperación**

Actualmente se está creando la documentación base del proyecto.

Documentos incorporados o previstos:

- `PROJECT_VISION.md`;
- `CURRENT_STATE.md`;
- `ARCHITECTURE.md`;
- `ROADMAP.md`;
- `AGENTS.md`;
- `docs/decisions/`.

La documentación existente anteriormente era muy limitada.

---

# 23. Repositorio

### Estado

🔴 **Requiere saneamiento**

Se han detectado artefactos que no deberían formar parte del repositorio fuente, como:

- archivos compilados;
- contenido de `target`;
- informes generados;
- estado interno de Maven;
- configuración compilada;
- determinados archivos del IDE.

También se detectaron diferencias históricas de capitalización en paquetes entre artefactos compilados y código fuente.

Debe definirse un `.gitignore` adecuado y limpiar el repositorio cuidadosamente sin eliminar archivos útiles de forma indiscriminada.

---

# 24. Arquitectura actual

NewtonBox presenta actualmente una arquitectura de backend monolítico por capas.

De forma simplificada:

```text
HTTP / REST
    ↓
Controllers
    ↓
Services / Validators
    ↓
Repositories
    ↓
JPA / Hibernate
    ↓
MySQL
```

Spring Security intercepta las peticiones mediante autenticación JWT y aplica reglas de autorización antes de determinadas operaciones.

Esta arquitectura se documentará con mayor detalle en `ARCHITECTURE.md`.

---

# 25. Funcionalidad actualmente presente

A partir del análisis del código se ha identificado presencia de:

- registro de usuarios;
- login;
- JWT;
- BCrypt;
- roles globales;
- permisos;
- usuarios;
- experimentos;
- participantes;
- roles locales de participación;
- resultados;
- comentarios;
- cambio de contraseña;
- consultas filtradas;
- autorización basada en usuario, propietario y participación;
- manejo global parcial de errores;
- DTOs;
- mappers;
- repositorios JPA.

Su presencia en código no implica todavía validación funcional completa.

---

# 26. Funcionalidad parcial o incompleta

Actualmente se consideran incompletas o pendientes de revisión profunda:

- inicialización de roles y permisos;
- auto-registro en experimentos;
- utilización real de permisos CRUD;
- roles locales de participantes;
- validación de entradas;
- uniformidad de errores;
- seguridad JWT;
- gestión de secretos;
- eliminación segura de entidades relacionadas;
- pruebas automatizadas;
- reproducibilidad del entorno;
- documentación de API;
- configuración de entornos.

---

# 27. Funcionalidad no implementada actualmente

No se ha identificado implementación actual de:

- centros educativos;
- clases;
- grupos académicos;
- gestión docente/alumno;
- asignación formal de prácticas;
- horarios;
- laboratorio virtual interactivo;
- laboratorio 3D;
- Three.js dentro del repositorio actual;
- instrumentos científicos virtuales;
- sustancias o materiales científicos;
- motor de simulación científica;
- modelos físicos o químicos;
- colaboración en tiempo real;
- WebSockets;
- integración con inteligencia artificial;
- asistente contextual mediante IA.

Estas capacidades pertenecen a la visión futura y no deben tratarse como funcionalidad existente.

---

# 28. Prioridades inmediatas derivadas del estado actual

Antes de ampliar significativamente NewtonBox resulta necesario:

1. garantizar un arranque reproducible del backend;
2. definir correctamente la preparación de la base de datos;
3. resolver la inicialización de roles;
4. retirar secretos del código versionado;
5. revisar los principales riesgos de seguridad;
6. revisar integridad y cascadas JPA;
7. validar el flujo funcional principal;
8. incorporar pruebas automatizadas;
9. estabilizar contratos de API;
10. documentar la arquitectura real.

Estas prioridades deberán convertirse posteriormente en fases concretas dentro de `ROADMAP.md`.

---

# 29. Flujo mínimo que debe poder validarse

La primera validación funcional completa del backend debería demostrar como mínimo:

1. preparar una base de datos limpia;
2. inicializar los roles necesarios;
3. registrar usuarios;
4. iniciar sesión;
5. crear un experimento;
6. añadir o registrar participantes;
7. guardar resultados;
8. crear comentarios;
9. consultar la información;
10. comprobar accesos permitidos;
11. comprobar accesos denegados.

Hasta completar esta validación no debería asumirse que el núcleo actual está completamente estabilizado.

---

# 30. Conclusión

NewtonBox no parte de cero.

Existe un backend con una base funcional relevante para usuarios, autenticación, experimentos y colaboración.

Sin embargo, el proyecto todavía necesita una fase de recuperación y estabilización antes de incorporar las capacidades más ambiciosas descritas en `PROJECT_VISION.md`.

La prioridad actual es conservar el trabajo útil existente, comprenderlo, probarlo y corregir los problemas concretos detectados antes de ampliar el alcance funcional.