package tritechplugin.swing;

import java.awt.BorderLayout;
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
import PamView.panel.PamAlignmentPanel;
import PamView.panel.PamNorthPanel;
import PamView.symbol.SwingSymbolOptionsPanel;

public class ECDSymbolOptionsPanel implements SwingSymbolOptionsPanel {

	private ECDSymbolChooser ecdSymbolChooser;
	
	private JPanel northPanel;

	private ColourComboBox colourComboBox;
	
	private JCheckBox scaleOpacity;
	
	private JCheckBox combinedImage;
	
	private JLabel colourName;

	public ECDSymbolOptionsPanel(ECDSymbolChooser ecdSymbolChooser) {
		this.ecdSymbolChooser = ecdSymbolChooser;
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();
		mainPanel.setBorder(new TitledBorder("Image colour map"));
		combinedImage = new JCheckBox("Combine image");
		colourComboBox=new ColourComboBox(130,20);
		scaleOpacity = new JCheckBox("Scale transparancy");
		mainPanel.add(combinedImage, c);
		c.gridy++;
		mainPanel.add(new JLabel("Colour map: ", JLabel.LEFT), c);
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
		combinedImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				combineImageAction();
			}
		});
		combinedImage.setToolTipText("combine multiple sonar images into a single image with a primary colour for each sonar (red, blue, green)");
		colourComboBox.setToolTipText("Colour map for single sonars");
		scaleOpacity.setToolTipText("Scale image opacity by sonar intensity");
		
		northPanel = new PamAlignmentPanel(mainPanel, BorderLayout.NORTH);
		
	}

	protected void combineImageAction() {
		enableControls();
	}

	private void enableControls() {
		int nSonars = ecdSymbolChooser.getGeminiControl().getGeminiParameters().nSonars;
		if (nSonars < 2) {
			combinedImage.setEnabled(false);
			combinedImage.setSelected(false);
			colourComboBox.setEnabled(true);
		}
		else {
			combinedImage.setEnabled(true);
			boolean isComb = combinedImage.isSelected();
			colourComboBox.setEnabled(isComb == false);
		}
	}

	@Override
	public JComponent getDialogComponent() {
		return northPanel;
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
		combinedImage.setSelected(params.isCombineSingleImage());
		enableControls();
		sayColour();
	}

	@Override
	public boolean getParams() {
		ECDSymbolOptions params = ecdSymbolChooser.getSymbolOptions();
		params.setColourArrayType(colourComboBox.getSelectedColourMap());
		params.setScaleOpacity(scaleOpacity.isSelected());
		params.setCombineSingleImage(combinedImage.isSelected());
		ecdSymbolChooser.configureImageMaker();
		return true;
	}

}
