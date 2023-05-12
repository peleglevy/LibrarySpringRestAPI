package stract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.home.libraryrestapi.model.Book;

@SpringBootTest(classes = LibraryLRUCacheTest.class)
public class LibraryLRUCacheTest {

    @Test
    public void testAddAndGetBookById() {
        LibraryLRUCache cache = LibraryLRUCache.getInstance();
        Book book1 = new Book("Book 1", "Author 1", 100);
        Book book2 = new Book("Book 2", "Author 2", 200);
        Book book3 = new Book("Book 3", "Author 3", 300);
        Book book4 = new Book("Book 4", "Author 4", 400);
        
        // add books to cache
        book1.setId(1);
        book2.setId(2);
        book3.setId(3);
        book4.setId(4);
        cache.addBook(book1);
        cache.addBook(book2);
        cache.addBook(book3);
        cache.addBook(book4);
        
        
        // get books from cache
       Book book = cache.getBookById(book2.getId());
        assertNotNull(book);
        assertEquals(book2, book);
        
        book = cache.getBookById(book3.getId());
        assertNotNull(book);
        assertEquals(book3, book);
        
        book = cache.getBookById(book4.getId());
        assertNotNull(book);
        assertEquals(book4, book);
        
        // getnon-existent deleted book from cache
        book = cache.getBookById(book1.getId());
        assertNull(book);
    }
    
    @Test
    public void testLRUCapacity() {
        LibraryLRUCache cache = LibraryLRUCache.getInstance();
        Book book1 = new Book("Book 1", "Author 1", 100);
        Book book2 = new Book("Book 2", "Author 2", 200);
        Book book3 = new Book("Book 3", "Author 3", 300);
        Book book4 = new Book("Book 4", "Author 4", 400);
        Book book5 = new Book("Book 5", "Author 5", 500);
        
        // add books to cache
        book1.setId(1);
        book2.setId(2);
        book3.setId(3);
        book4.setId(4);
        cache.addBook(book1);
        cache.addBook(book2);
        cache.addBook(book3);
        cache.addBook(book4);
        
        // access books to change LRU order
        cache.getBookById(book1.getId());
        cache.getBookById(book2.getId());
        cache.getBookById(book3.getId());
        
        // add another book to exceed cache capacity
        cache.addBook(book5);
        
        // get oldest book, should be book4
        Book book = cache.getBookById(book4.getId());
        assertNull(book);
    }
    
    @Test
    public void testBookExpiration() throws InterruptedException {
        LibraryLRUCache cache = LibraryLRUCache.getInstance();
        Book book1 = new Book("Book 1", "Author 1", 100);
        Book book2 = new Book("Book 2", "Author 2", 200);
        
        // add books to cache with 1 minutes TTL
        book1.setId(1);
        cache.addBook(book1);
        Thread.sleep(30000);
        book2.setId(2);
        cache.addBook(book2);
        
        // wait for 1 min  for cache to expire book1
        Thread.sleep(60000);
        
        // get expired book, should be null
        Book book = cache.getBookById(book1.getId());
        assertNull(book);
        
        // get unexpired book, should be book2
        book = cache.getBookById(book2.getId());
        assertNotNull(book);
        assertEquals(book2, book);
    }
}
