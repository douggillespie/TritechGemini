package tritechgemini.swing;

import java.awt.Color;

import PamView.ColourArray;
import PamView.GeneralProjector;
import PamView.PamSymbolType;
import PamView.symbol.PamSymbolManager;
import PamView.symbol.PamSymbolOptions;
import PamView.symbol.SymbolData;
import PamView.symbol.PamSymbolChooser;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import tritechgemini.target.TrackDataUnit;

public class TrackSymbolChooser extends PamSymbolChooser {

	private ColourArray colourArray = ColourArray.createRainbowArray(20);
	
	private SymbolData symbolData;
	
	public TrackSymbolChooser(PamSymbolManager pamSymbolManager, PamDataBlock pamDataBlock, String displayName,
			GeneralProjector projector) {
		super(pamSymbolManager, pamDataBlock, displayName, projector);
		symbolData = new SymbolData(PamSymbolType.SYMBOL_DIAMOND, 16, 16, false, Color.red, Color.red);
	}

	@Override
	public SymbolData getSymbolChoice(GeneralProjector projector, PamDataUnit dataUnit) {
		Color col = getColour((TrackDataUnit) dataUnit);
		symbolData.setLineColor(col);
		symbolData.setFillColor(col);
		return symbolData;
	}
	
	protected Color getColour(TrackDataUnit trackDataUnit) {
		Float score = trackDataUnit.getClassResult();
		if (score == null || score <= 0) {
			return colourArray.getColour(0);
		}
		else {
			// score should be on scale 0 to 1
			// go for a log scale of .01 to 1, i.e. -2 to 0
			double lMin = -2;
			double lMax = 0;
			double val = Math.log10(score);
			int ncol = colourArray.getNumbColours();
			val = (val-lMin)/(lMax-lMin);
			int iScore = (int) (val * ncol-1);
			iScore = Math.max(0, Math.min(iScore, ncol-1));
			return colourArray.getColour(iScore);
		}
	}

	@Override
	public void setSymbolOptions(PamSymbolOptions symbolOptions) {
		// TODO Auto-generated method stub

	}

	@Override
	public PamSymbolOptions getSymbolOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
