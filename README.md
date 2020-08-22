# tutorial-app project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `tutorial-app-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/tutorial-app-1.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/tutorial-app-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.

## Building an image

https://quarkus.io/guides/container-image

### Building
Install appropriate extension
`./mvnw quarkus:add-extension -Dextensions="container-image-docker"`

Configure which extension should run to build image om application.properties
`quarkus.container-image.builder=docker`

or remove if doesn't work
`./mvnw quarkus:add-extension -Dextensions="container-image-jib"`

### Pushing
Remember to setup all the necessary dependencies on your registry, for docker.io just create a new repository with the application name.

Set `DOCKER_USER` and `DOCKER_PASSWD` environment variables to your docker.io credentials (on linux `export` both variables).

Push the image 
`./mvnw clean package -Dquarkus.container-image.push=true`


#### Docker image for this project
Docker image are available at [DockerHub](https://hub.docker.com/r/joelbars/quarkusnative).