package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.jboss.logging.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.domain.Installments;

public interface InstallmentsRepository extends JpaRepository<Installments, Integer>{

    Optional<Installments> findByLoanIdAndInstallmentStatus(int loanId, String status);

    Optional<Installments> findFirstByLoanIdOrderByPaymentDateDesc(int loanId);
    
    List<Installments> findByInstallmentStatus(String status);

    @Query("SELECT i FROM Installments i WHERE i.loanId = :loanId AND i.installmentStatus = 'Pending'")
    List<Installments> findByLoanIdAndInstallmentStatusPending( int loanId);


}