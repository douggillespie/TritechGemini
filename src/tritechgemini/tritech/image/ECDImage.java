package tritechgemini.tritech.image;

import java.awt.image.BufferedImage;

import tritechgemini.tritech.ecd.ECDRecordSet;

public class ECDImage {

	private BufferedImage bufferedImage;
	
	private double yRange;
	
	private double[] xRange;

	private ECDRecordSet ecdRecord;
	

	public ECDImage(double yRange, double[] xRange, ECDRecordSet ecdRecord, BufferedImage bufferedImage) {
		super();
		this.yRange = yRange;
		this.xRange = xRange;
		this.ecdRecord = ecdRecord;
		this.bufferedImage = bufferedImage;
	}


	/**
	 * @return the bufferedImage
	 */
	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}


	/**
	 * @return the yRange
	 */
	public double getyRange() {
		return yRange;
	}


	/**
	 * @return the xRange
	 */
	public double[] getxRange() {
		return xRange;
	}


	/**
	 * @return the ecdRecord
	 */
	public ECDRecordSet getEcdRecord() {
		return ecdRecord;
	}
	
	
}
