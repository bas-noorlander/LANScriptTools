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
public class PathfindingTool {
	
	private static DPathNavigator navigator = new DPathNavigator();

	public static void update() {
		
		synchronized(ScriptToolsThread.LOCK) {
			ScriptToolsThread.tilesToDraw.clear();
		}

		String mode = getSelectedButtonText(ScriptToolsThread.dock.btngroupPathfinding);
		
		refreshSnippet(mode);

		if (ScriptToolsThread.selectedTile != null) {
			
			synchronized(ScriptToolsThread.LOCK) {
				ScriptToolsThread.tilesToDraw.add(ScriptToolsThread.selectedTile);
			}
			
			RSTile[] path = null;

			switch (mode) {
			
			case "DPathNavigator":
				path = navigator.findPath(ScriptToolsThread.selectedTile);
				break;

			case "PathFinding":
				path = PathFinding.generatePath(Player.getPosition(), ScriptToolsThread.selectedTile, Objects.getAt(ScriptToolsThread.selectedTile).length > 0);
				break;

			case "Walking Minimap": 
				path = Walking.generateStraightPath(ScriptToolsThread.selectedTile);
				break;

			case "Walking Screen Path":
				path = Walking.generateStraightScreenPath(ScriptToolsThread.selectedTile);
				break;

			default:
				break;
			}
			
			if (path != null && path.length > 0) {
				
				synchronized(ScriptToolsThread.LOCK) {
					ScriptToolsThread.tilesToDraw.addAll(Arrays.asList(path));
				}
				
			} else {
				General.println("Error: was not able to generate a path using "+mode);
			}
			
		}

	}

	private static void refreshSnippet(String mode) {

		StringBuilder sb = new StringBuilder();

		if (ScriptToolsThread.selectedTile != null) {
			
			String tileString = "new RSTile("+ScriptToolsThread.selectedTile.getX()+", "+ScriptToolsThread.selectedTile.getY()+", "+ScriptToolsThread.selectedTile.getPlane()+")";

			switch (mode) {

			case "DPathNavigator":
				sb.append("private DPathNavigator navigator = new DPathNavigator(); // Put this as a field in your class.");
				sb.append(System.getProperty("line.separator"));
				sb.append(System.getProperty("line.separator"));
				sb.append("RSTile[] path = navigator.findPath("+tileString+");");
				break;

			case "PathFinding": 
				sb.append("RSTile[] path = PathFinding.generatePath(Player.getPosition(), "+tileString+", "+(Objects.getAt(ScriptToolsThread.selectedTile).length > 0)+");");
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

		ScriptToolsThread.dock.outputPathFinding.setText(sb.toString());
	}
	
	/**
	 * Fired when the Copy to Clipboard button is clicked
	 */
	public static void btnCopyPathfindingActionPerformed(ActionEvent evt) {
		StringSelection stringSelection = new StringSelection(ScriptToolsThread.dock.outputPathFinding.getText());
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
		General.println("Snippet copied to clipboard.");
	}

	private static String getSelectedButtonText(ButtonGroup buttonGroup) {
		for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();

			if (button.isSelected()) {
				return button.getText();
			}
		}
		return null;
	}


}
