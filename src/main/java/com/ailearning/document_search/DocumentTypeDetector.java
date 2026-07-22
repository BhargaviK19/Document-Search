package com.ailearning.document_search;

import org.springframework.stereotype.Service;

@Service
public class DocumentTypeDetector {

        public String detect(String query) {
            String q = query.toLowerCase();

            if (q.contains("fraud") || q.contains("suspicious")
                    || q.contains("transaction") || q.contains("flagged")
                    || q.contains("unauthorized") || q.contains("stolen")) {
                return "fraud-policy";
            }

            if (q.contains("interest rate") || q.contains("rate")
                    || q.contains("apr") || q.contains("per annum")) {
                return "interest-rates";
            }

            if (q.contains("compliance") || q.contains("kyc")
                    || q.contains("aml") || q.contains("regulation")
                    || q.contains("audit") || q.contains("data privacy")) {
                return "compliance-rules";
            }

            if (q.contains("loan") || q.contains("borrow")
                    || q.contains("credit score") || q.contains("eligibility")
                    || q.contains("tenure") || q.contains("repayment")) {
                return "loan-policy";
            }

            // default — search everything
            return null;
        }
}
