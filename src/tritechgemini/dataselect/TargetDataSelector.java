package tritechgemini.dataselect;

import PamView.dialog.PamDialogPanel;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.dataSelector.DataSelectParams;
import PamguardMVC.dataSelector.DataSelector;
import pamViewFX.fxSettingsPanes.DynamicSettingsPane;
import tritechgemini.target.Target2DataBlock;
import tritechgemini.target.Target2DataUnit;
import tritechgemini.target.TargetType;

public class TargetDataSelector extends DataSelector {
	
	private TargetDataSelectParams targetDataSelectParams = new TargetDataSelectParams();

	public TargetDataSelector(Target2DataBlock pamDataBlock, boolean allowScores) {
		super(pamDataBlock, "Gemini Targets", allowScores);
	}

	@Override
	public void setParams(DataSelectParams dataSelectParams) {
		if (dataSelectParams instanceof TargetDataSelectParams) {
			targetDataSelectParams = (TargetDataSelectParams) dataSelectParams;
		}
		
		
	}

	@Override
	public DataSelectParams getParams() {
		return targetDataSelectParams;
	}

	@Override
	public PamDialogPanel getDialogPanel() {
		return new TargetSelectDialogPanel(this);
	}

	@Override
	public DynamicSettingsPane<Boolean> getDialogPaneFX() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double scoreData(PamDataUnit pamDataUnit) {
		Target2DataUnit t2du = (Target2DataUnit) pamDataUnit;
		int targetScore = TargetType.getScore(t2du.getTargetType());
		return targetScore >= targetDataSelectParams.minTargetType ? 1.0 : 0.0;
	}

	/**
	 * @return the targetDataSelectParams
	 */
	public TargetDataSelectParams getTargetDataSelectParams() {
		return targetDataSelectParams;
	}
	

}
