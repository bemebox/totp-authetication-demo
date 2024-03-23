package com.beom.api.totp.demo.dal.entity;

import com.beom.api.totp.demo.dal.MFAType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Entity {@link MfaInfo} tests
 *
 * @author beom
 * @since 2024/03/16
 */
@SpringBootTest
class MfaInfoTests {

    /**
     * method called before each test execution
     */
    @BeforeEach
    void setupTest() { }

    @Test
    void testMfaInfoEntityFields() {
        // GIVEN
        Long id = 1L;
        MFAType mfaType = MFAType.TOTP;
        String secretKey = "secretKey";
        User user = new User(); // Mock or instantiate User object based on your needs

        // WHEN
        MfaInfo mfaInfo = new MfaInfo(id, mfaType, secretKey, user);

        // THEN
        assertThat(mfaInfo.getId()).isEqualTo(id);
        assertThat(mfaInfo.getMfaType()).isEqualTo(mfaType);
        assertThat(mfaInfo.getSecretKey()).isEqualTo(secretKey);
        assertThat(mfaInfo.getUser()).isEqualTo(user);
    }

    @Test
    void testToStringMethod() {
        // GIVEN
        MfaInfo mfaInfo = new MfaInfo();
        mfaInfo.setId(1L);
        mfaInfo.setMfaType(MFAType.TOTP);
        mfaInfo.setSecretKey("secretKey");
        mfaInfo.setUser(new User());

        // WHEN & THEN
        assertThat(mfaInfo.toString()).contains(
                "id=" + mfaInfo.getId(),
                "mfaType=" + mfaInfo.getMfaType(),
                "secretKey='" + mfaInfo.getSecretKey() + "'"
        );
    }

    @Test
    void testEqualsAndHashCode() {
        // GIVEN
        MfaInfo mfaInfo1 = new MfaInfo(1L, MFAType.TOTP, "secret1", new User());
        MfaInfo mfaInfo2 = new MfaInfo(1L, MFAType.TOTP, "secret1", new User());
        MfaInfo mfaInfo3 = new MfaInfo(2L, MFAType.TOTP, "secret2", new User());

        // WHEN & THEN
        assertThat(mfaInfo1).isEqualTo(mfaInfo2); // Equal objects
        assertThat(mfaInfo1.hashCode()).hasSameHashCodeAs(mfaInfo2.hashCode()); // Equal hash codes
        assertThat(mfaInfo1).isNotEqualTo(mfaInfo3); // Non-equal objects
    }
}

