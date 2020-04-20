package ru.aplaksin.libraryrest.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.aplaksin.libraryrest.model.Book;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private Long id;
    private String name;
    private int pages;
    private String published;
    private Long authorId;
    private AuthorDto authorDto;

    public static boolean validDto(BookDto bookDto) {
        return bookDto.getName() != null && bookDto.getPages() > 0 && bookDto.getPublished() != null;
    }

    public static Book fromDto(BookDto bookDto) {
        Book book = new Book();
        book.setName(bookDto.getName());
        book.setPages(bookDto.getPages());
        book.setPublished(bookDto.getPublished());
        return book;
    }

    public static BookDto toDto(Book book, boolean forAuthorDto) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getBookId());
        bookDto.setName(book.getName());
        bookDto.setPages(book.getPages());
        bookDto.setPublished(book.getPublished());
        if (book.getAuthor() != null) {
            bookDto.setAuthorId(book.getAuthor().getAuthId());
            if (!forAuthorDto) bookDto.setAuthorDto(AuthorDto.toDto(book.getAuthor(), true));
        }
        return bookDto;
    }


}
