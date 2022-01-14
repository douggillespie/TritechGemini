package tritechgemini.dataselect;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import PamView.dialog.PamDialogPanel;
import PamView.dialog.PamGridBagContraints;
import tritechgemini.GeminiControl;

public class ECDDataDialogPanel implements PamDialogPanel {
	
	private JPanel mainPanel;
	
	private int nSonars;
	
	private JCheckBox[] selectSonar;

	private GeminiControl geminiControl;

	private ECDDataSelector ecdDataSelector;

	public ECDDataDialogPanel(GeminiControl geminiControl, ECDDataSelector ecdDataSelector) {
		this.geminiControl = geminiControl;
		this.ecdDataSelector = ecdDataSelector;
		nSonars = geminiControl.getGeminiParameters().nSonars;
		mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();
		mainPanel.setBorder(new TitledBorder("Select sonars"));
		selectSonar = new JCheckBox[nSonars];
		for (int i = 0; i < nSonars; i++) {
			selectSonar[i] = new JCheckBox("Sonar " + i);
			mainPanel.add(selectSonar[i], c);
			c.gridy++;
		}
		
	}

	@Override
	public JComponent getDialogComponent() {
		return mainPanel;
	}

	@Override
	public void setParams() {
		ECDDataSelectParams params = ecdDataSelector.getParams();
		for (int i = 0; i < nSonars; i++) {
			selectSonar[i].setSelected((params.usedSonars & 1<<i) != 0);
		}
	}

	@Override
	public boolean getParams() {
		ECDDataSelectParams params = ecdDataSelector.getParams();
		params.usedSonars = 0;
		for (int i = 0; i < nSonars; i++) {
			if (selectSonar[i].isSelected()) {
				params.usedSonars |= (1<<i);
			}
		}
		return true;
	}

}
