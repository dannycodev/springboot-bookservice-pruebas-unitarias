# 🧪 Guía práctica de testing unitario en Spring Boot con JUnit 5 y Mockito

Este proyecto es una práctica completa de testing unitario con **JUnit 5** y **Mockito** usando Spring Boot. Simula una gestión de libros (`BookService`) para aprender a probar servicios de forma efectiva, con buenas prácticas como organización por `@Nested`, parametrización, captura de argumentos y verificación de interacciones.

---

## 📦 Dependencias necesarias

Agrega estas dependencias desde Spring Initializr:

- Spring Web
- Spring Data JPA
- H2 Database (opcional)
- Lombok
- Spring Boot Starter Test (incluye JUnit 5 y Mockito)

---

## ✅ Conceptos clave

### 🎯 Anotaciones y herramientas más importantes

| Anotación / Herramienta               | Propósito                                                |
|--------------------------------------|-----------------------------------------------------------|
| `@Mock`, `@InjectMocks`              | Simulación de dependencias y su inyección                |
| `@ExtendWith(MockitoExtension.class)`| Habilita Mockito con JUnit 5                             |
| `@BeforeEach`                        | Prepara objetos antes de cada test                       |
| `@Test`, `@ParameterizedTest`        | Define métodos de prueba (con o sin parámetros)          |
| `@ValueSource`                       | Lista de valores para tests parametrizados               |
| `@DisplayName`                       | Descripciones legibles para consola y reportes           |
| `@Nested`                            | Agrupa tests relacionados en una estructura clara        |
| `ArgumentCaptor`                     | Captura el objeto realmente pasado al mock               |
| `verify`, `assertThrows`, `assertEquals` | Verifican comportamiento esperado                    |

---

## 🧪 Criterios para diseñar buenos tests

1. ✅ **Casos felices (funcionamiento esperado)**
   - Probar entradas válidas y comportamiento correcto

2. ⚠️ **Entradas inválidas**
   - Verificar que se lanzan excepciones con entradas incorrectas (nulos, vacíos)

3. 🚫 **Casos límite**
   - Pruebas con listas vacías, ID inexistentes, campos con solo espacios, etc.

4. 🔁 **Errores en dependencias**
   - Simular fallos en el repositorio con `thenThrow(...)`

5. 📞 **Verificaciones y capturas**
   - Confirmar que los mocks se usan correctamente y capturar argumentos si es necesario

---

## 📐 Organización del test (estructura profesional)

El test `BookServiceTest` está organizado por métodos de servicio usando `@Nested`, lo que mejora la legibilidad:

```text
BookServiceTest
├── GuardarLibro
│   ├── deberiaRetornarLibroGuardado
│   ├── deberiaGuardarTituloYAutorSinEspacios (con ArgumentCaptor)
│   ├── deberiaLanzarExcepcionSiTituloInvalido (parametrizado)
│   ├── deberiaLanzarExcepcionSiAutorInvalido (parametrizado)
│   ├── guardarListaConLibroInvalido_deberiaLanzarExcepcion
│   └── guardarListaDeLibrosValidos_deberiaGuardarTodos
├── ObtenerLibros
│   ├── deberiaRetornarListaDeLibros
│   ├── deberiaRetornarLibroPorIdExistente
│   └── deberiaRetornarVacioSiLibroNoExiste
├── EliminarLibro
│   ├── deberiaEliminarLibroExistente
│   └── deberiaLanzarExcepcionSiLibroNoExiste
├── ActualizarLibro
│   ├── deberiaActualizarCamposCorrectamente
│   ├── deberiaLanzarExcepcionSiLibroNoExiste
│   └── deberiaLanzarExcepcionSiTituloInvalido
```
---

## ✍️ Convención sugerida para nombres de test

[accion]_deberia[resultado]_si[condicion]


Ejemplos:
- `guardarLibro_deberiaLanzarExcepcion_siTituloEsInvalido`
- `eliminarLibro_deberiaLanzarExcepcion_siNoExiste`

---

## 🚀 Funcionalidades cubiertas

- [x] Guardar libro con validaciones
- [x] Guardar lista de libros
- [x] Obtener todos / por ID
- [x] Eliminar por ID con verificación de existencia
- [x] Actualizar libro parcialmente con validaciones
- [x] Verificación de argumentos con `ArgumentCaptor`
- [x] Organización con `@Nested` y nombres claros con `@DisplayName`
- [x] Uso de `@ParameterizedTest` para validar múltiples entradas

---

## 🔁 Versión alternativa con AssertJ

Este proyecto incluye una clase adicional de pruebas llamada:
BookServiceAssertJTest.java

---

### 🎯 ¿Qué hace esta clase?

- Replica los mismos tests de `BookServiceTest`, pero usando la librería **[AssertJ](https://assertj.github.io/doc/)**
- Sustituye las aserciones tradicionales de JUnit 5 (`assertEquals`, `assertTrue`, etc.) por una sintaxis más fluida como:

```java
assertThat(valor).isEqualTo("esperado");
assertThat(lista).hasSize(2).contains("Libro 1", "Libro 2");
assertThat(optional).isPresent().contains(libroValido);
```

---

### ✅ ¿Por qué usar AssertJ?

| Ventaja                         | Descripción                                                        |
| ------------------------------- | ------------------------------------------------------------------ |
| ✅ Sintaxis fluida               | Más expresiva y fácil de leer que los `assertEquals` tradicionales |
| ✅ Aserciones avanzadas          | Asertivos para colecciones, opcionales, strings, fechas, etc.      |
| ✅ Mejor legibilidad en reportes | Mensajes de error más claros y detallados si un test falla         |


---

### 👨‍🏫 ¿Cuándo usar esta clase?

Para comparar AssertJ con JUnit puro

Para adoptar un estilo de testing más moderno

Como plantilla para futuros proyectos que usen AssertJ

---

### 🛠️ Dependencia necesaria en pom.xml

<dependency>
  <groupId>org.assertj</groupId>
  <artifactId>assertj-core</artifactId>
  <version>3.25.3</version>
  <scope>test</scope>
</dependency>

---

## ✅ Este proyecto es ideal para:

- Practicar testing unitario puro con Spring Boot
- Usar como plantilla en nuevos proyectos
- Refactorizar pruebas existentes aplicando buenas prácticas

---

## 👨‍💻 Autor

Este archivo fue generado como parte de una práctica guiada para aprender a testear servicios en Spring Boot aplicando técnicas profesionales y cobertura completa de casos.

