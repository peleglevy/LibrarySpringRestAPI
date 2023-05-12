package com.home.libraryrestapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.home.libraryrestapi.controller.BookController;
import com.home.libraryrestapi.model.Book;
import com.home.libraryrestapi.repository.BookRepository;

import stract.LibraryLRUCache;
@SpringBootTest(classes =LibraryrestapiApplication.class)
public class LibraryrestapiApplicationTests {
    @InjectMocks
    private BookController bookController;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddBook() {
        Book book = new Book("Test Book", "Test Author", 100);
        when(bookRepository.save(any())).thenReturn(book);
        ResponseEntity<Book> response = bookController.addBook(book);
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    public void testGetBookByIdFromCache() {
        Book book = new Book("Test Book", "Test Author", 100);
        LibraryLRUCache.getInstance().addBook(book);
        ResponseEntity<Book> response = bookController.getBookById(book.getId());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), book);
    }

    @Test
    public void testGetBookByIdFromRepository() {
        Book book = new Book("Test Book", "Test Author", 100);
        when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        ResponseEntity<Book> response = bookController.getBookById(book.getId());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), book);
        assertTrue(LibraryLRUCache.getInstance().getBookById(book.getId()) != null);
    }

    @Test
    public void testDeleteBookById() {
        Book book = new Book("Test Book", "Test Author", 100);
        LibraryLRUCache.getInstance().addBook(book);
        ResponseEntity<HttpStatus> response = bookController.deleteBookById(book.getId());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
        verify(bookRepository, times(1)).deleteById(any());
        assertTrue(LibraryLRUCache.getInstance().getBookById(book.getId()) == null);
    }
}
