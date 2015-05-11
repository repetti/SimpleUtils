package org.repetti.utils.jar;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author repetti
 */
public class LibHelperMini {

    /**
     * Adds libraries (*.jar) from the directory (recursive only if loading "fromClassPath")
     * <p/>
     * PS. Method can be removed if not needed.
     *
     * @throws JarException the only possible exception. RuntimeExceptions are packed into JarExceptions
     */
    public static void loadLibs(List<File> files) throws JarException {
        List<File> elements = new LinkedList<File>(files);
        Set<URL> urls = new HashSet<URL>();
        while (!elements.isEmpty()) {
            File f = elements.remove(0);

            if (!f.exists()) {
                throw new JarException("File not found: " + f, null);
            }
            if (f.isFile()) {
                urls.add(fileToURL(f));
            }
            if (f.isDirectory()) {
                System.out.println(f + " is a directory");
                File[] tmp = f.listFiles();
                if (tmp == null) {
                    throw new JarException("Some error occurred. Unable to list files: " + f, null);
                }
                elements.addAll(0, Arrays.asList(tmp));
            }
        }
        loadLibsURLs(urls);
    }

    private static URL fileToURL(File f) throws JarException {
        try {
            return f.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new JarException("Malformed URL for file: " + f, e);
        }
    }

    /**
     * Adds libraries (*.jar) from the directory (recursive only if loading "fromClassPath")
     *
     * @throws JarException the only possible exception. RuntimeExceptions are packed into JarExceptions
     */
    public static void loadLibsURLs(Collection<URL> urls) throws JarException {
        try {
            System.out.println("Urls to be added to classpath: " + urls);
            addSoftwareLibrary(urls);
        } catch (Exception e) {
            if (e instanceof JarException) {
                throw (JarException) e;
            }
            throw new JarException("Unexpected RuntimeException", e);
        }
    }

    /**
     * Adds all the urls to the system ClassLoader using reflection
     *
     * @param urls list of URLs
     */
    private static void addSoftwareLibrary(Collection<URL> urls) throws JarException {
        final Method method;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
        } catch (NoSuchMethodException e) {
            throw new JarException(null, e);
        }
        method.setAccessible(true);
        for (URL u : urls) {
            addJar(method, u);
        }
    }

    /**
     * Executes private URLClassLoader method "addURL"
     *
     * @param method addURL
     * @param url    path to the jar/resource file
     * @throws JarException on security/path exceptions
     */
    private static void addJar(Method method, URL url) throws JarException {
        try {
            method.invoke(ClassLoader.getSystemClassLoader(), url);
        } catch (IllegalAccessException e) {
            throw new JarException("the underlying method is inaccessible", e);
        } catch (InvocationTargetException e) {
            throw new JarException("method throws an exception", e);
        }
    }
}
