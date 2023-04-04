package sic.sim.addons;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Vector;
import java.util.jar.Attributes;

import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AddonLoader extends URLClassLoader {
    private URL url;
    public AddonLoader(URL url) {
        super(new URL[] {url});
        this.url = url;
    }

    public String getMainClassName() throws IOException {
        URL u = new URL("jar", "", url + "!/");
        JarURLConnection uc = (JarURLConnection)u.openConnection();
        Attributes attr = uc.getMainAttributes();
        return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
    }

    private static Addon loadAddon(String className, ClassLoader cl) {
        try {
            Class<?> c = cl.loadClass(className);
            Class<? extends Addon> addonClass = c.asSubclass(Addon.class);
            Constructor<? extends Addon> constructor = addonClass.getConstructor();
            Addon a =  constructor.newInstance();
            return a;
        } catch (NoSuchMethodException e) {
            System.out.println(e);
            System.exit(1);
        } catch (InstantiationException e) {
            System.out.println(e);
            System.exit(1);
        } catch (IllegalAccessException e) {
            System.out.println(e);
            System.exit(1);
        } catch (InvocationTargetException e) {
            System.out.println(e);
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.out.println("should not happen");
            System.out.println(e);
            System.exit(1);
        }
        return null;
    }

    public static Addon loadJar(String filepath) throws IOException {
        try {
            if (!filepath.endsWith(".jar")) {
                filepath = filepath + ".jar";
            }
            File addon = new File(filepath);
            AddonLoader jarLoader = new AddonLoader(addon.toURI().toURL());
            String className;
            className = jarLoader.getMainClassName();
            return loadAddon(className, jarLoader);
        } catch (MalformedURLException e) {
            System.out.println(e);
        }
        return null;
    }

    public static Addon loadInternal(String className) {
        ClassLoader cl = AddonLoader.class.getClassLoader();
        return loadAddon(className, cl);
    }
}
