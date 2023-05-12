package stract;


import com.home.libraryrestapi.model.Book;

public class Node {

    private Book book;
    private Long key;
    private Node next;
    private Node prev;
    private long lut;

    public Node(Book book, Long key) {
        this.book = book;
        this.key = key;
        this.lut = System.currentTimeMillis();
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public long getLut() {
        return lut;
    }

    public void setLut(long lut) {
        this.lut = lut;
    }
    

}
