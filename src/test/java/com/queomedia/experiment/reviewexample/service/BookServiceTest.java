package com.queomedia.experiment.reviewexample.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.queomedia.experiment.reviewexample.domain.Book;
import com.queomedia.experiment.reviewexample.exception.DuplicateResourceException;
import com.queomedia.experiment.reviewexample.exception.ResourceNotFoundException;
import com.queomedia.experiment.reviewexample.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book("Clean Code", "Robert C. Martin", "978-0132350884", 2008);
        testBook.setId(1L);
    }

    @Test
    void createBook_shouldSaveAndReturnBook() {
        when(bookRepository.findByIsbn("978-0132350884")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.createBook(testBook);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Clean Code");
        assertThat(result.getIsbn()).isEqualTo("978-0132350884");
    }

    @Test
    void createBook_withDuplicateIsbn_shouldThrowException() {
        when(bookRepository.findByIsbn("978-0132350884")).thenReturn(Optional.of(testBook));

        assertThatThrownBy(() -> bookService.createBook(testBook))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("A book with this ISBN already exists");
    }

    @Test
    void getBookById_shouldReturnBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        Book result = bookService.getBookById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getBookById_withInvalidId_shouldThrowException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Book not found with id: 99");
    }
}
