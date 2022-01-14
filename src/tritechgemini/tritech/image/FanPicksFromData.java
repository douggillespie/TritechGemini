package tritechgemini.tritech.image;

import tritechgemini.tritech.GeminiRecord;

/**
 * Fan maker where a LUT is used to work through each point in the fan image and take 
 * data from the GeminiRecord to populate that point. 
 * @author dg50
 *
 */
public class FanPicksFromData extends ImageFanMaker {

	int nNearPoints = 2;
	
	/**
	 * LUT which tells us where to take data from in the raw data array. 
	 */
	private int[][] dataPickLUT;
	/**
	 * LUT which tells us where to put data in the image. Need to test speed to 
	 * see if we're better off making this one or 2D. 
	 */
	private int[] dataPutLUT;
	
	public FanPicksFromData() {
	}

	
	public FanPicksFromData(int nNearPoints) {
		nNearPoints = 2;
	}
	
	@Override
	public FanImageData createFanData(GeminiRecord geminiRecord, int nPixX, int nPixY) {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearTables() {
		dataPickLUT = null;
		dataPutLUT = null;
	}

}
