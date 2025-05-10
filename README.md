# AI-Driven-Resume-Screener
AI-Driven Resume Screener — using Spring Boot + Agent AI

# Overview
The AI-Driven Resume Screener automatically extracts, analyzes, and ranks resumes based on a given Job Description using AI. It streamlines bulk resume screening by reading resumes in parallel and utilizing Agent AI to compute semantic similarity and ranking.



# High Level Design
[HR Uploads ZIP + JD]
        |
[Spring Boot API Controller]
        |
[Resume Extraction → Apache Tika]
        |
[Parallel Processing with ExecutorService]
        |
[Call Agent AI for Scoring]
        |
[Aggregate & Rank Resumes]
        |
[Return Ranked JSON]

