package com.queomedia.experiment.reviewexample.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.queomedia.experiment.reviewexample.domain.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByMemberIdAndReturnDateIsNull(Long memberId);

    Optional<Loan> findByBookIdAndReturnDateIsNull(Long bookId);
}
