package ru.aplaksin.libraryrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aplaksin.libraryrest.model.Author;
import ru.aplaksin.libraryrest.model.Book;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author,Long> {
    Author findByFullName(String fullName);
    Author findByBooksContains(Book book);
}
