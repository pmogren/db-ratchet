package com.commercehub.dbratchet.util

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
    static Collection<String> getResources(final Pattern pattern) {
        List<String> retval = [] as Queue<String>
        String classPath = System.getProperty('java.class.path', '.')
        String[] classPathElements = classPath.split(':')
        for (String element : classPathElements) {
            retval.addAll(getResourcesFromClasspathElement(element, pattern))
        }
        return retval
    }

    private static Collection<String> getResourcesFromClasspathElement(final String element, final Pattern pattern) {
        List<String> retval = [] as Queue<String>
        File file = new File(element)
        if (file.isDirectory()) {
            retval.addAll(getResourcesFromDirectory(file, pattern))
        } else {
            retval.addAll(getResourcesFromJarFile(file, pattern))
        }
        return retval
    }

    private static Collection<String> getResourcesFromJarFile(final File file, final Pattern pattern) {
        List<String> retval = [] as Queue<String>
        ZipFile zf
        try {
            zf = new ZipFile(file)
            Enumeration e = zf.entries()
            while (e.hasMoreElements()) {
                ZipEntry ze = (ZipEntry) e.nextElement()
                String fileName = ze.name
                boolean accept = pattern.matcher(fileName).matches()
                if (accept) {
                    retval.add(fileName)
                }
            }
        } finally {
            zf.close()
        }
        return retval
    }

    private static Collection<String> getResourcesFromDirectory(final File directory, final Pattern pattern) {
        List<String> retval = [] as Queue<String>
        File[] fileList = directory.listFiles()
        for (File file : fileList) {
            if (file.isDirectory()) {
                retval.addAll(getResourcesFromDirectory(file, pattern))
            } else {
                String fileName = file.canonicalPath
                boolean accept = pattern.matcher(fileName).matches()
                if (accept) {
                    retval.add(fileName)
                }
            }
        }
        return retval
    }
}
