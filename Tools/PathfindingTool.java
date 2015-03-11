package scripts.LANScriptTools.Tools;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import org.tribot.api.General;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;

import scripts.LANScriptTools.Threading.ScriptToolsThread;

/**
 * @author Laniax
 *
 */
public class PathfindingTool implements AbstractTool {

	private final ScriptToolsThread script;
	private static DPathNavigator navigator = new DPathNavigator();

	public PathfindingTool(ScriptToolsThread script) {
		this.script = script;
	}

	public void update() {

		synchronized(script.LOCK) {
			script.tilesToDraw.clear();
		}

		String mode = getSelectedButtonText(script.dock.btngroupPathfinding);

		refreshSnippet(mode);

		if (script.selectedTile != null) {

			synchronized(script.LOCK) {
				script.tilesToDraw.add(script.selectedTile);
			}

			RSTile[] path = null;

			switch (mode) {

			case "DPathNavigator":
				path = navigator.findPath(script.selectedTile);
				break;

			case "PathFinding":
				path = PathFinding.generatePath(Player.getPosition(), script.selectedTile, Objects.getAt(script.selectedTile).length > 0);
				break;

			case "Walking Minimap": 
				path = Walking.generateStraightPath(script.selectedTile);
				break;

			case "Walking Screen Path":
				path = Walking.generateStraightScreenPath(script.selectedTile);
				break;

			default:
				break;
			}

			if (path != null && path.length > 0) {

				synchronized(script.LOCK) {
					script.tilesToDraw.addAll(Arrays.asList(path));
				}

			} else {
				General.println("Error: was not able to generate a path using "+mode);
			}
		}
	}

	private void refreshSnippet(String mode) {

		StringBuilder sb = new StringBuilder();

		if (script.selectedTile != null) {

			String tileString = "new RSTile("+script.selectedTile.getX()+", "+script.selectedTile.getY()+", "+script.selectedTile.getPlane()+")";

			switch (mode) {

			case "DPathNavigator":
				sb.append("private DPathNavigator navigator = new DPathNavigator(); // Put this as a field in your class.");
				sb.append(System.getProperty("line.separator"));
				sb.append(System.getProperty("line.separator"));
				sb.append("RSTile[] path = navigator.findPath("+tileString+");");
				break;

			case "PathFinding": 
				sb.append("RSTile[] path = PathFinding.generatePath(Player.getPosition(), "+tileString+", "+(Objects.getAt(script.selectedTile).length > 0)+");");
				break;

			case "Walking Minimap": 
				sb.append("RSTile[] path = Walking.generateStraightPath("+tileString+")");
				break;

			case "Walking Screen Path": 
				sb.append("RSTile[] path = Walking.generateStraightScreenPath("+tileString+")");
				break;

			default:
				break;
			}
		}

		script.dock.outputPathFinding.setText(sb.toString());
	}

	/**
	 * Fired when the Copy to Clipboard button is clicked
	 */
	public void btnCopyPathfindingActionPerformed(ActionEvent evt) {
		StringSelection stringSelection = new StringSelection(script.dock.outputPathFinding.getText());
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
		General.println("Snippet copied to clipboard.");
	}

	private String getSelectedButtonText(ButtonGroup buttonGroup) {
		for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();

			if (button.isSelected()) {
				return button.getText();
			}
		}
		return null;
	}

	@Override
	public void onTabChange() {
		// Nothing to do!
	}

	@Override
	public void onTileSelected(RSTile tile) {
		update();
	}


}
