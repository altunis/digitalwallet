<br>After you navigate the root directory, you can easily run the project with mvn spring-boot:run command. So maven should be installed.

<br>When you type localhost:8080 on browser, the application navigates you to the login (localhost:8080/login) page which you can log in with admin user.

<br>Credentials of admin Username : admin Password : 1234 .

<br>Credentials of employee Username : EMPLOYEE Password : 1234

<br>After a successful login, you will be redirected to the Swagger UI page, where you can perform all the operations authorized for the user. By using the swagger ui, you can create customers 
and set their password via the APIs.

<br>You can invoke **/logout** URL to logout and log in again with regular customer so you can test the other cases.

<br>You can access the in-memory database via http://localhost:8080/h2-console You can see the default credentials and datasource url on application.properties.

<br>The unit tests are written under src/test package. They cover most of the cases.
