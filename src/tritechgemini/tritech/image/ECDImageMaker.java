package tritechgemini.tritech.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Arrays;

import PamUtils.PamUtils;
import PamView.ColourArray;
import PamView.ColourArray.ColourArrayType;
import tritechgemini.tritech.ecd.ECDRecordSet;
import tritechgemini.tritech.ecd.GeminiTargetImage;
import PamView.PamColors;

/**
 * Make an image. Now that this has colour options, I think it's 
 * going to have to live inside the ECDSymbolChooser so that different
 * displays can have different colours if we're to remain true to 
 * PAMGuards colouring options. 
 * @author Doug Gillespie
 *
 */
public class ECDImageMaker implements ClipboardOwner {

	private Point[] transformLUT;

	private double[] transformScale;

	private boolean[] transparentMask;

	private double imageScale = 1./1.7;
	
	public static final int NCOLOURS = 256;

	//	private ColourArray colourArray = ColourArray.createMergedArray(256, Color.blue, Color.white);
	private ColourArray colourArray = ColourArray.createHotArray(NCOLOURS);

	private boolean scaleOpacity = true;

	public ECDImageMaker() {
		super();
	}

	/**
	 * GEt a scaled image using a single colour array  
	 * @param ecdRecord
	 * @return
	 */
	public ECDImage extractImage(ECDRecordSet ecdRecord) {
		
		GeminiTargetImage targetImage = ecdRecord.getTargetImage();
		if (targetImage.getcData() == null) {
			ecdRecord.readFully();
		}

		int[] imageSize = getImageDimension(targetImage);
		
		double[] imageData = getImagedata(targetImage, imageSize[0], imageSize[1]);
		if (imageData == null) {
			return null;
		}
		BufferedImage bufferedImage = createImage(ecdRecord, imageData, imageSize[0], imageSize[1]);

		
		ECDImage ecdImage = new ECDImage(targetImage.getMaxRange(), targetImage.getXRange(), ecdRecord, bufferedImage);
		return ecdImage;
	}
	
	public ECDImage extractMultiImage(ECDRecordSet ...ecdRecordSets) {
		int nIm = Math.min(ecdRecordSets.length, 3);
		if (ecdRecordSets.length == 0) {
			return null;
		}
		double imageData[][] = new double[ecdRecordSets.length][];
		int[] imageSize = null;
		GeminiTargetImage targetImage = null;
		for (int i = 0; i < nIm; i++) {
			targetImage = ecdRecordSets[i].getTargetImage();
			if (targetImage.getcData() == null) {
				ecdRecordSets[i].readFully();
			}
			imageSize = getImageDimension(targetImage);

			imageData[i] = getImagedata(targetImage, imageSize[0], imageSize[1]);
			//		if (imageData == null) {
			//			return null;
			//		}
			if (imageData[i] ==null) {
				return null;
			}
		}
		BufferedImage bufferedImage = createImage(ecdRecordSets[0], imageData, imageSize[0], imageSize[1]);
		if (bufferedImage == null) {
			return null;
		}

		ECDImage ecdImage = new ECDImage(targetImage.getMaxRange(), targetImage.getXRange(), ecdRecordSets[0], bufferedImage);
		return ecdImage;
		
	}
	
	/**
	 * Take sqrt of a number on scale 0 - 255. i.e. so that the value 0->0 and 255->255 still. 
	 * @param val input value
	 * @return sqrt scaled to 255.
	 */
	private int sqrt255(int val) {
		return (int) Math.sqrt(val*255);
	}
	/**
	 * Get the sizeof the image, assuming equal pixs per m in x and y. 
	 * @param targetImage
	 * @return 2D array of {nBearingBins, nRangebins}
	 */
	private int[] getImageDimension(GeminiTargetImage targetImage) {
		double[] bearings = targetImage.getBearingTable();
		/*
		 *  we want to make the image equal in x and y. For a start, try as many x bins as there are
		 *  bearings, and then work out what the number of range bins should be. 
		 *  Note that angles are rel the y axis, so sin for x! 
		 */
		int nBearing = bearings.length;
		double yRange = targetImage.getMaxRange();
		double[] xRange = targetImage.getXRange();
		int[] imageSize = new int[2];
		imageSize[0] = (int) (nBearing * imageScale); // number of bearing pixels
		imageSize[1] = (int) (imageSize[0]*yRange/(xRange[1]-xRange[0])); // number of range pixels
		return imageSize;
	}
	
	/**
	 * Get the image data which is the raw data squished into an array the size of the image we want
	 * to create, 
	 * @param targetImage
	 * @param nBearingPix
	 * @param nRangePix
	 * @return
	 */
	public double[] getImagedata(GeminiTargetImage targetImage, int nBearingPix, int nRangePix) {

		double[] bearings = targetImage.getBearingTable();
		/*
		 *  we want to make the image equal in x and y. For a start, try as many x bins as there are
		 *  bearings, and then work out what the number of range bins should be. 
		 *  Note that angles are rel the y axis, so sin for x! 
		 */
		int nBearing = bearings.length;
		int nRange = targetImage.getM_nRngs();

		checkLUT(bearings, nRange, nBearingPix, nRangePix);

		byte[] targetData = targetImage.uncompressData();
		if (targetData == null) {
			return null;
		}

		double[] imageData = new double[nBearingPix*nRangePix]; // array for the image data

		double a = 0;
		try {
			for (int r = 0, t = 0; r < nRange; r++) {
				for (int b = 0; b < nBearing; b++, t++) {
					Point p = transformLUT[t];
					int imPoint = p.x*nRangePix+p.y;
					imageData[imPoint] += Byte.toUnsignedInt(targetData[t])/transformScale[imPoint]; 
					//				imageData[t] +=targetData[t];//; 
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return imageData;
	}

	public BufferedImage createImage(ECDRecordSet ecdRecord, double[] imageData, int nBearingPix, int nRangePix) {
		//		if (true) {
		//			return null;
		//		}
		// now we have an image, all data should be on the scale 0 to 255.  change to 4_BYTE_ABGR for transparancy
		BufferedImage bufferedImage = new BufferedImage(nBearingPix, nRangePix, BufferedImage.TYPE_4BYTE_ABGR);
		//		bufferedImage.get
		WritableRaster raster = bufferedImage.getRaster();
		int clear[] = {0,0,0,0};
		//		if (imageType == BufferedImage.TYPE_4BYTE_ABGR) {
//		Color baseColor = PamColors.getInstance().getChannelColor(ecdRecord.getSonar()-1);
//		int[] baseCol = {baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 0}; 
		//		}
		for (int r = 0, t = 0; r < nRangePix; r++) {
			for (int b = 0; b < nBearingPix; b++, t++) {
				t = b*nRangePix+r;
				if (transparentMask[t]) {
					raster.setPixel(b, r, clear);
				}
				else {
					int iCol = (int) Math.min(Math.max(imageData[t], 0), 255);
					// transform the colour to make values larger. 
					iCol = (int) (255*Math.sqrt((double) iCol / 255.));
					int[] colBits = colourArray.getIntColourArray(iCol);
					try {
						colBits = conv4IntColourArray(colBits);
					}
					catch(Exception e) {					
					}
					if (scaleOpacity && colBits.length >= 4) {
						colBits[3] = (iCol); //set the opacity the same as the colour density. 
					}
					raster.setPixel(b, r, colBits);
				}
			}
		}
		// ranges were in pixels, now convert to metres for the output image. 
		//		addSonarId(ecdImage);
		//		addRange(ecdImage);
		//		copyToClipboard(ecdImage);

		return bufferedImage;
	}

	/**
	 * Convert a 3 int array to a four int array. Do this here not in the ColourArray class
	 * so that it can be used within the MATLab library without the newer get4IntColourArray
	 * function which isn't in the old Java 8 PAMguard libaray.  
	 * @return
	 */
	private int[] conv4IntColourArray(int[] int3Arr) {
		int[] int4Arr = Arrays.copyOf(int3Arr, 4);
		int4Arr[3] = 255;
		return int4Arr;
	}
	/**
	 *  create an image using multiple layes of RGB for different image data
	 * @param ecdRecord
	 * @param imageData
	 * @param nBearingPix
	 * @param nRangePix
	 * @return
	 */
	private BufferedImage createImage(ECDRecordSet ecdRecord, double[][] imageData, int nBearingPix, int nRangePix) {
		BufferedImage bufferedImage = new BufferedImage(nBearingPix, nRangePix, BufferedImage.TYPE_4BYTE_ABGR);
		//		bufferedImage.get
		if (imageData == null) {
			return null;
		}
		WritableRaster raster = bufferedImage.getRaster();
		int clear[] = {0,0,0, 0};
		//		if (imageType == BufferedImage.TYPE_4BYTE_ABGR) {
		Color baseColor = PamColors.getInstance().getChannelColor(ecdRecord.getSonar()-1);
		int[] baseCol = {baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 0}; 
		int[] colOrd = {1, 2, 0};
		//		}
		int[] colBits = new int[4];
		int nImage = imageData.length;
		for (int r = 0, t = 0; r < nRangePix; r++) {
			for (int b = 0; b < nBearingPix; b++, t++) {
				t = b*nRangePix+r;
				if (transparentMask[t]) {
					raster.setPixel(b, r, clear);
				}
				else {
					int maxCol = 0;
					for (int i = 0; i < nImage; i++) {
						int iCol = (int) Math.min(Math.max(imageData[i][t], 0), 255);
						maxCol = Math.max(maxCol, iCol);
						// transform the colour to make values larger. 
						iCol = (int) (255*Math.sqrt((double) iCol / 255.));
						colBits[colOrd[i]] = iCol; //set the opacity the same as the colour density. 
					}
					colBits[3] = maxCol;
					raster.setPixel(b, r, colBits);
				}
			}
		}
		return bufferedImage;
	}

	public boolean addSonarId(ECDImage ecdImage) {
		BufferedImage bufferedImage = ecdImage.getBufferedImage();
		int w = bufferedImage.getWidth();
		int h = bufferedImage.getHeight();
		Graphics g = bufferedImage.getGraphics();
		g.setColor(Color.WHITE);
		String str = String.format("%d", ecdImage.getEcdRecord().getSonar());
		int cw = g.getFontMetrics().charWidth('2');
		g.drawString(str, (w-cw)/2, h-1);
		return true;
	}

	public boolean addRange(ECDImage ecdImage) {
		BufferedImage bufferedImage = ecdImage.getBufferedImage();
		int w = bufferedImage.getWidth();
		int h = bufferedImage.getHeight();
		Graphics g = bufferedImage.getGraphics();
		g.setColor(Color.WHITE);
		String str = String.format("%3.1fm", ecdImage.getyRange());
		//		int cw = g.getFontMetrics().charWidth('2');
		LineMetrics lm = g.getFontMetrics().getLineMetrics(str, g);
		g.drawString(str, w/2, (int) (lm.getAscent()+1));
		return true;
	}


	/**
	 * Copy an image to the clipboard. 
	 * @param ecdImage
	 * @return true if success
	 */
	public boolean copyToClipboard(ECDImage ecdImage) {
		try {
			TransferableImage trans = new TransferableImage(ecdImage.getBufferedImage());
			Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
			c.setContents( trans, this );
			System.out.println("Image added to clipboard");
		}
		catch (Exception e) {
			System.out.println("Error copying image to clipboard: " + e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Make lookup tables to convert range and bearing to image pixels. 
	 * Note that 1D arrays are a lot faster than 2D ones, so we're going to try to
	 * do the whole thing with 1D arrays. 
	 * <p>
	 * The Gemini data arrive in a 1D array, with the inner loop over bearing and the 
	 * outer over range, i.e. all bearings for the first range come first then all
	 * bearings for the second range, etc.  
	 * @param bearings array of bearings in the raw data
	 * @param nRange number of ranges in the raw data
	 * @param nBearingPix number of bearing pixels in the output image
	 * @param nRangePix  number of range pixels in the output image. 
	 */
	private void checkLUT(double[] bearings, int nRange, int nBearingPix, int nRangePix) {
		if (needLUT(bearings.length, nRange, nBearingPix, nRangePix) == false) {
			return;
		}
		int nBearing = bearings.length;
		transformLUT = new Point[nBearing*nRange]; // array for all incoming Gemini data to tranlate Gemini points to image points
		transformScale = new double[nBearingPix*nRangePix]; // array for the image scaling
		transparentMask = new boolean[nBearingPix*nRangePix];
		double[] bearingRange = PamUtils.getMinAndMax(bearings);
		Arrays.fill(transparentMask, true);
		// they should be between -60 and +60 degrees. 
		double xMin = Math.sin(bearingRange[0])*nRange;
		double xMax = Math.sin(bearingRange[1])*nRange;
		for (int r = 0, t = 0; r < nRange; r++) {
			for (int b = 0; b < nBearing; b++, t++) {
				// position relative to the original image
				double xf = r*Math.sin(bearings[b]);
				double yf = r*Math.cos(bearings[b]);
				// position in the scaled image
				xf = (xf-xMin) * nBearingPix / (xMax-xMin);
				yf = yf *nRangePix/nRange;
				int x = (int) Math.round(xf);
				int y = (int) Math.round(yf);
				x = Math.max(0, Math.min(x, nBearingPix-1));
				x = nBearingPix-x-1;
				y = Math.max(0, Math.min(y, nRangePix-1));
				y = nRangePix-y-1;
				transformLUT[t] = new Point(x,y);
				int tPt = x*nRangePix+y;
				transformScale[tPt] += 1;
				transparentMask[tPt] = false;
			}
		}
	}


	private boolean needLUT(int nBearings, int nRange, int nBearingPix, int nRangePix) {
		if (transformLUT == null) {
			return true;
		}
		if (transformLUT.length != nBearings*nRange) {
			return true;
		}
		//		if (imageData == null || imageData.length != nBearingPix*nRangePix) {
		//			return true;
		//		}
		return false;
	}

	private class TransferableImage implements Transferable {

		Image i;

		public TransferableImage( Image i ) {
			this.i = i;
		}

		public Object getTransferData( DataFlavor flavor )
				throws UnsupportedFlavorException, IOException {
			if ( flavor.equals( DataFlavor.imageFlavor ) && i != null ) {
				return i;
			}
			else {
				throw new UnsupportedFlavorException( flavor );
			}
		}

		public DataFlavor[] getTransferDataFlavors() {
			DataFlavor[] flavors = new DataFlavor[ 1 ];
			flavors[ 0 ] = DataFlavor.imageFlavor;
			return flavors;
		}

		public boolean isDataFlavorSupported( DataFlavor flavor ) {
			DataFlavor[] flavors = getTransferDataFlavors();
			for ( int i = 0; i < flavors.length; i++ ) {
				if ( flavor.equals( flavors[ i ] ) ) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {

	}

	/**
	 * @return the imageScale
	 */
	public double getImageScale() {
		return imageScale;
	}

	/**
	 * @param imageScale the imageScale to set
	 */
	public void setImageScale(double imageScale) {
		this.imageScale = imageScale;
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

	public void setOpacity(boolean scaleOpacity) {
		this.scaleOpacity = scaleOpacity;
	}
}
