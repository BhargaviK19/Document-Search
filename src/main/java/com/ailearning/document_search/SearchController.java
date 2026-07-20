package com.ailearning.document_search;

import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.ai.document.Document;
import java.util.List;

@RestController
public class SearchController {
    private final VectorStore vectorStore;

    public SearchController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @GetMapping("/search")
    public List<String> search(@RequestParam String query) {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(3)
                        .similarityThreshold(0.5)
                        .build()
        );

        return results.stream()
                .map(doc -> "Content: " + doc.getText()
                + " | Score: " + doc.getMetadata().get("distance")
                + " | Source: " + doc.getMetadata().get("source"))
                .toList();
    }
}
