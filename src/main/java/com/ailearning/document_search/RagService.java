package com.ailearning.document_search;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class RagService {

    private final VectorStore vectorStore;

    private final ChatClient chatClient;

    private final DocumentTypeDetector documentTypeDetector;

    private static final Logger log = LoggerFactory.getLogger(RagService.class);

    public RagService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder, DocumentTypeDetector documentTypeDetector){
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
        this.documentTypeDetector = documentTypeDetector;
    }

    //Search the Vector Store
    public RagResponse ask(String query) {

       String documentType = documentTypeDetector.detect(query);

       SearchRequest searchRequest;

       if (documentType != null ) {
           log.info("Filtering search to document type: {}", documentType);
           searchRequest = SearchRequest.builder()
                   .query(query)
                   .topK(3)
                   .similarityThreshold(0.5)
                   .filterExpression("type == '" + documentType + "'")
                   .build();
       } else {
           log.info("No specific document type detected, searching all documents");
           searchRequest = SearchRequest.builder()
                   .query(query)
                   .topK(3)
                   .similarityThreshold(0.5)
                   .build();
       }

       List<Document> documents = vectorStore.similaritySearch(searchRequest);

        //This takes each document, gets just the text, and joins them all together with a blank line btw each chunk

        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        //Extract sources from retrieved documents
        List<String> sources = documents.stream()
                .map(doc -> (String) doc.getMetadata().get("source"))
                .distinct()
                .collect(Collectors.toList());

        //writing instructions for the LLM -- here is some info from our documents, use only that information

        String answer = chatClient.prompt()
                .system("""
            You are a helpful financial assistant.
            Answer the question using only the context provided below.
            If the answer is not in the context, say "I don't have that information."
            
            Context:
            """ + context)
                .user(query)
                .call()
                .content();

        return new RagResponse(answer, sources, documentType);
    }

}
