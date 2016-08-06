package sic.link;


import sic.Link;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LinkerGui {

    public static void gui(Options options, List<String> inputs, LinkListener listener) {

        JFrame frame = new JFrame("Select .obj files to link");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // central scrollpane
        DefaultListModel<String> model = new DefaultListModel();
        JList list = new JList(model);
        JScrollPane scrollPane = new JScrollPane(list);

        //top name input
        JTextField name = new JTextField(20);
        JLabel label = new JLabel("Output obj file:");
        JPanel topPanel = new JPanel();
        topPanel.add(label);
        topPanel.add(name);

        // bottom buttons
        JButton add = new JButton("Add .obj files");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                fc.setFileFilter(new FileNameExtensionFilter("Sic object files", "obj"));
                fc.setMultiSelectionEnabled(true);

                if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File[] files = fc.getSelectedFiles();
                    for (File f : files)
                        model.addElement(f.getPath());
                }
            }
        });

        JButton done = new JButton("Link");
        done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Options opt = new Options();
                if (options != null)
                    opt = options;

                String strName = name.getText();

                if (strName.length() <= 0) {
                    showError("Output file not specified");
                    return;
                } else if (!strName.endsWith(".obj")) {
                    strName += ".obj";
                }

                File out = new File(strName);
                opt.setOutputName(out.getName());
                opt.setOutputPath(out.getAbsolutePath());

                List<String> in = new ArrayList<String>();
                for (int i=0; i<model.size(); i++)
                    in.add(model.get(i));

                try {
                    File file = Link.link(opt, in);

                    //notify the simulator
                    if (listener != null)
                        listener.onLinked(file, "0");

                    frame.dispose();
                } catch (LinkerError linkerError) {
                    System.err.println(linkerError.getMessage());

                    //notify the simulator
                    if (listener != null)
                        listener.onLinked(null, linkerError.getMessage());
                }

            }
        });

        JButton up = new JButton("Up");
        up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selection = list.getSelectedIndex();
                if (selection > 0 && selection < model.size()) {
                    String selectedStr = model.remove(selection);
                    model.add(selection - 1, selectedStr);
                    list.setSelectedIndex(selection - 1);
                }
            }
        });
        up.setEnabled(false);

        JButton down = new JButton("Down");
        down.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selection = list.getSelectedIndex();
                if (selection >= 0 && selection < model.size()-1) {
                    String selectedStr = model.remove(selection);
                    model.add(selection + 1, selectedStr);
                    list.setSelectedIndex(selection + 1);
                }
            }
        });
        down.setEnabled(false);

        JButton remove = new JButton("Remove");
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selection = list.getSelectedIndex();
                if (selection >= 0 && selection < model.size()) {
                    model.remove(selection);
                }
            }
        });
        remove.setEnabled(false);

        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (list.getSelectedIndex() == -1) {
                    up.setEnabled(false);
                    down.setEnabled(false);
                    remove.setEnabled(false);
                } else {
                    up.setEnabled(true);
                    down.setEnabled(true);
                    remove.setEnabled(true);
                }
            }
        });


        JPanel botPanel = new JPanel();
        JPanel botLeft = new JPanel();
        JPanel botRight = new JPanel();
        botLeft.add(add);
        botLeft.add(done);
        botPanel.add(botLeft);

        botPanel.add(new JToolBar.Separator());

        botRight.add(up);
        botRight.add(down);
        botRight.add(remove);
        botPanel.add(botRight);

        // fill with any info already given
        if (options != null) {
            if (options.getOutputPath() != null)
                name.setText(options.getOutputPath());
            if (options.describeOptions().length() > 0)
                topPanel.add(new JLabel("flags:" + options.describeOptions()));
        }
        if (inputs != null) {
            for (String str : inputs)
                model.addElement(str);
        }

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(botPanel, BorderLayout.SOUTH);

        frame.setSize(720, 560);
        frame.setVisible(true);

    }

    public static class GuiLinkListener implements LinkListener {

        @Override
        public void onLinked(File f, String message) {
            if (f != null) {
                showSuccess(f.getAbsolutePath());
            } else {
                showError(message);
            }
        }

    }

    public static void showSuccess(String path) {
        JOptionPane optionPane = new JOptionPane("Linking successful, output: " + path, JOptionPane.PLAIN_MESSAGE);
        JDialog dialog = optionPane.createDialog("Success");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    public static void showError(String error) {
        JOptionPane optionPane = new JOptionPane(error, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = optionPane.createDialog("Error");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }
}
