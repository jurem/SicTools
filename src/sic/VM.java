package sic;

import sic.common.Logger;
import sic.loader.Loader;
import sic.sim.Args;
import sic.sim.Executor;
import sic.sim.addons.GraphicalScreen;
import sic.sim.addons.TextualScreen;
import sic.sim.addons.Addon;
import sic.sim.addons.AddonLoader;
import sic.sim.vm.Machine;

import java.io.IOException;
import java.util.*;

/**
 * Created by jure on 9. 02. 16.
 */
public class VM {

    public static final int Version_Major = 2;
    public static final int Version_Minor = 0;
    public static final int Version_Patch = 1;

    static void printHelp() {
        System.out.print(
            "Sic/XE Virtual Machine " + Version_Major + "." + Version_Minor + "." + Version_Patch + "\n" +
            "Usage: java sic.VM options parameters\n" +
            "Options:\n");
        Args.printArgs();
    }

    public static void main(String[] args) throws Exception {
        Args arg = new Args(args);
        if (arg.isHelp()) {
            printHelp();
            System.exit(0);
        }

        Vector<Addon> addons = new Vector<Addon>();
        addons.add(AddonLoader.loadInternal("sic.sim.addons.stdio.Main"));
        if (arg.isTextScr()) {
            addons.add(AddonLoader.loadInternal("sic.sim.addons.text.Main"));
        }

        for (Args.AddonArgs a : arg.getAddons()) {
            try {
                Addon p = AddonLoader.loadJar(a.path);
                p.load(a.pars);
                addons.add(p);
            } catch (IOException e) {
                System.out.printf("cannot open addon file %s%n", a.path);
                System.out.println(e);
                System.exit(1);
            } catch (ClassCastException e) {
                System.out.printf("main class of %s does not extend Addon class%n", a.path);
                System.exit(1);
            }
        }

        Machine machine = new Machine();
        Executor executor = new Executor(machine, arg);

        Vector<TimerTask> timerTasks = new Vector<TimerTask>();
        for (Addon a : addons) {
            a.init(executor);
            machine.devices.setDevices(a.getDevices());
            Vector<TimerTask> tasks = a.getTimerTasks();
            if (tasks != null) {
                timerTasks.addAll(tasks);
            }
        }

        if (arg.hasFilename()) {
            String ext = arg.getFileext();
            if ("asm".equals(ext)) Loader.loadAsm(machine, arg.getFilename());
            else if ("obj".equals(ext)) Loader.loadObj(machine, arg.getFilename());
            else Logger.fmterr("Invalid filename extension '%s'", ext);
        }

        final TextualScreen textScreen = arg.isTextScr() ? new TextualScreen(executor) : null;
        final GraphicalScreen graphicalScreen = arg.isGraphScr() ? new GraphicalScreen(executor) : null;

        if (arg.isGraphScr() || arg.isTextScr()) {
            if (textScreen != null) {
                textScreen.setSize(arg.getTextScrCols(), arg.getTextScrRows());
                textScreen.toggleView();
            }
            if (graphicalScreen != null) {
                graphicalScreen.setSize(arg.getGraphScrCols(), arg.getGraphScrRows());
                graphicalScreen.toggleView();
            }
            java.util.Timer timer = new java.util.Timer();
            for (TimerTask t : timerTasks) {
                timer.schedule(t, 0, 50);
            }
            TimerTask timerTask = new TimerTask() {
                public void run() {
                    if (textScreen != null) textScreen.updateView();
                }
            };
            timer.schedule(timerTask, 0, 50);
        }

        executor.start();

        // trick to close the application if any window is open
        while (executor.isRunning()) {
            Thread.sleep(500);
        }
        System.exit(0);
    }

}
