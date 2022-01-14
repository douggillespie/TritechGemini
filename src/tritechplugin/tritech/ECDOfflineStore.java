package tritechplugin.tritech;

import java.awt.Window;
import java.io.File;
import java.io.FileFilter;

import PamController.OfflineDataStore;
import PamController.OfflineFileDataStore;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;
import PamguardMVC.dataOffline.OfflineDataLoadInfo;
import dataGram.DatagramManager;
import dataMap.OfflineDataMap;
import dataMap.OfflineDataMapPoint;
import dataMap.filemaps.FileDataMapPoint;
import dataMap.filemaps.OfflineFileServer;
import pamScrollSystem.ViewLoadObserver;
import tritechplugin.GeminiControl;

public class ECDOfflineStore implements OfflineFileDataStore {

	private GeminiControl gemininControl;
	
	public static final String DATASOURCENAME = "Gemini data files";
	
	
	public ECDOfflineStore(GeminiControl gemininControl) {
		super();
		this.gemininControl = gemininControl;
	}


	@Override
	public void createOfflineDataMap(Window parentFrame) {
		gemininControl.getEcdOfflineStore().createOfflineDataMap(parentFrame);
	}


	@Override
	public String getDataSourceName() {
		return DATASOURCENAME;
	}


	@Override
	public boolean loadData(PamDataBlock dataBlock, OfflineDataLoadInfo offlineDataLoadInfo,
			ViewLoadObserver loadObserver) {
		return gemininControl.getEcdOfflineStore().loadData(dataBlock, offlineDataLoadInfo, loadObserver);
	}


	@Override
	public boolean saveData(PamDataBlock dataBlock) {
		// nthing to save for these data. 
		return true;
	}


	@Override
	public boolean rewriteIndexFile(PamDataBlock dataBlock, OfflineDataMapPoint dmp) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public DatagramManager getDatagramManager() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public OfflineFileServer getOfflineFileServer() {
		return gemininControl.getOfflineFileServer();
	}


	@Override
	public PamDataBlock getRawDataBlock() {
		return gemininControl.getGeminiProcess().getEcdDataBlock();
	}


	@Override
	public PamProcess getParentProcess() {
		return gemininControl.getGeminiProcess();
	}


	@Override
	public String getUnitName() {
		return gemininControl.getUnitName();
	}

}
