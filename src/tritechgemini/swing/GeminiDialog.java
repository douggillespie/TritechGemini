package tritechgemini.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import PamView.dialog.PamGridBagContraints;
import PamView.panel.PamAlignmentPanel;
import annotation.handler.AnnotationsSelectionPanel;
import dataMap.filemaps.OfflineFileDialogPanel;
import dataMap.filemaps.OfflineFileParameters;
import tritechgemini.target.TargetType;
import tritechgemini.tritech.OfflineEDCfileServer;
import tritechplugin.GeminiControl;
import tritechplugin.GeminiLocationParams;
import tritechplugin.GeminiParameters;

public class GeminiDialog extends PamView.dialog.PamDialog {
	
	private static GeminiDialog singleInstance;
	
	private GeminiParameters geminiParams;
	
	private JComboBox<Integer> nSonars;
	
	private JTextField rxPort;
	
	private JTextField loggingInterval;
	
	private JTabbedPane locationTabs;
	
	private GeminiLocationPanel[] locationPanels;
	
	private JComboBox<String> minTrackScore;

	private GeminiControl geminiControl;

	private AnnotationsSelectionPanel annotationPanel;
	
	private OfflineEDCfileServer offlineFileServer;

	private OfflineFileDialogPanel offlineFileDialogPanel;

	private GeminiDialog(Window parentFrame, GeminiControl geminiControl) {
		super(parentFrame, "Gemini options", true);
		this.geminiControl = geminiControl;
		offlineFileServer = geminiControl.getOfflineFileServer();
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel topHalf = new PamAlignmentPanel(new GridBagLayout(), BorderLayout.NORTH);
		topHalf.setBorder(new TitledBorder("General options"));
		JPanel botHalf = new JPanel(new BorderLayout());
		JTabbedPane tabPane = new JTabbedPane();
		mainPanel.add(BorderLayout.CENTER, tabPane);
		tabPane.add("General", topHalf);
		tabPane.add("Sonars", botHalf);
//		mainPanel.add(BorderLayout.NORTH, topHalf);
//		mainPanel.add(BorderLayout.CENTER, botHalf);
		GridBagConstraints c = new PamGridBagContraints();
		topHalf.add(new JLabel("Number of sonars ", JLabel.RIGHT),c);
		c.gridx++;
		c.gridwidth = 2;
		topHalf.add(nSonars = new JComboBox<Integer>(), c);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		topHalf.add(new JLabel("RX UDP Port ", JLabel.RIGHT),c);
		c.gridx++;
		c.gridwidth=2;
		topHalf.add(rxPort = new JTextField(7), c);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		topHalf.add(new JLabel("Status Logging interval ", JLabel.RIGHT),c);
		c.gridx++;
		topHalf.add(loggingInterval = new JTextField(4), c);
		c.gridx ++;
		topHalf.add(new JLabel(" s"), c);
		c.gridx = 0;
		c.gridy++;
		topHalf.add(new JLabel("Min track type ", JLabel.RIGHT),c);
		c.gridx++;
		c.gridwidth = 2;
		topHalf.add(minTrackScore = new JComboBox<String>(), c);
		minTrackScore.setToolTipText("Minimum target type for at least one target in the track for it to be stored");
		
//		c.gridx = 0;
//		c.gridwidth = 3;
//		c.gridy+=2;
//		topHalf.add(new JLabel("Sonar Location Data"), c);
		String[] trackTypes = TargetType.types;
		for (int i = 0; i < trackTypes.length; i++) {
			minTrackScore.addItem(trackTypes[i]);
		}
		
		botHalf.add(locationTabs = new JTabbedPane());
		
		for (int i = 1; i <= GeminiControl.MAX_SONARS; i++) {
			nSonars.addItem(new Integer(i));
		}
		nSonars.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newNSonars();
			}
		});
		
		annotationPanel = geminiControl.getGeminiProcess().getAnnotationHandler().getSelectionPanel();
		if (annotationPanel != null) {
			PamAlignmentPanel alPanel;
			tabPane.add("Annotations", alPanel = new PamAlignmentPanel(annotationPanel.getDialogComponent(), BorderLayout.NORTH));
			alPanel.setBorder(new TitledBorder("Select Annotations"));
		}
		
		if (offlineFileServer != null) {
			offlineFileDialogPanel = new OfflineFileDialogPanel(geminiControl.getEcdOfflineStore(), this); 
			tabPane.add("Offline Files", offlineFileDialogPanel.getComponent());
		}
		
		setDialogComponent(mainPanel);
		
		rxPort.setToolTipText("Match UDP port set on SeaTec software");
		loggingInterval.setToolTipText("Will always log status data as soon as a file name changes");
		
	}
	
	protected void newNSonars() {
		int currentN = locationTabs.getTabCount();
		int newN = currentN;
		newN = (int) nSonars.getSelectedItem();
		if (newN == currentN) {
			return;
		}
		// clear and restart ...
		locationTabs.removeAll();
		locationPanels = new GeminiLocationPanel[newN];
		for (int i = 0; i < newN; i++) {
			locationPanels[i] = new GeminiLocationPanel(getOwner(), i);
			locationTabs.addTab(String.format("TG %d", i), locationPanels[i].getDialogComponent());
		}

		fillLocationPanels();
	}

	public static GeminiParameters showDialog(GeminiControl geminiControl, Window window, GeminiParameters geminiParameters) {
//		if (singleInstance == null) {
			singleInstance = new GeminiDialog(window, geminiControl);
//		}
		singleInstance.geminiParams = geminiParameters.clone();
		singleInstance.setParams();
		singleInstance.setVisible(true);
		return singleInstance.geminiParams;
	}

	private void setParams() {
		nSonars.setSelectedItem(geminiParams.nSonars);
		rxPort.setText(String.format("%d", geminiParams.rxUDPPort));
		loggingInterval.setText(String.format("%d", geminiParams.statusStorageIntervalS));
		int ts = geminiParams.minTrackScore - 1; // this is indexed from one for some bizar reason. 
		if (ts >= 0 && ts < minTrackScore.getItemCount()) {
			minTrackScore.setSelectedIndex(ts);
		}
		newNSonars();
		if (annotationPanel != null) {
			annotationPanel.setParams();
		}
		if (offlineFileDialogPanel != null) {
			offlineFileDialogPanel.setParams();
		}
		pack();
	}

	private void fillLocationPanels() {
		for (int i = 0; i < locationPanels.length; i++) {
			locationPanels[i].setParams(geminiParams.getGeminiLocation(i));
		}
	}

	@Override
	public boolean getParams() {
		geminiParams.nSonars = (int) nSonars.getSelectedItem();
		try {
			geminiParams.rxUDPPort = Integer.valueOf(rxPort.getText());
		}
		catch (NumberFormatException e) {
			return showWarning("Invalid UDP Receive Port");
		}
		try {
			geminiParams.statusStorageIntervalS = Integer.valueOf(loggingInterval.getText());
		}
		catch (NumberFormatException e) {
			return showWarning("Invalid Status Logging interval");
		}
		if (geminiParams.nSonars != locationPanels.length) {
			return showWarning("Something is totally wrong with the location panels");
		}
		for (int i = 0; i < geminiParams.nSonars; i++) {
			GeminiLocationParams lp = locationPanels[i].getParams(geminiParams.getGeminiLocation(i));
			if (lp == null) {
				return false;
			}
			else {
				geminiParams.setGeminiLocation(i, lp);
			}
		}
		geminiParams.minTrackScore = minTrackScore.getSelectedIndex()+1;
		if (annotationPanel != null) {
			if (!annotationPanel.getParams()) {
				return false;
			}
		}
		if (offlineFileDialogPanel != null) {
			OfflineFileParameters offlineFileParams = offlineFileDialogPanel.getParams();
			if (offlineFileParams == null) {
				return false;
			}
			else {
				offlineFileServer.setOfflineFileParameters(offlineFileParams);
//				geminiParams.setOfflineFileParams(offlineFileParams);
			}
		}
		
		return true;
	}

	@Override
	public void cancelButtonPressed() {
		geminiParams = null;
	}
	
	@Override
	public void restoreDefaultSettings() {
		// TODO Auto-generated method stub
		
	}

}
