package com.dannycodev.bookstore;

import static org.assertj.core.api.Assertions.*;
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
@DisplayName("Pruebas unitarias para BookService usando AssertJ")
class BookServiceAssertJTest {

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

            assertThat(result)
                .isNotNull()
                .extracting(Book::getTitle, Book::getAuthor)
                .containsExactly("1984", "George Orwell");

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
            assertThat(capturado.getTitle()).isEqualTo("1984");
            assertThat(capturado.getAuthor()).isEqualTo("George Orwell");
        }

        @ParameterizedTest(name = "Título inválido: \"{0}\"")
        @ValueSource(strings = {"", " ", "\t"})
        @DisplayName("Debe lanzar excepción si el título es inválido")
        void deberiaLanzarExcepcionSiTituloInvalido(String titulo) {
            Book libro = Book.builder().title(titulo).author("Autor válido").build();
            Throwable thrown = catchThrowable(() -> bookService.saveBook(libro));

            assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El libro debe tener un título válido");

            verify(bookRepository, never()).save(any());
        }

        @ParameterizedTest(name = "Autor inválido: \"{0}\"")
        @ValueSource(strings = {"", " ", "\t"})
        @DisplayName("Debe lanzar excepción si el autor es inválido")
        void deberiaLanzarExcepcionSiAutorInvalido(String autor) {
            Book libro = Book.builder().title("Título válido").author(autor).build();
            Throwable thrown = catchThrowable(() -> bookService.saveBook(libro));

            assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El libro debe tener un autor válido");

            verify(bookRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si un libro de la lista es inválido")
        void guardarListaConLibroInvalido_deberiaLanzarExcepcion() {
            Book libroValido = Book.builder().title("Título válido").author("Autor válido").build();
            Book libroInvalido = Book.builder().title("Título válido").author("").build();
            List<Book> libros = List.of(libroValido, libroInvalido);

            Throwable ex = catchThrowable(() -> bookService.saveBooks(libros));

            assertThat(ex)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El libro debe tener un autor válido");

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

            assertThat(resultado)
                .hasSize(2)
                .extracting(Book::getTitle)
                .containsExactly("Libro 1", "Libro 2");

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

            assertThat(result)
                .isNotEmpty()
                .hasSize(1)
                .extracting(Book::getTitle)
                .containsExactly("1984");

            verify(bookRepository).findAll();
        }

        @Test
        void deberiaRetornarLibroPorIdExistente() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(libroValido));
            Optional<Book> result = bookService.getBookById(1L);

            assertThat(result)
                .isPresent()
                .contains(libroValido);

            verify(bookRepository).findById(1L);
        }

        @Test
        void deberiaRetornarVacioSiLibroNoExiste() {
            when(bookRepository.findById(2L)).thenReturn(Optional.empty());
            Optional<Book> result = bookService.getBookById(2L);

            assertThat(result).isNotPresent();
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

            Throwable ex = catchThrowable(() -> bookService.deleteBookById(99L));

            assertThat(ex)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El libro no existe");

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

            assertThat(actualizado.getTitle()).isEqualTo("Animal Farm");
            assertThat(actualizado.getAuthor()).isEqualTo("George Orwell");

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

            Throwable ex = catchThrowable(() -> bookService.updateBook(99L, nuevosDatos));

            assertThat(ex)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No se puede actualizar: el libro no existe");

            verify(bookRepository, never()).save(any());
        }

        @Test
        void deberiaLanzarExcepcionSiTituloInvalido() {
            Book nuevosDatos = Book.builder()
                    .title("")
                    .author("Autor válido")
                    .build();

            when(bookRepository.findById(1L)).thenReturn(Optional.of(libroValido));

            Throwable ex = catchThrowable(() -> bookService.updateBook(1L, nuevosDatos));

            assertThat(ex)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El libro debe tener un título válido");

            verify(bookRepository, never()).save(any());
        }
    }
}

