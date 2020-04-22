package ru.aplaksin.libraryrest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.aplaksin.libraryrest.exception.BadDtoException;
import ru.aplaksin.libraryrest.exception.ResourceNotFoundException;
import ru.aplaksin.libraryrest.model.Author;
import ru.aplaksin.libraryrest.model.Book;
import ru.aplaksin.libraryrest.model.dto.AuthorDto;
import ru.aplaksin.libraryrest.model.dto.BookDto;
import ru.aplaksin.libraryrest.service.AuthorService;
import ru.aplaksin.libraryrest.service.BookService;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @Nested
    @DisplayName("get book by id")
    class GetBookByIdTest {

        @Test
        @DisplayName("When get book without author")
        void getBookByIdClearTest() throws Exception {
            Book book = new Book();
            book.setBookId(1L);
            book.setName("War and peace");
            book.setPublished("1887");
            book.setPages(654);

            when(bookService.getBookById(anyLong())).thenReturn(BookDto.toDto(book, false));

            mockMvc.perform(MockMvcRequestBuilders.get("/book/1"))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("War and peace"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.published").value("1887"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.pages").value("654"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("When get book with author")
        void getBookByIdTest() throws Exception {
            Book book = new Book();
            book.setBookId(2L);
            book.setName("Evgeniy Onegin");
            book.setPublished("1887");
            book.setPages(654);

            Author author = new Author();
            author.setAuthId(2L);
            author.setLastName("Pushkin");
            author.setFirstName("Aleksandr");
            author.setMiddleName("Sergeevich");
            author.setFullName("Pushkin Aleksandr Sergeevich");

            book.setAuthor(author);

            when(bookService.getBookById(anyLong())).thenReturn(BookDto.toDto(book, false));

            mockMvc.perform(MockMvcRequestBuilders.get("/book/1"))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Evgeniy Onegin"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.published").value("1887"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.pages").value("654"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.authorDto.authId").value("2"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.authorDto.lastName").value("Pushkin"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.authorDto.firstName").value("Aleksandr"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.authorDto.middleName").value("Sergeevich"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.authorDto.fullName").value("Pushkin Aleksandr Sergeevich"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("When throw ResourceNotFoundException in getBookById")
    void getBookWrongIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/book/100"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Nested
    @DisplayName("get all books")
    class GetAllBooks {
        @Test
        @DisplayName("When get all books")
        void getAllBooks() throws Exception {
            Book book = new Book();
            book.setBookId(1L);
            book.setName("War and peace");
            book.setPublished("1887");
            book.setPages(654);

            Book book2 = new Book();
            book2.setBookId(2L);
            book2.setName("Evgeniy Onegin");
            book2.setPublished("1887");
            book2.setPages(654);

            Author author = new Author();
            author.setAuthId(2L);
            author.setLastName("Pushkin");
            author.setFirstName("Aleksandr");
            author.setMiddleName("Sergeevich");
            author.setFullName("Pushkin Aleksandr Sergeevich");

            book2.setAuthor(author);

            when(bookService.getAllBooks())
                    .thenReturn(new ArrayList<>(Arrays.asList(
                            BookDto.toDto(book,false),
                            BookDto.toDto(book2,false))));

            mockMvc.perform(MockMvcRequestBuilders.get("/book/all"))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.*",hasSize(2)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("War and peace"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("Evgeniy Onegin"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[1].authorId").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[1].authorDto.authId").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.[1].authorDto.lastName").value("Pushkin"))
                    .andExpect(status().isOk());

        }
    }

    @Nested
    @DisplayName("Add new book")
    class AddBookTest {
        @Test
        @DisplayName("Add new book without author")
        void addBookClearTest() throws Exception {
            Book book = new Book();
            book.setName("War and peace");
            book.setPublished("1887");
            book.setPages(654);

            BookDto bookDto = BookDto.toDto(book,false);

            when(bookService.addBook(any(BookDto.class),any()))
                    .thenReturn(bookDto);

            mockMvc.perform(post("/book/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(bookDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("War and peace"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.pages").value("654"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.published").value("1887"));
        }

        @Test
        @DisplayName("Add new book with author")
        void addBookAuthTest() throws Exception {
            Book book = new Book();
            book.setName("War and peace");
            book.setPublished("1887");
            book.setPages(654);

            Author author = new Author();
            author.setAuthId(1L);
            author.setLastName("Pushkin");
            author.setFirstName("Aleksandr");
            author.setMiddleName("Sergeevich");
            author.setFullName("Pushkin Aleksandr Sergeevich");

            book.setAuthor(author);

            BookDto bookDto = BookDto.toDto(book,false);

            when(bookService.addBook(any(BookDto.class),anyLong()))
                    .thenReturn(bookDto);
            when(authorService.validateAuthorId(anyLong()))
                    .thenReturn(true);

            mockMvc.perform(post("/book/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(bookDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("War and peace"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.pages").value("654"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.published").value("1887"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.authorId").value("1"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.authorDto.fullName").value("Pushkin Aleksandr Sergeevich"));
        }
    }


        @Test
        @DisplayName("When throw BadDtoException in addBook")
        void addBookWrongDtoTest() throws Exception {
            Book book = new Book();
            book.setPublished("1887");
            book.setPages(654);

            BookDto bookDto = BookDto.toDto(book,false);

            mockMvc.perform(post("/book/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(bookDto)))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadDtoException));
        }

        @Test
        @DisplayName("When throw ResourceNotFoundException in addBook")
        void addBookWrongAuthId() throws Exception {
            Book book = new Book();
            book.setName("War and peace");
            book.setPublished("1887");
            book.setPages(654);

            BookDto bookDto = BookDto.toDto(book,false);

            mockMvc.perform(post("/book/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(bookDto)))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
        }


    @Nested
    @DisplayName("Edit book")
    class EditBookTest {
        @Test
        @DisplayName("Edit book without author")
        void editBookClear() throws Exception {
            Book book = new Book();
            book.setBookId(1L);
            book.setName("War and peace");
            book.setPublished("1887");
            book.setPages(654);

            BookDto bookDto = BookDto.toDto(book,false);

            when(bookService.editBook(anyLong(),any(BookDto.class),any()))
                    .thenReturn(bookDto);

            mockMvc.perform(put("/book/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(bookDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("War and peace"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.pages").value("654"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.published").value("1887"));
        }

        @Test
        @DisplayName("Edit book with author")
        void editBookWithAuthor() throws Exception {
            Book book = new Book();
            book.setBookId(1L);
            book.setName("War and peace");
            book.setPublished("1887");
            book.setPages(654);

            Author author = new Author();
            author.setAuthId(1L);
            author.setLastName("Pushkin");
            author.setFirstName("Aleksandr");
            author.setMiddleName("Sergeevich");
            author.setFullName("Pushkin Aleksandr Sergeevich");

            book.setAuthor(author);

            BookDto bookDto = BookDto.toDto(book,false);

            when(bookService.editBook(anyLong(),any(BookDto.class),any()))
                    .thenReturn(bookDto);
            when(authorService.validateAuthorId(anyLong()))
                    .thenReturn(true);

            mockMvc.perform(put("/book/1/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(bookDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("War and peace"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.pages").value("654"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.published").value("1887"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.authorId").value("1"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.authorDto.authId").value("1"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.authorDto.firstName").value("Aleksandr"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.authorDto.lastName").value("Pushkin"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.authorDto.middleName").value("Sergeevich"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.authorDto.fullName").value("Pushkin Aleksandr Sergeevich"));
        }
    }


        @Test
        @DisplayName("When throw BadDtoException in editBook")
        void editBookWrongDtoTest() throws Exception {
            Book book = new Book();
            book.setBookId(1L);
            book.setPublished("1887");
            book.setPages(654);

            BookDto bookDto = BookDto.toDto(book,false);

            mockMvc.perform(put("/book/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(bookDto)))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadDtoException));
        }

        @Test
        @DisplayName("When throw ResourceNotFoundException in editBook with wrong authId")
        void editBookWrongAuthId() throws Exception {
            Book book = new Book();
            book.setName("War and peace");
            book.setPublished("1887");
            book.setPages(654);

            BookDto bookDto = BookDto.toDto(book,false);

            mockMvc.perform(put("/book/1/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(bookDto)))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
        }

        @Test
        @DisplayName("When throw ResourceNotFoundException in editBook with wrong bookId")
        void editBookWrongBookId() throws Exception {
            Book book = new Book();
            book.setBookId(1L);
            book.setName("War and peace");
            book.setPublished("1887");
            book.setPages(654);

            BookDto bookDto = BookDto.toDto(book,false);

            when(bookService.editBook(anyLong(),any(BookDto.class),anyLong()))
                    .thenReturn(null);
            when(authorService.validateAuthorId(anyLong()))
                    .thenReturn(true);

            mockMvc.perform(put("/book/2/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(bookDto)))
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
        }


    @Nested
    @DisplayName("Delete book")
    class DeleteBookTest {
        @Test
        @DisplayName("Delete book by id")
        void deleteBook() throws Exception {
            when(bookService.deleteBook(anyLong()))
                    .thenReturn(true);
            mockMvc.perform(delete("/book/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }
    }

    @Test
    @DisplayName("Delete book with wrong id")
    void deleteBookException() throws Exception {
        when(bookService.deleteBook(anyLong()))
                .thenReturn(false);
        mockMvc.perform(delete("/book/1"))
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }


    @Nested
    @DisplayName("GetAuthorByBookId")
    class GetAuthorByBookIdTest {
        @Test
        @DisplayName("Get Author by book id")
        void getAuthorByBookId() throws Exception {
            Book book = new Book();
            book.setBookId(1L);
            book.setPages(222);
            book.setPublished("1885");
            book.setName("Onegin");

            Author author = new Author();
            author.setAuthId(1L);
            author.setLastName("Pushkin");
            author.setFirstName("Aleksandr");
            author.setMiddleName("Sergeevich");
            author.setFullName("Pushkin Aleksandr Sergeevich");

            book.setAuthor(author);

            author.setBooks(new ArrayList<>(Arrays.asList(book)));

            when(bookService.getAuthorByBookId(anyLong())).thenReturn(AuthorDto.toDto(author,true));

            mockMvc.perform(get("/book/author/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.authId").value("1"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Aleksandr"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Pushkin"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.middleName").value("Sergeevich"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("Pushkin Aleksandr Sergeevich"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.bookIds.[0]").value("1"));
        }
    }

    @Test
    @DisplayName("Delete book with wrong book id or author is not exist")
    void getAuthorByBookIdException() throws Exception {
        when(bookService.getAuthorByBookId(anyLong()))
                .thenReturn(null);
        mockMvc.perform(get("/book/author/1"))
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }
}
