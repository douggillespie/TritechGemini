package tritechplugin.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import PamUtils.LatLong;
import PamView.dialog.PamDialog;
import PamView.dialog.PamGridBagContraints;
import PamView.panel.PamAlignmentPanel;
import PamView.symbol.StandardSymbolManager;
import PamView.symbol.StandardSymbolOptionsPanel;

public class GeminiSymbolChooserPanel extends StandardSymbolOptionsPanel {
	
	private JPanel mainPanel;
	
	private JRadioButton[] boxOptions = new JRadioButton[3];
	
	private JTextField vertical;

	public GeminiSymbolChooserPanel(StandardSymbolManager standardSymbolManager,
			GeminiSymbolChooser standardSymbolChooser) {
		super(standardSymbolManager, standardSymbolChooser);		
		JPanel gPanel = new PamAlignmentPanel(BorderLayout.WEST);
		gPanel.setLayout(new GridBagLayout());
		gPanel.setBorder(new TitledBorder("Target drawing options"));
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(BorderLayout.CENTER, super.getDialogComponent());
		mainPanel.add(BorderLayout.SOUTH, gPanel);
		GridBagConstraints c = new PamGridBagContraints();
		ButtonGroup bg = new ButtonGroup();
		c.gridwidth = 3;
		for (int i = 0; i < 3; i++) {
			boxOptions[i] = new JRadioButton(GeminiSymbolOptions.boxNames[i]);
			boxOptions[i].setToolTipText(GeminiSymbolOptions.boxToolTips[i]);
			gPanel.add(boxOptions[i], c);
			c.gridy++;
			bg.add(boxOptions[i]);
			boxOptions[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					enableControls();
				}
			});
		}
		c.gridwidth = 1;
		gPanel.add(new JLabel(" Vertical angle ", JLabel.RIGHT), c);
		c.gridx++;
		gPanel.add(vertical = new JTextField(4), c);
		c.gridx++;
		gPanel.add(new JLabel("" + LatLong.deg + " half height"), c);
	}

	@Override
	public JComponent getDialogComponent() {
		return mainPanel;
	}

	@Override
	public void setParams() {
		super.setParams();
		GeminiSymbolOptions params = (GeminiSymbolOptions) getStandardSymbolChooser().getSymbolOptions();
		if (boxOptions == null) {
			return;
		}
		for (int i = 0; i < 3; i++) {
			if (boxOptions[i] != null) {
				boxOptions[i].setSelected(params.boxOption == i);
			}
		}
		vertical.setText(String.format("%3.1f", params.beamHalfHeightAngle));
		enableControls();
	}
	
	private void enableControls() {
		vertical.setEnabled(boxOptions[GeminiSymbolOptions.DRAW_3D_BOX].isSelected());
	}

	@Override
	public boolean getParams() {
		if (super.getParams() == false) {
			return false;
		};
		GeminiSymbolOptions params = (GeminiSymbolOptions) getStandardSymbolChooser().getSymbolOptions();
		for (int i = 0; i < 3; i++) {
			if (boxOptions[i].isSelected()) {
				params.boxOption = i;
			}
		}
		if (params.boxOption == GeminiSymbolOptions.DRAW_3D_BOX) {
			try {
				params.beamHalfHeightAngle = Double.valueOf(vertical.getText());
			}
			catch (NumberFormatException e) {
				return PamDialog.showWarning(null, "Invalid number", "Invalid vertical beam beight");
			}
		}

		return true;
	}

}
