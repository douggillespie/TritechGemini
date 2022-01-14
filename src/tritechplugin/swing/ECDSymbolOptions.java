package tritechplugin.swing;

import PamView.ColourArray.ColourArrayType;
import PamView.symbol.PamSymbolOptions;

public class ECDSymbolOptions extends PamSymbolOptions {

	public static final long serialVersionUID = 1L;
	
	private ColourArrayType colourArrayType = ColourArrayType.FIRE;
	
	/**
	 * Scale the opacity by the image value. 
	 */
	private boolean scaleOpacity = true;
	
	/**
	 * If multi sonar are present, then make a single image (using
	 * the alignment of the first image) and use the values for each
	 * image for a different primary colour component.  
	 */
	private boolean combineSingleImage = false;

	public ECDSymbolOptions() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the colourArrayType
	 */
	public ColourArrayType getColourArrayType() {
		return colourArrayType;
	}

	/**
	 * @param colourArrayType the colourArrayType to set
	 */
	public void setColourArrayType(ColourArrayType colourArrayType) {
		this.colourArrayType = colourArrayType;
	}

	/**
	 * @return the scaleOpacity
	 */
	public boolean isScaleOpacity() {
		return scaleOpacity;
	}

	/**
	 * @param scaleOpacity the scaleOpacity to set
	 */
	public void setScaleOpacity(boolean scaleOpacity) {
		this.scaleOpacity = scaleOpacity;
	}

	/**
	 * @return the combineSingleImage
	 */
	public boolean isCombineSingleImage() {
		return combineSingleImage;
	}

	/**
	 * @param combineSingleImage the combineSingleImage to set
	 */
	public void setCombineSingleImage(boolean combineSingleImage) {
		this.combineSingleImage = combineSingleImage;
	}

}
