# Web Crawler

A Spring Boot-based web crawler application that extracts and classifies content from web pages with support for JavaScript rendering, robots.txt compliance, and intelligent content classification.

## Features

- **RESTful API** with Swagger/OpenAPI documentation
- **HTML Parsing** using Jsoup for static content
- **JavaScript Rendering** with Selenium WebDriver for dynamic content
- **Robots.txt Compliance** to respect website crawling policies
- **Content Classification** using Apache OpenNLP (8 categories: news, sports, technology, health, entertainment, business, education, other)
- **Language Detection** with confidence scoring
- **Multiple Classifications** - Returns top 3 content categories with confidence scores
- **H1 Tag Extraction** for page titles
- **Comprehensive Test Coverage** - 74 tests with 50% instruction coverage

## Technology Stack

- **Java 21**
- **Spring Boot 3.2.5**
- **Maven** - Build and dependency management
- **Jsoup 1.18.1** - HTML parsing
- **Selenium 4.27.0** - JavaScript rendering
- **Apache OpenNLP 2.5.0** - Text classification and language detection
- **Crawler-Commons 1.5** - Robots.txt parsing
- **Lombok** - Reduce boilerplate code
- **JUnit 5 & Mockito** - Testing framework
- **JaCoCo** - Code coverage reporting

## Prerequisites

### Required
- **Java 21** or higher
- **Maven 3.6+**
- **Google Chrome** (for Selenium WebDriver)

### Optional
- **Docker** (for containerized deployment)
- **Nginx** (for reverse proxy setup)

## Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd web-crawler
```

### 2. Build the Project
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

Or run the packaged JAR:
```bash
mvn clean package
java -jar target/web-crawler-1.0.0-SNAPSHOT.jar
```

### 4. Access the Application
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **API Endpoint**: http://localhost:8080/api/v1/crawler/crawl

## Configuration

### Application Properties

Located in `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Jsoup Configuration
jsoup.user-agent=Mozilla/5.0 (compatible; WebCrawler/1.0)
jsoup.timeout=30000
jsoup.follow-redirects=true
jsoup.max-body-size=1048576

# Selenium Configuration
selenium.enabled=true
selenium.headless=true
selenium.page-load-timeout=30
selenium.implicit-wait=10
selenium.javascript-wait-time=3000

# Logging
logging.level.com.webcrawler=INFO
```

### Selenium Configuration

To disable JavaScript rendering:
```properties
selenium.enabled=false
```

## API Usage

### Crawl Endpoint

**POST** `/api/v1/crawler/crawl`

**Request Body:**
```json
{
  "url": "https://example.com"
}
```

**Response:**
```json
{
  "url": "https://example.com",
  "title": "Example Domain",
  "content": "This domain is for use in illustrative examples...",
  "h1Tags": ["Example Domain"],
  "classification": {
    "categories": [
      {
        "category": "technology",
        "confidence": 0.85
      },
      {
        "category": "education",
        "confidence": 0.72
      },
      {
        "category": "business",
        "confidence": 0.65
      }
    ],
    "language": "eng",
    "languageConfidence": 0.95
  },
  "statusCode": 200,
  "success": true,
  "errorMessage": null
}
```

**cURL Example:**
```bash
curl -X POST "http://localhost:8080/api/v1/crawler/crawl" \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com"}'
```

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Tests with Coverage Report
```bash
mvn clean test
```

### View Coverage Report
After running tests, open:
```
target/site/jacoco/index.html
```

### Current Test Coverage
- **Instruction Coverage**: 50%
- **Branch Coverage**: 16%
- **Total Tests**: 74
- **Test Suites**: 11

### Test Categories
- **Unit Tests**: Service layer, Model validation, Config tests
- **Integration Tests**: Controller tests with Spring context
- **Application Tests**: Full application context loading

## Project Structure

```
web-crawler/
├── src/
│   ├── main/
│   │   ├── java/com/webcrawler/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── model/            # DTOs and data models
│   │   │   └── service/          # Business logic
│   │   └── resources/
│   │       ├── application.properties
│   │       └── models/           # OpenNLP model files
│   └── test/
│       └── java/com/webcrawler/  # Test classes
├── pom.xml
└── README.md
```

## Content Classification Categories

The application classifies content into the following categories:

1. **News** - Breaking news, current events, journalism
2. **Sports** - Athletics, games, competitions
3. **Technology** - Tech news, software, gadgets, innovation
4. **Health** - Medical, wellness, fitness
5. **Entertainment** - Movies, music, celebrities, TV
6. **Business** - Finance, economy, corporate news
7. **Education** - Learning, academic content
8. **Other** - General content not fitting above categories

## Deployment

### Package for Production
```bash
mvn clean package -DskipTests
```

### Deploy to EC2

#### 1. Transfer JAR to EC2
```bash
scp -i your-key.pem target/web-crawler-1.0.0-SNAPSHOT.jar ec2-user@<EC2_IP>:/opt/web-crawler/
```

#### 2. Create Systemd Service
Create `/etc/systemd/system/web-crawler.service`:
```ini
[Unit]
Description=Web Crawler Service
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/opt/web-crawler
ExecStart=/usr/bin/java -jar web-crawler-1.0.0-SNAPSHOT.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

#### 3. Start Service
```bash
sudo systemctl daemon-reload
sudo systemctl enable web-crawler
sudo systemctl start web-crawler
sudo systemctl status web-crawler
```

### Docker Deployment (Optional)

Create `Dockerfile`:
```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/web-crawler-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
docker build -t web-crawler .
docker run -p 8080:8080 web-crawler
```

## Troubleshooting

### Selenium/Chrome Issues
If JavaScript rendering fails:
1. Ensure Chrome is installed: `google-chrome --version`
2. Check Selenium logs in application output
3. Set `selenium.enabled=false` to disable JavaScript rendering

### Memory Issues
Increase JVM heap size:
```bash
java -Xmx2G -jar web-crawler-1.0.0-SNAPSHOT.jar
```

### Port Already in Use
Change port in `application.properties`:
```properties
server.port=8081
```

## Development

### Build Without Tests
```bash
mvn clean install -DskipTests
```

### Run in Development Mode
```bash
mvn spring-boot:run
```

### Check Code Coverage
```bash
mvn clean test jacoco:report
open target/site/jacoco/index.html
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Documentation

Detailed documentation is available in the `/docs` folder:

- **[PRD (Product Requirements Document)](docs/PRD%20v0.md)** - Product vision and requirements
- **[HLD (High-Level Design)](docs/HLD%20v0.md)** - System architecture and design
- **[LLD (Low-Level Design)](docs/LLD%20v0.md)** - Detailed technical implementation
- **[MVP and Further Phases](docs/MVP%20and%20Further%20phases.md)** - Product roadmap
- **[POC Breakdown](docs/POC%20Break%20Down.md)** - Proof of concept details
- **[Ownership Structure](docs/Ownership%20Structure.md)** - Team and ownership model
- **[Cost Breakdown](docs/Cost%20Breakdown.md)** - Infrastructure and operational costs
- **[SLO/SLA Definition](docs/SLO_SLA_Definition.md)** - Service level objectives and agreements
- **[Release Checklist](docs/Release_Checklist.md)** - Pre-deployment verification steps

## Support

For issues and questions:
- Create an issue in the repository
- Check existing documentation in `/docs`
- Review API documentation at `/swagger-ui/index.html`
