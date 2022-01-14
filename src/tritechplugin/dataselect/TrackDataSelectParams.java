package tritechplugin.dataselect;

import java.io.Serializable;

import PamguardMVC.dataSelector.DataSelectParams;

public class TrackDataSelectParams extends DataSelectParams implements Serializable {

	public static final long serialVersionUID = 1L;

	int minTargetType = 2;
	
	int minTargetCount = 3;

}
