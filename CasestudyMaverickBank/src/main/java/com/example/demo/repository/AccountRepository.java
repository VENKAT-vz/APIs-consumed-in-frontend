package com.example.demo.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.Account;
import com.example.demo.domain.User;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

	List<Account> findByUsername(String username);
	
	Optional<Account> findByAccountNumber(String accountNumber);

    @Query("SELECT a.balance FROM Account a WHERE a.accountNumber = :accountNumber")
    double findBalanceByAccountNumber(String accountNumber);

    @Query("SELECT a.balance FROM Account a WHERE a.user.username = :username")
    double findBalanceByUsername(String username);
    
    @Query("SELECT MAX(a.accountNumber) FROM Account a")
    String findMaxAccountNumber();
    
    @Query("SELECT a FROM Account a JOIN User u ON a.username = u.username "
    	       + "WHERE u.contactNumber = :contactnumber AND a.status = 'active' ORDER BY a.dateCreated ASC")
    List<Account> findActiveAccountsByContactNumber(@Param("contactnumber") String contactnumber);

    
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.balance = a.balance + :amount WHERE a.accountNumber = :accountNumber")
    void depositAmount(String accountNumber, double amount);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.balance = a.balance - :amount WHERE a.accountNumber = :accountNumber")
    void withdrawAmount(String accountNumber, double amount);
}
