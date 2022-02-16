package com.example.login.repository;

import com.example.login.model.Role;
import com.example.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("update User u set u.password = :password where u.id = :id")
    void updatePassword(String password, int id);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.enabled = TRUE WHERE u.email = :email")
    void enableUser(String email);


    //@Transactional
    Long countById(Integer id);


    @Query("select u from User u WHERE u.email = (?1)")
    User findUserByEmail(String userEmail);

    List<User> findAllByRole(Role role);

    int countByRole(Role role);

    boolean existsByEmail(String keyword);

    @Query("select u from User u WHERE u.verificationCodeCheckout = (?1)")
    User findByVerificationCodeCheckout(String code);
}
