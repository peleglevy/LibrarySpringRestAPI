package com.home.libraryrestapi.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.home.libraryrestapi.repository.BookRepository;

import stract.LibraryLRUCache;

import com.home.libraryrestapi.model.Book;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class BookController {

	@Autowired
	private BookRepository bookRepository;

	private LibraryLRUCache libraryLRUCache = LibraryLRUCache.getInstance();

	

	@PostMapping("/add")
	public ResponseEntity<Book> addBook(@RequestBody Book book) {
		try {
			libraryLRUCache.addBook(book);
			Book _book = bookRepository.save(new Book(book.getName(), book.getAuthor(), book.getNumberOfpages()));
			return new ResponseEntity<>(_book, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<Book> getBookById(@PathVariable("id") long id) {
		Book book = libraryLRUCache.getBookById(id);
		if (book != null) {
			return new ResponseEntity<>(book, HttpStatus.OK);
		}

		Optional<Book> bookData = bookRepository.findById(id);

		if (bookData.isPresent()) {
			libraryLRUCache.addBook(bookData.get());
			return new ResponseEntity<>(bookData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<HttpStatus> deleteBookById(@PathVariable("id") long id) {
		try {
			bookRepository.deleteById(id);
			libraryLRUCache.deleteBookById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
