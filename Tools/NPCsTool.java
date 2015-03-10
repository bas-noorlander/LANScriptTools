package scripts.LANScriptTools.Tools;

import java.awt.event.ActionEvent;

import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSNPC;
import scripts.LANScriptTools.NPCTableModel;
import scripts.LANScriptTools.ScriptToolsThread;

/**
 * @author Laniax
 *
 */
public class NPCsTool {
	
	public static boolean doAutoUpdate = true;

	/*
	 * Fired when the auto update checkbox is toggled
	 */
	public static void chkUpdateNPCsActionPerformed(ActionEvent evt) {
		doAutoUpdate = ScriptToolsThread.dock.chkUpdateNPCs.isSelected();
		ScriptToolsThread.dock.btnUpdateNPCs.setEnabled(!doAutoUpdate);
	}
	
	/*
	 * Fired when the auto update timer executes or the update button was clicked.
	 */
	public static void update() {
		
		NPCTableModel model = (NPCTableModel)ScriptToolsThread.dock.tableNPCs.getModel();
		
		RSNPC[] npcs = NPCs.sortByDistance(Player.getPosition(), NPCs.getAll());
		
		if (model != null && npcs != null && npcs.length > 0) {
			model.setData(npcs);
		}
	}
}