package tritechplugin.dataselect;

import PamguardMVC.dataSelector.DataSelectParams;
import PamguardMVC.dataSelector.DataSelector;
import PamguardMVC.dataSelector.DataSelectorCreator;
import tritechplugin.GeminiControl;
import tritechplugin.tritech.ECDDataBlock;

public class ECDDataSelectCreator extends DataSelectorCreator {

	private GeminiControl geminiControl;
	private ECDDataBlock ecdDataBlock;

	public ECDDataSelectCreator(GeminiControl geminiControl, ECDDataBlock ecdDataBlock) {
		super(ecdDataBlock);
		this.geminiControl = geminiControl;
		this.ecdDataBlock = ecdDataBlock;
	}

	@Override
	public DataSelector createDataSelector(String selectorName, boolean allowScores, String selectorType) {
		return new ECDDataSelector(geminiControl, ecdDataBlock, selectorName);
	}

	@Override
	public DataSelectParams createNewParams(String name) {
		return new ECDDataSelectParams();
	}

}
