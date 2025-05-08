package com.dannycodev.bookstore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class BookServiceTest {

    // Arrange (Preparar): Preparamos el entorno con mocks (when().thenReturn())
    // Act (Ejecutar): Llamamos al método real de la clase que queremos probar
    // Assert (Verificar): Comprobamos que el resultado sea el esperado
    // (Extra) Verify: Comprobamos que se llamaron los métodos simulados como se esperaba

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .title("1984")
                .author("George Orwell")
                .build();
    }

    @Test
    void guardarLibro_deberiaRetornarLibroGuardado() {
        // Arrange
        when(bookRepository.save(book)).thenReturn(book);

        // Act
        Book result = bookService.saveBook(book);

        // Assert
        assertNotNull(result);
        assertEquals(book.getId(), result.getId());
        assertEquals("1984", result.getTitle());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
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

    @Test
    void guardarLibro_sinTitulo_deberiaLanzarException(){
        Book libroSinTitulo = Book.builder()
                .id(2L)
                .title("")
                .author("Autor válido")
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.saveBook(libroSinTitulo)
        );

        assertEquals("El libro debe tener un título válido", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void guardarLibro_sinAutor_deberiaLanzarException(){
        Book libroSinAutor = Book.builder()
                .id(2L)
                .title("Título válido")
                .author(null)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.saveBook(libroSinAutor)
        );

        assertEquals("El libro debe tener un autor válido", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void guardarListaConLibroInvalido_deberiaLanzarExcepcion() {
        // Arrange
        Book libroValido = Book.builder()
                .id(1L)
                .title("Título valido")
                .author("Autor válido")
                .build();
        Book libroInvalido = Book.builder()
                .id(1L)
                .title("Título valido")
                .author("")
                .build();

        List<Book> libros = List.of(libroValido, libroInvalido);
        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            bookService.saveBooks(libros);
        });

        assertEquals("El libro debe tener un autor válido", ex.getMessage());
        // Verificamos que NO se haya llamado al repositorio
        verify(bookRepository, never()).saveAll(any());
    }

    @Test
    void obtenerTodosLosLibros_deberiaRetornarListaDeLibros() {
        // Arrange
        List<Book> books = List.of(book);
        when(bookRepository.findAll()).thenReturn(books);

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertEquals(1, result.size());
        assertEquals("1984", result.get(0).getTitle());
        verify(bookRepository).findAll();
    }

    @Test
    void obtenerLibroPorId_existente_deberiaRetornarLibro() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        Optional<Book> result = bookService.getBookById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("George Orwell", result.get().getAuthor());
        verify(bookRepository).findById(1L);
    }

    @Test
    void obtenerLibroPorId_inexistente_deberiaRetornarVacio() {
        // Arrange
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        Optional<Book> result = bookService.getBookById(2L);

        // Assert
        assertFalse(result.isPresent());
        verify(bookRepository).findById(2L);
    }
}
