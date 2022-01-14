package tritechplugin.tritech;


import PamguardMVC.PamDataUnit;
import tritechgemini.fileio.GeminiFileCatalog;
import tritechgemini.imagedata.GeminiImageRecordI;

public class ECDDataUnit extends PamDataUnit {

	private GeminiFileCatalog geminiFileCatalog;

	public ECDDataUnit(long timeMilliseconds, GeminiFileCatalog geminiFileCatalog) {
		super(timeMilliseconds);
		this.geminiFileCatalog = geminiFileCatalog;
	}
	/**
	 * Used in viewer to get a single image as close in time as possible to the 
	 * requested time
	 * @param timeMillis
	 * @param iSonar sonar id (indexed from 1)
	 * @return a record set (or null)
	 */
	public GeminiImageRecordI findRecordSet(long timeMillis, int iSonar) {
		if (geminiFileCatalog == null) {
			return null;
		}
		GeminiImageRecordI geminiRecord = geminiFileCatalog.findRecordForIndexandTime(iSonar, timeMillis);
//		ArrayList<ECDRecordSet> ecdRecs = ecdFile.getEmptyECDRecords();
//		for (ECDRecordSet ecdRecordSet : ecdRecs) {
//			if (ecdRecordSet.getTimeMillis() > timeMillis) { //   in summer ! - (3600000L)
//				if (ecdRecordSet.getSonar() == iSonar) {
//					return ecdRecordSet;
//				}
//			}
//		}
		return geminiRecord;
	}

	public void clearRecordImages() {
		if (geminiFileCatalog == null) {
			return;
		}
		geminiFileCatalog.freeAllImageData();
		
	}

}
