package ru.aplaksin.libraryrest.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.aplaksin.libraryrest.model.Author;
import ru.aplaksin.libraryrest.model.Book;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {
    private Long authId;
    private String lastName;
    private String firstName;
    private String middleName;
    private String fullName;
    private List<BookDto> bookDtos = new ArrayList<>();
    private List<Long> bookIds = new ArrayList<>();

    public static boolean validDto(AuthorDto authorDto) {
        return authorDto.lastName != null && authorDto.firstName != null && authorDto.middleName != null
                && authorDto.fullName != null;
    }

    public static AuthorDto toDto(Author author, boolean forBookDto) {
        AuthorDto authorDto = getBasicDto(author);
        if (!author.getBooks().isEmpty()) {
            if (forBookDto) {
                authorDto.setBookIds(getBookDtosIds(author.getBooks()));
            } else {
                authorDto.setBookDtos(getBookDtos(author.getBooks()));
            }
        }
        return authorDto;
    }

    public static Author fromDto(AuthorDto authorDto) {
        Author author = new Author();
        author.setLastName(authorDto.getLastName());
        author.setFirstName(authorDto.getFirstName());
        author.setMiddleName(authorDto.getMiddleName());
        author.setFullName(authorDto.getFullName());
        return author;
    }

    private static List<BookDto> getBookDtos(List<Book> list) {
        List<BookDto> bookDtos = new ArrayList<>();
        list.forEach(book -> bookDtos.add(BookDto.toDto(book, true)));
        return bookDtos;
    }

    private static List<Long> getBookDtosIds(List<Book> list) {
        List<Long> bookIds = new ArrayList<>();
        list.forEach(book -> bookIds.add(book.getBookId()));
        return bookIds;
    }

    private static AuthorDto getBasicDto(Author author) {
        AuthorDto authorDto = new AuthorDto();
        authorDto.setAuthId(author.getAuthId());
        authorDto.setLastName(author.getLastName());
        authorDto.setFirstName(author.getFirstName());
        authorDto.setMiddleName(author.getMiddleName());
        authorDto.setFullName(author.getFullName());
        return  authorDto;
    }
}
