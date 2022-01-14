package tritechplugin.swing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import PamView.ColourArray;
import PamView.ColourArray.ColourArrayType;
import tritechgemini.imagedata.FanImageData;

/**
 * Functions to convert from a FanImageData object, which only has the numbers, 
 * into a coloured / transparent buffered image for display. 
 * @author dg50
 *
 */
class GeminiImageMaker {

	
	public static final int NCOLOURS = 256;

	private ColourArray colourArray = ColourArray.createHotArray(NCOLOURS);
	
	private Color transparent = new Color(0,0,0,0);

	private boolean scaleOpacity;
	
	/**
	 * shifts to make a multi primary image when 2 sonar are present. first sonar will color the 
	 * red, second the blue, third the green part of any given pixel. 
	 */
	private int[] colShifts = {16, 0, 8, 0}; // (red, blue, green, blue again)
	
	public GeminiImageMaker() {
		
	}
	
	/**
	 * @return the colourArray
	 */
	public ColourArray getColourArray() {
		return colourArray;
	}

	/**
	 * Set the colour array - it should always be 256 colours long.  
	 * @param colourArray the colourArray to set
	 */
	public void setColourArray(ColourArray colourArray) {
		this.colourArray = colourArray;
	}

	public void setColours(ColourArrayType colourArrayType) {
		if (colourArrayType == null) {
			return;
		}
		setColourArray(ColourArray.createStandardColourArray(NCOLOURS, colourArrayType));
	}
	
	/**
	 * Convert a fan image data into an actual image for display 
	 * @param fanImageData Fan image data
	 * @return buffered image. 
	 */
	public BufferedImage createImage(FanImageData fanImageData) {
		short[][] data = fanImageData.getImageValues();
		if (data == null || data.length == 0) {
			return null;
		}
		int nX = data.length;
		int nY = data[0].length;
		BufferedImage image = new BufferedImage(nX, nY, BufferedImage.TYPE_4BYTE_ABGR);
		int argb;
		for (int i = 0; i < nX; i++) {
			short[] dataRow = data[i];
			for (int j = 0; j < nY; j++) {
				if (dataRow[j]< 0) {
					argb = 0;
				}
				else {
					argb = colourArray.getColour(dataRow[j]).getRGB();
					if (scaleOpacity) {
						argb &= 0x00FFFFFF;
						argb |= dataRow[j] << 24;
//						argb |= (scaleOpacity ?  dataRow[j] << 24 : 0xFF000000);
					}
				}
				image.setRGB(i, j, argb);
			}
		}
		return image;
	}
	
	/**
	 * Create an image for dual sonars which uses red for one and blue for the second sonar (no green)
	 * and if opacity is set, then it scales opacity to the max of the above. 
	 * @param fanImageData
	 * @return buffered image with a primary for each sonar. 
	 */
	public BufferedImage createDualImage(FanImageData[] fanImageData) {
		if (fanImageData == null || fanImageData.length == 0) {
			return null;
		}
		if (fanImageData.length == 1) {
			return createImage(fanImageData[0]);
		}
		int[] colShifts = {16, 0, 8, 0};
		// now check the fans are the same size. 
		FanImageData fan0 = fanImageData[0];
		short[][] fan0vals = fan0.getImageValues();
		int maxFan = 0;
		for (int i = 1; i < fanImageData.length; i++) {
			FanImageData fani = fanImageData[i];
			if (fani == null) {
				continue;
			}
			maxFan = i;
			short[][] fanivals = fani.getImageValues();
			if (fanivals.length != fan0vals.length || fanivals[0].length != fan0vals[0].length) {
				return createImage(fanImageData[0]);
			}
		}

		int nX = fan0vals.length;
		int nY = fan0vals[0].length;

		BufferedImage image = new BufferedImage(nX, nY, BufferedImage.TYPE_4BYTE_ABGR);
		int argb = 0;
		int maxVal;
		FanImageData fanData;
		for (int i = 0; i < nX; i++) {
			for (int j = 0; j < nY; j++) {
				argb = 0;
				maxVal = -1;
				for (int s = 0; s <= maxFan; s++) {
					fanData = fanImageData[s];
					if (fanData == null) {
						continue;
					}
					int dataP = fanData.getImageValues()[i][j];
					if (dataP < 0) {
						break;
					}
					maxVal = Math.max(maxVal, dataP);
					argb |= dataP << colShifts[s];
				}
				if (maxVal >= 0) {
					if (scaleOpacity) {
						argb &= 0x00FFFFFF;
						argb |= maxVal << 24;
					}
					else {
						argb |= 0xFF000000;
					}
				}
				image.setRGB(i, j, argb);
			}
		}
		return image;
	}

	public void setOpacity(boolean scaleOpacity) {
		this.scaleOpacity = scaleOpacity;
	}
}
