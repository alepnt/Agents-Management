package com.example.server.service;

import com.example.common.dto.MailAttachmentDTO;
import com.example.common.dto.MailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<Void> response;

    private MailService service;

    @BeforeEach
    void setUp() {
        service = new MailService();
        ReflectionTestUtils.setField(service, "httpClient", httpClient);
    }

    @Test
    void shouldSendMailSuccessfully() throws Exception {
        when(response.statusCode()).thenReturn(202);
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<Void>>any())).thenReturn(response);
        MailRequest request = new MailRequest("subject", "body", List.of("to@test.it"), List.of(), List.of(), List.of());

        service.sendMail("token", request);

        verify(httpClient).send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<Void>>any());
    }

    @Test
    void shouldFailWhenTokenMissing() {
        MailRequest request = new MailRequest("subject", "body", List.of("to@test.it"), List.of(), List.of(), List.of());

        assertThatThrownBy(() -> service.sendMail(" ", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token Microsoft Graph non presente");
    }

    @Test
    void shouldReportHttpErrorStatus() throws Exception {
        when(response.statusCode()).thenReturn(500);
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<Void>>any())).thenReturn(response);
        MailRequest request = new MailRequest("subject", "body", List.of("to@test.it"), List.of(), List.of(),
                List.of(new MailAttachmentDTO("f", "c", "d")));

        assertThatThrownBy(() -> service.sendMail("token", request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Invio email fallito con stato 500");
    }

    @Test
    void shouldWrapIoExceptions() throws Exception {
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<Void>>any()))
                .thenThrow(new IOException("boom"));
        MailRequest request = new MailRequest("subject", "body", List.of("to@test.it"), List.of(), List.of(), List.of());

        assertThatThrownBy(() -> service.sendMail("token", request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Errore durante l'invio dell'email")
                .hasCauseInstanceOf(IOException.class);
    }
}
