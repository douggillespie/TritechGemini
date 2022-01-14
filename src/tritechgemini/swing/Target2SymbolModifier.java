package tritechgemini.swing;

import java.awt.Color;

import PamView.GeneralProjector;
import PamView.PamSymbolType;
import PamView.symbol.PamSymbolChooser;
import PamView.symbol.SymbolData;
import PamView.symbol.modifier.SymbolModType;
import PamView.symbol.modifier.SymbolModifier;
import PamguardMVC.PamDataUnit;
import tritechgemini.target.Target2DataUnit;
import tritechgemini.target.TargetType;

public class Target2SymbolModifier extends SymbolModifier {
	
	private SymbolData symbolData = new SymbolData(PamSymbolType.SYMBOL_CIRCLE, 6, 6, true, Color.RED, Color.RED);

	public Target2SymbolModifier(PamSymbolChooser symbolChooser) {
		super("Gemini Target Class", symbolChooser, SymbolModType.FILLCOLOUR | SymbolModType.LINECOLOUR);
	}

	@Override
	public SymbolData getSymbolData(GeneralProjector projector, PamDataUnit dataUnit) {
		if (dataUnit instanceof Target2DataUnit) {
			Target2DataUnit target2DataUnit = (Target2DataUnit) dataUnit;
			Color col = TargetType.getColour(target2DataUnit.getTargetType());
			symbolData.setLineColor(col);
			symbolData.setFillColor(col);
			return symbolData;
		}
		return null;
	}

}
