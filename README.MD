# MCPing
A simple library to ping minecraft servers

# Releases
## Gradle/Maven
To use MCPing with Gradle/Maven you can follow the instructions on [maven central](https://central.sonatype.com/search?q=net.lenni0451%20MCPing).

Gradle:
`````groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "net.lenni0451:MCPing:1.0.0"
}
`````

Maven:
`````xml
<dependency>
    <groupId>net.lenni0451</groupId>
    <artifactId>MCPing</artifactId>
    <version>1.0.0</version>
</dependency>
`````
Make sure to use the latest version from maven central.
## Jar File
You can download the compiled jar file from my [Jenkins server](https://build.lenni0451.net/job/MCPing/).\
Gson is required and not included in the jar file.

# Usage
Implemented ping protocols:
- Modern (>= 1.7)
- Legacy (<= 1.6)
- Classic
- Bedrock
- Query (default/full)
- Socket

The only class you need to ping a server is the ``MCPing`` class.\
The class has a builder pattern to easily create a ping request.

After creating the request you can use the ``getSync()``, ``getAsync()`` or ``getAsync(ExecutorService)`` methods to get the ping result.

To set listener for the ping result you can use the ``handler(IStatusListener)`` method to listen for all ping states.\
To set listener for each specific ping state you can use the following methods:
- ``exceptionHandler(Consumer<Throwable>)``
- ``responseHandler(Consumer<? extends IPingResponse>)``
- ``finishHandler(Consumer<? extends IPingResponse>)``

## Example:
Get the ping result of a modern server synchronously.\
No handler is set so exceptions will be thrown.
`````java
MCPing.pingModern(761).address("lenni0451.net").getSync();
`````

Ping a legacy server using the 1.5 protocol.\
The ping result is printed to the console and the future is joined.
`````java
MCPing.pingLegacy(LegacyPing.Version.V1_5).responseHandler(response -> {
    System.out.println(response);
}).address("lenni0451.net").getAsync().join();
`````

Ping a modern server and correctly handle all status responses.
`````java
MCPing
    .pingModern()
    .address("lenni0451.net")
    .noResolve()
    .exceptionHandler(t -> {
        //TODO: Show error message if no response was received yet
        //It is recommended to not cancel the entire ping process if a response was already received
        //See the javadoc of IStatusListener for more information
    })
    .responseHandler(response -> {
        //TODO: Show response like online players, max players, motd, ...
    })
    .finishHandler(response -> {
        //TODO: Also show ping time
    })
    .getAsync();
`````
