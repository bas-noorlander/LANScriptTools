package scripts.LANScriptTools.Tools;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;

import org.tribot.api.Timing;
import org.tribot.api2007.types.RSTile;

import scripts.LANScriptTools.Threading.ScriptToolsThread;
import scripts.LANScriptTools.Threading.SettingsThread;
import scripts.LANScriptTools.Threading.VarBitThread;

/**
 * @author Laniax
 *
 */
public class VarBitTool implements AbstractTool {
	
	public final ScriptToolsThread script;
	
	public boolean doUpdate = true; // runs by default, even through tabs.
	
	public VarBitTool(ScriptToolsThread script) {
		this.script = script;
		
		writeLog("Started!");
		new Thread(new VarBitThread(this)).start();
	}
	
	public void writeLog(String str) {
		
		DefaultListModel<String> model = (DefaultListModel<String>) script.dock.listVarbitLog.getModel();
		if (model != null)
			model.add(0,"("+Timing.msToString(System.currentTimeMillis()%86400000)+") " + str);
		
	}

	/**
	 * Fired when the start/stop button is pushed.
	 * @param evt
	 */
	public void btnSettingsStopStartActionPerformed(ActionEvent evt) {
		doUpdate = !doUpdate;
		writeLog(doUpdate ? "Started!" : "Stopped");
		script.dock.btnVarbitsStopStart.setText(doUpdate ? "Stop" : "Start");

		if (doUpdate) {
			// if we start again, update the jtable with current values.
			((DefaultTableModel)script.dock.tableVarbits.getModel()).setNumRows(0);
		}
	}

	/**
	 * Fired when the search field is modified.
	 */
	public void doFilter() {
		
		try {
			int input = Integer.parseInt(script.dock.inputSearchVarbit.getText());
			
			if (input < script.dock.tableVarbits.getRowCount()-1) {
				
				script.dock.tableVarbits.setRowSelectionInterval(input, input);
				Rectangle rect = script.dock.tableVarbits.getCellRect(input, 0, true);
				script.dock.tableVarbits.scrollRectToVisible(rect);
			}
		} catch (NumberFormatException e) {}
	}
	
	@Override
	public void onTabChange() { }

	@Override
	public void onTileSelected(RSTile tile) { }
}