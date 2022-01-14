package tritechgemini.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import PamController.PamController;
import PamView.dialog.PamGridBagContraints;
import PamView.dialog.PamLabel;
import PamView.dialog.PamTextArea;
import PamView.dialog.ScrollingPamLabel;
import PamView.panel.PamPanel;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamObserver;
import PamguardMVC.PamObserverAdapter;
import pamViewFX.fxNodes.PamTextField;
import tritechgemini.status.GeminiStatusDataBlock;
import tritechgemini.status.GeminiStatusDataUnit;
import tritechplugin.GeminiControl;

public class GeminiStatusPanel {

	private GeminiControl geminiControl;
	
	private JPanel mainPanel;
	
	private JLabel frame;
	private JLabel status;
	private JLabel speedOfSound;
	private PamLabel file;

	private long lastStatusTime;

	private Timer timer;

	public GeminiStatusPanel(GeminiControl geminiControl) {
		this.geminiControl = geminiControl;
		GeminiStatusDataBlock statusDataBlock = geminiControl.getGeminiProcess().getGeminiStatusDataBlock();
		statusDataBlock.addObserver(new StatusObserver());
		
		mainPanel = new PamPanel();
		file = new PamLabel("");
		status = new PamLabel(" ");
		speedOfSound = new PamLabel(" ");
		file.setToolTipText("Name and frame number of current output file");
		status.setToolTipText("Gemini SeaTec software status");
		speedOfSound.setToolTipText("Speed of sound from Gemini sonar");
		
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();
		mainPanel.add(new PamLabel("Status ", JLabel.RIGHT), c);
		c.gridx++;
		mainPanel.add(status, c);
		c.gridx++;
		mainPanel.add(new PamLabel(" SoS", PamLabel.RIGHT), c);
		c.gridx++;
		mainPanel.add(speedOfSound, c);
		c.gridx = 0;
		c.gridy++;
		mainPanel.add(new PamLabel("File ", PamLabel.LEFT), c);
		c.gridx++;
		c.gridwidth = 2;
		mainPanel.add(new PamLabel("Frame ", PamLabel.RIGHT), c);
		c.gridx+=c.gridwidth;
		c.gridwidth = 1;
		mainPanel.add(frame = new PamLabel(" - "), c);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 5;
		mainPanel.add(file, c);
		
		timer = new Timer(5000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkIdleStatus();
			}
		});
		if (PamController.getInstance().getRunMode() == PamController.RUN_NORMAL) {
			timer.start();
		}
	}
	
	/**
	 * Called every few seconds to check there has been a recent status
	 * data unit. 
	 */
	protected void checkIdleStatus() {
		if (System.currentTimeMillis() - lastStatusTime > 5000) {
			sayStatus(null);
		}
	}

	private class StatusObserver extends PamObserverAdapter {

		@Override
		public String getObserverName() {
			return "Gemini Status Panel";
		}

		@Override
		public void addData(PamObservable dataBlock, PamDataUnit dataUnit) {
			GeminiStatusDataUnit statusDataUnit = (GeminiStatusDataUnit) dataUnit;
			sayStatus(statusDataUnit);
		}
		
	}

	public void sayStatus(GeminiStatusDataUnit statusDataUnit) {
		if (statusDataUnit == null) {
			status.setText(" - ");
			speedOfSound.setText(" - ");
			file.setText("SeaTec not running");
		}
		else {
			lastStatusTime = System.currentTimeMillis();
			status.setText(statusDataUnit.getStatus() > 0 ? "Ok" : "Idle");
			float sos = statusDataUnit.getSpeedOfSound();
			if (sos > 0) {
				speedOfSound.setText(String.format("%3.1fm/s", sos));
			}
			else {
				speedOfSound.setText("Err");
			}
			String f = statusDataUnit.getFileName();
			if (f == null || f.length() < 4) {
				file.setText("No file (not enabled)");
			}
			else {
				if (f.endsWith(".ecd")) {
					f = f.substring(0, f.length()-4);
				}
				file.setText(f);
				frame.setText(Integer.valueOf(statusDataUnit.getFrame()).toString());
			}
		}
	}

	public Component getComponent() {
		return mainPanel;
	}
}
