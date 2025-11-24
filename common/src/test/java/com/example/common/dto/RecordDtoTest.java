package com.example.common.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class RecordDtoTest {

    @Test
    void shouldExposeRecordComponents() {
        Instant timestamp = Instant.parse("2024-02-01T10:15:30Z");
        ChatMessageDTO chatMessage = new ChatMessageDTO(1L, "conv-1", 2L, 3L, "body", timestamp);
        ChatMessageRequest chatRequest = new ChatMessageRequest(5L, "conv-1", "body");
        ChatConversationDTO conversation = new ChatConversationDTO("conv-1", 7L, 8L);
        MailAttachmentDTO attachment = new MailAttachmentDTO("file.txt", new byte[]{1, 2, 3});
        MailRequest mailRequest = new MailRequest("subject", "text/plain", "hello", "target@example.com", attachment);
        MonthlyCommissionDTO monthly = new MonthlyCommissionDTO(2024, 1, BigDecimal.TEN);
        AgentStatisticsDTO agentStatistics = new AgentStatisticsDTO(2024, 1, 2, 3, 4, BigDecimal.ONE);
        TeamStatisticsDTO teamStatistics = new TeamStatisticsDTO(2024, 2, 10, 20, BigDecimal.TEN);
        AgentCommissionDTO agentCommission = new AgentCommissionDTO(9L, BigDecimal.ONE, BigDecimal.ZERO);
        TeamCommissionDTO teamCommission = new TeamCommissionDTO(10L, BigDecimal.TEN, BigDecimal.ONE);

        assertThat(chatMessage.id()).isEqualTo(1L);
        assertThat(chatMessage.conversationId()).isEqualTo("conv-1");
        assertThat(chatMessage.createdAt()).isEqualTo(timestamp);

        assertThat(chatRequest.senderId()).isEqualTo(5L);
        assertThat(chatRequest.body()).isEqualTo("body");

        assertThat(conversation.teamId()).isEqualTo(8L);

        assertThat(attachment.filename()).isEqualTo("file.txt");
        assertThat(attachment.content()).containsExactly(1, 2, 3);

        assertThat(mailRequest.subject()).isEqualTo("subject");
        assertThat(mailRequest.contentType()).isEqualTo("text/plain");
        assertThat(mailRequest.body()).isEqualTo("hello");
        assertThat(mailRequest.to()).isEqualTo("target@example.com");
        assertThat(mailRequest.attachment()).isEqualTo(attachment);

        assertThat(monthly.year()).isEqualTo(2024);
        assertThat(monthly.commission()).isEqualTo(BigDecimal.TEN);

        assertThat(agentStatistics.contracts()).isEqualTo(3);
        assertThat(teamStatistics.totalContracts()).isEqualTo(20);

        assertThat(agentCommission.totalCommission()).isEqualTo(BigDecimal.ONE);
        assertThat(teamCommission.paidCommission()).isEqualTo(BigDecimal.ONE);
    }
}
