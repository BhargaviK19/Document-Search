package com.ailearning.document_search;

import jakarta.annotation.PostConstruct;
import org.antlr.v4.runtime.tree.pattern.TokenTagToken;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IngestionPipeline {

    private static final Logger log = LoggerFactory.getLogger(IngestionPipeline.class);

    private final VectorStore vectorStore;

    @Value("classpath:documents/loan-policy.md")
    Resource loanPolicy;

    @Value("classpath:documents/fraud-policy.md")
    Resource fraudPolicy;

    @Value("classpath:documents/interest-rates.md")
    Resource interestRates;

    @Value("classpath:documents/compliance-rules.md")
    Resource complianceRules;

    public IngestionPipeline(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void run() {
        log.info("Loading loan policy document..");

        List<Document> allDocuments = new ArrayList<>();

        var loanReader = new MarkdownDocumentReader(loanPolicy,
                MarkdownDocumentReaderConfig.builder()
                        .withAdditionalMetadata("source", "loan-policy.md")
                        .withAdditionalMetadata("type", "loan-policy")
                        .build());
        allDocuments.addAll(loanReader.get());

        var fraudReader = new MarkdownDocumentReader(fraudPolicy,
                MarkdownDocumentReaderConfig.builder()
                        .withAdditionalMetadata("source", "fraud-policy.md")
                        .withAdditionalMetadata("type", "fraud-policy")
                        .build());
        allDocuments.addAll(fraudReader.get());

        var interestRatesReader = new MarkdownDocumentReader(interestRates,
                MarkdownDocumentReaderConfig.builder()
                        .withAdditionalMetadata("source", "interest-rates.md")
                        .withAdditionalMetadata("type", "interest-rates")
                        .build());
        allDocuments.addAll(interestRatesReader.get());

        var complianceRulesReader = new MarkdownDocumentReader(complianceRules,
                MarkdownDocumentReaderConfig.builder()
                        .withAdditionalMetadata("source", "compliance-rules.md")
                        .withAdditionalMetadata("type", "compliance-rules")
                        .build());
        allDocuments.addAll(complianceRulesReader.get());

        log.info("Loaded {} documents total", allDocuments.size());

        log.info("Splitting into chunks..");
        List<Document> chunks = new TokenTextSplitter().split(allDocuments);
        log.info("Created {} chunks", chunks.size());

        log.info("Storing embeddings in pgvector...");
        vectorStore.add(chunks);
        log.info("Done. {} chunks stored.", chunks.size());

    }

}
