package tritechgemini.tritech;

import PamController.OfflineDataStore;
import PamguardMVC.PamDataBlock;
import dataMap.OfflineDataMap;
import dataMap.filemaps.FileDataMapPoint;

public class ECDDataMap extends OfflineDataMap<FileDataMapPoint> {

	public ECDDataMap(OfflineDataStore offlineDataStore, PamDataBlock parentDataBlock) {
		super(offlineDataStore, parentDataBlock);
	}

}
