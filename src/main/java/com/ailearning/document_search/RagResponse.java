package com.ailearning.document_search;

import java.util.List;

public class RagResponse {
    private String answer;
    private List<String> sources;
    private String documentType;

    public RagResponse(String answer, List<String> sources, String documentType) {
        this.answer = answer;
        this.sources = sources;
        this.documentType = documentType;
    }

    public String getAnswer() { return answer; }

    public List<String> getSources() {
        return sources;
    }

    public String getDocumentType() { return documentType; }
}
