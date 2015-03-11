package scripts.LANScriptTools.GUI;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import org.tribot.api2007.types.RSModel;

import scripts.LANScriptTools.GUI.NPCTableModel;
import scripts.LANScriptTools.GUI.ObjectTableModel;
import scripts.LANScriptTools.Threading.ScriptToolsThread;

/**
 * @author Laniax
 *
 */
public class ButtonEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 8911310406960923820L;

	private final ScriptToolsThread script;

	protected JButton button;

	private RSModel object;

	private boolean isPushed;

	public ButtonEditor(JCheckBox checkBox, ScriptToolsThread script) {
		super(checkBox);
		this.script = script;
		button = new JButton();
		button.setOpaque(true);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
		});
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (isSelected) {
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		} else {
			button.setForeground(table.getForeground());
			button.setBackground(table.getBackground());
		}

		Object model = table.getModel();
		if (model != null) {
			if (model instanceof ObjectTableModel) {
				object = ((ObjectTableModel)model).getObjectAt(row).getModel();
			} else if (model instanceof NPCTableModel) {
				object = ((NPCTableModel)model).getNPCAt(row).getModel();
			}
		}

		isPushed = true;
		return button;
	}

	public RSModel getCellEditorValue() {
		if (isPushed) {

			synchronized(script.LOCK){

				boolean found = false;

				for (Iterator<RSModel> iterator = script.entitiesToDraw.iterator(); iterator.hasNext();) {
					if (iterator.next().getIndexCount() == object.getIndexCount()) {
						iterator.remove();
						found = true;
						break;
					}
				}

				if (!found) {
					script.entitiesToDraw.add(object);
				}
			}
		}
		isPushed = false;
		return object;
	}

	public boolean stopCellEditing() {
		isPushed = false;
		return super.stopCellEditing();
	}

	protected void fireEditingStopped() {
		super.fireEditingStopped();

	}
}