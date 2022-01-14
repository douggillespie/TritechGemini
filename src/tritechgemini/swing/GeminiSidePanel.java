package tritechgemini.swing;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.border.TitledBorder;

import PamView.PamSidePanel;
import PamView.panel.PamPanel;
import tritechgemini.GeminiControl;

public class GeminiSidePanel implements PamSidePanel {

	private GeminiControl geminiControl;
	private GeminiStatusPanel geminiStatusPanel;
	private PamPanel mainPanel;

	public GeminiSidePanel(GeminiControl geminiControl) {
		this.geminiControl = geminiControl;
		geminiStatusPanel = new GeminiStatusPanel(geminiControl);
		mainPanel = new PamPanel(new BorderLayout());
		mainPanel.add(geminiStatusPanel.getComponent());
		mainPanel.setBorder(new TitledBorder(geminiControl.getUnitName()));
	}
	@Override
	public JComponent getPanel() {
		return mainPanel;
	}

	@Override
	public void rename(String arg0) {
		// TODO Auto-generated method stub
		
	}

}
