package scripts.LANScriptTools.Threading;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;

import org.tribot.api.General;
import org.tribot.api.Screen;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSTile;

import scripts.LANScriptTools.GUI.Dock;
import scripts.LANScriptTools.GUI.TABS;
import scripts.LANScriptTools.Tools.AbstractTool;
import scripts.LANScriptTools.Tools.InspectTool;
import scripts.LANScriptTools.Tools.NPCsTool;
import scripts.LANScriptTools.Tools.ObjectsTool;
import scripts.LANScriptTools.Tools.PathfindingTool;
import scripts.LANScriptTools.Tools.PathsTool;
import scripts.LANScriptTools.Tools.SettingsTool;

/**
 * The thread that keeps running even after the script stops.
 * 
 * @author Laniax
 *
 */
public class ScriptToolsThread implements Runnable {

	// Dock/misc stuff
	private final Thread scriptThread;
	private final PaintThread paint;
	
	private Frame tribotFrame;
	private Component applet;
	
	public Dock dock;
	public Object LOCK = new Object();
	
	public boolean quitting = false;

	public final Adapters adapters = new Adapters(this);
	public HashMap<TABS, AbstractTool> observers = new HashMap<TABS, AbstractTool>();
	
	// Tool stuff
	public RSTile selectedTile;
	public ArrayList<RSTile> generatedPath = new ArrayList<RSTile>();

	// Paint stuff
	public ArrayList<RSModel> entitiesToDraw = new ArrayList<RSModel>();
	public ArrayList<RSTile> tilesToDraw = new ArrayList<RSTile>();

	public ScriptToolsThread(Thread scriptThread, Graphics g) {

		this.scriptThread = scriptThread;
		
		observers.put(TABS.INSPECT_TOOL, new InspectTool(this));
		observers.put(TABS.PATHS, new PathsTool(this));
		observers.put(TABS.OBJECTS, new ObjectsTool(this));
		observers.put(TABS.NPCS, new NPCsTool(this));
		observers.put(TABS.PATHFINDING, new PathfindingTool(this));
		observers.put(TABS.SETTINGS, new SettingsTool(this));

		// Since the original paint thread died, create a new one.
		paint = new PaintThread((Graphics2D)g, this);
		new Thread(paint).start();
		
		// Prepare our GUI
		dock = new Dock(this);
	}
	
	public void setSelectedTile(RSTile tile) {
		
		synchronized (LOCK) {
			paint.selectTile = tile;
		}
		
		selectedTile = tile;
	}

	public void run() {
		
		// Wait until previous thread properly died down.
		// We do this because it has a nack of closing all script-made frames when stopping.
		while (scriptThread.isAlive()){
			General.sleep(1000);
		}

		// Apparently, people don't want me to use the Frame or Window classes to get the tribot window.
		// Sorry! you forgot the JFrame class!
		Frame[] frames = JFrame.getFrames();
		for (Frame frame : frames) {
			if (frame.getTitle().contains("TRiBot Old-School")) 
				tribotFrame = frame;	
		}

		/*Boot up our GUI
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}*/

		// and lets try to attach to tribot now
		if (tribotFrame != null) {
			// set the Dock to the current tribot position/size
			Dimension dim = tribotFrame.getSize();
			dock.setPosition(tribotFrame.getLocation(), (int) dim.getWidth());

			// Listen to resize events on the tribot frame
			tribotFrame.addComponentListener(adapters.getResizeListener());

			// Listen to move events on the tribot frame
			tribotFrame.addComponentListener(adapters.getMoveListener());

			// Tribot's mouse interfaces only work when a script is running.
			// So we have to hook the mouse to the applet the old fashioned way.
			applet = tribotFrame.findComponentAt(new Point((int)Screen.getViewport().getCenterX(), (int)Screen.getViewport().getCenterY()));
			if (applet != null) {
				applet.addMouseListener(adapters.getMouseListener());
			}
		}

		while(!quitting) {

			// Tribot closes all frames when a script stops.
			// We might get away with a sleep() on our script, but that won't work if the user runs another script and stops that.
			// So we have to check continuously 
			if (!dock.isVisible())
				dock.setVisible(true);

			General.sleep(1000);
		}

		dispose();
		dock.dispose();

		General.println("[LAN] ScriptTools closed.");
	}

	public void dispose() {
		// Clean up with what we messed with!
		dock.removeWindowListener(adapters.getCloseListener());

		if (tribotFrame != null) {
			tribotFrame.removeComponentListener(adapters.getMoveListener());
			tribotFrame.removeComponentListener(adapters.getResizeListener());
			applet.removeMouseListener(adapters.getMouseListener());
		}
	}
}
