Aquí tienes la traducción completa al español:

---

# Estructura del Proyecto

## Clases separadas por paquetes

### Auth (Autenticación)

- **AuthenticationController**: Maneja las solicitudes de registro e inicio de sesión.  
- **AuthenticationRequest**: Objeto tipo DTO que contiene únicamente los datos necesarios para autenticarse con Spring Security.  
- **AuthenticationResponse**: Representa el token de un usuario autenticado. Esta clase se utiliza para devolver el token al cliente después de un inicio de sesión exitoso.  
- **AuthenticationService**: Contiene la lógica de negocio relacionada con la autenticación de usuarios.  
- **RegisterRequest**: Objeto tipo DTO que contiene únicamente los datos necesarios para registrar un usuario con Spring Security.

### Config (Configuración)

- ApplicationConfig  
- JwtAuthenticationFilter  
- JwtService  
- SecurityConfig  

### User (Usuario)

- Role  
- User  
- UserRepository  

---

## Descripción de métodos por clase

### JwtService

**Objetivo**: Proporcionar un conjunto de métodos utilitarios para trabajar con JWT.

- `extractAllClaims`: Extrae todo el payload del token.  
- `extractClaim`: Método genérico útil para extraer una "claim" específica del usuario.  
- `generateToken`: Genera un token sin "claims" privadas, usando su contraparte sobrecargada. Por eso se le pasa un `HashMap` vacío.  
- `generateToken`: Genera un token con "claims" privadas y toda la información necesaria.  
- `isTokenValid`: Verifica que el nombre de usuario en el token coincida con el usuario autenticado (`UserDetails`). También verifica que el token no haya expirado.  
- `isTokenExpired`: Usa el método `extractExpiration` y verifica si la fecha de expiración ya pasó.  
- `extractExpiration`: Usa el método `extractClaim` con ayuda de programación funcional en Java, pasando el método `getExpiration` y aplicándolo sobre el `Map` de "claims".  
- `getSignInKey`: Decodifica la clave secreta usando base64 y crea una clave segura para firmar con HMAC.

---

# Aprendido por curiosidad

- El primer filtro que interceptará la solicitud HTTP será: `JwtAuthFilter`.  
- No es necesario especificar modificadores de acceso dentro de una interfaz, ya que todo es público. Ejemplo:
```java
public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByEmail(String email);
}
```
- La anotación de Lombok `@AllRequiredArguments` asegura que los campos `final` sean inicializados.  
- Los campos y variables locales `final` pueden asignarse después de su declaración.  
- `::` se llama el operador de referencia a método.  
- `HashMap` es una implementación concreta de `Map`, mientras que `Map` es solo una interfaz.  
- El patrón *Builder* ofrece beneficios sobre la creación directa de objetos y configuración de campos, como la inmutabilidad.  
	- Esto ocurre porque cuando se usa el patrón *builder*, no se están estableciendo campos en el objeto final, sino en el *builder*, cuyos campos suelen ser `private final`, ofreciendo así inmutabilidad.
- Una clase anónima es una clase sin nombre, declarada e instanciada de una sola vez. Útil para comportamientos de un solo uso.  
- Los métodos *bean* por defecto son singleton, a menos que se cambie su alcance (*scope*).  
- `WebAuthenticationDetailsSource` funciona como convertidor de objetos `HttpServletRequest`. Retorna un nuevo objeto `WebAuthenticationDetails`.  
	> Su única responsabilidad es convertir una instancia de `HttpServletRequest` en una instancia de `WebAuthenticationDetails`. Se puede pensar como un convertidor simple.  
	> — [Respuesta de StackOverflow](https://stackoverflow.com/a/69816853)
- El método `setDetails` es heredado de `AbstractAuthenticationToken`.  
- La interfaz `Customizer` sirve como envoltorio para expresiones lambda o referencias a método, permitiendo personalizar objetos de configuración de forma *type-safe* (segura en cuanto a tipos).

---

## Específico de JWT

- Estas tres dependencias forman parte de la librería JJWT (Java JWT):
	- `jjwt-api`: Proporciona las interfaces y clases principales para trabajar con JWT, como `JwtBuilder`, `JwtParser`, `Claims`, etc.
	- `jjwt-impl`: Contiene las implementaciones internas de las interfaces del API definidas en `jjwt-api`.
	- `jjwt-jackson`: Proporciona soporte para serialización/deserialización JSON usando Jackson.
- En el contexto de JJWT, un objeto `Jws<T>` representa un JWT firmado que ha sido exitosamente analizado y verificado.
- `Jwts` es una clase utilitaria estática, mientras que `Jws<T>` es una interfaz que representa el resultado del análisis de un JWT.
- [DefaultJwtBuilder (Documentación)](https://javadoc.io/doc/io.jsonwebtoken/jjwt-impl/0.12.2/io/jsonwebtoken/impl/DefaultJwtBuilder.html)

---

# Aprendido por cometer errores

- La clave de firma debe tener al menos **64 caracteres hexadecimales** o **32 caracteres ASCII normales**. De lo contrario, se lanza el siguiente error:

```
io.jsonwebtoken.security.WeakKeyException: The specified key byte array is 168 bits which is not secure enough for any JWT HMAC-SHA algorithm.  The JWT JWA Specification (RFC 7518, Section 3.2) states that keys used with HMAC-SHA algorithms MUST have a size >= 256 bits (the key size must be greater than or equal to the hash output size).  Consider using the io.jsonwebtoken.security.Keys#secretKeyFor(SignatureAlgorithm) method to create a key guaranteed to be secure 
enough for your preferred HMAC-SHA algorithm.  See https://tools.ietf.org/html/rfc7518#section-3.2 for more information.
```

- Cuando uses un archivo `.env` con la librería **Dotenv**, asegúrate de estar leyendo la variable correcta. Por ejemplo, si intentas leer `JWT_SECRET`, asegúrate de que no haya otra variable en el entorno local con el mismo nombre, ya que Dotenv podría leer esa en lugar del valor dentro del archivo.

---

# Mis aportes

## Lógica de email

No se permite un email repetido. Se hicieron cambios en `AuthenticationService` y en la entidad `User` para garantizar integridad de datos tanto a nivel de aplicación como de base de datos.

- En el método `register` de `AuthenticationService`:
```java
    userRepository.findByEmail(registerRequest.getEmail())
        .ifPresent(user -> {
          throw new IllegalArgumentException("Email already in use.");
        });
```

- Restricción en la entidad:
```java
  @Column(unique = true)
  private String email;
``` 

- También añadí la anotación de validación `@Email` en la clase `RegisterRequest` (tipo DTO), para aceptar solo direcciones válidas de correo electrónico:
```java
  @Email
  private String email;
```
