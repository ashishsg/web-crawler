# **Detailed Capacity & Cost Breakdown (1M URLs)**

This document provides a service-by-service capacity and cost breakdown to process 1 million URLs using the SEO crawler system. Two scenarios are considered: completing the crawl in 1 hour and in 2 hours. These are theoretical numbers. Actual numbers can change significantly depending on size of data, page load latencies, dynamic content rendering etc.

## **1\. Key Assumptions**

\- Average page size: 2 MB  
\- Each URL results in: 1 PUT to S3, 1 GET from S3, 1 DynamoDB write, 2 SQS messages  
\- Instance type: c7g.xlarge (4 vCPU, 8 GB RAM) on AWS Spot  
\- Estimated pod capacity:  
  • Fetcher: \~40 URLs/sec  
  • Extractor: \~25 URLs/sec  
\- Data transfer within AWS (EC2→S3, EC2→DynamoDB) is free  
\- RDS MySQL t4g.large used instead of Aurora  
\- Redis is used for robots.txt cache and rate limiter  
\- Spot pricing assumed for EKS worker nodes

## **2\. 1-Hour Target: Process 1M URLs in 1 Hour**

Required Throughput: \~278 URLs/sec

| Service | Estimated Cost (1 Hour) |
| :---- | :---- |
| EKS Compute (8 fetch nodes \+ 12 extract nodes \+ control) | $1.10 |
| S3 (1M PUT \+ 1M GET \+ 1-day storage) | $6.93 |
| SQS (4M requests) | $1.60 |
| DynamoDB (1M writes, \~5KB each) | $6.25 |
| RDS MySQL (1.5 hr of t4g.large) | $0.10 |
| Redis (1 hr of cache.t4g.small) | $0.02 |

* Total Estimated Cost (1 Hour): \~$16.00

## **3\. 2-Hour Target: Process 1M URLs in 2 Hours**

Required Throughput: \~139 URLs/sec

| Service | Estimated Cost (2 Hours) |
| :---- | :---- |
| EKS Compute (4 fetch nodes \+ 6 extract nodes \+ control) | $1.20 |
| S3 (1M PUT \+ 1M GET \+ 1-day storage) | $6.93 |
| SQS (4M requests) | $1.60 |
| DynamoDB (1M writes, \~5KB each) | $6.25 |
| RDS MySQL (2.5 hr of t4g.large) | $0.17 |
| Redis (2 hr of cache.t4g.small) | $0.05 |

* Total Estimated Cost (2 Hours): \~$16.20

## **4\. Observations & Optimization Levers**

\- The cost difference between 1h and 2h is minimal because compute duration scales inversely with concurrency.  
\- S3 and DynamoDB dominate total cost.  
\- S3 costs can be reduced using lifecycle policies to IA or Glacier after 1–7 days.  
\- Spot instances help keep compute cost low.  
\- Redis and RDS costs are minimal.  
\- RDS MySQL offers 5–7% cost saving over Aurora Serverless for predictable workloads.