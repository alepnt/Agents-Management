package com.example.server.controller;

import com.example.common.dto.AgentDTO;
import com.example.server.service.AgentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgentController.class)
class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AgentService agentService;

    @Test
    @DisplayName("List agents returns collection payload")
    void listAgents() throws Exception {
        List<AgentDTO> agents = List.of(
                new AgentDTO(1L, 11L, "A1", "Leader"),
                new AgentDTO(2L, 12L, "A2", "Member")
        );
        when(agentService.findAll()).thenReturn(agents);

        mockMvc.perform(get("/api/agents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].agentCode").value("A1"))
                .andExpect(jsonPath("$[1].teamRole").value("Member"));
    }

    @Test
    @DisplayName("Create agent returns created entity")
    void createAgent() throws Exception {
        AgentDTO request = new AgentDTO(null, 20L, "NEW", "Leader");
        AgentDTO saved = new AgentDTO(5L, 20L, "NEW", "Leader");
        when(agentService.create(any(AgentDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/agents")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.agentCode").value("NEW"));
    }

    @Test
    @DisplayName("Create agent propagates service errors")
    void createAgentError() throws Exception {
        when(agentService.create(any(AgentDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate"));

        AgentDTO request = new AgentDTO(null, 20L, "NEW", "Leader");

        mockMvc.perform(post("/api/agents")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update agent returns updated payload")
    void updateAgent() throws Exception {
        AgentDTO request = new AgentDTO(null, 30L, "UPD", "Member");
        AgentDTO updated = new AgentDTO(7L, 30L, "UPD", "Member");
        when(agentService.update(eq(7L), any(AgentDTO.class))).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/agents/7")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.teamRole").value("Member"));
    }

    @Test
    @DisplayName("Update agent returns 404 when missing")
    void updateAgentNotFound() throws Exception {
        when(agentService.update(eq(99L), any(AgentDTO.class))).thenReturn(Optional.empty());

        AgentDTO request = new AgentDTO(null, 30L, "UPD", "Member");

        mockMvc.perform(put("/api/agents/99")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete agent returns 404 when service reports failure")
    void deleteAgentNotFound() throws Exception {
        when(agentService.delete(55L)).thenReturn(false);

        mockMvc.perform(delete("/api/agents/55"))
                .andExpect(status().isNotFound());
    }
}
