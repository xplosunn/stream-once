# StreamOnce

Not yet released.

Safe and flexible streaming library for Kotlin with zero dependencies. 

If that sentence means nothing to you, imagine that you have a very large file and would like to make a list with the lines that start with "//". The following code allows you to do so without blowing up your memory:

```kotlin
val lines: List<String> = BoundedStreamOnce.linesFromFile(pathToFile)
    .filter { it.startsWith("//") }
    .toList()
```

While that probably does not look like something you'll need to do, you might want to process individually little pieces of data from a huge source of data. This library is meant to help with such use cases.  

## How to use

In order to have up to date and working examples, we keep them as tests. 

* [Getting started](src/test/kotlin/streamonce/example/GettingStartedTests.kt)
* [Combining streams](src/test/kotlin/streamonce/example/CombiningStreamsTests.kt)
* [Error handling](src/test/kotlin/streamonce/example/ErrorHandlingTests.kt)
