package com.beom.api.totp.demo.controller;

import com.beom.api.totp.demo.dal.dto.QRCodeResponse;
import com.beom.api.totp.demo.dal.dto.TotpRequest;
import com.beom.api.totp.demo.exception.GenerateQRCodeImageException;
import com.beom.api.totp.demo.exception.InvalidMfaTypeException;
import com.beom.api.totp.demo.service.ITotpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TotpController} integration tests class
 *
 * @author beom
 * @since 2024/03/16
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
class TotpControllerIntegrationTests {

    private static final String CONTEXT_PATH = "/api";
    private static final String GET_QRCODE_IMAGE_ROOT_ENDPOINT = "/api/v1.0/totp/JohnDoe/qrcode";

    @MockBean
    private ITotpService totpService;

    private final WebApplicationContext webApplicationContext;
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    @Value("${beom.payload.dto.totp.request.path}")
    private String totpRequestPayloadPath;
    private TotpRequest totpRequest;
    @Value("${beom.payload.dto.qrcode.response.path}")
    private String qrCodeResponsePayloadPath;
    private QRCodeResponse qrCodeResponse;

    /**
     * constructor
     */
    @Autowired
    public TotpControllerIntegrationTests(WebApplicationContext webApplicationContext,
                               ObjectMapper objectMapper, MockMvc mockMvc){

        this.webApplicationContext = webApplicationContext;
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
    }

    /**
     * method called before each test execution
     */
    @BeforeEach
    void setupTest() throws Exception {
        assertThat(webApplicationContext)
                .as("webApplicationContext cannot be null!")
                .isNotNull();

        assertThat(objectMapper)
                .as("objectMapper cannot be null!")
                .isNotNull();

        assertThat(mockMvc)
                .as("mockMvc cannot be null!")
                .isNotNull();

        assertThat(totpService)
                .as("totpService cannot be null")
                .isNotNull();

        this.totpRequest = getPayloadContent(totpRequestPayloadPath, TotpRequest.class);
        assertThat(totpRequest)
                .as("totpRequest cannot be null")
                .isNotNull();

        this.qrCodeResponse = getPayloadContent(qrCodeResponsePayloadPath, QRCodeResponse.class);
        assertThat(qrCodeResponse)
                .as("qrCodeResponse cannot be null")
                .isNotNull();
    }

    @Test
    void givenValidInputsWhenGenerateQRCodeBase64ImageThenExpectOk() throws Exception {
        // GIVEN
        String qrCode64Image = qrCodeResponse.qrCode();

        // WHEN
        when(this.totpService.generateQRCodeBase64Image(anyString(), anyString())).thenReturn(qrCode64Image);

        // THEN
        mockMvc.perform(MockMvcRequestBuilders.post(GET_QRCODE_IMAGE_ROOT_ENDPOINT)
                        .contextPath(CONTEXT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(totpRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.qr_code").value(qrCodeResponse.qrCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.issuer").value(qrCodeResponse.issuer()));

        verify(totpService).generateQRCodeBase64Image(anyString(), anyString());
    }

    @Test
    void givenInvalidUserWithoutMfaInfoWhenGenerateQRCodeBase64ImageThenExpectBadRequest() throws Exception {
        // GIVEN
        String username = "testuser";

        // WHEN
        when(totpService.generateQRCodeBase64Image(anyString(), anyString()))
                .thenThrow(new InvalidMfaTypeException("No MFA type added to the user " + username));

        // THEN
        mockMvc.perform(MockMvcRequestBuilders.post(GET_QRCODE_IMAGE_ROOT_ENDPOINT)
                        .contextPath(CONTEXT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(totpRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("No MFA type added to the user " + username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists());

        verify(totpService).generateQRCodeBase64Image(anyString(), anyString());
    }

    @Test
    void whenGenerateQRCodeImageExceptionOccursWillGenerateQRCodeBase64ImageThenExpectInternalServerError() throws Exception {
        // GIVEN

        // WHEN
        when(totpService.generateQRCodeBase64Image(anyString(), anyString()))
                .thenThrow(new GenerateQRCodeImageException("Unable to generate the TOTP QRCode base64 image."));

        // THEN
        mockMvc.perform(MockMvcRequestBuilders.post(GET_QRCODE_IMAGE_ROOT_ENDPOINT)
                        .contextPath(CONTEXT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(totpRequest)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("Unexpected error has occurred."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists());

        verify(totpService).generateQRCodeBase64Image(anyString(), anyString());
    }

    /**
     * method takes a file path string and deserialize its content, using the JsonMapper library,
     * and maps it to an object of the specified class type.
     *
     *
     * @param <T> This parameter specify the specific class to which the file content should be deserialized.
     * @param payloadPath This parameter represents the file path with the content that needs to be deserialized.
     * @param clazz This parameter represents the class type to which the file content should be mapped.
     *
     * @return class type (T). If the deserialization is successful, the method returns the deserialized object.
     * @throws Exception can throw jsonMapper executions
     */
    private <T> T getPayloadContent(String payloadPath, Class<T> clazz) throws Exception {

        if(!StringUtils.hasText(payloadPath)) {
            return null;
        }

        if(clazz == null) {
            return null;
        }

        String jsonString = StreamUtils.copyToString(
                ClassLoader.getSystemResourceAsStream(payloadPath),
                Charset.defaultCharset());

        JsonMapper.Builder builder = JsonMapper.builder();
        JsonMapper jsonMapper = builder.build();
        jsonMapper.registerModule(new JavaTimeModule());

        return jsonMapper.readValue(jsonString, clazz);
    }
}
