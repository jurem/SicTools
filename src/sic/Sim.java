package sic;

import sic.common.Mnemonics;
import sic.disasm.Disassembler;
import sic.sim.Executor;
import sic.sim.MainView;
import sic.sim.vm.Machine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Simulator of the SIC/XE computer.
 *
 * @author jure
 */
public class Sim {

    // TODO: -freq 10
    // -registers
    // -memory start len
    // -debug level


    public static void main(String[] args) throws Exception {
//        ToolTipManager.sharedInstance().setDismissDelay(15000);
//        UIManager.put("ToolTip.font", new FontUIResource("Courier New", Font.PLAIN, 14));
        //
        Machine machine = new Machine();
        Executor executor = new Executor(machine);
        Disassembler disassembler = new Disassembler(new Mnemonics(), machine);

        final MainView mainView = new MainView(executor, disassembler);

        if (args.length > 0) mainView.load(new File(args[0]));

//        executor.start();
        mainView.updateView();

        executor.onBreakpoint = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mainView.updateView();
            }
        };
    }
}
