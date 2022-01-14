package tritechgemini.dataselect;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import PamView.dialog.PamDialogPanel;
import PamView.dialog.PamGridBagContraints;
import tritechgemini.target.TargetType;

public class TargetSelectDialogPanel implements PamDialogPanel {

	private JPanel mainPanel;
	
	private JComboBox<String> minTargetType;

	private TargetDataSelector targetDataSelector;
	
	public TargetSelectDialogPanel(TargetDataSelector targetDataSelector) {
		super();
		this.targetDataSelector = targetDataSelector;
		mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();
		
		mainPanel.add(new JLabel("Minimum target type ", JLabel.RIGHT));
		c.gridx++;
		mainPanel.add(minTargetType = new JComboBox<String>());
//		mainPanel.setBackground(Color.RED);
		
		String[] types = TargetType.types;
		for (int i = 0; i < types.length; i++) {
			minTargetType.addItem(types[i]);
		}
		
	}

	@Override
	public JComponent getDialogComponent() {
		return mainPanel;
	}

	@Override
	public void setParams() {
		// remember that target types are indexed from 1
		TargetDataSelectParams params = targetDataSelector.getTargetDataSelectParams();
		int minType = params.minTargetType -1;
		if (minType >= 0 && minType < minTargetType.getItemCount()) {
			minTargetType.setSelectedIndex(minType);
		}
	}

	@Override
	public boolean getParams() {
		TargetDataSelectParams params = targetDataSelector.getTargetDataSelectParams();
		params.minTargetType = minTargetType.getSelectedIndex()+1;
		return true;
	}

}
