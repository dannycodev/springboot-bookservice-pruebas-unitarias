# 🧪 Guía práctica de testing unitario en Spring Boot con JUnit 5 y Mockito

Este proyecto es una práctica básica de testing con **JUnit 5** y **Mockito** usando Spring Boot. Simula una gestión de libros (`BookService`) para aprender a probar servicios de forma efectiva.

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

### 🎯 Anotaciones de prueba más importantes

| Anotación | Propósito |
|----------|-----------|
| `@Mock` | Crea una dependencia simulada |
| `@InjectMocks` | Inyecta los mocks en la clase a testear |
| `@ExtendWith(MockitoExtension.class)` | Habilita Mockito con JUnit 5 |
| `@BeforeEach` | Ejecuta código antes de cada test |
| `@Test` | Declara un método de prueba |
| `assertThrows` | Verifica que se lance una excepción |
| `verify` | Verifica si un método fue llamado |

---

## 🧪 Criterios para diseñar buenos tests

### 1. ✅ Casos felices (funcionamiento normal)
✔️ Probar que funciona bien con datos válidos.  
Ej: guardar un libro con autor y título.

### 2. ⚠️ Entradas inválidas
❌ Probar que se lanza una excepción si los datos son incorrectos.  
Ej: título o autor nulos o vacíos.

### 3. 🚫 Casos límite (edge cases)
⚙️ Probar listas vacías, campos muy largos, ID nulos, etc.

### 4. 🔁 Fallos en dependencias
💥 Simular que el repositorio lanza errores o devuelve datos inconsistentes.

### 5. 📞 Verificaciones
🔍 Confirmar que se llaman correctamente los métodos simulados.  
Ej: `verify(bookRepository).save(libro);`

---

## 🧠 Regla de oro

❓ "¿Qué podría salir mal aquí?"  
Convierte cada respuesta en un test.

---

## ✍️ Convención sugerida para nombres de test

Ejemplos:
- `guardarLibro_deberíaLanzarExcepcion_siTituloEsInvalido`
- `buscarLibroPorId_deberíaRetornarLibro_siExiste`

---

## 🚀 Siguientes pasos para practicar

- Implementar y testear `eliminarLibroPorId(Long id)`
- Simular errores con `when(...).thenThrow(...)`
- Usar `ArgumentCaptor` para verificar datos pasados al mock
- Escribir tests para controladores con `@WebMvcTest`

---

## 👨‍💻 Autor
Este archivo fue generado como parte de una práctica guiada para aprender a testear servicios en Spring Boot desde cero.

