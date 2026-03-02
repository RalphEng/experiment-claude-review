package com.queomedia.experiment.reviewexample.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queomedia.experiment.reviewexample.domain.Book;
import com.queomedia.experiment.reviewexample.exception.DuplicateResourceException;
import com.queomedia.experiment.reviewexample.exception.ResourceNotFoundException;
import com.queomedia.experiment.reviewexample.repository.BookRepository;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book createBook(Book book) {
        bookRepository.findByIsbn(book.getIsbn()).ifPresent(existing -> {
            throw new DuplicateResourceException("A book with this ISBN already exists");
        });
        return bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }
}
