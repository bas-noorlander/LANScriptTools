package scripts.LANScriptTools.Threading;

import java.util.Arrays;

import javax.swing.table.DefaultTableModel;

import org.tribot.api.General;
import org.tribot.api2007.Game;

import scripts.LANScriptTools.Tools.SettingsTool;

/**
 * This thread continuesly checks for changes in the settings.
 * 
 * @author Laniax
 *
 */
public class SettingsThread implements Runnable{

	private final SettingsTool tool;
	
	private int[] oldSettings;
	
	
	public SettingsThread(SettingsTool tool) {
		this.tool = tool;
	}

	@Override
	public void run() {
		
		// Quit this thread ASAP when the scripttools thread stops.
		while (!tool.script.quitting) {
			
			if (tool.doUpdate) {
				
				DefaultTableModel model = (DefaultTableModel)tool.script.dock.tableSettings.getModel();

				final int[] settings = Game.getSettingsArray();
				
				if (oldSettings == null) {
					
					for (int i = 0; i < settings.length; i++) {
						model.addRow(new Object[] { i, settings[i] } );
					}
					
				} else if (!Arrays.equals(oldSettings, settings)) {
					
					for (int i = 0; i < settings.length; i++) {
						
						final int cur = settings[i];
						final int old = oldSettings[i];
						
						if (cur != old) {
							// This setting changed!
							tool.writeLog("Setting: '"+i+"' changed from '"+old+"' into '"+cur+"'.");
							model.setValueAt(cur, i, 1);
						}
					}
				}
				
				oldSettings = settings.clone();
			} else {
				oldSettings = null;
			}
			
			General.sleep(50);
		}
		
	}

}
