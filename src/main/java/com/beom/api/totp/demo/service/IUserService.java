package com.beom.api.totp.demo.service;

import com.beom.api.totp.demo.dal.entity.User;

import java.util.Optional;

/**
 * Interface for User service.
 * Defines methods to manage user operations.
 *
 * @author beom
 * @since 2024/03/16
 */
public interface IUserService {

    /**
     * Creates a new user.
     *
     * @param user The user to create.
     * @return An optional containing the created user if successful, otherwise empty.
     */
    Optional<User> createUser(User user);

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return An optional containing the user with the specified ID if found, otherwise empty.
     */
    Optional<User> getUserById(Long id);

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to retrieve.
     * @return An optional containing the user with the specified username if found, otherwise empty.
     */
    Optional<User> getUserByUsername(String username);

    /**
     * Creates a new user.
     *
     * @param id The ID of the user to be updated.
     * @param user The user to create.
     * @return An optional containing the updated user if successful, otherwise empty.
     */
    Optional<User> updateUser(Long id, User user);
}

