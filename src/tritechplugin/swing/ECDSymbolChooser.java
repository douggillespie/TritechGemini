package tritechplugin.swing;

import PamView.GeneralProjector;
import PamView.symbol.PamSymbolChooser;
import PamView.symbol.PamSymbolOptions;
import PamView.symbol.SwingSymbolOptionsPanel;
import PamView.symbol.SymbolData;
import PamguardMVC.PamDataUnit;
import tritechplugin.GeminiControl;
import tritechplugin.tritech.ECDDataBlock;

public class ECDSymbolChooser extends PamSymbolChooser{
	
	public ECDSymbolOptions ecdSymbolOptions = new ECDSymbolOptions();
	
//	private ECDImageMaker ecdImageMaker;
	private GeminiImageMaker imageMaker;

	private ECDDataBlock ecdDataBlock;

	private GeminiControl geminiControl;

	public ECDSymbolChooser(ECDSymbolManager pamSymbolManager, GeminiControl geminiControl, ECDDataBlock pamDataBlock, String displayName,
			GeneralProjector projector) {
		super(pamSymbolManager, pamDataBlock, displayName, projector);
		this.geminiControl = geminiControl;
		this.ecdDataBlock = pamDataBlock;
//		ecdImageMaker = new ECDImageMaker();
		imageMaker = new GeminiImageMaker();
	}

	@Override
	public SymbolData getSymbolChoice(GeneralProjector projector, PamDataUnit dataUnit) {
		/**
		 * No need to return anything here, since it's not plotted normally. 
		 * Whatever choses the colour palet for this is going to have to find 
		 * the data some other way. 
		 */
		return null;
	}

	@Override
	public void setSymbolOptions(PamSymbolOptions symbolOptions) {
		if (symbolOptions instanceof ECDSymbolOptions) {
			ecdSymbolOptions = (ECDSymbolOptions) symbolOptions;
		}
		configureImageMaker();
	}

	@Override
	public ECDSymbolOptions getSymbolOptions() {
		return ecdSymbolOptions;
	}

	@Override
	public SwingSymbolOptionsPanel getSwingOptionsPanel(GeneralProjector projector) {
		return new ECDSymbolOptionsPanel(this);
	}

	public void configureImageMaker() {
		if (ecdSymbolOptions == null || ecdSymbolOptions.getColourArrayType() == null) {
			return;
		}
		imageMaker.setColours(ecdSymbolOptions.getColourArrayType());
		imageMaker.setOpacity(ecdSymbolOptions.isScaleOpacity());
		ecdDataBlock.clearAllImages(imageMaker);
		
	}

	/**
	 * @return the ecdImageMaker
	 */
	public GeminiImageMaker getImageMaker() {
		return imageMaker;
	}

	/**
	 * @return the geminiControl
	 */
	public GeminiControl getGeminiControl() {
		return geminiControl;
	}

}
