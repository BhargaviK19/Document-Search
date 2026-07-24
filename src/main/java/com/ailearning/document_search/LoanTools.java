package com.ailearning.document_search;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;


@Component
public class LoanTools {

    private final RagService ragService;

    public LoanTools (RagService ragService) {
        this.ragService = ragService;
    }

    @Tool(description = """
    Check if a customer is eligible for a loan.
    Use this when the user asks about loan eligibility,
    whether they qualify for a loan, or provides their
    income and credit score for evaluation.
    """)
    public String checkEligibility(
        @ToolParam(description = "Monthly income in dollars") double monthlyIncome,
        @ToolParam(description = "Credit score between 300 and 850") int creditScore) {


    if (creditScore < 650) {
        return String.format(
                "Not eligible.Credit score %d is below minimum requirement of 650.", creditScore);
    }

    if (monthlyIncome < 3000) {
        return String.format(
                "Not eligible. Monthly income $%.0f is below minimum requirement of $3,000.",
                monthlyIncome);
    }

        // Calculate max loan amount — typically 10x monthly income
        double maxLoanAmount = monthlyIncome * 10;

        return String.format(
                "Eligible for a loan. Credit score: %d. Monthly income: $%.0f. " +
                        "Maximum loan amount: $%.0f. Interest rate: %.1f%% per annum.",
                creditScore, monthlyIncome, maxLoanAmount,
                creditScore >= 750 ? 7.0 : 8.5);
    }


    @Tool(description = """
            Calculate the interest rate for a loan.
            Use this when the user asks about interest rates,
            what rate they would get, or provides a loan amount
            and credit score to get a specific rate.
            """)
    public String calculateInterestRate(
            @ToolParam(description = "Loan amount in dollars") double loanAmount,
            @ToolParam(description = "Credit score between 300 and 850") int creditScore) {
        double rate;
        String category;

        if (creditScore >= 750) {
            rate = 7.0;
            category = "Premium";
        } else if (creditScore >= 650) {
            rate = 8.5;
            category = "Standard";
        } else {
            rate = 12.5;
            category = "High Risk";
        }

        double monthlyPayment = (loanAmount * (rate / 100 / 12)) / (1 - Math.pow(1 + (rate / 100 / 12), -60));

        return String.format(
                "Loan amount: $%.0f. Credit score: %d (%s). " +
                        "Interest rate: %.1f%% per annum. " +
                        "Estimated monthly payment over 60 months: $%.2f.",
                loanAmount, creditScore, category, rate, monthlyPayment);

    }

        @Tool(description = """
                Search financial policy documents to answer questions.
                Use this when the user asks about loan policies,
                fraud rules, compliance requirements, interest rate policies,
                KYC requirements, or any other policy related questions.
                """)
        public String searchDocuments(
                @ToolParam(description = "The question to search for in financial documents")
                        String question) {

            RagResponse response = ragService.ask(question);
            return response.getAnswer() + " (Source: " + response.getSources() + ")";
        }
}
