package stract;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.home.libraryrestapi.model.Book;

public class LibraryLRUCache {

    private final int LRU_CAPACITY = 3;
    private final int TIMER_INTERVAL_IN_MS = 10000;
    private final int TTL_IN_MS = 60000;
  
    private static LibraryLRUCache instance;
    private Node head, tail;
    private HashMap<Long, Node> lruMap;
   
    private Timer agingTimer = new Timer();
    private TimerTask agingTask = new TimerTask() {
        @Override
        public void run() {
            Node lastNode = tail.getPrev();
            if (lastNode.getKey() != Long.MIN_VALUE && System.currentTimeMillis() - lastNode.getLut() >= TTL_IN_MS) {
                deleteBookById(lastNode.getKey());
            }

        }
    };
    private int cacheCount;

    // #region Constractors

    private LibraryLRUCache() {
        lruMap = new HashMap<>();
        head = new Node(null, Long.MIN_VALUE);
        tail = new Node(null, Long.MIN_VALUE);
        head.setNext(tail);
        head.setPrev(null);
        tail.setPrev(head);
        tail.setNext(null);
        cacheCount = 0;
        agingTimer.schedule(agingTask,0, TIMER_INTERVAL_IN_MS);
    }
    // #endregion

    public static LibraryLRUCache getInstance() {
        if (instance == null)
            instance = new LibraryLRUCache();
        return instance;
    }

    // #region Private methods
    private void deleteNode(Node node) {
        node.getPrev().setNext(node.getNext());
        node.getNext().setPrev(node.getPrev());
    }

    private void pushNodeToHead(Node node) {
        node.setNext(head.getNext());
        node.setPrev(head);
        head.getNext().setPrev(node);
        head.setNext(node);
    }
    // #endregion

    // #region Public methods

    public Book getBookById(Long bookId) {
        Node node = lruMap.get(bookId);
        if (node != null) {
            Book book = new Book(node.getBook());
            deleteNode(node);
            pushNodeToHead(node);
            node.setLut(System.currentTimeMillis());
            return book;
        }
        return null;
    }

    public void addBook(Book book) {
        Node node = new Node(book, book.getId());
        lruMap.put(book.getId(), node);
        if (cacheCount < LRU_CAPACITY) {
            cacheCount++;
            pushNodeToHead(node);
        } else {
            lruMap.remove(tail.getPrev().getKey());
            deleteNode(tail.getPrev());
            pushNodeToHead(node);
        }

    }

    public void deleteBookById(Long id) {
        Node node = lruMap.get(id);
        if (node != null) {
            lruMap.remove(id);
            deleteNode(node);
            cacheCount--;
        }
    }

    // #endregion

}
