package com.ailearning.document_search;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final VectorStore vectorStore;

    private final ChatClient chatClient;

    public RagService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder){
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    //Search the Vector Store
    public String ask(String query) {
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(3)
                        .similarityThreshold(0.5)
                        .build()
        );

        //This takes each document, gets just the text, and joins them all together with a blank line btw each chunk

        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        //writing instructions for the LLM -- here is some info from our documents, use only that information

        return chatClient.prompt()
                .system("""
                        You are a helpful financial assistant.
                                Answer the question using only the context provided below.
                                If the answer is not in the context, say "I don't have that information."
                        Context:
                        """ + context)
                .user(query)
                .call()
                .content();
    }

}
