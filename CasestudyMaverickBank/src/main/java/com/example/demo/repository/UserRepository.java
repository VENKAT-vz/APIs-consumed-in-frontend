package com.example.demo.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{

    User findByEmailid(String emailid);
    User findByUsername(String username);
  

    
    @Query("SELECT u FROM User u JOIN Account a ON u.username = a.username WHERE a.accountNumber = :accountNumber")
    User findByAccountno(@Param("accountNumber") String accountNumber);
}


