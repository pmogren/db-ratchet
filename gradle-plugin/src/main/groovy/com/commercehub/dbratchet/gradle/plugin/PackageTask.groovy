package com.commercehub.dbratchet.gradle.plugin

import org.gradle.api.artifacts.Configuration
import org.gradle.api.java.archives.Manifest
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.bundling.Jar

/**
 * Created by jgelais on 5/9/2014.
 */
class PackageTask extends Jar {
    public static final String MAIN_CLASS = 'com.commercehub.dbratchet.OnejarDeployerMain'

    Logger logger
    File oneJarBuildDir
    Configuration targetConfiguration

    PackageTask() {
        logger = project.logger
        oneJarBuildDir  = new File(project.buildDir, 'one-jar-build')
        description = 'Create a One-JAR runnable archive that will install the ' +
                'database configured in this directory structure.'

        targetConfiguration = project.configurations.dbRatchet

        // set db-ratchet as classifier if unspecified
        if (!classifier || classifier.isEmpty()) {
            classifier = 'db-ratchet'
        }

        Jar databaseJar = project.tasks.databaseJar
        inputs.files([databaseJar.archivePath.absoluteFile])
        outputs.file(new File(databaseJar.archivePath.parentFile.absolutePath, archiveName))

        doFirst {
            oneJarBuildDir.mkdirs()

            // unpack OneJar root layout to build dir
            unpackOneJarBoot(oneJarBuildDir.absolutePath)

            // create main/main.jar from the current project's jar
            ant.copy(file: databaseJar.archivePath.absolutePath,
                    toFile: new File(oneJarBuildDir, "lib/${databaseJar.archiveName}"))

            // copy /lib/* from the current project's runtime dependencies
            def libs = targetConfiguration.resolve()
            libs.each {
                logger.debug("Including dependency: ${it.absolutePath}")
                ant.copy(file: it,
                        todir: new File(oneJarBuildDir.absolutePath, 'lib'))
            }

            File finalJarFile = generateOneJar(databaseJar)
            logger.debug("Built One-JAR: ${finalJarFile.absolutePath}")
        }
    }

    /**
     * Unpack one-jar-boot to create the one-jar base layout.
     */
    @SuppressWarnings('FileCreateTempFile')
    void unpackOneJarBoot(targetDir) {

        // pull one-jar-boot out of the classpath to this file
        def oneJarBootFile = File.createTempFile('one-jar-boot', '.jar')
        oneJarBootFile.deleteOnExit()
        logger.debug("Extacting temporary boot file: ${oneJarBootFile.absolutePath}")

        def oneJarBootFilename = 'one-jar-boot-0.97.1.jar'
        outputResourceFromClasspath(oneJarBootFilename, oneJarBootFile)

        ant.unzip(
                src: oneJarBootFile.absolutePath,
                dest: targetDir,
                failOnEmptyArchive: true,
        ) {
            ant.patternset(excludes: 'src/**, boot-manifest.mf')
        }
    }

    /**
     * Return the destination File for the output of the final One-JAR archive..
     */
    File generateOneJar(Jar jar) {
        File manifestFile = writeOneJarManifestFile(manifest)

        File finalJarFile = new File(jar.destinationDir, archiveName)
        ant.jar(destfile: finalJarFile,
                basedir: oneJarBuildDir.absolutePath,
                manifest: manifestFile.absolutePath)
        return finalJarFile
    }

    /**
     * Return a manifest configured to boot the jar using One-JAR and then
     * passing over control to the configured main class.
     */
    @SuppressWarnings('FileCreateTempFile')
    File writeOneJarManifestFile(Manifest manifest) {
        File manifestFile = File.createTempFile('one-jar-manifest', '.mf')
        manifestFile.deleteOnExit()

        manifestFile.withWriter { writer ->
            manifest.attributes.put('Main-Class', 'com.simontuffs.onejar.Boot')
            manifest.attributes.put('One-Jar-Main-Class', MAIN_CLASS)
            manifest.attributes.put('One-Jar-Show-Expand', false)
            manifest.attributes.put('One-Jar-Confirm-Expand', false)
            manifest.writeTo(writer)
        }
        return manifestFile
    }

    /**
     * Pull a resource out of the current classpath and write a copy of it to
     * the given location.
     *
     * @param classpathName
     * @param outputFile
     */
    private void outputResourceFromClasspath(String classpathName, File outputFile) {
        outputFile.delete()
        outputFile.withOutputStream { os ->
            os << PackageTask.getResourceAsStream("/${classpathName}")
        }
    }
}
