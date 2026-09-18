Run ./gradlew build
Fetching distribution.
Downloading https://services.gradle.org/distributions/gradle-8.7-bin.zip
............10%.............20%.............30%.............40%............50%.............60%.............70%.............80%.............90%............100%

Welcome to Gradle 8.7!

Here are the highlights of this release:
 - Compiling and testing with Java 22
 - Cacheable Groovy script compilation
 - New methods in lazy collection properties

For more details see https://docs.gradle.org/8.7/release-notes.html

Starting a Gradle Daemon (subsequent builds will be faster)

> Configure project :
Fabric Loom: 1.6.12
:remapping 56 mods from modImplementation (java-api)

> Task :compileJava
/home/runner/work/supralandmod/supralandmod/src/main/java/ru/supraland/entity/SupralandNpcEntity.java:55: error: method does not override or implement a method from a supertype
    @Override
    ^
/home/runner/work/supralandmod/supralandmod/src/main/java/ru/supraland/UpgradeChestBlock.java:42: error: method does not override or implement a method from a supertype
    @Override
    ^
Note: Some input files use or override a deprecated API.

Note: Recompile with -Xlint:deprecation for details.
> Task :compileJava FAILED
2 errors
1 actionable task: 1 executed

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':compileJava'.
> Compilation failed; see the compiler error output for details.

* Try:
> Run with --info option to get more log output.
> Run with --scan to get full insights.

BUILD FAILED in 56s
Error: Process completed with exit code 1.
