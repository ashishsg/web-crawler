

## **1\. POC Goal**

Prove end-to-end crawling and processing pipeline with measurable throughput, correctness, and cost. Decide the primary language for the MVP based on benchmarking during the POC phase. Libraries can be changed post-POC, but the programming language must be finalized. Expected duration: 3-4 weeks with 3 senior developers having basic understanding of go, python, java and one public cloud. Access to AI tools like cursor or claude would be an added advantage. 

## **2\. POC Scope**

\- Ingestion directly to SQS using cli \-\>  Fetch \-\> Raw HTML to S3 \-\>  Extraction & Classification → DynamoDB  
\- Enforce politeness and robots.txt rules  
\- Define and persist unified result schema  
\- Evaluate libraries and runtime performance for Go, Python, and Java 21 (Virtual Threads)  
\- Minimal observability (metrics, DLQ, retries)

## **3\. Language & Library Candidates**

### **3.1 Java 21 \+ Virtual Threads**

\- HTTP: JDK HttpClient with Virtual Threads, OkHttp  
\- Robots.txt: crawler-commons  
\- HTML Parsing: jsoup  
\- HTML Sanitization: OWASP Java HTML Sanitizer  
\- Dynamic Rendering (Optional): Playwright for Java  
\- Metrics: Micrometer \+ OpenTelemetry

### **3.2 Python**

\- HTTP: httpx, aiohttp  
\- Robots.txt: Protego, urllib.robotparser  
\- HTML Parsing: lxml, BeautifulSoup4, trafilatura, readability-lxml  
\- HTML Sanitization: bleach  
\- Dynamic Rendering: Playwright  
\- Metrics: prometheus\_client, OpenTelemetry

### **3.3 Go**

\- HTTP: net/http, fasthttp  
\- Robots.txt: temoto/robotstxt  
\- HTML Parsing: goquery, bluemonday  
\- Crawling: Colly  
\- Dynamic Rendering: chromedp or playwright-go  
\- Metrics: prometheus/client\_golang, OpenTelemetry

Note: Security signoff required every lib before moving to MVP

| Area | Go | Python | Java 21 (VT) |
| ----- | ----- | ----- | ----- |
| Fetch throughput (pages/sec/core) | 🟢 expected high | 🟡 moderate (async helps) | 🟢 high (virtual threads) |
| Extraction quality (boilerplate removal) | 🟡 good with tuning | 🟢 strong libs | 🟡 good with jsoup \+ heuristics |
| Dev velocity / ecosystem | 🟡 | 🟢 | 🟢 |
| Single‑language MVP viability | 🟡 | 🟡 (fetch throughput risk) | 🟢 |
| Operational footprint | 🟢 small | 🟡 | 🟡 JVM resources but predictable |

## **4\. Complexity Breakdown**

### **4.1 Potential Blockers**

\- Anti-bot protection by origin servers  
\- Robots.txt edge cases  
\- DNS and SSRF hardening  
\- JS-rendered pages (if needed)  
\- IAM and VPC restrictions  
\- HTML extraction accuracy

### **4.2 Known Areas**

\- SQS → S3 → DynamoDB pipelines  
\- Token bucket rate limiter  
\- Basic classification with fastText  
\- S3 lifecycle and DDB upserts

### **4.3 Trivial**

\- API Gateway & FastAPI/Fiber endpoints  
\- Metrics collection and dashboard setup  
\- ETag/hash dedup store in Redis

## **5\. Work Breakdown & Estimates**

| Workstream | Deliverables | Estimate |
| :---- | :---- | :---- |
| Env & Infra | IaC for SQS, S3, DDB, Redis, KMS, VPC | 4-5 dev‑days |
| Fetcher (Go/Python/Java) | SQS consumer, robots.txt, SSRF guard, S3 write | 6-8 dev‑days per variant |
| Extractor (Go/Python/Java) | HTML parse \+ sanitization \+ schema fill | 5–7 dev‑days |
| Classifier (Go/Python/Java) | Baseline topic classifier | 5–7 dev‑days |
| API \+ Batch | Demo API & small batch support | 3–4 dev‑days |
| Observability | Tracing \+ Prometheus metrics | 4–5 dev‑days |
| Benchmarking | Corpus run \+ cost model \+ decision doc | 6-8 dev‑days |
|  |  |  |

## **6\. Implementation Schedule**

Week 1 – Infra, Skeletons, First End-to-End URL:  
\-Set up infra, queues, tables  
\- Build fetchers (Go, Py, Java)  
\- Store raw HTML → S3  
\- API stub working

Week 2 – Schema, Extraction, Robots:  
\- Implement unified schema  
\- Extract metadata, sanitize HTML  
\- Robots.txt \+ token bucket

Week 3 – Benchmark, DLQ, Classification:  
\- 10k–50k URLs through pipeline  
\- DLQ \+ replay tool  
\- Baseline classification & evaluation

Week 4 – Hardening & Decision:  
\- IAM, cost, correctness  
\- Final decision: language & top libraries  
\- POC presentation \+ metrics

## **7\. Final POC Deliverables**

\- Batch processing running in sandbox  
\- Unified schema objects in DDB, raw HTML in S3  
\- Dashboards (queue depth, DLQ, success rate)  
\- Language+library decision doc  
\- Cost and performance benchmark doc  
\- Known limitations & MVP backlog

## **8\. POC Evaluation Criteria**

\- Demonstrate web crawling objectives without too many hacks.

\- OSS evaluation like actively maintained, number of maintainers, community adoption, security scores

\- All components should have a clear path to scale.

\- Low coupling

