package com.dev.aes.repository;

import com.dev.aes.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByPhoneNo(String phoneNo);

    List<User> findAllByParent(User currentuser);

    @Query(value = "select * from users where email = :useremail", nativeQuery = true)
    User findUserByEmail(String useremail);
}
