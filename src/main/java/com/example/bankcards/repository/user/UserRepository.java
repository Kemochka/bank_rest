package com.example.bankcards.repository.user;

import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional
    @Modifying
    @Query("""
            update User u
                    set u.login = :#{#user.login},
                    u.username = :#{#user.username}
                    where u.id=:#{#user.id}
            """)
    int update(@Param("user") User user);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByLogin(String login);
}
