# Bank Account and Transactions

This is a simple banking application that consists of two microservices, a command service, and a query service. The application is responsible for handling bank account transactions such as account opening, credit, debit, overdraft checking, and querying the transaction history.

## Modules

The application is organized into the following modules:

###### bank-lib:
contains the common code for the command and query services.

###### bank-command-service:
provides the REST API for account transactions and exposes the Axon Framework command bus.

###### bank-query-service:
provides the REST API for querying the transaction history and exposes the Axon Framework query bus.

## Technologies

Java 8, Axon Framework - a CQRS and Event Sourcing Framework, Spring Boot, Kafka, H2 DB(In Memory), JUnit, Maven

## How to run the application

* Download Kafka and run the kafka server at port 9092
* Clone the repository: git clone https://github.com/ranjithjan21/bank-account-and-txns.git
* Navigate to the project directory: cd bank-account-and-txns
* Build the project: mvn clean install
* Start the command service: cd bank-command-service && mvn spring-boot:run
* Start the query service: cd ../bank-query-service && mvn spring-boot:run
* Navigate to the postman directory: cd bank-account-and-txns/postman and use the postman collection to test all the APIs

## REST APIs

The following REST APIs are exposed by the application:

### Command Service

| Endpoint	                                | Method | 	Description                              |
|------------------------------------------|--------|-------------------------------------------|
| /v1/bank-command/open-account            | PUT	   | Open a new account                        |
| /v1/bank-command/account/transaction     | 	PUT   | 	Credit the account with a certain amount |
| /v1/bank-command/account/transaction     | PUT    | 	Debit the account with a certain amount  |
| /v1/bank-command/account/check-overdraft | POST   | 	Check if the account has an overdraft    |

### Query Service
| Endpoint                                            | 	Method	 | Description                                                            |
|-----------------------------------------------------|----------|------------------------------------------------------------------------|
| /v1/bank-query/accounts                             | 	GET     | 	Get all accounts                                                      |
| /v1/bank-query/accounts/{accountId}	                | GET      | 	Get the account with the specified ID                                 |
| /v1/bank-query/transactions/{accountId}/{fromDate}	 | GET	     | Get all transactions for the specified account from the specified date |
| /v1/bank-query/accounts/negative-balance	           | GET      | 	Get all accounts with a negative balance                              |