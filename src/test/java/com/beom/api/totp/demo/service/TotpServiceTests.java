package com.beom.api.totp.demo.service;

import com.beom.api.totp.demo.dal.MFAType;
import com.beom.api.totp.demo.dal.entity.MfaInfo;
import com.beom.api.totp.demo.dal.entity.User;
import com.beom.api.totp.demo.exception.GenerateQRCodeImageException;
import com.beom.api.totp.demo.exception.InvalidMfaTypeException;
import com.beom.api.totp.demo.exception.InvalidSecretKeyException;
import com.beom.api.totp.demo.exception.UserNotFoundException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * {@link TotpService} tests class
 *
 * @author beom
 * @since 2024/03/16
 */
@SpringBootTest
class TotpServiceTests {

    @Mock
    private IUserService userService;

    private ITotpService totpService;

    /**
     * method called before each test execution
     */
    @BeforeEach
    void setupTest() {
        assertThat(userService)
                .as("userService cannot be null")
                .isNotNull();

        this.totpService = new TotpService(userService);
        assertThat(totpService)
                .as("totpService cannot be null")
                .isNotNull();
    }

    @Test
    void whenGenerateSecretKeyReturnSecretKey() {
        // GIVEN

        // WHEN
        String secretKey = TotpService.generateSecretKey();

        // THEN
        assertThat(secretKey)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void givenValidInputsWhenGenerateQRCodeBase64ImageReturnQRCodeImage() {
        // GIVEN
        String issuer = "issuer";
        String username = "testuser";
        String secretKey = "testSecretKey";

        User user = new User();
        MfaInfo mfaInfo = new MfaInfo();
        mfaInfo.setMfaType(MFAType.TOTP);
        mfaInfo.setSecretKey(secretKey);
        user.setMfaInfoList(Collections.singletonList(mfaInfo));

        // WHEN
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.of(user));

        // THEN
        String result = this.totpService.generateQRCodeBase64Image(issuer, username);

        assertThat(result).isNotBlank();
        assertThat(Base64.decodeBase64(result)).isNotEmpty();
    }

    @Test
    void givenInvalidIssuerWhenGenerateQRCodeBase64ImageThrowIllegalArgumentException() {
        // GIVEN
        String issuer = "";
        String username = "testuser";

        // WHEN & THEN
        assertThatThrownBy(() -> this.totpService.generateQRCodeBase64Image(issuer, username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The issuer cannot be null or empty.");
    }

    @Test
    void givenInvalidUsernameWhenGenerateQRCodeBase64ImageThrowIllegalArgumentException() {
        // GIVEN
        String issuer = "issuer";
        String username = "";

        // WHEN & THEN
        assertThatThrownBy(() -> this.totpService.generateQRCodeBase64Image(issuer, username))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The username cannot be null or empty.");
    }

    @Test
    void givenInvalidSecretKeyWhenGenerateQRCodeBase64ImageThrowIllegalArgumentException() {
        // GIVEN
        String issuer = "issuer";
        String username = "testuser";
        String secretKey = "";

        User user = new User();
        MfaInfo mfaInfo = new MfaInfo();
        mfaInfo.setMfaType(MFAType.TOTP);
        mfaInfo.setSecretKey(secretKey);
        user.setMfaInfoList(Collections.singletonList(mfaInfo));

        // WHEN
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.of(user));

        // THEN
        assertThatThrownBy(() -> this.totpService.generateQRCodeBase64Image(issuer, username))
                .isInstanceOf(InvalidSecretKeyException.class)
                .hasMessage("The User secretKey cannot be null or empty. Username: " + username);
    }

    @Test
    void givenNonExistentUserWhenGenerateQRCodeBase64ImageThrowUserNotFoundException() {
        // GIVEN
        String issuer = "issuer";
        String username = "nonexistentuser";

        // WHEN
        when(userService.getUserByUsername(username)).thenReturn(Optional.empty());

        // THEN
        assertThatThrownBy(() -> this.totpService.generateQRCodeBase64Image(issuer, username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("No user found by the username: " + username);
    }

    @Test
    void givenUserWithoutMfaInfoWhenGenerateQRCodeBase64ImageThrowGenerateQRCodeImageException() {
        // GIVEN
        String issuer = "issuer";
        String username = "testuser";

        User user = new User();

        // WHEN
        when(userService.getUserByUsername(username)).thenReturn(Optional.of(user));

        // THEN
        assertThatThrownBy(() -> this.totpService.generateQRCodeBase64Image(issuer, username))
                .isInstanceOf(InvalidMfaTypeException.class)
                .hasMessage("No MFA added for the user " + username);
    }

    @Test
    void givenInvalidMfaTypeWhenGenerateQRCodeBase64ImageThrowInvalidMfaTypeException() {
        // GIVEN
        String issuer = "issuer";
        String username = "testuser";

        User user = new User();
        MfaInfo mfaInfo = new MfaInfo();
        mfaInfo.setMfaType(MFAType.SMS); // Non-TOTP MFA type
        user.setMfaInfoList(Collections.singletonList(mfaInfo));

        // WHEN
        when(this.userService.getUserByUsername(anyString())).thenReturn(Optional.of(user));

        // THEN
        assertThatThrownBy(() -> this.totpService.generateQRCodeBase64Image(issuer, username))
                .isInstanceOf(InvalidMfaTypeException.class)
                .hasMessage("No TOTP MFA type added for the user " + username);
    }

    @Test
    void testGenerateQRCodeBase64ImageWhenRuntimeException() {
        // GIVEN
        String issuer = "issuer";
        String username = "testuser";

        User user = new User();
        MfaInfo mfaInfo = new MfaInfo();
        mfaInfo.setMfaType(MFAType.SMS); // Non-TOTP MFA type
        user.setMfaInfoList(Collections.singletonList(mfaInfo));

        // WHEN
        when(this.userService.getUserByUsername(anyString())).thenThrow(new RuntimeException("Unidentified error."));

        // THEN
        assertThatThrownBy(() -> this.totpService.generateQRCodeBase64Image(issuer, username))
                .isInstanceOf(GenerateQRCodeImageException.class)
                .hasMessage("Unable to generate the TOTP QRCode base64 image.");
    }
}
