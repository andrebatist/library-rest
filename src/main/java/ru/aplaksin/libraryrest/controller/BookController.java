package ru.aplaksin.libraryrest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.aplaksin.libraryrest.exception.BadDtoException;
import ru.aplaksin.libraryrest.exception.ResourceNotFoundException;
import ru.aplaksin.libraryrest.model.dto.AuthorDto;
import ru.aplaksin.libraryrest.model.dto.BookDto;
import ru.aplaksin.libraryrest.service.AuthorService;
import ru.aplaksin.libraryrest.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBook(@PathVariable Long id) throws ResourceNotFoundException {
        BookDto bookDto = bookService.getBookById(id);
        if (bookDto == null) throw new ResourceNotFoundException(String.format("Employee not found for this id : %s",id));
        return new ResponseEntity<>(bookDto, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return new ResponseEntity<>(bookService.getAllBooks(), HttpStatus.OK);
    }

    @PostMapping(value = {"/","/{authId}"})
    public ResponseEntity<BookDto> addBook(@RequestBody BookDto bookDto,
                                        @PathVariable(required = false) Long authId) throws BadDtoException, ResourceNotFoundException {
        if (!BookDto.validDto(bookDto)) throw new BadDtoException("BookDto data is inconsistent");
        if (authId != null && !authorService.validateAuthorId(authId))
            throw new ResourceNotFoundException(String.format("Author not found with this id : %s", authId));
        return new ResponseEntity<>(bookService.addBook(bookDto,authId), HttpStatus.OK);
    }

    @PutMapping(value = {"/{id}","/{id}/{authId}"})
    public ResponseEntity<BookDto> editBook(@RequestBody BookDto bookDto, @PathVariable Long id,
                                         @PathVariable(required = false) Long authId) throws BadDtoException, ResourceNotFoundException {
        if (!BookDto.validDto(bookDto)) throw new BadDtoException("BookDto data is inconsistent");
        if (authId != null && !authorService.validateAuthorId(authId))
            throw new ResourceNotFoundException(String.format("Author not found with this id : %s", authId));
        BookDto res = bookService.editBook(id, bookDto, authId);
        if (res == null) throw new ResourceNotFoundException(String.format("Book not found with this id : %s", id));
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteBook(@PathVariable Long id) throws ResourceNotFoundException {
        boolean val = bookService.deleteBook(id);
        if (!val) throw new ResourceNotFoundException(String.format("Cannot delete. Book not found for this id : %s",id));
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @GetMapping("/author/{id}")
    public ResponseEntity<AuthorDto> getAuthorByBookId(@PathVariable Long id) throws ResourceNotFoundException {
        AuthorDto authorDto = bookService.getAuthorByBookId(id);
        if (authorDto == null) throw new ResourceNotFoundException(String.format("Author not found for this book id : %s",id));
        return new ResponseEntity<>(authorDto, HttpStatus.OK);
    }

}
