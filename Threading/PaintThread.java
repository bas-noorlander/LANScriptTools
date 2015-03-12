package scripts.LANScriptTools.Threading;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import org.tribot.api.General;
import org.tribot.api.Screen;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSTile;

/**
 * @author Laniax
 *
 */
public class PaintThread implements Runnable {

	private final Graphics2D g;
	private final Graphics2D gMinimap;
	private final ScriptToolsThread script;

	private final RenderingHints antialiasing = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	private Color blackTransparent = new Color(0, 0, 0, 120);
	private Color cyanSlightTransparent = new Color(0, 246, 255, 200);
	private Color redSlightTransparent = new Color(228, 2, 2, 200);

	RSTile selectTile = null;

	final Rectangle screenDimension = Screen.getViewport();

	public PaintThread(Graphics2D g, ScriptToolsThread script) {

		g.setRenderingHints(antialiasing);

		this.g = g;
		this.script = script;
		gMinimap = (Graphics2D)g.create();
	}

	@Override
	public void run() {

		g.setClip(screenDimension);
		gMinimap.setColor(cyanSlightTransparent);

		// Quit this thread ASAP when the scripttools thread stops.
		while(!script.quitting) {

			synchronized(script.LOCK) {

				if (selectTile != null && selectTile.isOnScreen()) {
					Polygon tilePoly = Projection.getTileBoundsPoly(selectTile, 0);
					g.setColor(blackTransparent);
					g.fillPolygon(tilePoly);
					g.setColor(redSlightTransparent);
					g.drawPolygon(tilePoly);
				}

				for (RSModel obj : script.entitiesToDraw) {

					if (obj.getVisiblePoints().length > 0) {

						for (Polygon triangle : obj.getTriangles()) {
							g.setColor(blackTransparent);
							g.fillPolygon(triangle);

							g.setColor(cyanSlightTransparent);
							g.drawPolygon(triangle);
						}
					}
				}

				for (int i = 0; i < script.tilesToDraw.size(); i++) {

					RSTile tile = script.tilesToDraw.get(i);

					if (tile == null)
						continue;

					Polygon tilePoly = Projection.getTileBoundsPoly(tile, 0);
					if (tilePoly != null) {
						g.setColor(blackTransparent);
						g.fillPolygon(tilePoly);
						g.setColor(cyanSlightTransparent);
						g.drawPolygon(tilePoly);
					}

					// Draw a line between all tiles in a path.
					if (i > 0) {
						Point curTile = Projection.tileToScreen(tile, 0);
						Point prevTile = Projection.tileToScreen(script.tilesToDraw.get(i-1), 0);
						g.drawLine(curTile.x, curTile.y, prevTile.x, prevTile.y);

						// Draw on minimap as well.
						Point curTileMinimap = Projection.tileToMinimap(tile);
						if (Projection.isInMinimap(curTileMinimap)) {

							Point prevTileMinimap = Projection.tileToMinimap(script.tilesToDraw.get(i-1));
							if (Projection.isInMinimap(prevTileMinimap)) {
								gMinimap.drawLine(curTileMinimap.x, curTileMinimap.y, prevTileMinimap.x, prevTileMinimap.y);
							}
						}
					}
				}
			}

			General.sleep(16);
		}
	}

}
