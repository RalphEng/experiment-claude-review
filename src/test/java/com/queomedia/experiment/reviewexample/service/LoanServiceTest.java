package com.queomedia.experiment.reviewexample.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.queomedia.experiment.reviewexample.domain.Book;
import com.queomedia.experiment.reviewexample.domain.Loan;
import com.queomedia.experiment.reviewexample.domain.Member;
import com.queomedia.experiment.reviewexample.exception.BusinessRuleException;
import com.queomedia.experiment.reviewexample.repository.BookRepository;
import com.queomedia.experiment.reviewexample.repository.LoanRepository;
import com.queomedia.experiment.reviewexample.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private LoanService loanService;

    private Book testBook;
    private Member testMember;

    @BeforeEach
    void setUp() {
        testBook = new Book("Clean Code", "Robert C. Martin", "978-0132350884", 2008);
        testBook.setId(1L);
        testBook.setAvailable(true);

        testMember = new Member("Max Mustermann", "max@example.com");
        testMember.setId(1L);
        testMember.setMemberNumber("MEM-12345678");
    }

    @Test
    void createLoan_shouldCreateLoanSuccessfully() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(loanRepository.findByMemberIdAndReturnDateIsNull(1L)).thenReturn(new ArrayList<>());
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan loan = invocation.getArgument(0);
            loan.setId(1L);
            return loan;
        });

        Loan result = loanService.createLoan(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getBook()).isEqualTo(testBook);
        assertThat(result.getMember()).isEqualTo(testMember);
        assertThat(result.getLoanDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void createLoan_whenMemberAtLimit_shouldThrowException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        List<Loan> activeLoans = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            activeLoans.add(new Loan());
        }
        when(loanRepository.findByMemberIdAndReturnDateIsNull(1L)).thenReturn(activeLoans);

        assertThatThrownBy(() -> loanService.createLoan(1L, 1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Member has reached the maximum of 3 loans");
    }

    @Test
    void returnBook_shouldSetReturnDateAndMakeBookAvailable() {
        Loan testLoan = new Loan(testBook, testMember, LocalDate.now(), LocalDate.now().plusDays(14));
        testLoan.setId(1L);
        testBook.setAvailable(false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = loanService.returnBook(1L);

        assertThat(result.getReturnDate()).isEqualTo(LocalDate.now());
        assertThat(testBook.isAvailable()).isTrue();
    }
}
