package com.ailearning.document_search;

import com.ailearning.document_search.RagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService){
        this.ragService = ragService;
    }

    @GetMapping("/ask")
    public RagResponse getQuery(@RequestParam String query) {
        return ragService.ask(query);

    }

}
