package tritechgemini.swing;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import PamView.panel.PamPanel;
import tritechplugin.GeminiControl;
import userDisplay.UserDisplayComponent;
import userDisplay.UserDisplayControl;

public class GeminiTrackDisplay implements UserDisplayComponent {

	private GeminiControl geminiControl;
	
	private JPanel mainPanel;
	
	private String uniqueName;
	
	private GeminiTrackTable trackTable;

	public GeminiTrackDisplay(GeminiControl geminiControl, UserDisplayControl userDisplayControl, String uniqueDisplayName) {
		this.geminiControl = geminiControl;
		mainPanel = new PamPanel(new BorderLayout());
		uniqueName = uniqueDisplayName;
		trackTable = new GeminiTrackTable(geminiControl, geminiControl.getGeminiProcess().getTrackDataBlock(), "Gemini Tracks");
		mainPanel.add(BorderLayout.CENTER, trackTable.getComponent());
	}

	@Override
	public Component getComponent() {
		return mainPanel;
	}

	@Override
	public void openComponent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeComponent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyModelChanged(int changeType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUniqueName() {
		return uniqueName;
	}

	@Override
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	@Override
	public String getFrameTitle() {
		return "Gemini Tracks";
	}

}
