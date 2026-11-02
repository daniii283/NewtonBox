# NewtonBox — Roadmap

## 1. Propósito

Este documento define la evolución prevista de NewtonBox por fases.

El roadmap no representa una lista cerrada de tareas ni compromete fechas concretas.

Su objetivo es:

- ordenar prioridades;
- evitar desarrollar capacidades futuras antes de estabilizar la base;
- definir qué debe conseguirse en cada etapa;
- establecer criterios para considerar una fase suficientemente completada;
- permitir que el proyecto evolucione de forma incremental.

La visión del producto se encuentra en `PROJECT_VISION.md`.

El estado actual se documenta en `CURRENT_STATE.md`.

La arquitectura y su dirección de evolución se describen en `ARCHITECTURE.md`.

---

# 2. Principios del roadmap

La evolución de NewtonBox seguirá estas reglas:

1. estabilizar antes de ampliar;
2. conservar funcionalidad existente cuando siga siendo válida;
3. corregir problemas concretos antes de refactorizar por estética;
4. introducir nuevas capacidades en versiones pequeñas;
5. acompañar los cambios relevantes con pruebas y documentación;
6. evitar decisiones arquitectónicas prematuras;
7. priorizar seguridad, integridad de datos y mantenibilidad;
8. mantener el rigor científico como requisito de las futuras capacidades de simulación.

Las fases podrán revisarse cuando aumente el conocimiento del sistema.

---

# 3. Fase 0 — Recuperación y comprensión del proyecto

## Objetivo

Recuperar el contexto técnico y funcional de NewtonBox antes de realizar cambios significativos.

## Trabajo principal

- recuperar la visión original del proyecto;
- analizar el repositorio existente;
- identificar entidades, controllers, services y repositories;
- reconstruir los flujos principales;
- revisar autenticación y autorización;
- identificar problemas de configuración y persistencia;
- separar estado actual de visión futura;
- establecer una dirección arquitectónica inicial;
- crear la documentación base.

## Documentación asociada

- `PROJECT_VISION.md`;
- `CURRENT_STATE.md`;
- `ARCHITECTURE.md`;
- `ROADMAP.md`;
- posteriormente `AGENTS.md`.

## Estado

🟢 **En progreso avanzado**

## Criterio de salida

La fase podrá considerarse cerrada cuando exista suficiente conocimiento del proyecto para comenzar cambios técnicos sin depender de suposiciones importantes sobre el código existente.

---

# 4. Fase 1 — Entorno reproducible y saneamiento básico

## Objetivo

Conseguir que NewtonBox pueda prepararse y ejecutarse de forma controlada desde un entorno limpio.

## Prioridades

### Configuración

- revisar `application.properties`;
- retirar credenciales y secretos versionados;
- externalizar valores sensibles;
- definir una configuración de desarrollo reproducible;
- documentar los valores necesarios para arrancar la aplicación.

### Base de datos

- comprobar la versión real de MySQL utilizada;
- documentar la preparación de la base de datos;
- identificar los datos mínimos requeridos;
- resolver la inicialización de roles necesarios;
- determinar el estado actual de roles y permisos.

### Repositorio

- crear o revisar `.gitignore`;
- identificar artefactos generados versionados;
- limpiar `target` y otros archivos que no deban mantenerse;
- conservar únicamente archivos necesarios;
- revisar diferencias históricas de capitalización de paquetes.

### Build

- comprobar versión de Java;
- ejecutar build desde limpio;
- verificar Maven;
- documentar cualquier requisito adicional.

## Criterio de salida

Desde una copia limpia del repositorio debe ser posible:

1. preparar la configuración;
2. preparar una base de datos vacía;
3. inicializar los datos imprescindibles;
4. compilar;
5. arrancar NewtonBox;
6. llegar hasta un estado preparado para probar la API.

---

# 5. Fase 2 — Seguridad y autenticación

## Objetivo

Estabilizar los mecanismos actuales de autenticación y autorización antes de ampliar el sistema.

## Prioridades

### JWT

- eliminar tokens completos de los logs;
- revisar manejo de JWT inválidos;
- revisar comportamiento del filtro sobre endpoints públicos;
- revisar expiración;
- revisar tratamiento del estado del usuario durante autenticación por JWT.

### Secretos

- retirar secretos del código fuente;
- configurar secretos mediante entorno o mecanismo equivalente;
- revisar si los secretos versionados necesitan rotación.

### Registro

- garantizar inicialización del rol `RESEARCHER`;
- validar correctamente los datos de entrada;
- comprobar comportamiento ante usuarios duplicados.

### Login

- revisar tratamiento de excepciones;
- evitar ocultar errores de infraestructura como credenciales inválidas;
- comprobar respuestas HTTP.

### Autorización

- revisar reglas de propietario;
- revisar reglas de creador de experimento;
- revisar participación;
- diferenciar correctamente roles globales y roles locales.

## Criterio de salida

Debe existir un flujo seguro y probado para:

```text
registro
→ login
→ acceso autenticado
→ acceso permitido
→ acceso denegado
```

sin exposición de credenciales o tokens sensibles.

---

# 6. Fase 3 — Persistencia e integridad del modelo actual

## Objetivo

Garantizar que las relaciones existentes no produzcan pérdidas de datos o comportamientos inesperados.

## Prioridades

### Relaciones JPA

Revisar:

- `UserEntity`;
- `Role`;
- `Permission`;
- `Experiment`;
- `Participant`;
- `Result`;
- `Comment`.

### Cascadas

Analizar especialmente:

- usuario → roles;
- roles → permisos;
- experimento → participantes;
- experimento → resultados;
- experimento → comentarios.

No modificar cascadas hasta comprender el comportamiento actual.

### Restricciones

Evaluar restricciones para:

- username;
- nombre de rol;
- participación usuario-experimento;
- otros valores que deban ser únicos.

### Eliminaciones

Definir comportamiento esperado al eliminar:

- usuario;
- experimento;
- participante;
- resultado;
- comentario.

### Mappers

Revisar:

- referencias transitorias;
- creación de entidades relacionadas;
- identificación de autores;
- creación anidada.

## Criterio de salida

Las operaciones principales deberán conservar la integridad de la información y disponer de reglas de eliminación claramente definidas.

---

# 7. Fase 4 — Contrato de API y errores

## Objetivo

Hacer que la API tenga un comportamiento consistente y predecible.

## Prioridades

### Validación

- incorporar `@Valid` donde corresponda;
- revisar campos obligatorios;
- validar IDs;
- validar formatos;
- revisar contraseñas;
- evitar entradas inconsistentes.

### DTOs

Revisar la necesidad de separar:

```text
CreateRequest
UpdateRequest
Response
```

cuando los contratos actuales permitan operaciones no deseadas.

### Errores

- consolidar excepciones específicas;
- corregir códigos HTTP incorrectos;
- revisar `RuntimeException`;
- unificar formato de errores;
- revisar errores procedentes de Spring Security.

### Contratos

Documentar progresivamente:

- parámetros;
- respuestas;
- errores;
- autenticación requerida.

## Criterio de salida

Cada operación principal debe disponer de entradas, respuestas y errores coherentes y predecibles.

---

# 8. Fase 5 — Tests del núcleo existente

## Objetivo

Dejar de depender exclusivamente de revisión manual para validar NewtonBox.

## Prioridades

Crear pruebas para:

- registro;
- login;
- JWT;
- autorización;
- usuarios;
- experimentos;
- participantes;
- resultados;
- comentarios;
- persistencia;
- errores.

## Flujo end-to-end mínimo

El sistema deberá validar al menos:

1. crear base de datos de pruebas;
2. inicializar roles;
3. registrar dos usuarios;
4. iniciar sesión;
5. crear experimento;
6. añadir participante;
7. guardar resultado;
8. crear comentario;
9. consultar información;
10. comprobar acceso permitido;
11. comprobar acceso denegado.

## Criterio de salida

El núcleo principal deberá disponer de cobertura suficiente para permitir cambios posteriores con menor riesgo de regresión.

---

# 9. Fase 6 — Documentación de API y desarrollo

## Objetivo

Facilitar el uso, mantenimiento y evolución del backend.

## Prioridades

### OpenAPI

Evaluar e incorporar documentación OpenAPI cuando los contratos principales estén suficientemente estabilizados.

Debe describirse:

- endpoints;
- autenticación;
- parámetros;
- DTOs;
- códigos HTTP;
- errores.

### Documentación de desarrollo

Documentar:

- preparación del entorno;
- ejecución;
- base de datos;
- tests;
- configuración;
- estructura principal.

## Criterio de salida

Un desarrollador que no haya trabajado previamente en NewtonBox deberá poder preparar y comprender el backend con una dependencia mínima de conocimiento informal.

---

# 10. Fase 7 — Consolidación del núcleo colaborativo

## Objetivo

Completar y aclarar las capacidades actuales relacionadas con experimentos y colaboración.

## Prioridades

### Experimentos

- revisar ciclo de vida;
- definir estados cuando sea necesario;
- revisar propiedad y administración.

### Participantes

- definir reglas de incorporación;
- resolver auto-registro;
- impedir duplicados;
- definir comportamiento de roles locales.

### Roles locales

Determinar qué acciones puede realizar cada rol dentro de un experimento.

No asumir que los roles globales y locales representan lo mismo.

### Resultados

- definir mejor su significado;
- establecer propiedad;
- definir modificación;
- preparar una evolución hacia resultados científicos más estructurados.

### Comentarios

- unificar reglas de lectura;
- unificar reglas de escritura;
- revisar visibilidad.

## Criterio de salida

El sistema deberá permitir utilizar de forma coherente el flujo colaborativo completo del backend actual.

---

# 11. Fase 8 — Evolución hacia monolito modular

## Objetivo

Mejorar progresivamente los límites internos del backend sin realizar una reescritura.

## Dirección

La evolución deberá favorecer áreas como:

```text
auth
users
experiments

education
laboratory
simulation
ai
```

Los módulos existentes solo se reorganizarán cuando exista una necesidad real durante el trabajo sobre ellos.

## Arquitectura hexagonal

Se podrán introducir principios como:

- casos de uso;
- dominio;
- puertos;
- adaptadores;

especialmente en módulos con lógica compleja.

No se aplicará arquitectura hexagonal de forma mecánica a todo el código.

## Eventos internos

Podrán incorporarse eventos cuando una operación necesite desacoplar efectos secundarios.

Ejemplos futuros:

- experimento completado;
- usuario añadido a una clase;
- práctica asignada;
- sesión finalizada.

## Criterio de salida

El crecimiento del backend deberá poder continuar sin aumentar innecesariamente el acoplamiento entre capacidades.

---

# 12. Fase 9 — Entorno educativo

## Objetivo

Extender NewtonBox hacia su principal caso de uso educativo.

## Posibles capacidades

- centros educativos;
- docentes;
- estudiantes;
- grupos;
- clases;
- prácticas;
- asignaciones;
- sesiones de laboratorio.

## Regla principal

Antes de introducir nuevas entidades deberá estudiarse si conceptos actuales pueden evolucionar o reutilizarse.

Especialmente:

- `User`;
- `Experiment`;
- `Participant`;
- `Role`;
- `Result`;
- `Comment`.

## Primera iteración posible

Una primera versión educativa podría centrarse únicamente en:

```text
Docente
    ↓
Clase
    ↓
Estudiantes
    ↓
Práctica
    ↓
Experimento
```

sin implementar inicialmente horarios, centros complejos o administración avanzada.

## Criterio de salida

Un docente deberá poder organizar una actividad y permitir que un grupo de estudiantes la realice dentro de NewtonBox.

---

# 13. Fase 10 — Frontend base

## Objetivo

Crear una interfaz que permita utilizar de forma efectiva el núcleo de NewtonBox.

## Capacidades iniciales

- autenticación;
- gestión del usuario;
- listado de experimentos;
- creación de experimentos;
- participantes;
- resultados;
- comentarios;
- entorno educativo básico.

La interfaz deberá consumir los contratos de API ya estabilizados.

## Criterio de salida

Las principales capacidades del backend deberán poder utilizarse sin depender de herramientas externas como Postman.

---

# 14. Fase 11 — Laboratorio virtual mínimo

## Objetivo

Construir la primera versión funcional del laboratorio virtual.

No se intentará crear inicialmente un laboratorio científico universal.

## Primera iteración

Definir un número muy limitado de conceptos.

Por ejemplo:

- un entorno;
- un conjunto reducido de objetos;
- interacción básica;
- estado;
- acciones;
- observaciones.

Podrán introducirse elementos como:

- recipientes;
- instrumentos;
- materiales;
- herramientas de medida.

## Representación

Se evaluará el uso de Three.js para el entorno tridimensional cuando los requisitos de interacción estén suficientemente definidos.

## Criterio de salida

Debe existir al menos una práctica completa que pueda realizarse mediante interacción con el laboratorio virtual.

---

# 15. Fase 12 — Motor de simulación científica

## Objetivo

Separar progresivamente la lógica científica de la representación visual y de la lógica general de la aplicación.

## Principios

Priorizar:

- reglas deterministas;
- fórmulas;
- propiedades científicas;
- modelos;
- datasets;
- fuentes fiables.

## Primera iteración

No construir un motor genérico.

Seleccionar un dominio o práctica concreta y desarrollar únicamente las reglas necesarias.

Conceptualmente:

```text
Acción
  ↓
Simulation Engine
  ↓
Reglas / modelo
  ↓
Nuevo estado
  ↓
Resultado
```

## Evolución

Posteriormente podrán incorporarse distintos mecanismos según el dominio:

```text
Simulation Engine
    ├── Chemistry
    ├── Physics
    └── otros dominios
```

La estructura concreta deberá decidirse a partir de los primeros casos reales.

## Criterio de salida

Al menos una práctica deberá producir resultados mediante lógica científica reproducible y separada de la representación visual.

---

# 16. Fase 13 — Inteligencia artificial contextual

## Objetivo

Incorporar IA únicamente cuando exista un núcleo funcional y científico suficientemente estable.

## Primera responsabilidad

La primera integración deberá orientarse preferentemente a asistencia contextual.

Ejemplos:

- explicar conceptos;
- responder dudas;
- interpretar observaciones;
- guiar durante prácticas;
- explicar resultados.

## Simulación

La IA podrá estudiarse como componente secundario cuando no exista todavía un modelo determinista suficiente.

En ningún caso se asumirá automáticamente como fuente científica autoritativa.

## Arquitectura

La integración deberá mantener un límite claro entre:

```text
motor científico
≠
servicio de inteligencia artificial
```

## Criterio de salida

La IA deberá aportar valor educativo sin comprometer la trazabilidad de los resultados científicos.

---

# 17. Fase 14 — Colaboración en tiempo real

## Objetivo

Evaluar capacidades de interacción simultánea cuando existan casos de uso que realmente las necesiten.

## Posibles usos

- experimentos compartidos;
- sesiones de clase;
- presencia de usuarios;
- sincronización del estado;
- actualizaciones en directo.

## Tecnología

WebSockets u otras tecnologías solo deberán incorporarse después de definir el caso funcional.

## Criterio de salida

La colaboración en tiempo real deberá aportar una capacidad que no pueda resolverse adecuadamente mediante el modelo HTTP existente.

---

# 18. Fase 15 — Evaluación de servicios independientes

## Objetivo

Revisar si alguna capacidad ha alcanzado una complejidad que justifique salir del monolito modular.

## Candidatos potenciales

### Simulación

Podría requerir:

- escalado independiente;
- computación intensiva;
- tecnologías científicas específicas.

### Inteligencia artificial

Podría requerir:

- ejecución local de modelos;
- GPU;
- procesamiento asíncrono;
- proveedores externos;
- colas.

## Regla

No se extraerá un microservicio por preferencia arquitectónica.

Deberá existir una necesidad comprobada.

## Criterio de salida

Si no existe ninguna necesidad real de separación, NewtonBox continuará como monolito modular.

---

# 19. Prioridad actual

La prioridad inmediata corresponde a las primeras fases:

```text
Recuperación
     ↓
Entorno reproducible
     ↓
Seguridad
     ↓
Persistencia
     ↓
API
     ↓
Tests
     ↓
Núcleo funcional estable
```

Las fases relacionadas con:

- educación;
- frontend;
- laboratorio 3D;
- simulación;
- IA;
- tiempo real;
- microservicios;

no deben desplazar el trabajo de estabilización actual.

---

# 20. Evolución del roadmap

Este roadmap no es definitivo.

Podrá modificarse cuando:

- se descubra nuevo comportamiento en el código;
- cambien los requisitos del producto;
- una hipótesis técnica resulte incorrecta;
- aparezcan nuevas necesidades;
- una fase resulte demasiado grande y deba dividirse;
- una funcionalidad deje de aportar valor.

Los cambios importantes en la dirección técnica deberán reflejarse también en `ARCHITECTURE.md` y, cuando corresponda, mediante ADR.

---

# 21. Objetivo final

La evolución prevista puede resumirse como:

```text
Proyecto recuperado
        ↓
Backend estable
        ↓
Núcleo colaborativo sólido
        ↓
Plataforma educativa
        ↓
Laboratorio virtual
        ↓
Simulación científica
        ↓
IA contextual
        ↓
Capacidades avanzadas
```

Cada etapa deberá construirse sobre una base suficientemente estable antes de avanzar a la siguiente.