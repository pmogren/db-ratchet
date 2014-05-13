package com.commercehub.dbratchet.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.CleanRule
import org.gradle.api.tasks.bundling.Jar

/**
 * Created by jgelais on 5/8/2014.
 */
class DBRatchetPlugin implements Plugin<Project> {

    public static final String DATABASE_JAR_TASK_NAME = 'databaseJar'
    public static final String BUILD_TASK_NAME = 'build'
    public static final String PACKAGE_TASK_NAME = 'package'
    public static final String DB_RATCHET_EXTENSION_NAME = 'dbRatchet'
    public static final String DB_RATCHET_CONFIGURATION_NAME = 'dbRatchet'

    @Override
    void apply(Project project) {
        project.logger.info("Applying db-ratchet plugin to $project.name")
        project.apply(plugin: 'base')

        DBRatchetExtension extension = project.extensions.create(DB_RATCHET_EXTENSION_NAME, DBRatchetExtension, project)
        if (!project.configurations.asMap[DB_RATCHET_CONFIGURATION_NAME]) {
            project.configurations.create(DB_RATCHET_CONFIGURATION_NAME)
            project.afterEvaluate {
                project.dependencies {
                    dbRatchet("com.commercehub:db-ratchet-core:${extension.dbRatchetVersion}")
                }
            }
        }

        populateTaskGraph(project)
    }

    void populateTaskGraph(Project project) {
        // databaseJar task
        project.tasks.create(name: DATABASE_JAR_TASK_NAME, type: Jar)
        Jar databaseJarTask = project.tasks.getByName(DATABASE_JAR_TASK_NAME)
        databaseJarTask.destinationDir = new File(project.buildDir, 'libs')
        databaseJarTask.archiveName = 'schemaAndData.jar'
        databaseJarTask.into('schema') {
            from "${project.rootDir}/versions"
        }
        databaseJarTask.into('data') {
            from "${project.rootDir}/data"
        }

        // package task
        project.tasks.create(name: PACKAGE_TASK_NAME, type: PackageTask, dependsOn: databaseJarTask)
        PackageTask packageTask = project.tasks.getByName(PACKAGE_TASK_NAME)

        // build task
        project.tasks.create(name: BUILD_TASK_NAME, type: DefaultTask, dependsOn: packageTask)
    }
}
