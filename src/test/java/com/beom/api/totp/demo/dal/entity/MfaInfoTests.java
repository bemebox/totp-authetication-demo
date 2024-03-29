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
}

