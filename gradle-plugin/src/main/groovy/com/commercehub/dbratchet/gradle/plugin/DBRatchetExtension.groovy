package com.commercehub.dbratchet.gradle.plugin

import org.gradle.api.Project

/**
 * Created by jgelais on 5/8/2014.
 */
class DBRatchetExtension {
    String dbRatchetVersion = '0.2.0'

    DBRatchetExtension(Project project) {
        project.logger.info "Creating db-ratchet extension for project ${project.name}"
    }
}
