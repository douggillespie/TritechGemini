package tritechplugin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import Array.ArrayManager;
import annotation.handler.AnnotationChoices;
import dataMap.filemaps.OfflineFileParameters;

public class GeminiParameters implements Serializable, Cloneable {

	public static final long serialVersionUID = 1L;

	public int rxUDPPort = 52904;
	
	public int statusStorageIntervalS = 300;
	
	public int nSonars = 2;
	
	public int minTrackScore = 2;
	
	private Hashtable<Integer, GeminiLocationParams> geminiLocations;
	
	private AnnotationChoices annotationChoices;

//	private OfflineFileParameters offlineFileParams;
	
	/**
	 * Get a gemini location. If the location is not set for that sonar index, 
	 * it will return the location of the highest indexed sonar. If there is nothing
	 * there it will return a default location. 
	 * @param iSonar sonar index (zero indexed)
	 * @return sonar location (Lat Long, heading and pitch) 
	 */
	public GeminiLocationParams getGeminiLocation(int iSonar) {
		if (geminiLocations == null) {
			geminiLocations = new Hashtable<>();
		}
		GeminiLocationParams gl = geminiLocations.get(iSonar);
		if (gl != null) {
			return gl;
		}
		while (gl == null && iSonar >= 0) {
			iSonar--;
			gl = geminiLocations.get(iSonar);
		}
		if (gl == null) {
			return new GeminiLocationParams();
		}
		else return gl.clone();
	}
	
	/**
	 * Set the location for a particular sonar. 
	 * @param iSonar sonar index
	 * @param geminiLocation location data
	 */
	public void setGeminiLocation(int iSonar, GeminiLocationParams geminiLocation) {
		if (geminiLocations == null) {
			geminiLocations = new Hashtable<>();
		}
		geminiLocations.put(iSonar, geminiLocation);
	}

	@Override
	public GeminiParameters clone() {
		try {
			return (GeminiParameters) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public AnnotationChoices getAnnotationChoices() {
		if (annotationChoices == null) {
			annotationChoices = new AnnotationChoices();
		}
		return annotationChoices;
	}

	public void setAnnotationChoices(AnnotationChoices annotationChoices) {
		this.annotationChoices = annotationChoices;
	}

//	/**
//	 * Set params for offline files. 
//	 * @param offlineFileParams
//	 */
//	public void setOfflineFileParams(OfflineFileParameters offlineFileParams) {
//		this.offlineFileParams = offlineFileParams;	
//	}
//
//	/**
//	 * @return the offlineFileParams
//	 */
//	public OfflineFileParameters getOfflineFileParams() {
//		if (offlineFileParams == null) {
//			offlineFileParams = new OfflineFileParameters();
//		}
//		return offlineFileParams;
//	}
	

}
