package tritechplugin.swing;

import PamView.GeneralProjector;
import PamView.symbol.PamSymbolOptions;
import PamView.symbol.StandardSymbolChooser;
import PamView.symbol.StandardSymbolManager;
import PamView.symbol.StandardSymbolOptions;
import PamView.symbol.SwingSymbolOptionsPanel;
import PamView.symbol.SymbolData;
import PamguardMVC.PamDataBlock;

public class GeminiSymbolChooser extends StandardSymbolChooser {
	
	private GeminiSymbolOptions geminiSymbolOptions;

	public GeminiSymbolChooser(StandardSymbolManager standardSymbolManager, PamDataBlock pamDataBlock,
			String displayName, SymbolData defaultSymbol, GeneralProjector projector) {
		super(standardSymbolManager, pamDataBlock, displayName, defaultSymbol, projector);
		this.addSymbolModifier(new Target2SymbolModifier(this));
	}

//	@Override
//	public SymbolData colourBySpecial(SymbolData symbolData, GeneralProjector projector, PamDataUnit dataUnit) {
//		if (dataUnit instanceof Target2DataUnit) {
//			Target2DataUnit target2DataUnit = (Target2DataUnit) dataUnit;
//			Color col = TargetType.getColour(target2DataUnit.getTargetType());
//			symbolData.setLineColor(col);
//			symbolData.setFillColor(col);
//		}
//		return symbolData;
//	}

	@Override
	public StandardSymbolOptions getSymbolOptions() {
		if (geminiSymbolOptions == null) {
			geminiSymbolOptions = new GeminiSymbolOptions(getDefaultSymbol()); 
		}
		return geminiSymbolOptions;
	}

//	@Override
//	public SymbolData colourByAnnotation(SymbolData symbolData, GeneralProjector projector, PamDataUnit dataUnit, AnnotationSymbolOptions annotationSymbolOptions) {
//		if (dataUnit instanceof Target2DataUnit) {
//			dataUnit = dataUnit.getSuperDetection(TrackDataUnit.class);
//			if (dataUnit == null) {
//				return symbolData;
//			}
//		}
//		return super.colourByAnnotation(symbolData, projector, dataUnit, annotationSymbolOptions);
//	}

	@Override
	public void setSymbolOptions(PamSymbolOptions symbolOptions) {
		if (symbolOptions instanceof GeminiSymbolOptions) {
			geminiSymbolOptions = (GeminiSymbolOptions) symbolOptions;
		}
		else {
			geminiSymbolOptions = new GeminiSymbolOptions(getDefaultSymbol());
		}
		super.setSymbolOptions(geminiSymbolOptions);
	}

	@Override
	public SwingSymbolOptionsPanel getSwingOptionsPanel(GeneralProjector projector) {
		return new GeminiSymbolChooserPanel(getSymbolManager(), this);
	}

}
