# Financial Document Search — Spring AI + pgvector

AI-powered semantic document search built with Spring AI and pgvector.

## What it does
- Loads financial policy documents
- Splits them into chunks using TokenTextSplitter
- Creates embeddings using OpenAI
- Stores vectors in pgvector (PostgreSQL)
- Searches semantically using natural language queries

## Tech Stack
- Java 17
- Spring Boot 3.3.5
- Spring AI 1.0.0
- pgvector (PostgreSQL vector extension)
- OpenAI Embeddings API
- Docker

## How to run
1. Add your OpenAI API key to application.properties
2. Make sure Docker Desktop is running
3. Run DocumentSearchApplication
4. Search at http://localhost:8080/search?query=your question here

## Example queries
- what is the maximum loan amount
- what credit score do I need
- how long does approval take
- what is the interest rate for home loans
