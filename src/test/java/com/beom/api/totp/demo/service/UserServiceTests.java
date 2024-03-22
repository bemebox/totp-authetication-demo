package com.beom.api.totp.demo.service;

import com.beom.api.totp.demo.dal.entity.User;
import com.beom.api.totp.demo.exception.UserNotCreatedException;
import com.beom.api.totp.demo.exception.UserNotFoundException;
import com.beom.api.totp.demo.exception.UserNotUpdatedException;
import com.beom.api.totp.demo.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * {@link UserService} tests class
 *
 * @author beom
 * @since 2024/03/16
 */
@SpringBootTest
class UserServiceTests {

    @Mock
    private IUserRepository userRepository;

    private IUserService userService;

    /**
     * method called before each test execution
     */
    @BeforeEach
    void setupTest() {
        assertThat(userRepository)
                .as("userRepository cannot be null")
                .isNotNull();

        this.userService = new UserService(userRepository);
        assertThat(userService)
                .as("userService cannot be null")
                .isNotNull();
    }

    @Test
    void givenValidUserWhenCreateUserThenReturnUser() {
        // GIVEN
        String userName = "testuser";
        User user = new User();
        user.setUsername(userName);

        // WHEN
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // THEN
        User savedUser = userService.createUser(user).orElseThrow();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(userName);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenInvalidUserWhenCreateUserThenThrowUserNotCreatedException() {
        //GIVEN
        User user = new User();
        user.setUsername("testuser");

        //WHEN
        when(userRepository.save(any(User.class))).thenThrow(RuntimeException.class); // Simulating an exception

        // THEN
        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(UserNotCreatedException.class)
                .hasMessage("Unable to create the user.");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenValidUserIdWhenGetUserByIdThenReturnUser() {
        // GIVEN
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        // WHEN
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // THEN
        User retrievedUser = userService.getUserById(userId).orElseThrow();
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getId()).isEqualTo(userId);

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void givenInvalidUserIdWhenGetUserByIdThenThrowUserNotFoundException() {
        // GIVEN
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("No user found by id " + userId);

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void givenValidUserWhenGetUserByUsernameThenReturnUser() {
        //GIVEN
        String userName = "testuser";
        User user = new User();
        user.setUsername(userName);

        //WHEN
        when(userRepository.findByUsername(userName)).thenReturn(Optional.of(user));

        //THEN
        Optional<User> retrievedUser = userService.getUserByUsername(userName);
        assertThat(retrievedUser).isNotEmpty();
        assertThat(retrievedUser.get().getUsername()).isEqualTo(userName);

        verify(userRepository, times(1)).findByUsername(userName);
    }

    @Test
    void givenInvalidUserIdWhenGetUserByUsernameThenThrowUserNotFoundException() {
        // GIVEN
        String userName = "testuser";
        when(userRepository.findByUsername(userName)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> userService.getUserByUsername(userName))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("No user found by username " + userName);
        verify(userRepository, times(1)).findByUsername(userName);
    }

    @Test
    void givenValidUserWhenUpdateUserThenReturnUser() {
        // GIVEN
        Long userId = 1L;
        String userName = "updatedtestuser";

        User user = new User();
        user.setId(userId);
        user.setUsername(userName);

        // WHEN
        when(userRepository.save(any(User.class))).thenReturn(user);

        // THEN
        User updatedUser = userService.updateUser(userId, user).orElseThrow();
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getId()).isEqualTo(userId);
        assertThat(updatedUser.getUsername()).isEqualTo(userName);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenInvalidUserWhenUpdateUserThenReturnUserNotUpdatedException() {
        // GIVEN
        Long userId = 1L;
        String userName = "updatedtestuser";

        User user = new User();
        user.setId(userId);
        user.setUsername(userName);

        // WHEN
        when(userRepository.save(any(User.class))).thenThrow(RuntimeException.class); // Simulating an exception

        // THEN
        assertThatThrownBy(() -> userService.updateUser(userId, user))
                .isInstanceOf(UserNotUpdatedException.class)
                .hasMessage("Unable to update the user.");

        verify(userRepository, times(1)).save(user);
    }
}
