# DRES Example Client: Kotlin

Back to the [DRES Example Clients](../README.md)

In this project, there is example code on how to use the DRES Client library for kotlin.

The main example code is in the [Client.ks](src/main/kotlin/dev/dres/example/Client.kt) file.

## Setup

Please run
```
./gradlew openApiGenerate
```

to get the client side bindings.

**NOTE AS OF OCTOBER 2023 THERE IS A BUG IN THE OPENAPI GENERATOR AND SOME GENERATED CODE REQUIRES A FIX**

Due to https://github.com/OpenAPITools/openapi-generator/issues/16714 , the file `ApiClient.kt` which has been generated has to be replaced with the [a fixed version](https://gist.github.com/sauterl/25e7ce2a68d943422a4307a26c05d02f).
