package com.beom.api.totp.demo.service;

import com.beom.api.totp.demo.dal.entity.User;
import com.beom.api.totp.demo.exception.UserNotCreatedException;
import com.beom.api.totp.demo.exception.UserNotFoundException;
import com.beom.api.totp.demo.exception.UserNotUpdatedException;
import com.beom.api.totp.demo.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for manage the account operations.
 *
 * @author beom
 * @see IUserService
 * @since 2024/03/16
 */
@Slf4j
@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> createUser(User user) {
        try {
            return Optional.of(userRepository.save(user));
        } catch (Exception exception) {
            log.error("Unable to create the user.", exception);
            throw new UserNotCreatedException("Unable to create the user.", exception);
        }
    }

    @Override
    public Optional<User> getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("No user found by id " + id);
        }
        return optionalUser;
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("No user found by username " + username);
        }
        return optionalUser;
    }

    @Override
    public Optional<User> updateUser(Long id, User user) {
        try {
            user.setId(id); // Ensure the ID is set for update operation
            return Optional.of(userRepository.save(user));
        } catch (Exception exception) {
            log.error("Unable to update the user.", exception);
            throw new UserNotUpdatedException("Unable to update the user.", exception);
        }
    }
}
