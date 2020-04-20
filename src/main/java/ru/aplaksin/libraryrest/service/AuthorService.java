package ru.aplaksin.libraryrest.service;

import org.springframework.stereotype.Service;
import ru.aplaksin.libraryrest.model.Author;
import ru.aplaksin.libraryrest.model.Book;
import ru.aplaksin.libraryrest.model.dto.AuthorDto;
import ru.aplaksin.libraryrest.model.dto.BookDto;
import ru.aplaksin.libraryrest.repository.AuthorRepository;
import ru.aplaksin.libraryrest.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public boolean validateAuthorId(Long authId) {
        Author author = authorRepository.findById(authId).orElse(null);
        return author != null;
    }

    public AuthorDto getAuthorById(Long id) {
        Author author = authorRepository.findById(id).orElse(null);
        return author != null ? AuthorDto.toDto(author, false) : null;
    }

    public List<AuthorDto> getAllAuthors() {
        List<AuthorDto> authorDtos = new ArrayList<>();
        List<Author> authors = authorRepository.findAll();
        authors.forEach(author -> authorDtos.add(AuthorDto.toDto(author, false)));
        return authorDtos;
    }

    public AuthorDto addAuthor(AuthorDto authorDto) {
        Author author = AuthorDto.fromDto(authorDto);
        authorRepository.save(author);
        setBooks(authorDto, author);
        return AuthorDto.toDto(author,false);
    }

    public AuthorDto editAuthor(Long id, AuthorDto authorDto) {
        Author authorEdit = authorRepository.findById(id).orElse(null);
        if (authorEdit == null) return null;
        authorEdit.setLastName(authorDto.getLastName());
        authorEdit.setFirstName(authorDto.getFirstName());
        authorEdit.setMiddleName(authorDto.getMiddleName());
        authorEdit.setFullName(authorDto.getFullName());
        removeBooks(authorEdit);
        setBooks(authorDto, authorEdit);
        authorRepository.save(authorEdit);
        return AuthorDto.toDto(authorEdit,false);
    }

    public boolean deleteAuthor(Long id) {
        Author delAuthor = authorRepository.findById(id).orElse(null);
        if (delAuthor == null) return false;
        authorRepository.delete(delAuthor);
        return true;
    }

    public List<BookDto> getBooksByAuthorId(Long id) {
        Author author = authorRepository.findById(id).orElse(null);
        if (author==null || author.getBooks() == null) return null;
        List<BookDto> bookDtos = new ArrayList<>();
        author.getBooks().forEach(book -> {
            bookDtos.add(BookDto.toDto(book,true));
        });
        return bookDtos;
    }

    private void removeBooks(Author authorEdit) {
        authorEdit.getBooks().forEach(book -> {
            book.setAuthor(null);
            bookRepository.save(book);
        });
        authorEdit.getBooks().clear();
    }

    private void setBooks(AuthorDto authorDto, Author author) {
        if (authorDto.getBookIds() != null) {
            authorDto.getBookIds().forEach(id -> {
                Book book = bookRepository.findById(id).orElse(null);
                author.getBooks().add(book);
                book.setAuthor(author);
                bookRepository.save(book);
            });
        }
    }
}
