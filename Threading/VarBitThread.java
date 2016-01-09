package scripts.LANScriptTools.Threading;

import java.util.HashMap;

import javax.swing.table.DefaultTableModel;

import org.tribot.api.General;

import org.tribot.api2007.Login;
import org.tribot.api2007.types.RSVarBit;
import scripts.LANScriptTools.Tools.VarBitTool;

/**
 * This thread continually checks for changes in the varbits.
 * 
 * @author Laniax
 *
 */
public class VarBitThread implements Runnable{

	private final VarBitTool tool;
	
	private HashMap<Integer, Integer> oldSettings;

	public VarBitThread(VarBitTool tool) {
		this.tool = tool;
	}

	@Override
	public void run() {
//		final HashMap<Integer, Integer> settings = getVarbits();



		int[] oldValues = getVarbits();

    	DefaultTableModel model = (DefaultTableModel)tool.script.dock.tableVarbits.getModel();

        for (int i = 0; i < oldValues.length; i++) {
            model.addRow(new Object[] { i, oldValues[i] } );
        }

		// Quit this thread ASAP when the scripttools thread stops.
		while (!tool.script.quitting) {

			General.sleep(50);

			if (Login.getLoginState() != Login.STATE.INGAME)
				continue;

			if (tool.doUpdate) {

				int[] values = getVarbits();

				for (int i = 0; i < values.length && i < oldValues.length; i++) {

					int old = oldValues[i];
					int cur = values[i];

					if (old != cur) {
//						 This setting changed!
							tool.writeLog("Varbit: '"+i+"' changed from '"+old+"' into '"+cur+"'.");
							model.setValueAt(cur, i, 1);
					}
				}

                oldValues = values.clone();

				
//				DefaultTableModel model = (DefaultTableModel)tool.script.dock.tableVarbits.getModel();
//
//				if (oldSettings == null) {
//
//					for (int i = 0; i < settings.size(); i++) {
//						model.addRow(new Object[] { i, settings.get(i) } );
//					}
//
//				} else if (!oldSettings.keySet().equals(settings.keySet())) {
//
//					for (int i = 0; i < settings.size(); i++) {
//
//						final int cur = settings.get(i);
//						final int old = oldSettings.get(i);
//
//						if (cur != old) {
//
//						}
//					}
//				}
//
//				oldSettings = (HashMap<Integer, Integer>) settings.clone();
			} else {
//				oldSettings = null;
        }
			
		}
	}

//	private HashMap<Integer, Integer> getVarbits() {
//
//		HashMap<Integer, Integer> result = new HashMap<>();
//
//		for (int i = 0; i < 4927; i++) {
//
//			RSVarBit vb = RSVarBit.get(i);
//
//			if (vb != null)
//				result.put(i, vb.getValue());
//		}
//
//		return result;
//
//	}

	private int[] getVarbits() {

		int[] result = new int[4926];


		for (int i = 0; i < 4926; i++) {

			RSVarBit vb = RSVarBit.get(i);

			if (vb != null)
				result[i] = vb.getValue();
			else
				result[i] = -1;
		}

		return result;
	}
}
