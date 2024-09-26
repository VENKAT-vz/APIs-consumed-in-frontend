package com.example.demo.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.Login;
import com.example.demo.domain.NewLoan;

@Repository
public interface LoginRepository extends JpaRepository<Login,String>{

    Login findByUsername(String username);
    Login findByEmailid(String emailid);
    
    @Query("SELECT COUNT(l) FROM Login l WHERE l.username = :username  AND l.role = :role")
    int authenticate(@Param("username") String username, @Param("role") String role);
    
}
