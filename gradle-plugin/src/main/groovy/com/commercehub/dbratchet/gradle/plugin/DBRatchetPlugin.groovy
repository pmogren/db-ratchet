package com.commercehub.dbratchet.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
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
    public static final String VERSIONS_DIR = 'versions'
    public static final String DATA_DIR = 'data'

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
                    dbRatchet('net.sourceforge.jtds:jtds:1.2.4')
                }
            }
        }

        populateTaskGraph(project)
    }

    void populateTaskGraph(Project project) {
        // package task
        project.tasks.create(name: PACKAGE_TASK_NAME, type: Jar)
        Jar packageTask = project.tasks.getByName(PACKAGE_TASK_NAME)
        packageTask.dependsOn project.configurations.dbRatchet
        packageTask.classifier = 'db-ratchet'
        packageTask.inputs.files([new File(project.rootDir, VERSIONS_DIR), new File(project.rootDir, DATA_DIR)])
        packageTask.manifest {
            attributes('Main-Class': 'com.commercehub.dbratchet.OnejarDeployerMain')
        }
        packageTask.doFirst {
            packageTask.from((project.configurations.dbRatchet).collect {
                it.isDirectory() ? it : project.zipTree(it)
            }) {
                exclude 'META-INF/*.SF'
                exclude 'META-INF/*.DSA'
                exclude 'META-INF/*.RSA'
                exclude 'META-INF/DEPENDENCIES'
                exclude 'META-INF/LICENSE'
                exclude 'META-INF/NOTICE'
            }
            packageTask.into(VERSIONS_DIR) {
                from "${project.rootDir}/${VERSIONS_DIR}"
            }
            packageTask.into(DATA_DIR) {
                from "${project.rootDir}/${DATA_DIR}"
            }
        }

        // build task
        project.tasks.create(name: BUILD_TASK_NAME, type: DefaultTask, dependsOn: packageTask)
    }
}
