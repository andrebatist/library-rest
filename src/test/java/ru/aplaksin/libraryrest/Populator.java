package ru.aplaksin.libraryrest;

import ru.aplaksin.libraryrest.model.Author;
import ru.aplaksin.libraryrest.model.Book;

public class Populator {
    public static Book populateBookWithoutAuthor() {
        Book book = new Book();
        book.setBookId(1L);
        book.setName("1984");
        book.setPublished("1949");
        book.setPages(349);
        return book;
    }

    public static Book populateBookWithAuthor() {
        Book book = populateBookWithoutAuthor();
        Author author = populateAuthorWithoutBooks();
        book.setAuthor(author);
        return book;
    }

    public static Author populateAuthorWithoutBooks() {
        Author author = new Author();
        author.setAuthId(1L);
        author.setLastName("Orwell");
        author.setFirstName("George");
        author.setMiddleName("John");
        author.setFullName("Orwell George John");
        return author;
    }
}
