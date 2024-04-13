package com.beom.api.totp.demo.controller;

import com.beom.api.totp.demo.dal.dto.QRCodeRequest;
import com.beom.api.totp.demo.dal.dto.QRCodeResponse;
import com.beom.api.totp.demo.dal.dto.TotpRequest;
import com.beom.api.totp.demo.dal.dto.TotpResponse;
import com.beom.api.totp.demo.service.ITotpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TotpController} tests class
 *
 * @author beom
 * @since 2023/03/16
 */
@SpringBootTest
    class TotpControllerTests {

    @Mock
    private ITotpService totpService;

    private TotpController totpController;

    /**
     * method called before each test execution
     */
    @BeforeEach
    void setupTest() {
        assertThat(totpService)
                .as("totpService cannot be null")
                .isNotNull();

        this.totpController = new TotpController(totpService);
        assertThat(totpController)
                .as("totpController cannot be null")
                .isNotNull();
    }

    @Test
    void givenValidInputsWhenGetQRCodeImageThenReturnOK() {

        // GIVEN
        String username = "testuser";
        String issuer = "Test Issuer";
        QRCodeRequest qrCodeRequest = new QRCodeRequest(issuer);
        String qrCode64Image = "dummyBase64Image";

        // WHEN
        when(totpService.generateQRCodeBase64Image(issuer, username)).thenReturn(qrCode64Image);

        // THEN
        ResponseEntity<QRCodeResponse> responseEntity = totpController.getQRCodeImage(username, qrCodeRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().qrCode()).isEqualTo(qrCode64Image);
        assertThat(responseEntity.getBody().issuer()).isEqualTo(issuer);

        verify(totpService).generateQRCodeBase64Image(issuer, username);
    }

    @Test
    void givenInvalidInputsWhenGetQRCodeImageThenThrowRuntimeException() {
        // GIVEN
        String username = "testuser";
        String issuer = "Invalid Issuer";
        QRCodeRequest qrCodeRequest = new QRCodeRequest(issuer);

        // WHEN
        when(totpService.generateQRCodeBase64Image(issuer, username)).thenReturn(null);

        // THEN
        assertThatThrownBy(() -> this.totpController.getQRCodeImage(username, qrCodeRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No QRCode generated for the username " + username);

        verify(totpService).generateQRCodeBase64Image(issuer, username);
    }

    @Test
    void givenValidInputsWhenValidateTotpThenReturnOK() {
        // GIVEN
        String username = "testuser";
        String issuer = "Test Issuer";
        String code = "123456";

        TotpRequest totpRequest = new TotpRequest(issuer, code);

        // WHEN
        when(totpService.validateTOTP(username, code)).thenReturn(true);
        ResponseEntity<TotpResponse> responseEntity = totpController.validateTotp(username, totpRequest);

        // THEN
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().valid()).isTrue();
        assertThat(responseEntity.getBody().issuer()).isEqualTo(issuer);

        verify(totpService).validateTOTP(username, code);
    }

    @Test
    void givenInvalidInputsWhenValidateTotpThenThrowRuntimeException() {
        // GIVEN
        String username = "invalidusername";
        String issuer = "Invalid Issuer";

        TotpRequest totpRequest = new TotpRequest(issuer, null);

        // WHEN
        when(totpService.validateTOTP(username, null)).thenReturn(false);
        ResponseEntity<TotpResponse> responseEntity = totpController.validateTotp(username, totpRequest);

        // THEN
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().valid()).isFalse();

        verify(totpService).validateTOTP(username, null);
    }
}
