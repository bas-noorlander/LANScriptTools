package scripts.LANScriptTools.Tools;

import java.awt.event.ActionEvent;
import java.util.Timer;
import java.util.TimerTask;

import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;

import scripts.LANScriptTools.GUI.NPCTableModel;
import scripts.LANScriptTools.Threading.ScriptToolsThread;

/**
 * @author Laniax
 *
 */
public class NPCsTool implements AbstractTool {
	
	private final ScriptToolsThread script;
	
	public boolean doAutoUpdate = true;
	
	public Timer updateTimer = new Timer();

	/**
	 * @param scriptToolsThread
	 */
	public NPCsTool(ScriptToolsThread script) {
		this.script = script;
		
		updateTimer.scheduleAtFixedRate(new TimerTask(){
			public void run() {
				if (doAutoUpdate)
					update();
			}}, 2000, 2000);
	}

	/*
	 * Fired when the auto update checkbox is toggled
	 */
	public void chkUpdateNPCsActionPerformed(ActionEvent evt) {
		doAutoUpdate = script.dock.chkUpdateNPCs.isSelected();
		script.dock.btnUpdateNPCs.setEnabled(!doAutoUpdate);
	}
	
	/*
	 * Fired when the auto update timer executes or the update button was clicked.
	 */
	public void update() {
		
		NPCTableModel model = (NPCTableModel)script.dock.tableNPCs.getModel();
		
		RSNPC[] npcs = NPCs.sortByDistance(Player.getPosition(), NPCs.getAll());
		
		if (model != null && npcs != null && npcs.length > 0) {
			model.setData(npcs);
		}
	}
	
	@Override
	public void onTabChange() {
		doAutoUpdate = script.dock.chkUpdateNPCs.isSelected();
		script.dock.btnUpdateNPCs.setEnabled(!doAutoUpdate);
		
	}
	
	@Override
	public void onTileSelected(RSTile tile) {
		// Nothing to do!
	}
}