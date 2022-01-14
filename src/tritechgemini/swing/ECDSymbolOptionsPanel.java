package tritechgemini.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import PamView.ColourArray.ColourArrayType;
import PamView.ColourComboBox;
import PamView.dialog.PamGridBagContraints;
import PamView.symbol.SwingSymbolOptionsPanel;

public class ECDSymbolOptionsPanel implements SwingSymbolOptionsPanel {

	private ECDSymbolChooser ecdSymbolChooser;
	
	private JPanel mainPanel;

	private ColourComboBox colourComboBox;
	
	private JCheckBox scaleOpacity;
	
	private JLabel colourName;

	public ECDSymbolOptionsPanel(ECDSymbolChooser ecdSymbolChooser) {
		this.ecdSymbolChooser = ecdSymbolChooser;
		mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();
		mainPanel.setBorder(new TitledBorder("Image colour"));
		colourComboBox=new ColourComboBox(130,20);
		scaleOpacity = new JCheckBox("Scale transparancy");
		mainPanel.add(new JLabel("Colour: ", JLabel.LEFT), c);
		c.gridx++;
		mainPanel.add(colourName = new JLabel("         "), c);
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 2;
		mainPanel.add(colourComboBox, c);
		c.gridx = 0;
		c.gridy++;
//		c.gridwidth = 2;
		mainPanel.add(scaleOpacity, c);
		colourComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sayColour();
			}

		});
	}

	@Override
	public JComponent getDialogComponent() {
		return mainPanel;
	}

	private void sayColour() {
		ColourArrayType col = colourComboBox.getSelectedColourMap();
		if (col != null && colourName != null) {
			colourName.setText(col.toString());
		}
	}
	
	@Override
	public void setParams() {
		ECDSymbolOptions params = ecdSymbolChooser.getSymbolOptions();
		colourComboBox.setSelectedColourMap(params.getColourArrayType());
		scaleOpacity.setSelected(params.isScaleOpacity());
		sayColour();
	}

	@Override
	public boolean getParams() {
		ECDSymbolOptions params = ecdSymbolChooser.getSymbolOptions();
		params.setColourArrayType(colourComboBox.getSelectedColourMap());
		params.setScaleOpacity(scaleOpacity.isSelected());
		ecdSymbolChooser.configureImageMaker();
		return true;
	}

}
