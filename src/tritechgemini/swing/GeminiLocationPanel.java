package tritechgemini.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import PamUtils.Coordinate3d;
import PamUtils.LatLong;
import PamView.dialog.PamDialog;
import PamView.dialog.PamGridBagContraints;
import tritechgemini.GeminiLocationParams;

public class GeminiLocationPanel {

	private GeminiLocationParams locationParams;

	private JPanel mainPanel;

	private static final String[] coordNames = {"x (East)", "y (North)", "z (Height)"};

	private JTextField[] coords = new JTextField[3];
	
	private JTextField xOffset, yOffset;

	private JTextField head, pitch;
	
	private JCheckBox flipLeftRight;

	private int fieldWidth = 5;

	private Window parent;

	public GeminiLocationPanel(Window parent, int sonarId) {
		super();
		this.parent = parent;
		mainPanel = new JPanel(new GridBagLayout());
		String st = String.format("Sonar %d", sonarId);
		mainPanel.setBorder(new TitledBorder(st));
		GridBagConstraints c = new PamGridBagContraints();
		c.gridwidth = 3;
		mainPanel.add(new JLabel("Coordinates rel. Hydrophone Streamer"), c);
		for (int i = 0; i < 3; i++) {
			c.gridx = 0;
			c.gridy++;
			c.gridwidth = 1;
			mainPanel.add(new JLabel(coordNames[i] + " ", JLabel.RIGHT), c);
			c.gridx++;
			mainPanel.add(coords[i] = new JTextField(fieldWidth), c);
			c.gridx++;
			mainPanel.add(new JLabel(" m"), c);
			String tip = String.format("%s coordinate of the sonar device", coordNames[i]);
			coords[i].setToolTipText(tip);
		}
		c.gridx = 0;
		c.gridy++;
		mainPanel.add(new JLabel("Heading ", JLabel.RIGHT), c);
		c.gridx++;
		mainPanel.add(head = new JTextField(fieldWidth), c);
		c.gridx++;
		mainPanel.add(new JLabel(LatLong.deg + "T"), c);
		c.gridx = 0;
		c.gridy++;
		mainPanel.add(new JLabel("pitch ", JLabel.RIGHT), c);
		c.gridx++;
		mainPanel.add(pitch = new JTextField(fieldWidth), c);
		c.gridx++;
		mainPanel.add(new JLabel(LatLong.deg + ""), c);
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 3;
		mainPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		mainPanel.add(new JLabel("Display Offsets Streamer"), c);
		c.gridy++;
		c.gridx = 0;
		mainPanel.add(flipLeftRight = new JCheckBox("Flip image left/right"), c);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		mainPanel.add(new JLabel("x Offset", JLabel.RIGHT), c);
		c.gridx++;
		mainPanel.add(xOffset = new JTextField(fieldWidth), c);
		c.gridx++;
		mainPanel.add(new JLabel(" m"), c);
		c.gridx = 0;
		c.gridy++;
		mainPanel.add(new JLabel("y Offset", JLabel.RIGHT), c);
		c.gridx++;
		mainPanel.add(yOffset = new JTextField(fieldWidth), c);
		c.gridx++;
		mainPanel.add(new JLabel(" m"), c);
		flipLeftRight.setToolTipText("Sonar is upside down, so flip image");
		xOffset.setToolTipText("Value will be subtracted from x coorinates in the data");
		yOffset.setToolTipText("Value will be subtracted from y coorinates in the data");

	}

	public JComponent getDialogComponent() {
		return mainPanel;
	}

	public GeminiLocationParams getParams(GeminiLocationParams locationParams) {
		if (locationParams == null) {
			locationParams = new GeminiLocationParams();
		}
		else {
			locationParams = locationParams.clone();
		}
		Coordinate3d c3d = new Coordinate3d();
		for (int i = 0; i < 3; i++) {
			try {
				double c = Double.valueOf(coords[i].getText());
				c3d.setCoordinate(i, c);
			}
			catch (NumberFormatException e) {
				PamDialog.showWarning(parent, "Gemini Location", String.format("Error in %s coordinate", coordNames[i]));
				return null;
			}
		}
		locationParams.setSonarXYZ(c3d);
		try {
			locationParams.setSonarHeadingD(Double.valueOf(head.getText()));
		}
		catch (NumberFormatException e) {
			PamDialog.showWarning(parent, "Gemini Location", String.format("Error in sonar heading"));
			return null;
		}
		try {
			locationParams.setSonarPitchD(Double.valueOf(pitch.getText()));
		}
		catch (NumberFormatException e) {
			PamDialog.showWarning(parent, "Gemini Location", String.format("Error in sonar pitch"));
			return null;
		}
		locationParams.setFlipLeftRight(flipLeftRight.isSelected());
		try {
			locationParams.setOffsetX(Double.valueOf(xOffset.getText()));
			locationParams.setOffsetY(Double.valueOf(yOffset.getText()));
		}
		catch (NumberFormatException e) {
			PamDialog.showWarning(parent, "Gemini Location", "Eror in sonar offset");
			return null;
		}

		return locationParams;
	}

	public void setParams(GeminiLocationParams locationParams) {
		Coordinate3d xyz = locationParams.getSonarXYZ();
		for (int i = 0; i < 3; i++) {
			coords[i].setText(String.format("%3.2f", xyz.getCoordinate(i)));
		}
		head.setText(String.format("%3.1f", locationParams.getSonarHeadingD()));
		pitch.setText(String.format("%3.1f", locationParams.getSonarPitchD()));
		flipLeftRight.setSelected(locationParams.isFlipLeftRight());
		xOffset.setText(String.format("%3.2f", locationParams.getOffsetX()));
		yOffset.setText(String.format("%3.2f", locationParams.getOffsetY()));
	}

}
