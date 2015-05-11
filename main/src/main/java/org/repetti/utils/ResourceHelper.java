package org.repetti.utils;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

/**
 * @author repetti
 */
public class ResourceHelper {
    private static final Logger log = LoggerFactory.getLogger(ResourceHelper.class);
    private static final List<URL> emptyListOfURLs = Collections.emptyList();
    private static final ClassLoader classLoader = ResourceHelper.class.getClassLoader();
    private static final String FILE = "file";
    private static final String JAR = "jar";

    public static String getPackageVersion(Class c) {
        return c.getPackage().getImplementationVersion();
    }

    //AA build info: aa v.0.1 b.0 by x at 20131004.1300
    public static String getBuildInfo(Class c) {
        final Package p = c.getPackage();
        return p.getImplementationTitle() + " v." + p.getImplementationVersion();
    }

    public static InputStream getStream(String filename, boolean fromJar) throws UtilsException {
        return fromJar ?
                getStreamFromJar(filename) :
                getStreamFromFile(getFile(filename, false));
    }

    public static InputStream getStreamFromJar(String filename) {
        InputStream inputStream = ResourceHelper.class.getClassLoader().getResourceAsStream(filename);
        if (inputStream == null) {
            log.warn("getStreamFromJar: file not found ({})", filename);
        }
        return inputStream;
    }

    public static FileInputStream getStreamFromFile(File file) throws UtilsException {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new UtilsException(UtilsException.Type.NOT_FOUND, "file not found: " + file, e);
        }
    }

    public static File getFile(String filename, boolean fromResource) throws UtilsException {
        try {
            if (fromResource) {
                URL resourceUrl = ResourceHelper.class.getClassLoader().getResource(filename);
                if (resourceUrl == null) {
                    throw new UtilsException(UtilsException.Type.NOT_FOUND, "not found: " + filename, null);
                }
                return new File(resourceUrl.toURI());
            } else {
                return new File(filename);
            }
        } catch (URISyntaxException e) {
            throw new UtilsException(UtilsException.Type.PARAMETERS, "Unable to get file information (path)", e);
        }
    }

    /**
     * This method throws no exceptions
     *
     * @return null on error
     */
    public static Properties getPropertiesFromStream(InputStream inputStream) {
        if (inputStream == null) {
            log.warn("getPropertiesFromStream: inputStream=null");
            return null;
        }
        Properties prop = new Properties();
        try {
            prop.load(inputStream);
        } catch (IOException e) {
            log.error("getPropertiesFromStream: IOException reading file");
            return null;
        }
        return prop;
    }

    @Deprecated
    public static String getFileAsString(File file) throws UtilsException {
        return readFileAsString(file);
    }

    /**
     * Uses Scanner to read the file
     *
     * @throws UtilsException if FileNotFoundException is thrown
     */
    public static String readFileAsString(File file) throws UtilsException {
        Scanner s;
        try {
            s = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new UtilsException(UtilsException.Type.NOT_FOUND, "file not found: " + file, e);
        }
        return s.useDelimiter("\\Z").next();
    }

    public static void writeFile(File file, String text) throws UtilsException {
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new UtilsException(UtilsException.Type.CONNECTION, "unable to create file: " + file, e);
        }
        if (!file.canWrite()) {
            throw new UtilsException(UtilsException.Type.FORBIDDEN, "writing to file is not allowed: " + file, null);
        }
        BufferedWriter bw = null;
        try {
            try {
                bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            } catch (IOException e) {
                throw new UtilsException(UtilsException.Type.FORBIDDEN, "unable open file: " + file, e);
            }
            try {
                bw.write(text);
            } catch (IOException e) {
                throw new UtilsException(UtilsException.Type.CONNECTION, "unable write to file: " + file, e);
            }
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    log.warn("Exception while closing file {}", file, e);
                }
            }
        }
    }

    @NotNull
    public static List<URL> listURLs() {
        if (classLoader instanceof URLClassLoader) {
            URLClassLoader ucl = (URLClassLoader) classLoader;
            return Arrays.asList(ucl.getURLs());
        }
        return emptyListOfURLs;
    }

    /**
     * Tries to find file in both classpath and current directory and read it
     * Lookup order:
     * 1. in classpath
     * 2. in JAR
     * 3. current directory
     *
     * @param filename file to read (can be path)
     * @return String representation of file contents (UTF-8)
     * @throws UtilsException if file not found or cannot be read
     */
    @NotNull
    public static String readFile(@NotNull String filename) throws UtilsException {
        final URL u = classLoader.getResource(filename);
        urlBl:
        if (u != null) {
            final String protocol = u.getProtocol();
            if (FILE.equals(protocol)) {
                log.trace("{} is a file in classpath: {}", filename, u);
                try {
                    File f = new File(u.toURI());
                    return readFileAsString(f);
                } catch (URISyntaxException e) {
                    log.trace("Unable to make a file from URL[{}]", u, e);
                    break urlBl;
                }
            } else if (JAR.equals(protocol)) {
                log.trace("{} is a par of jar in classpath: {}", filename, u);
                InputStream inputStream;
                try {
                    inputStream = u.openStream();
                } catch (IOException e) {
                    log.trace("Unable to get resource as stream: {}", u, e);
                    break urlBl;
                }
                if (inputStream == null) {
                    log.trace("Unable to get resource as stream: {}", u);
                    break urlBl;
                }
                return getStreamAsString(inputStream);
//                return getStreamFromJar(filename);//classLoader.getResourceAsStream(filename)
            }
        }
        File f = new File(filename);
        if (f.exists() && f.isFile()) {
            log.trace("{} is a file in current directory: {}", filename, f);
            return readFileAsString(f);
        }
        log.trace("'{}' not found anywhere", filename);
        throw UtilsException.Type.NOT_FOUND.build("File not found: " + filename, null);
    }

    public static String getStreamAsString(@NotNull InputStream inputStream) throws UtilsException {
        Scanner s = new Scanner(inputStream);
        return s.useDelimiter("\\Z").next();
    }

    public static String getStreamAsString(@NotNull InputStream inputStream, @NotNull String charsetName) throws UtilsException {
        Scanner s = new Scanner(inputStream, charsetName);
        return s.useDelimiter("\\Z").next();
    }

    @NotNull
    public static Set<URL> getURLs(@NotNull String filename) throws UtilsException {
        Set<URL> ret = new HashSet<URL>();
        try {
            Enumeration<URL> e = classLoader.getResources(filename);
            while (e.hasMoreElements()) {
                ret.add(e.nextElement());
            }
        } catch (IOException e1) {
            log.trace("exception getting URLs from resources", e1);
        }
        File f = new File(filename);
        if (f.exists() && f.isFile()) {
            try {
                ret.add(f.toURI().toURL());
            } catch (MalformedURLException e) {
                log.trace("exception converting file to URL", e);
            }
        }
        return ret;
    }

    @NotNull
    public static String readFile(@NotNull Collection<URL> urls) throws UtilsException {
        for (URL u : urls) {
            final String protocol = u.getProtocol();
            if (FILE.equals(protocol)) {
                try {
                    File f = new File(u.toURI());
                    return readFileAsString(f);
                } catch (URISyntaxException e) {
                    log.trace("Unable to make a file from URL[{}]", u, e);
                    continue;
                }
            } else if (JAR.equals(protocol)) {
                InputStream inputStream;
                try {
                    inputStream = u.openStream();
                } catch (IOException e) {
                    log.trace("Unable to get resource as stream: {}", u);
                    continue;
                }
                if (inputStream == null) {
                    log.trace("Unable to get resource as stream: {}", u);
                    continue;
                }
                return getStreamAsString(inputStream);
            }
        }
        throw UtilsException.Type.NOT_FOUND.build("Resources cannot be read: " + urls, null);
    }
}