package com.commercehub.dbratchet.util

import groovy.io.FileType

import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Created by jgelais on 5/15/2014.
 */
class ClasspathUtil {
    /**
     * for all elements of java.class.path get a Collection of resources Pattern
     * pattern = Pattern.compile(".*"); gets all resources
     *
     * @param pattern
     *            the pattern to match
     * @return the resources in the order they are found
     */
    static Collection<String> getResources(final String path, final Pattern pattern) {
        List<String> retval = [] as Queue<String>
        String classPath = System.getProperty('java.class.path', '.')
        String[] classPathElements = classPath.split(System.getProperty('path.separator'))
        for (String element : classPathElements) {
            retval.addAll(getResourcesFromClasspathElement(element, path, pattern))
        }
        return retval
    }

    private static Collection<String> getResourcesFromClasspathElement(final String element, final String path,
                                                                       final Pattern pattern) {
        List<String> retval = [] as Queue<String>
        File file = new File(element)
        if (file.isDirectory()) {
            retval.addAll(getResourcesFromDirectory(file, path, pattern))
        } else {
            retval.addAll(getResourcesFromJarFile(file, path, pattern))
        }
        return retval
    }

    private static Collection<String> getResourcesFromJarFile(final File file, final String path,
                                                              final Pattern pattern) {
        List<String> retval = [] as Queue<String>
        ZipFile zf
        try {
            zf = new ZipFile(file)
            Enumeration e = zf.entries()
            while (e.hasMoreElements()) {
                ZipEntry ze = (ZipEntry) e.nextElement()
                String resourceName = "/${ze.name}"
                if (resourceName.contains("$path/") && resourceName.length() > path.length() + 1) {
                    String fileName = resourceName[path.length() + 1..-1]
                    if (pattern.matcher(fileName).matches()) {
                        retval.add(resourceName)
                    }
                }
            }
        } finally {
            if (zf != null) {
                zf.close()
            }
        }
        return retval
    }

    private static Collection<String> getResourcesFromDirectory(final File directory, final String path,
                                                                final Pattern pattern) {
        List<String> retval = [] as Queue<String>
        File dirToScan = new File(directory, path)
        if (dirToScan.exists() && dirToScan.directory) {
            dirToScan.eachFileRecurse(FileType.FILES) { file ->
                if (pattern.matcher(file.name).matches()) {
                    retval.add(file.path)
                }
            }
        }
        return retval
    }
}
