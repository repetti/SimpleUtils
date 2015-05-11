package org.repetti.utils.jar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author repetti
 */
public class JarHelperMini {

    /**
     * When true, debug information will be printed
     */
    static final boolean VERBOSE = true;

    /**
     * @param regex     jar filename regex to be matched for extract to take place
     * @param outputDir output folder
     * @return list of files extracted
     * @throws JarException if something bad happens
     */
    public static List<File> extractAll(String regex, File outputDir) throws JarException {
        final List<File> ret = new ArrayList<File>();
        System.out.println("pd: " + JarHelperMini.class.getProtectionDomain());
        CodeSource src = JarHelperMini.class.getProtectionDomain().getCodeSource();
        if (src == null) {
            throw new JarException("src == null", null);
        }
        URL jar = src.getLocation();
        final ZipInputStream zip;
        try {
            zip = new ZipInputStream(jar.openStream());
        } catch (IOException e) {
            throw new JarException("unable to read jar file", e);
        }
        while (true) {
            final ZipEntry e;

            try {
                e = zip.getNextEntry();
            } catch (IOException e1) {
                throw new JarException("unable to read jar file (next entry)", e1);
            }
//            System.out.println(e);
            if (e == null) {
                break;
            }
            final String name = e.getName();
            if (name.matches(regex)) {
                if (VERBOSE) {
                    System.out.println("+ " + name);
                }
                //File.pathSeparatorChar
                File f = new File(outputDir, name.contains("/") ? name.substring(name.lastIndexOf('/')) : name);
                if (VERBOSE) {
                    System.out.println("  -> " + f);
                }
                f.delete();
                try {
                    f.createNewFile();
                } catch (IOException e1) {
                    throw new JarException("Unable to create new file: " + f, e1);
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(f);
                } catch (FileNotFoundException e1) {
                    throw new JarException("Unable to open new file: " + f, e1);
                }
                int len;
                try {
                    final byte[] buffer = new byte[100];
                    while ((len = zip.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                } catch (IOException e1) {
                    throw new JarException("Unable to extract: " + f, e1);
                }
                try {
                    fos.close();
                } catch (IOException ignore) {
                }
                ret.add(f);
            } else {
                if (VERBOSE) {
                    System.out.println("  " + name);
                }
            }
            try {
                zip.closeEntry();
            } catch (IOException ignore) {
            }
        }
        if (VERBOSE) {
            System.out.println("all extracted");
        }
        return ret;
    }

    /**
     * Creates temporary folder
     *
     * @return path for created temporary folder
     */
    public static Path createDir() throws JarException {
        final Path p;
        try {
            p = Files.createTempDirectory("prefix", new FileAttribute[]{});
        } catch (IOException e) {
            throw new JarException("unable to create temporary directory", e);
        }
        return p;
    }

    public static File extract(File f, File directory) throws JarException {
        File dst = new File(directory, f.getName());
        FileChannel source = null;
        FileChannel destination = null;

        try {
            if (!dst.createNewFile()) {
                throw new JarException("file already exists: " + dst, null);
            }
        } catch (IOException e) {
            throw new JarException("unable to create the file: " + dst, e);
        }

        try {
            source = new FileInputStream(f).getChannel();
            destination = new FileOutputStream(dst).getChannel();
            destination.transferFrom(source, 0, source.size());
            //Files.copy(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new JarException("unable to extract file: " + f + " to " + dst, e);
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                    if (VERBOSE) {
                        e.printStackTrace(System.err);
                    }
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (IOException e) {
                    if (VERBOSE) {
                        e.printStackTrace(System.err);
                    }

                }
            }
        }
        return dst;
    }
}
