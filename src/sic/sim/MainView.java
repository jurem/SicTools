package sic.sim;

import sic.asm.Assembler;
import sic.asm.ErrorCatcher;
import sic.ast.Program;
import sic.common.GUI;
import sic.common.Utils;
import sic.disasm.Disassembler;
import sic.loader.Loader;
import sic.sim.addons.GraphicalScreen;
import sic.sim.addons.TextualScreen;
import sic.sim.views.CPUView;
import sic.sim.views.DisassemblyView;
import sic.sim.views.MemoryView;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class MainView {
    private final Executor executor;

    private JFrame mainFrame;
    // core views
    private CPUView cpuView;
    private DisassemblyView disassemblyView;
    private MemoryView memoryView;
    // addon views
    private TextualScreen textScreen;
    private GraphicalScreen graphScreen;


    public MainView(final Executor executor, Disassembler disassembler) {
        this.executor = executor;

        cpuView = new CPUView(executor, disassembler);
        disassemblyView = new DisassemblyView(executor, disassembler);
        memoryView = new MemoryView(executor);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(cpuView.mainPanel, BorderLayout.NORTH);
        panel.add(disassemblyView.mainPanel, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.WEST);
        mainPanel.add(memoryView.mainPanel, BorderLayout.CENTER);

        mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setJMenuBar(createMenuBar());
        mainFrame.setContentPane(mainPanel);
        mainFrame.pack();
        mainFrame.setLocation(0, 0);
        mainFrame.setVisible(true);

        textScreen = new TextualScreen(executor);
        graphScreen = new GraphicalScreen(executor);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                if (mainFrame.isVisible() && executor.hasChanged()) {
                    updateView();
                }
                textScreen.updateView();
                graphScreen.updateView();
            }
        };
        timer.schedule(timerTask, 0, 50);
    }

    public void updateView() {
        cpuView.updateView();
        disassemblyView.updateView(!executor.isRunning(), !executor.isRunning());
        memoryView.updateView();
    }

    private JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();

        // SicSim
        JMenu menu = new JMenu("SicTools");
        GUI.addMenuItem(menu, "About", KeyEvent.VK_A, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(mainFrame, "SicTools: SIC/XE assembler and simulator 1.1\n\n(C) 2011-2015, Jurij Mihelič (jurij.mihelic@fri.uni-lj.si)\n\nContributors\nTomaž Dobravec (tomaz.dobravec@fri.uni-lj.si)");
            }
        });
        menu.addSeparator();
        GUI.addMenuItem(menu, "Settings", KeyEvent.VK_S, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showSettingsView();
            }
        });
        menu.addSeparator();
        GUI.addMenuItem(menu, "Quit", KeyEvent.VK_Q, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
            }
        });
        mb.add(menu);

        // Machine
        menu = new JMenu("Machine");
        GUI.addMenuItem(menu, "Load asm", KeyEvent.VK_A, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File file = GUI.openFileDialog(mainFrame, new FileNameExtensionFilter("Sic assembler files", "asm"));
                if (file != null) loadAsm(file);
            }
        });
        GUI.addMenuItem(menu, "Load obj", KeyEvent.VK_O, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File file = GUI.openFileDialog(mainFrame, new FileNameExtensionFilter("Sic object files", "obj"));
                if (file != null) loadObj(file);
            }
        });
        menu.addSeparator();
        GUI.addMenuItem(menu, "Clear all", KeyEvent.VK_C, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                executor.getMachine().registers.reset();
                executor.getMachine().memory.reset();
                updateView();
            }
        });
        GUI.addMenuItem(menu, "Clear registers", KeyEvent.VK_R, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                executor.getMachine().registers.reset();
                updateView();
            }
        });
        GUI.addMenuItem(menu, "Clear memory", KeyEvent.VK_M, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                executor.getMachine().memory.reset();
                updateView();
            }
        });
        mb.add(menu);

        menu = new JMenu("View");
        GUI.addMenuItem(menu, "Textual screen", KeyEvent.VK_S, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                textScreen.toggleView();
            }
        });
        GUI.addMenuItem(menu, "Graphical screen", KeyEvent.VK_S, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                graphScreen.toggleView();
            }
        });
        mb.add(menu);

        return mb;
    }

    public void load(File file) {
        String ext = Utils.getFileExtension(file.getName());
        if ("asm".equals(ext))
            loadAsm(file);
        else if ("obj".equals(ext))
            loadObj(file);
        else
            JOptionPane.showMessageDialog(mainFrame, "Invalid filename extension.");
    }

    public void loadObj(File file) {
        try {
            Reader reader = new FileReader(file);
            Loader.loadSection(executor.machine, reader);
        } catch (FileNotFoundException e1) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading object file.");
            updateView();
        }
    }

    public void loadAsm(File file) {
        Assembler assembler = new Assembler();
        ErrorCatcher errorCatcher = assembler.errorCatcher;
        Program program = assembler.assemble(Utils.readFile(file));
        if (errorCatcher.count() > 0) {
            errorCatcher.print();
            return;
        }
        //
        Writer writer = new StringWriter();
        assembler.generateObj(program, writer, false);
        Reader reader = new StringReader(writer.toString());
        Loader.loadSection(executor.machine, reader);
        updateView();
    }


    private JPanel createSettingsGeneralPane() {
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(1, 2, 0, 0));

        final JTextField txtFreq = GUI.createField(pane, "Frequency", Integer.toString(executor.getSpeed()), 10);
        txtFreq.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    executor.setSpeed(Integer.parseInt(txtFreq.getText()));
                } catch (NumberFormatException e) {}
            }
        });
        return pane;
    }

    private void showSettingsView() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.addTab("General", null, createSettingsGeneralPane(), null);
        tabs.addTab("Textual screen", null, textScreen.createSettingsPane(), null);
        tabs.addTab("Graphical screen", null, graphScreen.createSettingsPane(), null);
        GUI.showInJFrame("Settings", tabs, 0, 0);
    }

}
