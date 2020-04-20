package ru.aplaksin.libraryrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aplaksin.libraryrest.model.Author;
import ru.aplaksin.libraryrest.model.Book;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Long> {
    Book findByName(String name);
    List<Book> findAllByAuthor(Author author);
}
