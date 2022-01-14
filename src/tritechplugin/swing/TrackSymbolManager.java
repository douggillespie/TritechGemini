package tritechplugin.swing;

import PamView.GeneralProjector;
import PamView.symbol.PamSymbolManager;
import PamguardMVC.PamDataBlock;
import tritechplugin.target.TrackDataBlock;

public class TrackSymbolManager extends PamSymbolManager<TrackSymbolChooser>{

	private TrackDataBlock trackDataBlock;

	public TrackSymbolManager(TrackDataBlock trackDataBlock) {
		super(trackDataBlock);
		this.trackDataBlock = trackDataBlock;
	}

	@Override
	protected TrackSymbolChooser createSymbolChooser(String displayName, GeneralProjector projector) {
		return new TrackSymbolChooser(this, trackDataBlock, displayName, projector);
	}


}
