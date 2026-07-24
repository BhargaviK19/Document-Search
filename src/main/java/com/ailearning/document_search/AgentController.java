package com.ailearning.document_search;

import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }


    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return agentService.chat(message);
    }

    @PostConstruct
    public void init() {
        System.out.println("AgentController loaded successfully");
    }
}
