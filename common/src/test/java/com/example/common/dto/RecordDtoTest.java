package com.example.common.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecordDtoTest {

    @Test
    void shouldExposeRecordComponents() {
        Instant timestamp = Instant.parse("2024-02-01T10:15:30Z");
        ChatMessageDTO chatMessage = new ChatMessageDTO(1L, "conv-1", 2L, 3L, "body", timestamp);
        ChatMessageRequest chatRequest = new ChatMessageRequest(5L, "conv-1", "body");
        ChatConversationDTO conversation = new ChatConversationDTO("conv-1", "Team 7", timestamp, "preview");
        MailAttachmentDTO attachment = new MailAttachmentDTO("file.txt", "text/plain", "YWJj");
        MailRequest mailRequest = new MailRequest(
                "subject",
                "hello",
                List.of("target@example.com"),
                List.of("cc@example.com"),
                List.of("bcc@example.com"),
                List.of(attachment)
        );
        MonthlyCommissionDTO monthly = new MonthlyCommissionDTO(2024, 1, BigDecimal.TEN);
        AgentCommissionDTO agentCommission = new AgentCommissionDTO(9L, "Agent 9", "Team 1", BigDecimal.ONE);
        TeamCommissionDTO teamCommission = new TeamCommissionDTO(10L, "Team 10", BigDecimal.ONE);
        AgentStatisticsDTO agentStatistics = new AgentStatisticsDTO(2024, List.of(2023, 2024), List.of(monthly), List.of(agentCommission));
        TeamStatisticsDTO teamStatistics = new TeamStatisticsDTO(2024, List.of(2024), List.of(teamCommission));

        assertThat(chatMessage.id()).isEqualTo(1L);
        assertThat(chatMessage.conversationId()).isEqualTo("conv-1");
        assertThat(chatMessage.createdAt()).isEqualTo(timestamp);

        assertThat(chatRequest.senderId()).isEqualTo(5L);
        assertThat(chatRequest.body()).isEqualTo("body");

        assertThat(conversation.conversationId()).isEqualTo("conv-1");
        assertThat(conversation.title()).isEqualTo("Team 7");
        assertThat(conversation.lastActivity()).isEqualTo(timestamp);
        assertThat(conversation.lastMessagePreview()).isEqualTo("preview");

        assertThat(attachment.filename()).isEqualTo("file.txt");
        assertThat(attachment.contentType()).isEqualTo("text/plain");
        assertThat(attachment.base64Data()).isEqualTo("YWJj");

        assertThat(mailRequest.subject()).isEqualTo("subject");
        assertThat(mailRequest.body()).isEqualTo("hello");
        assertThat(mailRequest.to()).containsExactly("target@example.com");
        assertThat(mailRequest.cc()).containsExactly("cc@example.com");
        assertThat(mailRequest.bcc()).containsExactly("bcc@example.com");
        assertThat(mailRequest.attachments()).containsExactly(attachment);

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
}
