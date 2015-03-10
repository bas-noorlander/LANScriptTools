package scripts.LANScriptTools.GUI;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;

/**
 * @author Laniax
 *
 */
public class NPCTableModel extends AbstractTableModel{

	private static final long serialVersionUID = -5164913355669075082L;

	public ArrayList<RSNPC> npcList = new ArrayList<RSNPC>();
	
	private static final String[] columnNames = {"Name", "ID", "Location", "Model Points", "Projection"};

	public NPCTableModel(RSNPC[] npcs) {
		filterNPCs(npcs);
	}
	
	private void filterNPCs(RSNPC[] npcs) {
		
		npcList.clear();
		
		for (RSNPC npc : npcs) {
			String name = npc.getName();
			if (name != null && !name.isEmpty() && Player.getPosition().distanceTo(npc) < 19)
				npcList.add(npc);
		}
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public int getRowCount() {
		return npcList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		Object value = "";
		RSNPC npc = npcList.get(rowIndex);
		
		switch (columnIndex) {
		case 0:
			value = npc.getName();
			break;
		case 1:
			value = npc.getID();
			break;
		case 2:
			RSTile pos = npc.getPosition();
			value = "X: "+pos.getX()+", Y: "+pos.getY()+", P: "+pos.getPlane()+".";
			break;
		case 3:
			RSModel mod = npc.getModel();
			value = mod != null ? mod.getVertexCount() : null;
			break;
		case 4:
			value = npc;
			break;
		}

		return value;
	}
	
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 4;
	}
	
	public RSNPC getNPCAt(int row) {
        return npcList.get(row);
    }
	
	public void setData(RSNPC[] npcs) {
		filterNPCs(npcs);
		fireTableDataChanged();
	}

}
