package scripts.LANScriptTools.Tools;

import java.awt.event.ActionEvent;

import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSObject;

import scripts.LANScriptTools.ObjectTableModel;
import scripts.LANScriptTools.ScriptToolsThread;

/**
 * @author Laniax
 *
 */
public class ObjectsTool {
	
	public static boolean doAutoUpdate = true;

	/*
	 * Fired when the auto update checkbox is toggled
	 */
	public static void chkUpdateObjectsActionPerformed(ActionEvent evt) {
		doAutoUpdate = ScriptToolsThread.dock.chkUpdateObjects.isSelected();
		ScriptToolsThread.dock.btnUpdateObjects.setEnabled(!doAutoUpdate);
	}
	
	/*
	 * Fired when the auto update timer executes or the update button was clicked.
	 */
	public static void update() {
		
		ObjectTableModel model = (ObjectTableModel)ScriptToolsThread.dock.tableObjects.getModel();
		
		RSObject[] objs = Objects.sortByDistance(Player.getPosition(), Objects.getAll(19));
		
		if (model != null && objs != null && objs.length > 0) {
			model.setData(objs);
		}
	}
}