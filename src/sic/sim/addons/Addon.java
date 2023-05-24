package sic.sim.addons;

import java.awt.event.ActionListener;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.KeyStroke;

import sic.sim.vm.Device;
import sic.sim.Executor;
import sic.sim.MainView;

public abstract class Addon {
    // Mehtod load is called immediately after the addon has been loaded.
    public void load(String params) {
        System.out.println("loading addon with parametres: " + params);
    }

    // Method init is called after the executor and graphical simulator
    // (for sic.Sim) have been initialized.
    public void init(Executor executor) {}

    public Vector<AddonDevice> getDevices() {
        return null;
    }

    public static class AddonDevice {
        public int name;
        public Device dev;
        public boolean force;
        public AddonDevice(int name, Device dev, boolean force) {
            this.name = name;
            this.dev = dev;
            this.force = force;
        }
    }

    // Timers execute their tasks at constant rate.
    public Vector<Timer> getTimers() {
        return null;
    }

    public static class Timer {
        public TimerTask task;
        public long refreshMs;
        public Timer(TimerTask task, long refreshMs) {
            this.task = task;
            this.refreshMs = refreshMs;
        }
    }

    // MenuEntries are added to the "Addons" menu in the menu bar.
    public Vector<MenuEntry> getMenuEntries() {
        return null;
    }

    public static class MenuEntry {
        public String name;
        public int keyEvent;
        public KeyStroke keyStroke;
        public ActionListener actionListener;
        public MenuEntry(String name, int keyEvent, KeyStroke keyStroke, ActionListener actionListener) {
            this.name = name;
            this.keyEvent = keyEvent;
            this.keyStroke = keyStroke;
            this.actionListener = actionListener;
        }
    }

    // SettingsPanels are added as tabs to the settings window.
    public Vector<SettingsPanel> getSettingsPanels() {
        return null;
    }

    public static class SettingsPanel {
        public String title;
        public JPanel panel;
        public SettingsPanel(String title, JPanel panel) {
            this.title = title;
            this.panel = panel;
        }
    }
}
