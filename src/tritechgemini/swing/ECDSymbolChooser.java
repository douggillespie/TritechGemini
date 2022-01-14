package tritechgemini.swing;

import PamView.ColourArray;
import PamView.GeneralProjector;
import PamView.symbol.PamSymbolChooser;
import PamView.symbol.PamSymbolManager;
import PamView.symbol.PamSymbolOptions;
import PamView.symbol.SwingSymbolOptionsPanel;
import PamView.symbol.SymbolData;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import tritechgemini.tritech.ECDDataBlock;
import tritechgemini.tritech.image.ECDImageMaker;

public class ECDSymbolChooser extends PamSymbolChooser{
	
	public ECDSymbolOptions ecdSymbolOptions = new ECDSymbolOptions();
	
	private ECDImageMaker ecdImageMaker;

	private ECDDataBlock ecdDataBlock;

	public ECDSymbolChooser(ECDSymbolManager pamSymbolManager, ECDDataBlock pamDataBlock, String displayName,
			GeneralProjector projector) {
		super(pamSymbolManager, pamDataBlock, displayName, projector);
		this.ecdDataBlock = pamDataBlock;
		ecdImageMaker = new ECDImageMaker();
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
		ecdImageMaker.setColours(ecdSymbolOptions.getColourArrayType());
		ecdImageMaker.setOpacity(ecdSymbolOptions.isScaleOpacity());
		ecdDataBlock.clearAllImages(ecdImageMaker);
		
	}

	/**
	 * @return the ecdImageMaker
	 */
	public ECDImageMaker getEcdImageMaker() {
		return ecdImageMaker;
	}

}
