package sic;

import sic.common.Mnemonics;
import sic.disasm.Disassembler;
import sic.sim.Args;
import sic.sim.Executor;
import sic.sim.MainView;
import sic.sim.vm.Machine;

import javax.swing.UIManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Simulator of the SIC/XE computer.
 *
 * @author jure
 */
public class Sim {

    public static final int Version_Major = 2;
    public static final int Version_Minor = 0;
    public static final int Version_Patch = 1;

    // TODO: -freq 10
    // -registers
    // -memory start len
    // -debug level

    static void printHelp() {
        System.out.print(
                "Sic/XE Simulator " + Version_Major + "." + Version_Minor + "." + Version_Patch + "\n" +
                        "Usage: java sic.Sim options parameters\n" +
                        "Options:\n");
        Args.printArgs();
    }


    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        ToolTipManager.sharedInstance().setDismissDelay(15000);
//        UIManager.put("ToolTip.font", new FontUIResource("Courier New", Font.PLAIN, 14));
        //
        Args processedArgs = new Args(args);

        if (processedArgs.isHelp()) {
            printHelp();
            return;
        }

        Machine machine = new Machine();
        Executor executor = new Executor(machine, processedArgs);
        Disassembler disassembler = new Disassembler(new Mnemonics(), machine);

        final MainView mainView = new MainView(executor, disassembler, processedArgs);

        if (processedArgs.hasFilename()) mainView.load(new File(processedArgs.getFilename()));

        mainView.updateView();

        executor.onBreakpoint = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mainView.updateView();
            }
        };

        if (processedArgs.isStart()) {
            executor.start();
        }
    }
}
