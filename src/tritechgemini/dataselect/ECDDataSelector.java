package tritechgemini.dataselect;

import PamView.dialog.PamDialogPanel;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.dataSelector.DataSelectParams;
import PamguardMVC.dataSelector.DataSelector;
import pamViewFX.fxSettingsPanes.DynamicSettingsPane;
import tritechgemini.GeminiControl;
import tritechgemini.tritech.ECDDataBlock;
import tritechgemini.tritech.ECDDataUnit;

public class ECDDataSelector extends DataSelector {
	
	private ECDDataSelectParams ecdDataParams = new ECDDataSelectParams();
	private GeminiControl geminiControl;

	public ECDDataSelector(GeminiControl geminiControl, ECDDataBlock ecdDataBlock, String selectorName) {
		super(ecdDataBlock, selectorName, false);
		this.geminiControl = geminiControl;
	}

	@Override
	public void setParams(DataSelectParams dataSelectParams) {
		if (dataSelectParams instanceof ECDDataSelectParams) {
			ecdDataParams = (ECDDataSelectParams) dataSelectParams;
		}
	}

	@Override
	public ECDDataSelectParams getParams() {
		return ecdDataParams;
	}

	@Override
	public PamDialogPanel getDialogPanel() {
		return new ECDDataDialogPanel(geminiControl, this);
	}

	@Override
	public DynamicSettingsPane<Boolean> getDialogPaneFX() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double scoreData(PamDataUnit pamDataUnit) {
		ECDDataUnit ecdData = (ECDDataUnit) pamDataUnit;
		int chanMap = ecdData.getChannelBitmap();
		chanMap &= ecdDataParams.usedSonars;
		return chanMap == 0 ? 0 : 1;
	}

}
