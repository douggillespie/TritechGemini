package tritechplugin.swing;

import PamView.GeneralProjector;
import PamView.symbol.StandardSymbolChooser;
import PamView.symbol.StandardSymbolManager;
import PamView.symbol.SymbolData;
import PamguardMVC.PamDataBlock;

public class GeminiSymbolManager extends StandardSymbolManager {

	public GeminiSymbolManager(PamDataBlock pamDataBlock, SymbolData defaultSymbol) {
		super(pamDataBlock, defaultSymbol);
//		addSymbolOption(HAS_SYMBOL);
		addSymbolOption(HAS_CHANNEL_OPTIONS);
		addSymbolOption(HAS_SPECIAL_COLOUR);
		removeSymbolOption(HAS_SYMBOL);
		this.setSpecialColourName("Colour by target type");
	}

	@Override
	protected StandardSymbolChooser createSymbolChooser(String displayName, GeneralProjector projector) {
		return new GeminiSymbolChooser(this, getPamDataBlock(), displayName, getDefaultSymbol(), projector);
	}

}
