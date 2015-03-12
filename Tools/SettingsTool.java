package scripts.LANScriptTools.Tools;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.types.RSTile;

import scripts.LANScriptTools.Threading.ScriptToolsThread;
import scripts.LANScriptTools.Threading.SettingsThread;

/**
 * @author Laniax
 *
 */
public class SettingsTool implements AbstractTool {
	
	public final ScriptToolsThread script;
	
	public boolean doUpdate = true; // runs by default, even through tabs.
	
	public SettingsTool(ScriptToolsThread script) {
		this.script = script;
		
		writeLog("Started!");
		new Thread(new SettingsThread(this)).start();
	}
	
	public void writeLog(String str) {
		
		DefaultListModel<String> model = (DefaultListModel<String>)script.dock.listSettingsLog.getModel();
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
		script.dock.btnSettingsStopStart.setText(doUpdate ? "Stop" : "Start");

		if (doUpdate) {
			// if we start again, update the jtable with current values.
			((DefaultTableModel)script.dock.tableSettings.getModel()).setNumRows(0);
		}
	}

	/**
	 * Fired when the search field is modified.
	 */
	public void doFilter() {
		
		try {
			int input = Integer.parseInt(script.dock.inputSearchSetting.getText());
			
			General.println(input);
			
			if (input < script.dock.tableSettings.getRowCount()-1) {
				
				script.dock.tableSettings.setRowSelectionInterval(input, input);
				Rectangle rect = script.dock.tableSettings.getCellRect(input, 0, true);
				script.dock.tableSettings.scrollRectToVisible(rect);
			}
		} catch (NumberFormatException e) {}
	}
	
	@Override
	public void onTabChange() { }

	@Override
	public void onTileSelected(RSTile tile) { }
}