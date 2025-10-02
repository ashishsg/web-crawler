# **SEO Crawler – Team Ownership & Structure**

## **1\. Ownership Model**

This document outlines the component-wise and horizontal ownership model for the SEO Crawler project. The structure is optimized for 4 senior developers and 2 junior developers, supported by a separate security team. The model ensures there is always a backup team member. Also each senior member gets to own different areas giving them more accountability.

## **2\. Team Composition**

\- 4 Senior Developers: Lead components, architecture, mentoring  
\- 2 Junior Developers: Deliver under guidance, own scoped features  
\- Security Team (separate): Handles IAM, SSRF, VPC, audit, security scanning

## **3\. Component Ownership**

| Component | Primary Owner | Backup / Pair |
| :---- | :---- | :---- |
| Fetcher Service | Senior Dev A | Junior Dev 1 |
| Extraction & Classification | Senior Dev B | Junior Dev 2 |
| Config Service \+ TTLs | Senior Dev C | Junior Dev 1 |
| API \+ UI \+ Job Management | Senior Dev D | Junior Dev 2 |
| Observability \+ Metrics | Senior Dev C \+ D | All |
| Infra (Terraform, CI/CD) | Senior Dev A | Senior Dev C |
| Security Integrations | Security Team (POC: Dev A) | \- |

## **4\. Horizontal Roles**

* \- Tech Lead: Senior Dev A  
* \- Release Manager: Senior Dev C  
* \- Quality Owner: Senior Dev B  
* \- Security POC: Senior Dev A  
* \- Infra & CI/CD: Senior Dev A \+ D

## **5\. Mentorship Pairing**

* \- Senior Dev A → Junior Dev 1: Fetcher, Infra, SQS  
* \- Senior Dev B → Junior Dev 2: Extraction, HTML parsing, ML  
* \- Senior Dev C → Junior Dev 1: Config, TTLs, quotas  
* \- Senior Dev D → Junior Dev 2: API, Batch, UI

## **6\. Security Team Integration**

* \- SSRF Protection: Security team sets rules, Dev A implements  
* \- IAM & Secrets: Security reviews roles, Dev A applies  
* \- Container Security: Security validates renderer sandbox, Dev B owns impl  
* \- CI/CD Scans: Security sets up scanners, Dev A gates builds  
* \- Audit Logging: Security reviews, dev team implements

## **7\. Quarterly Growth Track**

* \- Junior Dev 1: Lead deduplication TTL feature  
* \- Junior Dev 2: Own API response unification  
* \- Senior Dev A: Render sandbox design & strategy  
* \- Senior Dev B: Improve classifier accuracy  
* \- Senior Dev C: Multi-tenant config service migration  
* \- Senior Dev D: Lead UI/UX for batch ingestion