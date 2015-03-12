package scripts.LANScriptTools.Tools;

import java.awt.event.ActionEvent;
import java.util.Timer;
import java.util.TimerTask;

import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;

import scripts.LANScriptTools.GUI.ObjectTableModel;
import scripts.LANScriptTools.Threading.ScriptToolsThread;

/**
 * @author Laniax
 *
 */
public class ObjectsTool implements AbstractTool {

	private final ScriptToolsThread script;

	public boolean doAutoUpdate = true;

	private final Timer updateTimer = new Timer();

	public ObjectsTool(ScriptToolsThread script) {
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
	public void chkUpdateObjectsActionPerformed(ActionEvent evt) {
		doAutoUpdate = script.dock.chkUpdateObjects.isSelected();
		script.dock.btnUpdateObjects.setEnabled(!doAutoUpdate);
	}

	/*
	 * Fired when the auto update timer executes or the update button was clicked.
	 */
	public void update() {

		ObjectTableModel model = (ObjectTableModel)script.dock.tableObjects.getModel();

		RSObject[] objs = Objects.sortByDistance(Player.getPosition(), Objects.getAll(19));

		if (model != null && objs != null && objs.length > 0) {
			model.setData(objs);
		}
	}

	@Override
	public void onTabChange() {
		doAutoUpdate = script.dock.chkUpdateObjects.isSelected();
		script.dock.btnUpdateObjects.setEnabled(!doAutoUpdate);
	}

	@Override
	public void onTileSelected(RSTile tile) {
		// Nothing to do!
	}
}