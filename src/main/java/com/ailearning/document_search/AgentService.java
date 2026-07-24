package com.ailearning.document_search;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentService.class);

    private final ChatClient chatClient;
    private final LoanTools loanTools;

    public AgentService(ChatClient.Builder chatClientBuilder, LoanTools loanTools) {
        this.loanTools = loanTools;
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                    You are an intelligent financial assistant with access to tools.
                    
                    When a user asks about loan eligibility — use the checkEligibility tool.
                    When a user asks about interest rates or monthly payments — use the calculateInterestRate tool.
                    When a user asks about policies, rules, compliance, or fraud — use the searchDocuments tool.
                    
                    Always use the appropriate tool to get accurate information.
                    Never make up numbers or policies — always use tools to get real data.
                    """)
                .defaultTools(loanTools)
                .build();
    }

    public String chat(String userMessage) {
        log.info("Agent received message: {}", userMessage);

        String response = chatClient.prompt()
                .user(userMessage)
                .call()
                .content();

        log.info("Agent responded: {}", response);
        return response;
    }

}
