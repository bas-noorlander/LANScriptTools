package scripts.LANScriptTools.Threading;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;

import org.tribot.api.General;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSTile;

import scripts.LANScriptTools.GUI.TABS;

/**
 * @author Laniax
 *
 */
public class PaintThread implements Runnable {

	private final Graphics2D g;

	private final RenderingHints antialiasing = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	private Color blackTransparent = new Color(0, 0, 0, 120);
	private Color cyanSlightTransparent = new Color(0,246,255, 200);

	public PaintThread(Graphics2D g) {
		g.setRenderingHints(antialiasing);
		this.g = g;
	}

	@Override
	public void run() {

		// Quit this thread ASAP when the scripttools thread stops.
		while(!ScriptToolsThread.quitting) {

			synchronized(ScriptToolsThread.LOCK) {
				
				for (RSModel obj : ScriptToolsThread.entitiesToDraw) {
					
					if (obj.getVisiblePoints().length > 0) {
						
						for (Polygon triangle : obj.getTriangles()) {
							g.setColor(blackTransparent);
							g.fillPolygon(triangle);
							
							g.setColor(cyanSlightTransparent);
							g.drawPolygon(triangle);
						}
					}
				}

				for (int i = 0; i < ScriptToolsThread.tilesToDraw.size(); i++) {
					
					RSTile tile = ScriptToolsThread.tilesToDraw.get(i);

					if (tile == null || !tile.isOnScreen())
						continue;

					Polygon tilePoly = Projection.getTileBoundsPoly(tile, 0);
					g.setColor(blackTransparent);
					g.fillPolygon(tilePoly);
					g.setColor(cyanSlightTransparent);
					g.drawPolygon(tilePoly);
					
					if (ScriptToolsThread.dock.getOpenTab() == TABS.PATHS) {
						// Draw a line between all tiles in the path tool.
						if (i > 0) {
							Point curTile = Projection.tileToScreen(tile, 0);
							Point prevTile = Projection.tileToScreen(ScriptToolsThread.tilesToDraw.get(i-1), 0);
							g.drawLine(curTile.x, curTile.y, prevTile.x, prevTile.y);
						}
					}
				}
			}

			General.sleep(16);
		}
	}

}
