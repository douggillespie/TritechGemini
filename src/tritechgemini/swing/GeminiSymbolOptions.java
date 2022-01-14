package tritechgemini.swing;

import PamView.symbol.StandardSymbolOptions;
import PamView.symbol.SymbolData;

public class GeminiSymbolOptions extends StandardSymbolOptions {

	public static final long serialVersionUID = 1L;
	
	public static final int DRAW_NO_BOX = 0;
	
	public static final int DRAW_2D_BOX = 1;
	
	public static final int DRAW_3D_BOX = 2;
	
	public int boxOption = DRAW_2D_BOX;
	
	public double beamHalfHeightAngle = 10;
	
	public static final String[] boxNames = {"Draw standard symbol", "Draw plane box", "Draw 3D Box"};
	public static final String[] boxToolTips = {"Draw a symbol at the geometric centre of the targte box", 
			"Draw a box using the four corners of the Gemini target detection", "Draw a 3D Box using the vertical swath of the Gemini sonars"};

	public GeminiSymbolOptions(SymbolData defaultSymbol) {
		super(defaultSymbol);
		
	}

}
