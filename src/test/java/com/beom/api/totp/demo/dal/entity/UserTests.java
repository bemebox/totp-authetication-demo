package com.beom.api.totp.demo.dal.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Entity {@link User} tests
 *
 * @author beom
 * @since 2024/03/16
 */
@SpringBootTest
class UserTests {

    /**
     * method called before each test execution
     */
    @BeforeEach
    void setupTest() { }

    @Test
    void testUserEntityFields() {
        // GIVEN
        Long id = 1L;
        String username = "testuser";
        String email = "testuser@example.com";
        String password = "password";
        List<MfaInfo> mfaInfoList = new ArrayList<>();

        // WHEN
        User user = new User(id, username, email, password, mfaInfoList);

        // THEN
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getMfaInfoList()).isEqualTo(mfaInfoList);
    }

    @Test
    void testUserEntityToString() {
        // GIVEN
        Long id = 1L;
        String username = "testuser";
        String email = "testuser@example.com";
        String password = "password";
        List<MfaInfo> mfaInfoList = new ArrayList<>();

        // WHEN
        User user = new User(id, username, email, password, mfaInfoList);

        // THEN
        assertThat(user.toString()).contains(
                "id=" + id,
                "username=" + username,
                "email=" + email,
                "password=" + password,
                "mfaInfoList=" + mfaInfoList
        );
    }

    @Test
    void testSetterMethods() {
        // GIVEN
        User user = new User();

        // WHEN
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");

        // THEN
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("testuser@example.com");
        assertThat(user.getPassword()).isEqualTo("password");
    }

    @Test
    void testOneToManyRelationship() {
        // GIVEN
        User user = new User();
        MfaInfo mfaInfo = new MfaInfo();
        mfaInfo.setUser(user);

        // WHEN
        user.getMfaInfoList().add(mfaInfo);

        // THEN
        assertThat(user.getMfaInfoList()).containsExactly(mfaInfo);
        assertThat(mfaInfo.getUser()).isEqualTo(user);
    }

    @Test
    void testEqualsAndHashCode() {
        // GIVEN
        Long id = 1L;
        String username = "testuser";
        String email = "testuser@example.com";
        String password = "password";
        List<MfaInfo> mfaInfoList = new ArrayList<>();

        User user1 = new User(id, username, email, password, mfaInfoList);
        User user2 = new User(id, username, email, password, mfaInfoList);
        User user3 = new User(2L, username, email, password, mfaInfoList);

        // WHEN & THEN
        assertThat(user1).isEqualTo(user2); // Equality test
        assertThat(user1.hashCode()).hasSameHashCodeAs(user2.hashCode()); // Hash code test
        assertThat(user1).isNotEqualTo(user3); // Inequality test
    }
}
