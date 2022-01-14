package tritechgemini;

import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import PamController.OfflineDataStore;
import PamController.PamControlledUnit;
import PamController.PamControlledUnitSettings;
import PamController.PamController;
import PamController.PamControllerInterface;
import PamController.PamSettingManager;
import PamController.PamSettings;
import PamView.PamSidePanel;
import PamguardMVC.PamDataBlock;
import PamguardMVC.dataOffline.OfflineDataLoadInfo;
import backupmanager.BackupInformation;
import backupmanager.BackupManager;
import dataGram.DatagramManager;
import dataMap.OfflineDataMapPoint;
import pamScrollSystem.ViewLoadObserver;
import tritechgemini.swing.GeminiDialog;
import tritechgemini.swing.GeminiSidePanel;
import tritechgemini.swing.TrackDisplayProvider;
import tritechgemini.tritech.ECDOfflineStore;
import tritechgemini.tritech.OfflineEDCfileServer;
import userDisplay.UserDisplayControl;
import warnings.PamWarning;
import warnings.WarningSystem;

public class GeminiControl extends PamControlledUnit implements PamSettings, OfflineDataStore {

	public static final String unitType = "Tritech Gemini Control";
	
	private GeminiParameters geminiParameters = new GeminiParameters();
	
	private PamWarning geminiWarning;
	
	private GeminiProcess geminiProcess;
		
	public static final int MAX_SONARS = 4; 
	
	private GeminiImporter geminiImporter;

	private OfflineEDCfileServer offlineFileServer;

	private ECDOfflineStore ecdOfflineStore;
	
	/*
	 * Some typical strings
	 * 
Received: $PTRISTA,0,1,11092019,164402,*72
Received: $PTRISTA,0,1,11092019,164403,*73
Received: $PTRISTA,0,1,11092019,164404,*74
Received: $PTRISTA,0,1,11092019,164405,*75
Received: $PTRISTA,0,1,11092019,164406,*76
Received: $PTRISTA,0,1,11092019,164407,*77
Received: $PTRISTA,0,1,11092019,164408,*78
Received: $PTRISTA,0,1,11092019,164409,*79
	 */
	
	public GeminiControl(String unitName) {
		super(unitType, unitName);
		geminiWarning = new PamWarning(unitName, "", 0);
		geminiProcess = new GeminiProcess(this);
		addPamProcess(geminiProcess);
		geminiImporter = new GeminiImporter(this);
		PamSettingManager.getInstance().registerSettings(this);		

		UserDisplayControl.addUserDisplayProvider(new TrackDisplayProvider(this));
		
		if (isViewer()) {
			ecdOfflineStore = new ECDOfflineStore(this);
			offlineFileServer = new OfflineEDCfileServer(this, ecdOfflineStore);
		}
		
	}

	@Override
	public Serializable getSettingsReference() {
		return geminiParameters;
	}

	@Override
	public long getSettingsVersion() {
		return GeminiParameters.serialVersionUID;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings settings) {
		geminiParameters = (GeminiParameters) settings.getSettings();
		return geminiParameters != null;
	}
	
	/**
	 * Set a warning message. <br>
	 * Warning is removed if level == 0
	 * @param level warning level
	 * @param warning message
	 */
	public synchronized void setWarning(int level, String warning) {
		if (level == 0) {
			WarningSystem.getWarningSystem().removeWarning(geminiWarning);
		}
		else {
			geminiWarning.setWarnignLevel(level);
			geminiWarning.setWarningMessage(warning);
			WarningSystem.getWarningSystem().addWarning(geminiWarning);
		}
	}

	@Override
	public void notifyModelChanged(int changeType) {
		super.notifyModelChanged(changeType);
		if (changeType == PamController.INITIALIZATION_COMPLETE) {
			if (PamController.getInstance().getRunMode() == PamController.RUN_NORMAL && getInstanceIndex() == 0) {
				geminiProcess.startUDPThread();
//				BackupManager backupManager = BackupManager.getBackupManager();
//				if (backupManager != null) {
//					backupManager.addBackupStream(new GeminiBackupStream(this));
				//				}
			}
			checkECDDataMap();
		}
	}
	
	private void checkECDDataMap() {
		if (isViewer && offlineFileServer != null) {
			offlineFileServer.createOfflineDataMap(PamController.getMainFrame());
		}
	}

	/**
	 * @return the geminiParameters
	 */
	public GeminiParameters getGeminiParameters() {
		return geminiParameters;
	}

	/**
	 * @return the geminiProcess
	 */
	public GeminiProcess getGeminiProcess() {
		return geminiProcess;
	}

	@Override
	public PamSidePanel getSidePanel() {
		return new GeminiSidePanel(this);
	}

	@Override
	public JMenuItem createDetectionMenu(Frame parentFrame) {
		JMenu menu = new JMenu(this.getUnitName());
		JMenuItem menuItem = new JMenuItem(getUnitName() + " settings...");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				geminiSettings(parentFrame);
			}
		});
		menu.add(menuItem);
		
		geminiImporter.addMenuItems(menu);
		
		return menu;
	}

	protected void geminiSettings(Frame parentFrame) {
		GeminiParameters newParams = GeminiDialog.showDialog(this, parentFrame, geminiParameters);
		if (newParams != null) {
			geminiParameters = newParams;
			geminiProcess.sortSQLLogging();
			checkECDDataMap();
		}
	}

	/**
	 * @return the offlineFileServer
	 */
	public OfflineEDCfileServer getOfflineFileServer() {
		return offlineFileServer;
	}

	/**
	 * @return the ecdOfflineStore
	 */
	public ECDOfflineStore getEcdOfflineStore() {
		return ecdOfflineStore;
	}

	@Override
	public void createOfflineDataMap(Window parentFrame) {
		if (ecdOfflineStore == null) {
			return;
		}
		ecdOfflineStore.createOfflineDataMap(parentFrame);
	}

	@Override
	public String getDataSourceName() {
		return ECDOfflineStore.DATASOURCENAME;
	}

	@Override
	public boolean loadData(PamDataBlock dataBlock, OfflineDataLoadInfo offlineDataLoadInfo,
			ViewLoadObserver loadObserver) {
		if (ecdOfflineStore == null) {
			return false;
		}
		return ecdOfflineStore.loadData(dataBlock, offlineDataLoadInfo, loadObserver);
	}

	@Override
	public boolean saveData(PamDataBlock dataBlock) {
		if (ecdOfflineStore == null) {
			return false;
		}
		return ecdOfflineStore.saveData(dataBlock);
	}

	@Override
	public boolean rewriteIndexFile(PamDataBlock dataBlock, OfflineDataMapPoint dmp) {
		if (ecdOfflineStore == null) {
			return false;
		}
		return ecdOfflineStore.rewriteIndexFile(dataBlock, dmp);
	}

	@Override
	public DatagramManager getDatagramManager() {
		if (ecdOfflineStore == null) {
			return null;
		}
		return ecdOfflineStore.getDatagramManager();
	}


}
