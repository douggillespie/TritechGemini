package tritechplugin.dataselect;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import PamView.dialog.PamDialog;
import PamView.dialog.PamDialogPanel;
import PamView.dialog.PamGridBagContraints;
import tritechplugin.target.TargetType;

public class TrackSelectDialogPanel implements PamDialogPanel {

	private TrackDataSelector trackDataSelector;
	
	private JPanel mainPanel;
	
	private JTextField minCount;
	private JComboBox<String> minTargetType;
	
	public TrackSelectDialogPanel(TrackDataSelector trackDataSelector) {
		super();
		this.trackDataSelector = trackDataSelector;
		mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();
		mainPanel.add(new JLabel("Minimum number of targets ", JLabel.RIGHT), c);
		c.gridx++;
		mainPanel.add(minCount = new JTextField(3), c);
		c.gridx = 0;
		c.gridy ++;
		mainPanel.add(new JLabel("Minimum track type ", JLabel.RIGHT), c);
		c.gridx++;
		mainPanel.add(minTargetType = new JComboBox<String>(), c);
//		mainPanel.setBackground(Color.CYAN);
		
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
		TrackDataSelectParams params = trackDataSelector.getTrackParams();
		minCount.setText(Integer.valueOf(params.minTargetCount).toString());
		int minType = params.minTargetType -1;
		if (minType >= 0 && minType < minTargetType.getItemCount()) {
			minTargetType.setSelectedIndex(minType);
		}
	}

	@Override
	public boolean getParams() {
		TrackDataSelectParams params = trackDataSelector.getTrackParams();
		int minN = 0;
		try {
			minN = Integer.valueOf(minCount.getText()); 
		}
		catch (NumberFormatException e) {
			return PamDialog.showWarning(null, "Invalid Number", "Invalid minimum target count");
		}
		params.minTargetCount = minN;
		params.minTargetType = minTargetType.getSelectedIndex()+1;
		return true;
	}

}
