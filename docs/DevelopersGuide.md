#db-ratchet Developer's Guide
db-ratchet is written in Groovy and deploys with the groovy-all jar for version 2.1.7 of Groovy. Because this dependency is managed in the build script and 
the application is deployed with the groovy runtime, there is no dependency on a specific Groovy version being installed. In fact a Groovy installation is
not required to build and run db-ratchet. The Developer/User just needs a Java runtime 1.5 or higher.

##Building db-ratchet
db-ratchet's build is scripted in [Gradle](http://www.gradle.org/) 1.9 and includes a gradle wrapper. This build script uses Gradle plugin support for 
generating project files for both Eclipse and IntelliJ. This build script also includes the *Application Plugin* from Gradle. Run `gradlew installApp` 
to install your test copy locally between builds.

##Code Quality and Testing
The build script for db-ratchet runs the [CodeNarc](http://www.gradle.org/docs/current/userguide/codenarc_plugin.html) plugin for Gradle. CodeNarc is a static
analysis tool for Groovy. A rule set specific to the db-ratchet project is configured in config/codenarc/codenarc.xml of this Git repository. The build 
is configured to fail the build for any rule violations. Please keep this code clean.

The build script for db-ratchet runs Cobertura to capture code coverage data. We use an open source 
[Cobertura Plugin](https://github.com/stevesaliman/gradle-cobertura-plugin). To capture code coverage data run the **cobertura** task in gradle. Due to an issue with
the current version of this plugin, cobertura will print a stacktrace to the console during instrumentation claiming that it can't load several classes in the project.
Despite this noise in the console, instrumentation will complete successfully and a code coverage report will be generated. For now, these stack traces can be ignored.
