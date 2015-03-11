package scripts.LANScriptTools.Threading;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.tribot.api2007.types.RSTile;

import scripts.LANScriptTools.Tools.AbstractTool;
import scripts.LanAPI.Projecting;

/**
 * @author Laniax
 *
 */
public class Adapters {
	
	private final ResizeListener _resizeListener;
	private final MoveListener _moveListener;
	private final CloseListener _closeListener;
	private final CursorListener _mouseListener;

	public Adapters(ScriptToolsThread script) {
		_resizeListener = new ResizeListener(script);
		_moveListener = new MoveListener(script);
		_closeListener = new CloseListener(script);
		_mouseListener = new CursorListener(script);
	}

	public ResizeListener getResizeListener() {
		return _resizeListener;
	}

	public MoveListener getMoveListener() {
		return  _moveListener;
	}

	public CloseListener getCloseListener() {
		return  _closeListener;
	}

	public CursorListener getMouseListener() {
		return  _mouseListener;
	}
}

/**
 * A MouseAdapter which will allow us to hook mouse events.
 * We do this since Tribot doesn't relay mouse events when there isn't an 'active' script running.
 */
class CursorListener extends MouseAdapter {

	private final ScriptToolsThread script;

	public CursorListener(ScriptToolsThread script) {
		this.script = script;
	}

	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isMiddleMouseButton(e)) {
			
			RSTile clickTile = Projecting.getTileAtPoint(e.getPoint());
			
			if (clickTile != null) {
				
				script.setSelectedTile(clickTile);
				
				AbstractTool ob = script.observers.get(script.dock.getOpenTab());
				ob.onTileSelected(clickTile);
			}
		}
	}
}
/**
 * A ComponentAdapter which will keep track of the height of the frame it is listening to.
 * We use it to resize the Dock when the tribot window is resized.
 */
class ResizeListener extends ComponentAdapter {

	private final ScriptToolsThread script;

	public ResizeListener(ScriptToolsThread script) {
		this.script = script;
	}

	public void componentResized(ComponentEvent e) {
		if (script.dock.doDock) {
			script.dock.setPosition(e.getComponent().getLocation(),(int) e.getComponent().getSize().getWidth());
		}
	}
}

/**
 * A ComponentAdapter which will keep track of the position of the frame it is listening to.
 * We use it to reposition the Dock when the tribot window is moved.
 */
class MoveListener extends ComponentAdapter {

	private final ScriptToolsThread script;

	public MoveListener(ScriptToolsThread script) {
		this.script = script;
	}

	public void componentMoved(ComponentEvent e) {
		if (script.dock.doDock)
			script.dock.setPosition(e.getComponent().getLocation(),(int) e.getComponent().getSize().getWidth());
	}
}

/**
 * A WindowAdapter which will fire if the frame it is attached to closes.
 * We use it to stop the script if the dock is closed.
 */
class CloseListener extends WindowAdapter {

	private final ScriptToolsThread script;

	public CloseListener(ScriptToolsThread script) {
		this.script = script;
	}

	public void windowClosing(WindowEvent windowEvent) {
		script.quitting = true;
	}
}