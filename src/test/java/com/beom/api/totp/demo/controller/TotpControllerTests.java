package com.beom.api.totp.demo.controller;

import com.beom.api.totp.demo.dal.dto.QRCodeResponse;
import com.beom.api.totp.demo.dal.dto.TotpRequest;
import com.beom.api.totp.demo.service.ITotpService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

/**
 * {@link TotpController} tests class
 *
 * @author beom
 * @since 2023/03/16
 *
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
    void testGetQRCodeImage_Success() {
        //TODO: RENAME TEST CASE

        // GIVEN
        String username = "testuser";
        String issuer = "Test Issuer";
        TotpRequest totpRequest = new TotpRequest(issuer);
        String qrCode64Image = "dummyBase64Image";

        // WHEN
        when(totpService.generateQRCodeBase64Image(anyString(), anyString())).thenReturn(qrCode64Image);

        // THEN
        ResponseEntity<QRCodeResponse> responseEntity = totpController.getQRCodeImage(username, totpRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().qrCode()).isEqualTo(qrCode64Image);
        assertThat(responseEntity.getBody().issuer()).isEqualTo(issuer);
    }

    @Test
    void testGetQRCodeImage_NoQRCodeGenerated() {
        //TODO: RENAME TEST CASE

        // GIVEN
        String username = "testuser";
        String issuer = "Test Issuer";
        TotpRequest totpRequest = new TotpRequest(issuer);

        // WHEN
        when(totpService.generateQRCodeBase64Image(anyString(), anyString())).thenReturn(null);

        // THEN
        assertThatThrownBy(() -> this.totpController.getQRCodeImage(username, totpRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No QRCode generated for the username " + username);

        verify(totpService).generateQRCodeBase64Image(anyString(), anyString());
    }

}
