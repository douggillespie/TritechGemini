package tritechplugin.swing;

import PamView.ColourArray.ColourArrayType;
import PamView.symbol.PamSymbolOptions;

public class ECDSymbolOptions extends PamSymbolOptions {

	public static final long serialVersionUID = 1L;
	
	private ColourArrayType colourArrayType = ColourArrayType.FIRE;
	
	private boolean scaleOpacity = true;

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

}
