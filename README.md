# Online bookstore app

This application provides a backend functionality of a standard bookshop. It has a full spectre of operations and tools needed to run such project.
As a user, one can register, search for books, add them to the shopping cart , and create orders. 
An administrator, on the other hand, can create/delete/update new books and categories. 
In addition, this project supports JWT authentication functionality.
---
## Key features:

- Registration and authorisation as a user
- Administration functionality such as creating/deleting/updating books and categories
- Users can add books to their shopping cart and then transfer them to an order
- Users can view their order history
- Users can search for books by categories, authors, and titles
- Other CRUD functionality for administration
---

## Project structure

The project consists of following packages:

- config - classes for Mapping(Mapstruct) and Security configuration.
- controller - classes that represent controllers for REST functionality
- dto - classes for creating data transfer objects
- exception - custom exceptions and global exception handler realization
- lib - validation functionality
- model - classes that represent key entities such as User, Book, ShoppingCart, etc.
- repository - interfaces that extend JpaRepository to execute CRUD operations with the DB
- security - classes that ensure security of the application, for example authentication
- service - classes that implement business logic

---
## Controllers(Endpoints)
### AuthController 
* `POST: /auth/register` - endpoint for user registration
* `POST: /auth/login` - endpoint for user authentication

### BookController
* `POST: /books` - endpoint for saving new books in the DB (ADMIN only)
* `GET: /books` - endpoint to get the list of all books
* `GET: /books/{id}` - endpoint to get a specific book by id
* `DELETE: /books/{id}` - endpoint for deleting a specific book by id (safe delete, ADMIN only)
* `PUT: /books/{id}` - endpoint to update a specific book by id (ADMIN only)
* `GET: /books/search` - endpoint to search for books by parameters

### CategoryController
* `GET: /categories` - endpoint to get the list of all categories
* `GET: /categories/{id}/books` - endpoint to get the list of all books by a specific category
* `POST: /categories` - endpoint for saving new categories in the DB (ADMIN only)
* `GET: /categories/{id}` - endpoint to get a specific category by id
* `PUT: /categories/{id}` - endpoint to update a specific category by id (ADMIN only)
* `DELETE: /categories/{id}` - endpoint for deleting a specific category by id (safe delete, ADMIN only)

### OrderController
* `POST: /orders` - endpoint to create a new order for the current user
* `GET: /orders` - endpoint to get the list of all orders of the current user
* `PUT: /orders/{id}` - endpoint for updating an order by its id
* `GET: /orders/{id}/items` - endpoint to get a list of all order items of a specific order
* `GET: /{orderId}/items/{id}` - endpoint to get a specific order item in a specific order

### ShoppingCartController
* `GET: /cart` - endpoint for getting all information about the shopping cart of a current user
* `POST: /cart` - endpoint for adding a cart item to the shopping cart of a current user
* `PUT: /cart/cart-items/{id}` - endpoint for update a cart item in a specific shopping cart
* `DELETE: /cart/cart-items/{id}` - endpoint for deleting a cart item by its id from a specific cart

---
## Technologies used:

- Spring Boot
- Spring Data JPA
- Spring Boot Web
- Spring Security
- Spring Boot Testing
- Maven
- Docker
- Hibernate validator
- JWT
- Mapstruct
- Lombok
- Liquibase
- MySQL connector
- Intellij Idea

----

## How to deploy the project:

### Pre-requirements:

* Java installed 
* Intellij Idea installed
* Docker installed
* Maven installed
* Postman for sending HTTP requests installed (optional)

1) Download the project via gitHub or run the command `git clone https://github.com/waspp01/online-book-store-app.git`
2) Unzip the archive in the appropriate folder on your machine
3) Open the project with Intellij IDEA
4) Customize the `docker-compose.yml` for your environment
5) Run the following commands in terminal:

`mvn package`

`docker-compose build`

`docker-compose up`

6) Access the Swagger at `http://localhost:8088/swagger-ui/index.html`.
7) Log in using the following credentials:
`login: admin@gmail.com`
`password: 123456`

Feel free to test all endpoints.
    