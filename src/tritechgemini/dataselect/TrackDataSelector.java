package tritechgemini.dataselect;

import PamView.dialog.PamDialogPanel;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.dataSelector.DataSelectParams;
import PamguardMVC.dataSelector.DataSelector;
import pamViewFX.fxSettingsPanes.DynamicSettingsPane;
import tritechgemini.target.TrackDataUnit;

public class TrackDataSelector extends DataSelector {
	
	private TrackDataSelectParams trackParams = new TrackDataSelectParams();

	public TrackDataSelector(PamDataBlock pamDataBlock,  boolean allowScores) {
		super(pamDataBlock, "Gemini Tracks", allowScores);
	}

	@Override
	public void setParams(DataSelectParams dataSelectParams) {
		if (dataSelectParams instanceof TrackDataSelectParams) {
			trackParams = (TrackDataSelectParams) dataSelectParams;
		}

	}

	@Override
	public DataSelectParams getParams() {
		return trackParams;
	}

	@Override
	public PamDialogPanel getDialogPanel() {
		return new TrackSelectDialogPanel(this);
	}

	@Override
	public DynamicSettingsPane<Boolean> getDialogPaneFX() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double scoreData(PamDataUnit pamDataUnit) {
		TrackDataUnit tdu = (TrackDataUnit) pamDataUnit;
		return tdu.getHighScore() >= trackParams.minTargetType & tdu.getSubDetectionsCount() >= trackParams.minTargetCount ? 1.0 : 0;
	}

	/**
	 * @return the trackParams
	 */
	public TrackDataSelectParams getTrackParams() {
		return trackParams;
	}

	@Override
	public boolean isAllowScores() {
		return false;
	}

}
