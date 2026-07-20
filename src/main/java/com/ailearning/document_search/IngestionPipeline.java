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
import java.util.List;

@Controller
public class IngestionPipeline {

    private static final Logger log = LoggerFactory.getLogger(IngestionPipeline.class);

    private final VectorStore vectorStore;

    @Value("classpath:documents/loan-policy.md")
    Resource loanPolicy;

    public IngestionPipeline(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void run() {
        log.info("Loading loan policy document..");

        var reader = new MarkdownDocumentReader(loanPolicy,
                MarkdownDocumentReaderConfig.builder()
                        .withAdditionalMetadata("source", "loan-policy.md")
                        .withAdditionalMetadata("type", "loan-policy")
                        .build());

        List<Document> documents = reader.get();

        log.info("Splitting into chunks..");
        List<Document> chunks = new TokenTextSplitter().split(documents);
        log.info("Created {} chunks", chunks.size());

        log.info("Storing embeddings in pgvector..");
        vectorStore.add(chunks);
        log.info("Done. {} chunks stored.", chunks.size());

    }

}
