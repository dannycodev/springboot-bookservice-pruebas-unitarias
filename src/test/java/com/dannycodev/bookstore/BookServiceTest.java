package com.dannycodev.bookstore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para BookService")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book libroValido;

    @BeforeEach
    void setUp() {
        libroValido = Book.builder()
                .id(1L)
                .title("1984")
                .author("George Orwell")
                .build();
    }

    @Nested
    @DisplayName("Guardar libro")
    class GuardarLibro {

        @Test
        @DisplayName("Debe retornar el libro guardado")
        void deberiaRetornarLibroGuardado() {
            when(bookRepository.save(libroValido)).thenReturn(libroValido);

            Book result = bookService.saveBook(libroValido);

            assertNotNull(result);
            assertEquals("1984", result.getTitle());
            verify(bookRepository).save(libroValido);
        }

        @Test
        @DisplayName("Debe guardar título y autor sin espacios")
        void deberiaGuardarTituloYAutorSinEspacios() {
            Book libroConEspacios = Book.builder()
                    .title(" 1984 ")
                    .author(" George Orwell ")
                    .build();

            when(bookRepository.save(any())).thenReturn(libroConEspacios);
            ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);

            bookService.saveBook(libroConEspacios);
            verify(bookRepository).save(captor.capture());

            Book capturado = captor.getValue();
            assertEquals("1984", capturado.getTitle().trim());
            assertEquals("George Orwell", capturado.getAuthor().trim());
        }

        @ParameterizedTest(name = "Título inválido: \"{0}\"")
        @ValueSource(strings = {"", " ", "\t"})
        @DisplayName("Debe lanzar excepción si el título es inválido")
        void deberiaLanzarExcepcionSiTituloInvalido(String titulo) {
            Book libro = Book.builder().title(titulo).author("Autor válido").build();
            assertThrows(IllegalArgumentException.class, () -> bookService.saveBook(libro));
            verify(bookRepository, never()).save(any());
        }

        @ParameterizedTest(name = "Autor inválido: \"{0}\"")
        @ValueSource(strings = {"", " ", "\t"})
        @DisplayName("Debe lanzar excepción si el autor es inválido")
        void deberiaLanzarExcepcionSiAutorInvalido(String autor) {
            Book libro = Book.builder().title("Título válido").author(autor).build();
            assertThrows(IllegalArgumentException.class, () -> bookService.saveBook(libro));
            verify(bookRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si un libro de la lista es inválido")
        void guardarListaConLibroInvalido_deberiaLanzarExcepcion() {
            Book libroValido = Book.builder().title("Título válido").author("Autor válido").build();
            Book libroInvalido = Book.builder().title("Título válido").author("").build();
            List<Book> libros = List.of(libroValido, libroInvalido);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                bookService.saveBooks(libros);
            });

            assertEquals("El libro debe tener un autor válido", ex.getMessage());
            verify(bookRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Debe guardar una lista de libros válidos")
        void guardarListaDeLibrosValidos_deberiaGuardarTodos() {
            List<Book> libros = List.of(
                    new Book(null, "Libro 1", "Autor 1"),
                    new Book(null, "Libro 2", "Autor 2")
            );

            when(bookRepository.saveAll(libros)).thenReturn(libros);
            List<Book> resultado = bookService.saveBooks(libros);

            assertEquals(2, resultado.size());
            verify(bookRepository).saveAll(libros);
        }
    }

    @Nested
    @DisplayName("Obtener libros")
    class ObtenerLibros {

        @Test
        void deberiaRetornarListaDeLibros() {
            when(bookRepository.findAll()).thenReturn(List.of(libroValido));
            List<Book> result = bookService.getAllBooks();

            assertEquals(1, result.size());
            assertEquals("1984", result.get(0).getTitle());
            verify(bookRepository).findAll();
        }

        @Test
        void deberiaRetornarLibroPorIdExistente() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(libroValido));
            Optional<Book> result = bookService.getBookById(1L);

            assertTrue(result.isPresent());
            assertEquals("George Orwell", result.get().getAuthor());
        }

        @Test
        void deberiaRetornarVacioSiLibroNoExiste() {
            when(bookRepository.findById(2L)).thenReturn(Optional.empty());
            Optional<Book> result = bookService.getBookById(2L);

            assertFalse(result.isPresent());
            verify(bookRepository).findById(2L);
        }
    }

    @Nested
    @DisplayName("Eliminar libro")
    class EliminarLibro {

        @Test
        void deberiaEliminarLibroExistente() {
            when(bookRepository.existsById(1L)).thenReturn(true);
            bookService.deleteBookById(1L);
            verify(bookRepository).deleteById(1L);
        }

        @Test
        void deberiaLanzarExcepcionSiLibroNoExiste() {
            when(bookRepository.existsById(99L)).thenReturn(false);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                bookService.deleteBookById(99L);
            });

            assertEquals("El libro no existe", ex.getMessage());
            verify(bookRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Actualizar libro")
    class ActualizarLibro {

        @Test
        void deberiaActualizarCamposCorrectamente() {
            Book nuevosDatos = Book.builder()
                    .title("Animal Farm")
                    .author("George Orwell")
                    .build();

            when(bookRepository.findById(1L)).thenReturn(Optional.of(libroValido));
            when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Book actualizado = bookService.updateBook(1L, nuevosDatos);

            assertEquals("Animal Farm", actualizado.getTitle());
            assertEquals("George Orwell", actualizado.getAuthor());

            verify(bookRepository).findById(1L);
            verify(bookRepository).save(libroValido);
        }

        @Test
        void deberiaLanzarExcepcionSiLibroNoExiste() {
            Book nuevosDatos = Book.builder()
                    .title("Nuevo Título")
                    .author("Nuevo Autor")
                    .build();

            when(bookRepository.findById(99L)).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                bookService.updateBook(99L, nuevosDatos);
            });

            assertEquals("No se puede actualizar: el libro no existe", ex.getMessage());
            verify(bookRepository, never()).save(any());
        }

        @Test
        void deberiaLanzarExcepcionSiTituloInvalido() {
            Book nuevosDatos = Book.builder()
                    .title("")
                    .author("Autor válido")
                    .build();

            when(bookRepository.findById(1L)).thenReturn(Optional.of(libroValido));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                bookService.updateBook(1L, nuevosDatos);
            });

            assertEquals("El libro debe tener un título válido", ex.getMessage());
            verify(bookRepository, never()).save(any());
        }
    }
}
