package scripts.LANScriptTools.Tools;

import org.tribot.api2007.types.RSTile;

/**
 * @author Laniax
 *
 */
public interface AbstractTool {

	/**
	 * Fired when the tool's tab became active.
	 */
	public void onTabChange();
	
	/**
	 * Fired when a user selects a tile using the middle mouse button.
	 */
	public void onTileSelected(RSTile tile);
	
}
