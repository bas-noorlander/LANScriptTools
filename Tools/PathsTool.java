package scripts.LANScriptTools.Tools;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.tribot.api.General;
import org.tribot.api2007.types.RSTile;

import scripts.LANScriptTools.Threading.ScriptToolsThread;

/**
 * @author Laniax
 *
 */
public class PathsTool implements AbstractTool {

	private final ScriptToolsThread script;

	private boolean doGeneratePath = false;

	public PathsTool(ScriptToolsThread script) {
		this.script = script;
	}

	public void refreshFirstLine() {

		StringBuilder sb = new StringBuilder(script.dock.outputPath.getText());

		int removeIndex = 0;

		// clear first line (if any)
		try (BufferedReader rd = new BufferedReader(new StringReader(script.dock.outputPath.getText()))) {
			removeIndex = rd.readLine().length();
		} catch (IOException e) {}

		sb.delete(0, removeIndex);

		sb.insert(0, (script.dock.btnPublic.isSelected() ? "public": "private") + " static final RSTile[] "+script.dock.inputPathName.getText()+" = new RSTile[] {");

		script.dock.outputPath.setText(sb.toString());
	}

	public void refreshSnippet(RSTile[] path) {

		StringBuilder sb = new StringBuilder();

		sb.append(script.dock.btnPublic.isSelected() ? "public": "private");
		sb.append(" static final RSTile[] ");
		sb.append(script.dock.inputPathName.getText());
		sb.append(" = new RSTile[] {");

		sb.append(System.getProperty("line.separator"));

		// If there are 6 or more tiles in the path, it is nicer to write 2 tiles per line.
		if (path != null && path.length > 5) {

			boolean first = true;
			for (int i = 0; i < path.length; i++) {

				if (first) 
					sb.append("    "); // newline whitespace

				sb.append("new RSTile("+path[i].getX()+", "+path[i].getY()+", "+path[i].getPlane()+")");

				if (i+1 != path.length)
					sb.append(", ");

				if (!first || i+1 == path.length)
					sb.append(System.getProperty("line.separator"));

				first = !first;
			}
		} else if (path != null) {
			for (int i = 0; i < path.length; i++) {

				sb.append("    new RSTile("+path[i].getX()+", "+path[i].getY()+", "+path[i].getPlane()+")");

				if (i+1 != path.length)
					sb.append(",");

				sb.append(System.getProperty("line.separator"));

			}
		}
		sb.append("};");

		script.dock.outputPath.setText(sb.toString());
	}


	/**
	 * Fired when the 'public' or 'private' radiobutton is clicked
	 */
	public void btnAccessModifierActionPerformed(ActionEvent evt) {
		refreshFirstLine();
	}

	/**
	 * Fired when the Copy to Clipboard button is clicked
	 */
	public void btnCopyPathsActionPerformed(ActionEvent evt) {
		StringSelection stringSelection = new StringSelection(script.dock.outputPath.getText());
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
		General.println("Path copied to clipboard.");
	}

	/**
	 * Fired when the clear button is clicked
	 */
	public void btnClearActionPerformed(ActionEvent evt) {
		script.generatedPath.clear();
		
		synchronized(script.LOCK) {
			script.tilesToDraw.clear();
		}
		
		refreshSnippet(null);
	}

	/**
	 * Fired when the start/stop button is clicked
	 */
	public void btnPathsStartStopActionPerformed(ActionEvent evt) {

		if (doGeneratePath) {
			doGeneratePath = false;
			script.dock.btnPathsStartStop.setText("Start");
		} else {
			doGeneratePath = true;
			script.dock.btnPathsStartStop.setText("Stop");
		}
	}

	@Override
	public void onTabChange() {
		// Draw the entire currently selected path.
		for (RSTile tile : script.generatedPath)
			script.tilesToDraw.add(tile);
	}

	@Override
	public void onTileSelected(RSTile tile) {

		if (doGeneratePath) {

			if (script.generatedPath.contains(tile)) { // if we already had the tile selected, remove it instead.
				script.generatedPath.remove(tile);
				synchronized(script.LOCK) {
					script.tilesToDraw.remove(tile);
				}
			} else {
				script.generatedPath.add(tile);
				script.tilesToDraw.add(tile);
			}
		}
		refreshSnippet(script.generatedPath.toArray(new RSTile[script.generatedPath.size()]));
	}
}
