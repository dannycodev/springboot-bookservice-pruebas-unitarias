package com.dannycodev.bookstore;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book saveBook(Book book) {
        if(book.getTitle() == null || book.getTitle().isBlank()){
            throw new IllegalArgumentException("El libro debe tener un título válido");
        }
        if(book.getAuthor() == null || book.getAuthor().isBlank()){
            throw new IllegalArgumentException("El libro debe tener un autor válido");
        }
        return bookRepository.save(book);
    }

    public List<Book> saveBooks(List<Book> books){
        books.forEach(book -> {
            if(book.getTitle() == null || book.getTitle().isBlank()){
                throw new IllegalArgumentException("El libro debe tener un título válido");
            }
            if(book.getAuthor() == null || book.getAuthor().isBlank()){
                throw new IllegalArgumentException("El libro debe tener un autor válido");
            }  
        });
        return bookRepository.saveAll(books);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

}
