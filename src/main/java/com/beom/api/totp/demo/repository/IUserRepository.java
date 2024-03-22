package com.beom.api.totp.demo.repository;

import com.beom.api.totp.demo.dal.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User repository interface
 *
 * @author beom
 * @since 2024/03/16
 */
public interface IUserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "mfaInfoList")
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = "mfaInfoList")
    Optional<User> findByUsername(String username);
}
