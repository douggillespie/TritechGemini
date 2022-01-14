package tritechgemini.tritech;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;

import Acquisition.filedate.StandardFileDate;
import PamUtils.PamFileFilter;
import PamguardMVC.PamDataBlock;
import PamguardMVC.dataOffline.OfflineDataLoadInfo;
import dataMap.OfflineDataMap;
import dataMap.filemaps.FileDataMapPoint;
import dataMap.filemaps.OfflineFileServer;
import pamScrollSystem.ViewLoadObserver;
import tritechgemini.tritech.ecd.ECDFile;
import tritechplugin.GeminiControl;

public class OfflineEDCfileServer extends OfflineFileServer<FileDataMapPoint> {

	private StandardFileDate fileDate;
	private GeminiControl geminiControl;

	public OfflineEDCfileServer(GeminiControl geminiControl, ECDOfflineStore ecdOfflineStore) {
		super(ecdOfflineStore);
		this.geminiControl = geminiControl;
		fileDate = new StandardFileDate(geminiControl);
	}

	@Override
	public boolean loadData(PamDataBlock dataBlock, OfflineDataLoadInfo offlineDataLoadInfo,
			ViewLoadObserver loadObserver) {
		/*
		 * Need to go through the data map and load up the ECD file for every
		 * overlapping unit so that we can quickly get data out.
		 */
		ECDDataBlock ecdDataBlock = geminiControl.getGeminiProcess().getEcdDataBlock();
		ecdDataBlock.clearAll();
		OfflineDataMap<FileDataMapPoint> dataMap = getDataMap();
		Iterator<FileDataMapPoint> mapIt = dataMap.getListIterator();
		while (mapIt.hasNext()) {
			FileDataMapPoint mapPoint = mapIt.next();
			if (mapPoint.getEndTime() < offlineDataLoadInfo.getStartMillis()) {
				continue;
			}
			if (mapPoint.getStartTime() > offlineDataLoadInfo.getEndMillis()) {
				break;
			}
			ECDFile ecdFile = new ECDFile(mapPoint.getSoundFile());
			if (ecdFile.isFileOk()) {
				ECDDataUnit ecdDataUnit = new ECDDataUnit(mapPoint.getStartTime(), ecdFile);
				ecdDataBlock.addPamData(ecdDataUnit);
			}
		}
		return false;
	}

	@Override
	public OfflineDataMap<FileDataMapPoint> createDataMap(OfflineFileServer<FileDataMapPoint> offlineFileServer,
			PamDataBlock pamDataBlock) {
		return new ECDDataMap(offlineFileServer, pamDataBlock);
	}

	@Override
	public FileFilter getFileFilter() {
		PamFileFilter fileFilter = new PamFileFilter("Tritech Gemini Data Files", ".ecd");
		return fileFilter;
	}

	@Override
	public long[] getFileStartandEndTime(File file) {
		/*
		 * This is a PITA for these files since sometimes they have the date and time in
		 * the name and other times the data is in the folder and just the time in the
		 * name.
		 */
		boolean useFileName = false;
		long t = 0;
		if (useFileName) {
			// this is no good since file times are in local, but records inside them are UTC. 
			String name = file.getName();
			// count the number of digits in the file.
			int nDig = countDigits(name);
			if (nDig < 12) {
				// need to get more from the path
				File nextFolder = file.getParentFile();
				String nextName = nextFolder.getName();
				name = nextName + "_" + name;
			}
			// might now be lucky! 
			t = fileDate.getTimeFromFile(new File(name));
		}
		else {
			// but this is going to take a lot longer, so might be worth storing a data map. 
			// about 50ms per file, and 1000's of files per day 
			t = ECDFile.findFirstRecordTime(file);
		}
		long[] ts = new long[2];
		ts[0] = t;
//		ts[1] = t+300000;
//		ts[0] = t - 300000;
		return ts;
	}

	private int countDigits(String name) {
		int nDig = 0;
		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			if (ch == '.') {
				break;
			}
			if (ch >= '0' && ch <= '9') {
				nDig++;
			}
		}
		return nDig;
	}

	@Override
	public FileDataMapPoint createMapPoint(File file, long startTime, long endTime) {
		return new FileDataMapPoint(file, startTime, endTime);
	}

	@Override
	public void sortMapEndTimes() {
		/**
		 * Mostly these files are a few minutes long, typically 2-3.
		 */
		long meanDuration = 0;
		int nMean = 0;
		long tooLong = 300000L;// 5 minutes.
		OfflineDataMap<FileDataMapPoint> dataMap = this.getDataMap();
		Iterator<FileDataMapPoint> it = dataMap.getListIterator();
		FileDataMapPoint mapPoint;
		FileDataMapPoint prevPoint = null;
		while (it.hasNext()) {
			mapPoint = it.next();
			long start = mapPoint.getStartTime();
			if (prevPoint != null) {
				long gap = start - prevPoint.getStartTime();
				if (gap < tooLong) {
					prevPoint.setEndTime(start);
					meanDuration += gap;
					nMean++;
				} else if (nMean > 0) {
					// give the file the mean duraiton of files. They are all similar!
					prevPoint.setEndTime(prevPoint.getStartTime() + meanDuration / nMean);
				}
			}
			prevPoint = mapPoint;
		}
		if (prevPoint != null) {
			if (nMean > 0) {
				// give the last point the mean duration.
				prevPoint.setEndTime(prevPoint.getStartTime() + meanDuration / nMean);
			} else {
				// if there is only one file!
				prevPoint.setEndTime(prevPoint.getStartTime() + tooLong);
			}
		}
	}

}
