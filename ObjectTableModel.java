package scripts.LANScriptTools;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;

/**
 * @author Laniax
 *
 */
public class ObjectTableModel extends AbstractTableModel{

	private static final long serialVersionUID = -5164913355669075082L;

	public ArrayList<RSObject> objectList = new ArrayList<RSObject>();
	
	private static final String[] columnNames = {"Name", "ID", "Location", "Model Points", "Projection"};

	public ObjectTableModel(RSObject[] objects) {

		filterObjects(objects);
	}
	
	private void filterObjects(RSObject[] objects){
		objectList.clear();
		for (RSObject obj : objects){
			RSObjectDefinition objDef = obj.getDefinition();
			if (objDef != null && !objDef.getName().equals("null")) { // filter out the null objects
				objectList.add(obj);
			}
		}
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public int getRowCount() {
		return objectList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		Object value = "";
		RSObject obj = objectList.get(rowIndex);
		RSObjectDefinition objDef = obj.getDefinition();
		switch (columnIndex) {
		case 0:
			value = objDef != null ? objDef.getName() : "";
			break;
		case 1:
			value = obj.getID();
			break;
		case 2:
			RSTile pos = obj.getPosition();
			value = "X: "+pos.getX()+", Y: "+pos.getY()+", P: "+pos.getPlane()+".";
			break;
		case 3:
			RSModel mod = obj.getModel();
			value = mod != null ? mod.getVertexCount() : null;
			break;
		case 4:
			value = obj;
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
	
	public RSObject getObjectAt(int row) {
        return objectList.get(row);
    }
	
	public void setData(RSObject[] objects) {
		filterObjects(objects);
		fireTableDataChanged();
	}

}
