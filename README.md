# homework-lv - See [Project Details](#project-details)

## Goal
- Create a simple micro-lending rest api app similar to one of our existing products.

## Business requirements
- Applying for loan through the api - passing term and amount.
- Loan application risk analysis performed if:
  - the attempt to take loan is made after 00:00 with max possible amount.
  - reached max applications (e.g. 3) per day from a single IP.
- Loan can be extended, interest factor per week is 1.5.
- User can view their loans, including extensions.

## Technical requirements
- Backend in Java, XML-less Spring, Hibernate.
- Code quality (both production and test)
- How simple it is to run the application (embedded DB/embedded container)
- Use of spring-boot provided in this template is not obligatory, you are free to choose any other framework.

## What gets evaluated
- Requirements are met
- Code quality (both production and test)
- How simple it is to run the application (embedded DB/embedded container)

# Project Details
## Micro Lending REST API:
### Application Home
#### http://localhost:8080
- Request Type: **GET**
- Response Status: **20o0 - OK**
- Response Body:
    -     {
              "message": "Welcome to Micro lend API",
              "_links": {
                  "all-users": {
                      "href": "http://localhost:8080/api/v1/users"
                  },
                  "self": {
                      "href": "http://localhost:8080/"
                  }
              }
          }
- Navigate through the **HATEOAS** links to access the application

### User Creation
#### http://localhost:8080/api/v1/users
- Request Type: **POST**
- Request Body: raw (**JSON** - application/json)
    -     {
            "firstName": "Eden",
            "lastName": "Hazard"
          }
- Response Status: **201 - CREATED**
- Response Body:
    -     {
            "id": 1,
            "firstName": "Eden",  
            "lastName": "Hazard",  
            "_links": {  
              "self": {  
                  "href": "http://localhost:8080/api/v1/users/1"  
              },  
              "loans": {  
                  "href": "http://localhost:8080/api/v1/users/1/loans"  
              }  
            }  
          }
 
 ### Loan Creation and extending loans
 #### http://localhost:8080/api/v1/users/1/loans?amount=1500&term=3
 - Request Type: **POST**
 - Request Body: form-data (**JSON** - application/json)
     -     amount: 1500
           term: 3
 - Response Status: **201 - CREATED**
 - Response Body:
     -     {
             "id": 1,
             "amount": 1500,
             "term": 3,
             "interestRatePerWeek": 0.12,
             "user": {
                 "id": 1,
                 "firstName": "Eden",
                 "lastName": "Hazard"
               },
             "status": "APPROVED",
             "extended": false,
             "_links": {
                 "self": {
                     "href": "http://localhost:8080/api/v1/users/1/loans/1"
                 }
             }
           }
### Loan extension
#### http://localhost:8080/api/v1/users/1/loans/1/extend?term=1
 - Request Type: **POST**
 - Request Body: form-data (**JSON** - application/json)
     -     term: 1
 - Response Status: **202 - ACCEPTED**
 - Response Body:
     -     {
             "id": 1,
             "amount": 1500,
             "term": 3,
             "interestRatePerWeek": 0.18,
             "user": {
                 "id": 1,
                 "firstName": "Eden",
                 "lastName": "Hazard"
               },
             "status": "APPROVED",
             "extended": true,
             "_links": {
                 "self": {
                     "href": "http://localhost:8080/api/v1/users/1/loans/1"
                 }
             }
           }
