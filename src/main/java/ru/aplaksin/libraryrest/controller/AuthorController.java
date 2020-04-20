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
@RequestMapping("/author")
public class AuthorController {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthor(@PathVariable Long id) throws ResourceNotFoundException {
        AuthorDto authorDto = authorService.getAuthorById(id);
        if (authorDto == null) throw new ResourceNotFoundException(String.format("Author not found for this id : %s", id));
        return new ResponseEntity<>(authorDto, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AuthorDto>> getAllAuthors() {
        return new ResponseEntity<>(authorService.getAllAuthors(), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<AuthorDto> addAuthor(@RequestBody AuthorDto authorDto) throws BadDtoException {
        if (!AuthorDto.validDto(authorDto)) throw new BadDtoException("AuthorDto data is inconsistent");
        if (authorDto.getBookIds() != null && !bookService.validateBooksIds(authorDto))
            throw new BadDtoException("AuthorDto books id data is inconsistent");
        return new ResponseEntity<>(authorService.addAuthor(authorDto), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> editAuthor(@RequestBody AuthorDto authorDto, @PathVariable Long id)
            throws BadDtoException, ResourceNotFoundException {
        if (!AuthorDto.validDto(authorDto)) throw new BadDtoException("AuthorDto data is inconsistent");
        if (authorDto.getBookIds() != null && !bookService.validateBooksIds(authorDto))
            throw new BadDtoException("AuthorDto books id data is inconsistent");
        AuthorDto res = authorService.editAuthor(id, authorDto);
        if (res == null) throw new ResourceNotFoundException(String.format("Author not found with this id : %s", id));
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteAuthor(@PathVariable Long id) throws ResourceNotFoundException {
        boolean val = authorService.deleteAuthor(id);
        if (!val) throw new ResourceNotFoundException(String.format("Cannot delete. Author not found for this id : %s",id));
        return new ResponseEntity<>(true,HttpStatus.OK);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<List<BookDto>> getBooksByAuthorId(@PathVariable Long id) throws ResourceNotFoundException {
        List<BookDto> bookDtos = authorService.getBooksByAuthorId(id);
        if (bookDtos == null) throw new ResourceNotFoundException(String.format("Books not found for this author id : %s",id));
        return new ResponseEntity<>(bookDtos, HttpStatus.OK);
    }
}
