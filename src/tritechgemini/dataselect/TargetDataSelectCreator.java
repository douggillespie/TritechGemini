package tritechgemini.dataselect;

import PamguardMVC.dataSelector.DataSelectParams;
import PamguardMVC.dataSelector.DataSelector;
import PamguardMVC.dataSelector.DataSelectorCreator;
import tritechgemini.target.Target2DataBlock;

public class TargetDataSelectCreator extends DataSelectorCreator {

	private Target2DataBlock target2DataBlock;

	public TargetDataSelectCreator(Target2DataBlock target2DataBlock) {
		super(target2DataBlock);
		this.target2DataBlock = target2DataBlock;
	}

	@Override
	public DataSelector createDataSelector(String selectorName, boolean allowScores, String selectorType) {
		TargetDataSelector tdsl = new TargetDataSelector(target2DataBlock, allowScores);
		return tdsl;
	}

	@Override
	public DataSelectParams createNewParams(String name) {
		return new TargetDataSelectParams();
	}

}
