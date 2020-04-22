package ru.aplaksin.libraryrest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.Null;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.aplaksin.libraryrest.Populator;
import ru.aplaksin.libraryrest.exception.ResourceNotFoundException;
import ru.aplaksin.libraryrest.model.Author;
import ru.aplaksin.libraryrest.model.Book;
import ru.aplaksin.libraryrest.model.dto.AuthorDto;
import ru.aplaksin.libraryrest.model.dto.BookDto;
import ru.aplaksin.libraryrest.repository.AuthorRepository;
import ru.aplaksin.libraryrest.repository.BookRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    private BookService bookService;

    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;

    @BeforeEach
    void initBookService() {bookService = new BookService(bookRepository,authorRepository);}

    @Test
    @DisplayName("get clear book by id")
    void getBookClearById() {
        Book book = Populator.populateBookWithoutAuthor();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        BookDto bookDto = bookService.getBookById(1L);
        assertAll(
                () -> assertEquals(1L,bookDto.getId()),
                () -> assertEquals("1984",bookDto.getName()),
                () -> assertEquals("1949",bookDto.getPublished()),
                () -> assertEquals(349,bookDto.getPages())
        );
    }

    @Test
    @DisplayName("get book with author by id")
    void getBookWithAuthorById() {
        Book book = Populator.populateBookWithAuthor();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        BookDto bookDto = bookService.getBookById(1L);
        assertAll(
                () -> assertEquals(1L,bookDto.getId()),
                () -> assertEquals("1984",bookDto.getName()),
                () -> assertEquals("1949",bookDto.getPublished()),
                () -> assertEquals(349,bookDto.getPages()),
                () -> assertEquals(1L,bookDto.getAuthorId()),
                () -> assertEquals(1L,bookDto.getAuthorDto().getAuthId()),
                () -> assertEquals("Orwell",bookDto.getAuthorDto().getLastName()),
                () -> assertEquals("George",bookDto.getAuthorDto().getFirstName()),
                () -> assertEquals("John",bookDto.getAuthorDto().getMiddleName()),
                () -> assertEquals("Orwell George John",bookDto.getAuthorDto().getFullName())
        );
    }

    @Test
    @DisplayName("When return null in getBookById")
    void getBookWrongIdTest() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        BookDto bookDto = bookService.getBookById(1L);
        assertNull(bookDto, "value must be null");
    }

    @Test
    @DisplayName("add clear book")
    void addClearBook() {
        Book book = Populator.populateBookWithoutAuthor();
        when(bookRepository.save(any(Book.class))).then(returnsFirstArg());
        BookDto bookDto = bookService.addBook(BookDto.toDto(book,false),null);
        assertAll(
                () -> assertEquals("1984",bookDto.getName()),
                () -> assertEquals("1949",bookDto.getPublished()),
                () -> assertEquals(349,bookDto.getPages())
        );
    }

    @Test
    @DisplayName("add book with author")
    void addBookWithAuthor() {
        Book book = Populator.populateBookWithoutAuthor();
        Author author = Populator.populateAuthorWithoutBooks();
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).then(returnsFirstArg());
        BookDto bookDto = bookService.addBook(BookDto.toDto(book,false),anyLong());
        assertAll(
                () -> assertEquals("1984",bookDto.getName()),
                () -> assertEquals("1949",bookDto.getPublished()),
                () -> assertEquals(349,bookDto.getPages()),
                () -> assertEquals(1L,bookDto.getAuthorId()),
                () -> assertEquals(1L,bookDto.getAuthorDto().getAuthId()),
                () -> assertEquals("Orwell",bookDto.getAuthorDto().getLastName()),
                () -> assertEquals("George",bookDto.getAuthorDto().getFirstName()),
                () -> assertEquals("John",bookDto.getAuthorDto().getMiddleName()),
                () -> assertEquals("Orwell George John",bookDto.getAuthorDto().getFullName())
        );
    }

    @Test
    @DisplayName("edit book without author")
    void editBookClear() {
        Book book = Populator.populateBookWithoutAuthor();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).then(returnsFirstArg());
        BookDto bookDto = bookService.editBook(anyLong(), BookDto.toDto(book,false), null);
        assertAll(
                () -> assertEquals("1984",bookDto.getName()),
                () -> assertEquals("1949",bookDto.getPublished()),
                () -> assertEquals(349,bookDto.getPages())
        );
    }

    @Test
    @DisplayName("edit book with author")
    void editBookWithAuthor() {
        Book book = Populator.populateBookWithoutAuthor();
        Author author = Populator.populateAuthorWithoutBooks();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(authorRepository.findById(any())).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).then(returnsFirstArg());
        BookDto bookDto = bookService.editBook(anyLong(),BookDto.toDto(book, false), 1L);
        assertAll(
                () -> assertEquals("1984",bookDto.getName()),
                () -> assertEquals("1949",bookDto.getPublished()),
                () -> assertEquals(349,bookDto.getPages()),
                () -> assertEquals(1L,bookDto.getAuthorId()),
                () -> assertEquals(1L,bookDto.getAuthorDto().getAuthId()),
                () -> assertEquals("Orwell",bookDto.getAuthorDto().getLastName()),
                () -> assertEquals("George",bookDto.getAuthorDto().getFirstName()),
                () -> assertEquals("John",bookDto.getAuthorDto().getMiddleName()),
                () -> assertEquals("Orwell George John",bookDto.getAuthorDto().getFullName())
        );
    }

    @Test
    @DisplayName("When return null in editBook")
    void editBookWrongIdTest() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        BookDto bookDto = bookService.editBook(3L,any(BookDto.class),null);
        assertNull(bookDto, "value must be null");
    }

    @Test
    @DisplayName("delete book")
    void deleteBook() {
        Book book = Populator.populateBookWithoutAuthor();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        bookService.deleteBook(anyLong());
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    @DisplayName("delete book with wrong then null")
    void deleteBookFalse() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertFalse(bookService.deleteBook(anyLong()));
    }

    @Test
    @DisplayName("get all books")
    void getAllBooks() {
        Book book = Populator.populateBookWithoutAuthor();
        Book book2 = Populator.populateBookWithAuthor();
        when(bookRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(book,book2)));
        List<BookDto> bookDtos = bookService.getAllBooks();
        assertEquals(2,bookDtos.size());
        assertEquals("1984",bookDtos.get(0).getName());
        assertEquals("Orwell George John",bookDtos.get(1).getAuthorDto().getFullName());
    }

    @Test
    @DisplayName("get author by book id")
    void getAuthorByBookId() {
        Book book = Populator.populateBookWithAuthor();
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        AuthorDto authorDto = bookService.getAuthorByBookId(anyLong());
        assertAll(
                () -> assertEquals(1L, authorDto.getAuthId()),
                () -> assertEquals("Orwell",authorDto.getLastName()),
                () -> assertEquals("George",authorDto.getFirstName()),
                () -> assertEquals("John",authorDto.getMiddleName()),
                () -> assertEquals("Orwell George John",authorDto.getFullName())
        );
    }

    @Test
    @DisplayName("get author by wrong book id then null")
    void getAuthorByBookNull() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
        AuthorDto authorDto = bookService.getAuthorByBookId(anyLong());
        assertNull(authorDto, "value must be null");
    }
}
