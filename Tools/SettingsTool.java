package scripts.LANScriptTools.Tools;

import org.tribot.api2007.types.RSTile;

import scripts.LANScriptTools.Threading.ScriptToolsThread;

/**
 * @author Laniax
 *
 */
public class SettingsTool implements AbstractTool {
	
	private final ScriptToolsThread script;
	
	public SettingsTool(ScriptToolsThread script) {
		this.script = script;
	}

	@Override
	public void onTabChange() {
	}

	@Override
	public void onTileSelected(RSTile tile) {
	}
}