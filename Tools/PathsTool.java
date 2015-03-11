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
public class PathsTool {

	public static void refreshFirstLine() {

		StringBuilder sb = new StringBuilder(ScriptToolsThread.dock.outputPath.getText());

		int removeIndex = 0;

		// clear first line (if any)
		try (BufferedReader rd = new BufferedReader(new StringReader(ScriptToolsThread.dock.outputPath.getText()))) {
			removeIndex = rd.readLine().length();
		} catch (IOException e) {}

		sb.delete(0, removeIndex);

		sb.insert(0, (ScriptToolsThread.dock.btnPublic.isSelected() ? "public": "private") + " static final RSTile[] "+ScriptToolsThread.dock.inputPathName.getText()+" = new RSTile[] {");

		ScriptToolsThread.dock.outputPath.setText(sb.toString());
	}

	public static void refreshSnippet(RSTile[] path) {

		StringBuilder sb = new StringBuilder();

		sb.append(ScriptToolsThread.dock.btnPublic.isSelected() ? "public": "private");
		sb.append(" static final RSTile[] ");
		sb.append(ScriptToolsThread.dock.inputPathName.getText());
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

		ScriptToolsThread.dock.outputPath.setText(sb.toString());
	}


	/**
	 * Fired when the 'public' or 'private' radiobutton is clicked
	 */
	public static void btnAccessModifierActionPerformed(ActionEvent evt) {
		PathsTool.refreshFirstLine();
	}

	/**
	 * Fired when the Copy to Clipboard button is clicked
	 */
	public static void btnCopyPathsActionPerformed(ActionEvent evt) {
		StringSelection stringSelection = new StringSelection(ScriptToolsThread.dock.outputPath.getText());
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
		General.println("Path copied to clipboard.");
	}

	/**
	 * Fired when the clear button is clicked
	 */
	public static void btnClearActionPerformed(ActionEvent evt) {
		ScriptToolsThread.generatedPath.clear();
		PathsTool.refreshSnippet(null);
	}

	/**
	 * Fired when the start/stop button is clicked
	 */
	public static void btnPathsStartStopActionPerformed(ActionEvent evt) {

		if (ScriptToolsThread.doGeneratePath) {
			ScriptToolsThread.doGeneratePath = false;
			ScriptToolsThread.dock.btnPathsStartStop.setText("Start");
		} else {
			ScriptToolsThread.doGeneratePath = true;
			ScriptToolsThread.dock.btnPathsStartStop.setText("Stop");
		}
	}
}
