package com.home.libraryrestapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.home.libraryrestapi.model.Book;


@Repository
public interface BookRepository extends JpaRepository<Book,Long>{
    
    
}
