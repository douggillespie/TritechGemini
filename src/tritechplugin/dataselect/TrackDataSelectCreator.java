package tritechplugin.dataselect;

import PamguardMVC.dataSelector.DataSelectParams;
import PamguardMVC.dataSelector.DataSelector;
import PamguardMVC.dataSelector.DataSelectorCreator;
import tritechplugin.target.TrackDataBlock;

public class TrackDataSelectCreator extends DataSelectorCreator {

	private TrackDataBlock trackDataBlock;

	public TrackDataSelectCreator(TrackDataBlock trackDataBlock) {
		super(trackDataBlock);
		this.trackDataBlock = trackDataBlock;
	}

	@Override
	public DataSelector createDataSelector(String selectorName, boolean allowScores, String selectorType) {
		return new TrackDataSelector(trackDataBlock, allowScores);
	}

	@Override
	public DataSelectParams createNewParams(String name) {
		return new TrackDataSelectParams();
	}

}
