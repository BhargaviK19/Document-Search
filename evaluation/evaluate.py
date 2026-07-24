import os
import requests

os.environ["OPENAI_API_KEY"] = "OPENAI_API_KEY"

from openai import OpenAI

client = OpenAI()
BASE_URL = "http://localhost:8080"

test_cases = [
    {"question": "What is the maximum personal loan amount?", "expected_document": "loan-policy.md", "expected_answer": "50000"},
    {"question": "What is the minimum credit score required for a loan?", "expected_document": "loan-policy.md", "expected_answer": "650"},
    {"question": "How long does a fraud investigation take?", "expected_document": "fraud-policy.md", "expected_answer": "5 to 10"},
    {"question": "What is the home loan interest rate for first time buyers?", "expected_document": "interest-rates.md", "expected_answer": "5.9"},
    {"question": "What KYC documents are required?", "expected_document": "compliance-rules.md", "expected_answer": "photo ID"},
    {"question": "What happens when fraud is detected?", "expected_document": "fraud-policy.md", "expected_answer": "notified"},
]

def call_rag_api(question):
    response = requests.get(f"{BASE_URL}/ask", params={"query": question})
    data = response.json()
    return data.get("answer", ""), data.get("sources", [])

def check_faithfulness(question, answer, context):
    prompt = f"""Given this context: "{context}"
    And this answer: "{answer}"
    Is the answer supported by the context? Reply with only YES or NO."""
    response = client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=[{"role": "user", "content": prompt}],
        max_tokens=5
    )
    return response.choices[0].message.content.strip().upper() == "YES"

def check_relevancy(question, answer):
    prompt = f"""Question: "{question}"
    Answer: "{answer}"
    Does the answer address the question? Reply with only YES or NO."""
    response = client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=[{"role": "user", "content": prompt}],
        max_tokens=5
    )
    return response.choices[0].message.content.strip().upper() == "YES"

print("Starting RAG Evaluation...")
print(f"Testing {len(test_cases)} questions\n")

source_correct = []
faithful = []
relevant = []

for i, test in enumerate(test_cases):
    print(f"Q{i+1}: {test['question']}")
    answer, sources = call_rag_api(test["question"])
    print(f"  Answer: {answer[:80]}...")
    print(f"  Source: {sources}")

    correct = test["expected_document"] in sources
    source_correct.append(correct)
    print(f"  Source correct: {correct}")

    faith = check_faithfulness(test["question"], answer, answer)
    faithful.append(faith)
    print(f"  Faithful: {faith}")

    rel = check_relevancy(test["question"], answer)
    relevant.append(rel)
    print(f"  Relevant: {rel}\n")

print("\n=== EVALUATION RESULTS ===")
print(f"Source Routing Accuracy: {sum(source_correct)/len(source_correct)*100:.1f}%")
print(f"Faithfulness: {sum(faithful)/len(faithful)*100:.1f}%")
print(f"Answer Relevancy: {sum(relevant)/len(relevant)*100:.1f}%")
print(f"\nTotal questions tested: {len(test_cases)}")