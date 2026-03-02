package com.queomedia.experiment.reviewexample.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queomedia.experiment.reviewexample.domain.Book;
import com.queomedia.experiment.reviewexample.domain.Loan;
import com.queomedia.experiment.reviewexample.domain.Member;
import com.queomedia.experiment.reviewexample.exception.BusinessRuleException;
import com.queomedia.experiment.reviewexample.exception.ResourceNotFoundException;
import com.queomedia.experiment.reviewexample.repository.BookRepository;
import com.queomedia.experiment.reviewexample.repository.LoanRepository;
import com.queomedia.experiment.reviewexample.repository.MemberRepository;

@Service
@Transactional
public class LoanService {

    private static final int MAX_LOANS = 3;
    private static final int LOAN_PERIOD_DAYS = 14;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository,
            MemberRepository memberRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    public Loan createLoan(Long bookId, Long memberId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        List<Loan> activeLoans = loanRepository.findByMemberIdAndReturnDateIsNull(memberId);
        if (activeLoans.size() > MAX_LOANS) {
            throw new BusinessRuleException("Member has reached the maximum of " + MAX_LOANS + " loans");
        }

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(book, member, today, today.plusDays(28));

        book.setAvailable(false);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));

        loan.setReturnDate(LocalDate.now());

        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    @Transactional(readOnly = true)
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Loan> getLoansByMember(Long memberId) {
        return loanRepository.findByMemberIdAndReturnDateIsNull(memberId);
    }
}
