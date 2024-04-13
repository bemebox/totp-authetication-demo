package com.beom.api.totp.demo.controller;

import capital.scalable.restdocs.AutoDocumentation;
import capital.scalable.restdocs.jackson.JacksonResultHandlers;
import capital.scalable.restdocs.response.ResponseModifyingPreprocessors;
import com.beom.api.totp.demo.dal.dto.QRCodeRequest;
import com.beom.api.totp.demo.dal.dto.QRCodeResponse;
import com.beom.api.totp.demo.dal.dto.TotpRequest;
import com.beom.api.totp.demo.dal.dto.TotpResponse;
import com.beom.api.totp.demo.exception.GenerateQRCodeImageException;
import com.beom.api.totp.demo.exception.InvalidMfaTypeException;
import com.beom.api.totp.demo.exception.TotpValidationException;
import com.beom.api.totp.demo.exception.UserNotFoundException;
import com.beom.api.totp.demo.service.ITotpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;


/**
 * {@link TotpController} integration tests class
 *
 * @author beom
 * @since 2024/03/16
 */
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
class TotpControllerIntegrationTests {

    private static final String CONTEXT_PATH = "/api";
    private static final String GET_QRCODE_IMAGE_ROOT_ENDPOINT = "/api/v1.0/totp/JohnDoe/qrcode";
    private static final String VALIDATE_TOTP_ROOT_ENDPOINT = "/api/v1.0/totp/{username}/validate";

    @MockBean
    private ITotpService totpService;

    private final WebApplicationContext webApplicationContext;
    private final ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @Value("${beom.payload.dto.qrcode.request.path}")
    private String qrCodeRequestPayloadPath;
    private QRCodeRequest qrCodeRequest;
    
    @Value("${beom.payload.dto.qrcode.response.path}")
    private String qrCodeResponsePayloadPath;
    private QRCodeResponse qrCodeResponse;

    @Value("${beom.payload.dto.totp.request.path}")
    private String totpRequestPayloadPath;
    private TotpRequest totpRequest;

    @Value("${beom.payload.dto.totp.response.path}")
    private String totpResponsePayloadPath;
    private TotpResponse totpResponse;

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
    void setupTest(RestDocumentationContextProvider restDocumentation) throws Exception {

        // to automatically generate the rest documentation
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(JacksonResultHandlers.prepareJackson(objectMapper))
                .alwaysDo(MockMvcRestDocumentation.document("{class-name}/{method-name}",
                        Preprocessors.preprocessRequest(),
                        Preprocessors.preprocessResponse(
                                ResponseModifyingPreprocessors.replaceBinaryContent(),
                                ResponseModifyingPreprocessors.limitJsonArrayLength(objectMapper),
                                Preprocessors.prettyPrint())))
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                        .uris()
                        .withScheme("http")
                        .withHost("localhost")
                        .withPort(8080)
                        .and().snippets()
                        .withDefaults(CliDocumentation.curlRequest(),
                                HttpDocumentation.httpRequest(),
                                HttpDocumentation.httpResponse(),
                                AutoDocumentation.requestFields(),
                                AutoDocumentation.responseFields(),
                                AutoDocumentation.pathParameters(),
                                AutoDocumentation.requestParameters(),
                                AutoDocumentation.description(),
                                AutoDocumentation.methodAndPath(),
                                AutoDocumentation.section()))
                .build();

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

        this.qrCodeRequest = getPayloadContent(qrCodeRequestPayloadPath, QRCodeRequest.class);
        assertThat(qrCodeRequest)
                .as("qrCodeRequest cannot be null")
                .isNotNull();

        this.qrCodeResponse = getPayloadContent(qrCodeResponsePayloadPath, QRCodeResponse.class);
        assertThat(qrCodeResponse)
                .as("qrCodeResponse cannot be null")
                .isNotNull();

        this.totpRequest = getPayloadContent(totpRequestPayloadPath, TotpRequest.class);
        assertThat(totpRequest)
                .as("totpRequest cannot be null")
                .isNotNull();

        this.totpResponse = getPayloadContent(totpResponsePayloadPath, TotpResponse.class);
        assertThat(totpResponse)
                .as("totpResponse cannot be null")
                .isNotNull();
    }

    @Nested
    class GenerateTotpQRCodeImageTests {

        @Test
        void givenValidInputsWhenGenerateQRCodeBase64ImageThenExpectOk() throws Exception {
            // GIVEN
            String qrCode64Image = qrCodeResponse.qrCode();

            // WHEN
            when(totpService.generateQRCodeBase64Image(anyString(), anyString())).thenReturn(qrCode64Image);

            // THEN
            mockMvc.perform(MockMvcRequestBuilders.post(GET_QRCODE_IMAGE_ROOT_ENDPOINT)
                            .contextPath(CONTEXT_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(qrCodeRequest)))
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
                            .content(objectMapper.writeValueAsString(qrCodeRequest)))
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
                            .content(objectMapper.writeValueAsString(qrCodeRequest)))
                    .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("Unexpected error has occurred."))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists());

            verify(totpService).generateQRCodeBase64Image(anyString(), anyString());
        }
    }

    @Nested
    class ValidateTotpTests {
        @Test
        void givenValidInputsWhenValidateTotpThenExpectOk() throws Exception {
            // GIVEN
            String username = "JohnDoe";

            // WHEN
            when(totpService.validateTOTP(username, totpRequest.code())).thenReturn(true);

            // THEN
            mockMvc.perform(MockMvcRequestBuilders.post(VALIDATE_TOTP_ROOT_ENDPOINT, username)
                            .contextPath(CONTEXT_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(totpRequest)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.issuer").value(totpResponse.issuer()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.valid").value(totpResponse.valid()));

            verify(totpService).validateTOTP(username, totpRequest.code());
        }

        @Test
        void givenInvalidUsernameWhenValidateTOTPThenExpectBadRequest() throws Exception {
            // GIVEN
            String username = "testuser";

            // WHEN
            when(totpService.validateTOTP(username, totpRequest.code()))
                    .thenThrow(new UserNotFoundException("No user found by the username: " + username));

            // THEN
            mockMvc.perform(MockMvcRequestBuilders.post(VALIDATE_TOTP_ROOT_ENDPOINT, username)
                            .contextPath(CONTEXT_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(totpRequest)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.length()").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.[0]").value("No user found by the username: " + username))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists());

            verify(totpService).validateTOTP(username, totpRequest.code());
        }

        @Test
        void whenTotpValidationExceptionOccursWillValidateTOTPThenExpectInternalServerError() throws Exception {
            // GIVEN
            String username = "JaneDoe";

            // WHEN
            when(totpService.validateTOTP(username, totpRequest.code()))
                    .thenThrow(new GenerateQRCodeImageException("Unable to validate the TOTP code."));

            // THEN
            mockMvc.perform(MockMvcRequestBuilders.post(VALIDATE_TOTP_ROOT_ENDPOINT, username)
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

            verify(totpService).validateTOTP(username, totpRequest.code());
        }
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
