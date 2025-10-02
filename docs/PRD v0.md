  
**Product Requirements Document (PRD): Page Content Classifier and Metadata Extractor**

## **1\. Introduction**

### **1.1 Goal**

The primary goal is to design, build, and deploy a highly **scalable, performant, and cost-effective** service that can ingest billions of URLs, extract their foundational metadata, classify the page's core topic(s). This service is crucial for SEO initiatives, content analysis, and internal knowledge graph development.

### **1.2 Problem Statement**

We need a unified, high-volume service to reliably: **Extract** key HTML metadata, **Analyze** the main content, and **Classify** the page into relevant topics. The service must handle **billions of URLs** for batch processing while optimizing for **cost, performance, and availability**.

### **1.3 Target Audience**

* **SEO Specialists:** To audit content and optimize page signals.  
* **Content Strategists:** To categorize large content inventories.  
* **Engineering Teams:** To integrate topical classification into other systems.

---

## **2\. Scope and Features**

### **2.1 Core Features**

| Feature ID | Feature Name | Description | Priority |
| :---- | :---- | :---- | :---- |
| CF-001 | **URL Ingestion & Page Fetching** | Securely and efficiently fetch content, respecting robots.txt and polite fetching rules. | **High** |
| CF-002 | **HTML Metadata Extraction** | Extract **Title Tag, Meta Description, Canonical URL, H1-H6 tags, and OG/Twitter Card metadata**. | **High** |
| CF-003 | **Core Content Extraction** | Isolate the main, textual content, discarding boilerplate (headers, footers, navigation). | **High** |
| CF-004 | **Page Topic Classification** | Analyze content against a managed taxonomy, returning **top 1-5 relevant topics** with **confidence scores**. | **High** |
| CF-005 | **Batch Processing Endpoint** | An asynchronous mechanism for bulk processing. **Must support URL ingestion from uploaded text files and direct MySQL/SQL database queries.** | **High** |

### **2.2 Out-of-Scope Features (Initial Launch)**

* Deep entity extraction.  
* Sentiment analysis.  
* Multi-language support (initially assume English content).

---

## **3\. Technical Requirements**

### **3.1 Input/Output Definition**

#### **3.1.1 Test API Input (Example)**

| Parameter | Type | Required | Description |
| :---- | :---- | :---- | :---- |
| url | String | Yes | The absolute URL of the page to analyze. |

#### **3.1.2 Real-time API Output (JSON Structure)**

The output must include the analyzed URL, extracted metadata, and a classification object.

#### **3.1.3 Batch Processing Input Sources**

1. **Text File Upload:** Plain text file (.txt), one absolute URL per line. Limit: up to **1,000,000 URLs**.  
2. **Database Query (MySQL/SQL Source):** Requires secure connection and a specification (query or table/column) for URLs. Must handle **pagination/chunking** of results.

### **3.2 Performance and Scalability**

| Metric | Target | Rationale |
| :---- | :---- | :---- |
| **Batch Throughput** | **1,000,000 URLs/hour** | To handle batch processing of the web index. |
| **Availability (SLA)** | **99.99%** | Critical infrastructure service reliability. |

### **3.3 System Design Considerations**

1. **Microservices Architecture:** Decouple fetching, extraction, and classification services.  
2. **Classification Model:** Use an optimized, efficient ML model trainable on custom taxonomies. For POC use simple libs  
3. **Cost Optimization:** Use cloud-native serverless or container orchestration to optimize compute costs. Implement storage lifecycles to ensure storage cost is in check.

### **3.4 Data Model Considerations**

1. Store URL metadata  
2. Store HTML content  
3. Unified schema for extracted data

---

## **4\. Operational Requirements**

### **4.1 Monitoring and Alerting**

* **Latency Monitoring:** P50/P95/P99 latency tracking.  
* **Error Rate:** Alerting for fetch errors (4xx/5xx), classification errors, and internal server errors.  
* **Resource Utilization:** Monitor CPU/Memory usage during peak loads.

### **4.2 Security and Compliance**

| ID | Requirement | Description |
| :---- | :---- | :---- |
| SEC-001 | **Input Validation & Sanitization** | All URL inputs must be rigorously validated to prevent injection attacks. **URL parameters must be stripped of potential control characters.** |
| SEC-002 | **Rate Limiting & Throttling** | Implement strict rate limiting on URL fetching to avoid getting black listed. |
| **SEC-003** | **Content Execution Sandbox** | The fetching and parsing component must run in a highly **isolated, sandboxed execution environment**. **No execution of external JavaScript or dynamic code should be permitted outside this sandbox.** |
| **SEC-004** | **HTML/JS Stripping and Filtering** | Before data is passed to classification or persistence, **all executable content (JS, event handlers, etc.) must be scrubbed/filtered** from the extracted HTML and body content. |
| **SEC-005** | **URL Redirect Following Policy** | Implement a **strict maximum redirect limit (e.g., 5\)** and **reject redirection to internal/private IP address ranges** (SSRF mitigation). |
| SEC-006 | **Secure Credential Management** | All secrets (DB strings, API keys) must be stored in a dedicated, managed secret store. |
| SEC-007 | **Secure Database Access** | Database access must use credentials configured with **minimum necessary permissions** (read-only). |
| SEC-008 | **Network Isolation** | The fetching component must have restricted network access to protect the core infrastructure. |

### **4.3 Edge Cases & Error Handling**

The service must gracefully handle failures and unpredictable data. All unrecoverable errors must be logged with context.

| ID | Edge Case/Error Condition | Handling Strategy | Status Code/Output |
| :---- | :---- | :---- | :---- |
| EH-001 | **URL Fetch Failure (4xx/5xx)** | Implement a retry mechanism (e.g., 3 retries). If all fail, mark as permanently failed. | status: "fetch\_failed", Details: HTTP Status Code |
| EH-002 | **Server-Side Request Forgery (SSRF)** | Reject any URL that resolves to a private IP range (per SEC-005). | status: "security\_rejected", Details: "Private IP block" |
| EH-003 | **Timeout on Fetch** | Enforce a strict maximum timeout limit (e.g., 10 seconds). Record as a soft failure. | status: "fetch\_timeout", Details: "Request exceeded limit" |
| EH-004 | **Malformed or Invalid URL** | Reject the request immediately before attempting a fetch. Log the invalid input. | status: "validation\_error", Details: "Invalid URL format" |
| EH-006 | **No Extractable Core Content** | If core content (CF-003) is insufficient (e.g., \< 50 words), skip the classification step (CF-004). | status: "no\_content", Classification: Empty Array |
| EH-007 | **Classification Model Failure** | If the ML model fails, gracefully fail the classification but still return extracted metadata (CF-002). Alert immediately. | status: "classification\_error", Classification: Null/Empty Array |
| EH-008 | **Database Ingestion Failure (Batch)** | If the SQL connection fails during ingestion, the batch job must fail immediately and alert the operations team. | Batch Job Status: FAILED |

---

## **5\. Success Metrics**

The success of this product will be measured by the following Key Performance Indicators (KPIs):

| Metric | Description | Target |
| :---- | :---- | :---- |
| **Classification Accuracy** | The F1 score of the topic classification model against a gold standard dataset. | **\> 85%** |
| **Cost Per Request** | Total operational cost divided by the number of successful API/Batch requests. | **\< $0.001 per request** |
| **Service Uptime** | Total time the service is operational and available. | **99.99%** |
| **Adoption Rate** | Number of distinct internal teams/systems adopting new crawling system | **3+ teams within 6 months** |

---

## **6\. Development Phases**

### **Phase 0: Proof Of Concept (POC)**

* Demonstrate crawling and data extraction with different libraries for different types of data, robots.txt restriction for pre defined set of URL’s. Can be demonstrated by having a simple API that takes in URL and returns crawl result in unified format  
* Demonstrate simple classification with lightweight libs  
* Implement a baseline topic classification model and compare with lightweight libs in terms of accuracy, speed, cost.

### **Phase 1: Minimum Viable Product (MVP)**

* Deploy core URL Fetcher, Metadata Extractor, and Core Content Extraction (CF-001, CF-002, CF-003).  
* Implement a baseline topic classification service (CF-004).

### **Phase 2: Scale and Optimization**

* Implement the full **Batch Processing Endpoint (CF-005)**, including support for file and database ingestion.  
* Rate limiting  
* Deduplication  
* Implement a custom ML model to hit the **\> 85% accuracy** target.  
* Implement Caching and advanced scaling.  
* Enforce all major security requirements, particularly those for content handling   
* Implement cost optimization

### **Phase 3: Expansion**

* Expand the classification taxonomy and model capabilities.  
* Introduce multi-language content support.  
* Support for multimedia data extraction