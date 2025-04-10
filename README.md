# jwt-basics description
Simple repository in which I practice the basics of JWT. I followed this tutorial: https://www.youtube.com/watch?v=KxqlJblhzfI, but I made some tweaks to improve it.
# Project Structure
## Classes separated by their packages
### Auth
- **AuthenticationController**: Handles registration and login requests.
- **AuthenticationRequest**: A DTO-like object that contains only the necessary data for authenticating with Spring Security.
- **AuthenticationResponse**: Represents the token of a registered user. This class is used to return the token to the client upon successful login.
- **AuthenticationService**: Contains the business logic related to user authentication.
- **RegisterRequest**: A DTO-like object that contains only the necessary data for user registration with Spring Security.
### Config
- ApplicationConfig
- JwtAuthenticationFilter
- JwtService
- SecurityConfig
### User
- Role
- User
- UserRepository
## Methods description for each class
### JwtService
Objective: Provide a set of method utilities for JWT-related purposes.
- extractAllClaims: Extract the payload of the token.
- extractClaim: Generic method, useful for extracting a specific user claim.
- generateToken: Generates a token without private claims, using its overloaded counterpart. That's why it passes an empty HashMap.
- generateToken: Generates a token with private claims, and all the rest of the necessary information.
- isTokenValid: Verifies that the token's username, is equals to the authenticated user with UserDetails. It also verifies the token hasn't expired.
- isTokenExpired: Makes use of the extractExpiration method, and verifies if the expiration date was or not, before the current moment.
- extractExpiration: Makes use of extractClaim method with the help of functional programming in java, passing the getExpiration method, and applying it in all the Map of claims. 
- getSignInKey: Decodes the secret key using base64 decoder, and creates a a secure key for signing with HMAC.
# Learned by being curious
- The first filter that will intercept the http request will be: JwtAuthFilter.
- it is not necessary to specify access modifiers inside an interface since all are public. This is an example of what I've just said:
``` java
public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByEmail(String email);
}
```
- Lombok annotation @AllRequiredArguments, make sure final fields are initialized. 
- Final fields and local variables can be assigned after declaration.
- `::` is called the method reference operator.
- HashMap is an actual implementation of Map, Map is just an interface.
- The builder pattern provides some benefits over just creating an object, and the setting up the required fields. One of them if immutability, which is normally considered a good thing.
	- This happens because when using the builder pattern, one is not actually setting fields in the target object, but in the builder, and fields in the builder are normally private final, hence builder provides immutability.
- Anonymous class syntax: class without a name, declared and instantiated all at once. Useful for single-use behavior.
- Bean methods by default are singleton unless one changes their scope.
- WebAuthenticationDetailsSource works as a converter for HttpServletRequest objects. It returns a new WebAuthenticationDetails object.
	> It has a single responsibility to convert an instance of `HttpServletRequest` class into an instance of the `WebAuthenticationDetails` class. You can think of it as a simple converter.
	>  -- [StackOverflow Response](https://stackoverflow.com/a/69816853)
- setDetails method is one of the inheritated methods from AbstractAuthentication.
- The main job of a Customizer interface is to act as a wrapper for lambda expressions or method references — allowing you to customize configuration objects in a type-safe way.
## JWT specific
- These three dependencies are all part of the JJWT (Java JWT) library:
	- jjwt-api: Provides the core interfaces and classes for working with JWTs — this includes things like: JwtBuilder, JwtParser, Claims, etc.
	- jjwt-impl: Contains the internal implementations of the API interfaces from jjwt-api.
	- jjwt-jackson: Provides JSON serialization/deserialization support using Jackson, which is a JSON library.
- In the context of JJWT (Java JWT), a Jws< T > object represents a signed JWT that has been successfully parsed and verified.
- Jwts is a static utility class, while Jws< T > is an interface that represents the result of parsing a JWT.
- [DefaultJwtBuilder](https://javadoc.io/doc/io.jsonwebtoken/jjwt-impl/0.12.2/io/jsonwebtoken/impl/DefaultJwtBuilder.html)
# Learned by making mistakes
- The signing key must have at least 64 hex characters, or 32 regular ASCII characters. Otherwise, this error will be thrown:
```
io.jsonwebtoken.security.WeakKeyException: The specified key byte array is 168 bits which is not secure enough for any JWT HMAC-SHA algorithm.  The JWT JWA Specification (RFC 7518, Section 3.2) states that keys used with HMAC-SHA algorithms MUST have a size >= 256 bits (the key size must be greater than or equal to the hash output size).  Consider using the io.jsonwebtoken.security.Keys#secretKeyFor(SignatureAlgorithm) method to create a key guaranteed to be secure 
enough for your preferred HMAC-SHA algorithm.  See https://tools.ietf.org/html/rfc7518#section-3.2 for more information.
```
- When using a `.env` file with the Dotenv library, be mindful of which variables you're actually reading. For example, if you're trying to read a variable like `JWT_SECRET`, make sure there isn't another variable—such as a local environment variable—with the same name. Otherwise, Dotenv might read the unexpected one instead of the value from your `.env` file.
# My additions
## Email logic
Not repeated email allowed. Changes in AuthenticationService, and User Entity to guarantee Application and Database level data integrity.
- AuthenticationService, register method:
``` java
    userRepository.findByEmail(registerRequest.getEmail())
        .ifPresent(user -> {
          throw new IllegalArgumentException("Email already in use.");
        });
```

- Entity constraint:
``` java
  @Column(unique = true)
  private String email;
``` 
- I also added the Bean Validation annotation @Email in the RegisterRequest DTO-like class, this way only valid emails will be accepted, instead of any string.
``` java
  @Email
  private String email;
```
