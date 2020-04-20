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

import static ru.aplaksin.libraryrest.model.dto.BookDto.*;

@Service
public class BookService {
    private BookRepository bookRepository;
    private AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        return book != null ? BookDto.toDto(book, false) : null;
    }

    public BookDto addBook(BookDto bookDto, Long authId) {
        Book book = BookDto.fromDto(bookDto);
        setAuthor(authId, book);
        bookRepository.save(book);
        return BookDto.toDto(book, false);
    }

    public BookDto editBook(Long id, BookDto bookDto, Long authId) {
        Book bookEdit = bookRepository.findById(id).orElse(null);
        if (bookEdit == null) return null;
        bookEdit.setName(bookDto.getName());
        bookEdit.setPages(bookDto.getPages());
        bookEdit.setPublished(bookDto.getPublished());
        setAuthor(authId,bookEdit);
        bookRepository.save(bookEdit);
        return BookDto.toDto(bookEdit, false);
    }

    public boolean deleteBook(Long id) {
        Book delBook = bookRepository.findById(id).orElse(null);
        if (delBook == null) return false;
        bookRepository.delete(delBook);
        return true;
    }

    public List<BookDto> getAllBooks() {
        List<BookDto> bookDtos = new ArrayList<>();
        List<Book> books = bookRepository.findAll();
        books.forEach(book -> bookDtos.add(BookDto.toDto(book, false)));
        return bookDtos;
    }

    public AuthorDto getAuthorByBookId(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book==null || book.getAuthor()==null) return null;
        return BookDto.toDto(book, false).getAuthorDto();
    }

    private void setAuthor(Long authId, Book book) {
        if (authId != null) {
            authorRepository.findById(authId).ifPresent(book::setAuthor);
        }
    }

    public boolean validateBooksIds(AuthorDto authorDto) {
        for (Long id : authorDto.getBookIds()) {
            Book tmp = bookRepository.findById(id).orElse(null);
            if (tmp == null) {
                return false;
            }
        }
        return true;
    }
}
