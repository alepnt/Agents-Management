package com.example.common.dto; // Package dei DTO condivisi e dei relativi test.

import org.junit.jupiter.api.Test; // Annotazione JUnit 5 per definire un test.

import java.math.BigDecimal; // Tipo numerico per importi monetari.
import java.time.Instant; // Timestamp per validare i record che contengono date.
import java.util.List; // Utilizzato per costruire liste nei record di test.

import static org.assertj.core.api.Assertions.assertThat; // AssertJ per asserzioni fluide.

/**
 * Test di unità dedicato ai record presenti nel modulo common.
 * Verifica che ogni record esponga correttamente i propri componenti tramite
 * accessor.
 */
class RecordDtoTest {

    @Test
    void shouldExposeRecordComponents() { // Verifica di tutti i componenti dei record.
        Instant timestamp = Instant.parse("2024-02-01T10:15:30Z"); // Timestamp condiviso tra più record.

        ChatMessageDTO chatMessage = // Costruzione record ChatMessageDTO.
                new ChatMessageDTO(1L, "conv-1", 2L, 3L, "body", timestamp);

        ChatMessageRequest chatRequest = // Record per invio messaggi chat.
                new ChatMessageRequest(5L, "conv-1", "body");

        ChatConversationDTO conversation = // Record che rappresenta conversazione chat.
                new ChatConversationDTO("conv-1", "Team 7", timestamp, "preview");

        MailAttachmentDTO attachment = // Allegato email in Base64.
                new MailAttachmentDTO("file.txt", "text/plain", "YWJj");

        MailRequest mailRequest = // Record che rappresenta una mail completa.
                new MailRequest(
                        "subject",
                        "hello",
                        List.of("target@example.com"),
                        List.of("cc@example.com"),
                        List.of("bcc@example.com"),
                        List.of(attachment));

        MonthlyCommissionDTO monthly = // Commissioni mensili per un agente.
                new MonthlyCommissionDTO(2024, 1, BigDecimal.TEN);

        AgentCommissionDTO agentCommission = // Aggregato provvigioni per agente.
                new AgentCommissionDTO(9L, "Agent 9", "Team 1", BigDecimal.ONE);

        TeamCommissionDTO teamCommission = // Aggregato provvigioni per team.
                new TeamCommissionDTO(10L, "Team 10", BigDecimal.ONE);

        AgentStatisticsDTO agentStatistics = // Statistiche complete per agente.
                new AgentStatisticsDTO(
                        2024,
                        List.of(2023, 2024),
                        List.of(monthly),
                        List.of(agentCommission));

        TeamStatisticsDTO teamStatistics = // Statistiche complete per team.
                new TeamStatisticsDTO(
                        2024,
                        List.of(2024),
                        List.of(teamCommission));

        // ----- VALIDAZIONE DEI RECORD CHAT -----

        assertThat(chatMessage.id()).isEqualTo(1L); // Accessor id.
        assertThat(chatMessage.conversationId()).isEqualTo("conv-1");
        assertThat(chatMessage.createdAt()).isEqualTo(timestamp);

        assertThat(chatRequest.senderId()).isEqualTo(5L); // Accessor senderId.
        assertThat(chatRequest.body()).isEqualTo("body");

        assertThat(conversation.conversationId()).isEqualTo("conv-1");
        assertThat(conversation.title()).isEqualTo("Team 7");
        assertThat(conversation.lastActivity()).isEqualTo(timestamp);
        assertThat(conversation.lastMessagePreview()).isEqualTo("preview");

        // ----- VALIDAZIONE RECORD EMAIL -----

        assertThat(attachment.filename()).isEqualTo("file.txt");
        assertThat(attachment.contentType()).isEqualTo("text/plain");
        assertThat(attachment.base64Data()).isEqualTo("YWJj");

        assertThat(mailRequest.subject()).isEqualTo("subject");
        assertThat(mailRequest.body()).isEqualTo("hello");
        assertThat(mailRequest.to()).containsExactly("target@example.com");
        assertThat(mailRequest.cc()).containsExactly("cc@example.com");
        assertThat(mailRequest.bcc()).containsExactly("bcc@example.com");
        assertThat(mailRequest.attachments()).containsExactly(attachment);

        // ----- VALIDAZIONE RECORD COMMISSIONI -----

        assertThat(monthly.year()).isEqualTo(2024);
        assertThat(monthly.commission()).isEqualTo(BigDecimal.TEN);

        assertThat(agentStatistics.years()).containsExactly(2023, 2024);
        assertThat(agentStatistics.monthlyTotals()).containsExactly(monthly);
        assertThat(agentStatistics.agentTotals()).containsExactly(agentCommission);

        assertThat(teamStatistics.years()).containsExactly(2024);
        assertThat(teamStatistics.teamTotals()).containsExactly(teamCommission);

        assertThat(agentCommission.commission()).isEqualTo(BigDecimal.ONE);
        assertThat(teamCommission.commission()).isEqualTo(BigDecimal.ONE);
    }
} // Fine classe RecordDtoTest.
