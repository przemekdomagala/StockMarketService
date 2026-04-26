# StockMarketService
Service that simulates a simplified stock market.

# How to run
This project is containerized to support all major OS and architectures (x64/arm64)

### Command start
Ensure you have docker installed.

For windows os run
```run.bat <port>```
where port is the port you want to use.

For linux/macOS os run
```./run.sh <port>```
where port is the port you want to use.

If you will not provide port, the default port is 8080.

### Database
You can view the in-memory database in your browser.
* URL: `http://localhost:<port>/h2-console`
* JBDC URL: `jdbc:h2:mem:stockexchange`
* USER: `sa` (no password)