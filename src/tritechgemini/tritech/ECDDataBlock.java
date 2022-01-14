package tritechgemini.tritech;

import java.util.ListIterator;

import PamController.OfflineDataStore;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;
import PamguardMVC.dataOffline.OfflineDataLoadInfo;
import dataMap.OfflineDataMap;
import pamScrollSystem.ViewLoadObserver;
import tritechgemini.GeminiProcess;
import tritechgemini.dataselect.ECDDataSelectCreator;
import tritechgemini.tritech.ecd.ECDRecordSet;
import tritechgemini.tritech.image.ECDImageMaker;

public class ECDDataBlock extends PamDataBlock<ECDDataUnit> {

	public ECDDataBlock(GeminiProcess geminiProcess) {
		super(ECDDataUnit.class, "Gemini Raw Data", geminiProcess, 0);
	}

	@Override
	public OfflineDataMap getPrimaryDataMap() {
		return super.getPrimaryDataMap();
	}

	/**
	 * Used in viewer to get a single image as close in time as possible to the 
	 * requested time
	 * @param timeMillis
	 * @param iSonar sonar id (indexed from 1)
	 * @return a record set (or null)
	 */
	public ECDRecordSet findRecordSet(long timeMillis, int iSonar) {
		ECDDataUnit ecdDataUnit = null;
		synchronized (getSynchLock()) {
			ListIterator<ECDDataUnit> it = this.getListIterator(-1);
			while (it.hasPrevious()) {
				ecdDataUnit = it.previous();
				if (ecdDataUnit.getTimeMilliseconds() < timeMillis) {
					break; // as soon as we've a unit that starts before us.  
				}
			}
		}
		if (ecdDataUnit != null) {
			return ecdDataUnit.findRecordSet(timeMillis, iSonar);
		}
		return null;
	}

	@Override
	public boolean loadViewerData(OfflineDataLoadInfo offlineDataLoadInfo, ViewLoadObserver loadObserver) {
		// TODO Auto-generated method stub
		return super.loadViewerData(offlineDataLoadInfo, loadObserver);
	}

	@Override
	public boolean loadViewerData(long dataStart, long dataEnd, ViewLoadObserver loadObserver) {
		// TODO Auto-generated method stub
		return super.loadViewerData(dataStart, dataEnd, loadObserver);
	}

	@Override
	public OfflineDataMap getOfflineDataMap(int iMap) {
		// TODO Auto-generated method stub
		return super.getOfflineDataMap(iMap);
	}

	@Override
	public OfflineDataMap getOfflineDataMap(OfflineDataStore dataSource) {
		// TODO Auto-generated method stub
		return super.getOfflineDataMap(dataSource);
	}

	/**
	 * Clear all the image data, so that it gets recreated. 
	 * @param ecdImageMaker 
	 */
	public void clearAllImages(ECDImageMaker ecdImageMaker) {
		synchronized (ecdImageMaker) {
			ListIterator<ECDDataUnit> iter = getListIterator(0);
			while (iter.hasNext()) {
				ECDDataUnit du = iter.next();
				du.clearRecordImages();
			}
			
		}
		
	}

}
