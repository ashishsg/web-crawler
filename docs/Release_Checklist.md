# Release Checklist & Disaster Readiness

1. # **Phase Release Gates**

| Area | Metric / Gate | Pass Condition |
| :---- | :---- | :---- |
| Throughput | Crawl ≥ 100K pages/day sustained | ✔️ Load tested over 3 consecutive days |
| Metadata Accuracy | ≥ 95% title/meta/OG correctness | ✔️ On a sample of ≥ 500 pages |
| DLQ Rate | \< 1% of total URLs | ✔️ DLQ \<10k for 1M URLs |
| Traceability | ≥ 95% trace-id coverage per stage | ✔️ Validated via distributed tracing |
| Politeness | Zero origin blocks (e.g., HTTP 429\) | ✔️ No major origin denies in logs |
| Security | SSRF prevention \+ sandboxing active | ✔️ Security team signoff completed |

# **2\. Disaster Recovery & Failure Readiness**

The following table outlines disaster scenarios and how they are mitigated or recovered from.

| Scenario | Mitigation / Recovery Strategy |
| :---- | :---- |
| Region-level AWS outage | Multi-AZ EKS, S3, DynamoDB Global Tables with read replicas |
| SQS Poison-Message Storm | DLQ quarantine \+ requeue UI tool |
| Cost Blowout | CloudWatch budget alarms at $50/$100 thresholds |
| Metadata extraction regression | Canary pipeline for 10K URLs before full rollout |
| Model misclassification | Config-service based rollback on accuracy drop |
| Robots.txt logic bug | FeatureFlag to disable robots enforcement temporarily |
| IAM/VPC misconfig | Hourly preflight healthcheck job to validate access |

# **3\. Rollback Strategy**

All changes (classifiers, extractors, etc.) must be rolled out via feature flags. Rollback will be triggered if any of the following thresholds are breached:  
\- Accuracy drops \> 5%  
\- DLQ spike \> 2%  
\- Cost per 1000 URLs increases \> 15%

# **4\. Observability and Alerting Criteria**

| Alert | Trigger Condition | Action |
| :---- | :---- | :---- |
| Queue Age Alert | SQS queue age \> 10 min | PagerDuty alert \+ autoscaler check |
| DLQ Surge | DLQ \> 1% of batch size | Trigger quarantine script |
| Cost Spike | S3 or DDB daily cost \> $50 | Alert \+ stop new batches |
| Blacklisting Risk | HTTP 429/403 rate \> 1% | Reduce concurrency, backoff enabled |
| Model Accuracy Drift | Confidence \< 0.70 on \>10% pages | Auto-disable model \+ alert |

