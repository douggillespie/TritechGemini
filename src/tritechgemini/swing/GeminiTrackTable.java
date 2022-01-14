package tritechgemini.swing;

import java.awt.Color;

import javax.swing.ImageIcon;

import PamUtils.PamCalendar;
import PamView.PamSymbol;
import PamView.PamSymbolType;
import PamView.component.DataBlockTableView;
import tritechgemini.target.TrackDataBlock;
import tritechgemini.target.TrackDataUnit;
import tritechplugin.GeminiControl;

public class GeminiTrackTable extends DataBlockTableView<TrackDataUnit>{
	
	private String[] colNames = {"", "UID", "Start", "End", "Target ID", "n Points", "Classification"};
	
	private PamSymbol colSymbol;
	
	private TrackSymbolChooser symbolChoser;
	
	public GeminiTrackTable(GeminiControl geminiControl, TrackDataBlock trackDataBlock, String displayName) {
		super(trackDataBlock, displayName);
		symbolChoser = new TrackSymbolChooser(null, trackDataBlock, null, null);
		colSymbol = new PamSymbol(PamSymbolType.SYMBOL_CIRCLE, 16, 16, true, Color.RED, Color.BLACK);
	}

	@Override
	public String[] getColumnNames() {
		return colNames;
	}

	@Override
	public Object getColumnData(TrackDataUnit dataUnit, int column) {
		switch(column) {
		case 0:
//			return new PamSymbol(symbolChoser.getSymbolChoice(null, dataUnit));
			Color col = symbolChoser.getColour(dataUnit);
			colSymbol.setFillColor(col);
			colSymbol.setLineColor(col);
			return colSymbol;
		case 1:
			return dataUnit.getUID();
		case 2:
			return PamCalendar.formatDBDateTime(dataUnit.getTimeMilliseconds());
		case 3:
			return PamCalendar.formatDBDateTime(dataUnit.getEndTime());
		case 4:
			return dataUnit.getTargetID();
		case 5:
			return String.format("%d (%d)", dataUnit.getnPoints(), dataUnit.getSubDetectionsCount());
		case 6:
			return dataUnit.getClassResult();
		}
		return null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0) {
			return ImageIcon.class;
		}
		return super.getColumnClass(columnIndex);
	}


}
