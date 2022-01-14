package tritechgemini.swing;

import tritechplugin.GeminiControl;
import userDisplay.UserDisplayComponent;
import userDisplay.UserDisplayControl;
import userDisplay.UserDisplayProvider;

public class TrackDisplayProvider implements UserDisplayProvider {

	private GeminiControl geminiControl;

	public TrackDisplayProvider(GeminiControl geminiControl) {
		this.geminiControl = geminiControl;
	}

	@Override
	public String getName() {
		return geminiControl.getUnitName() + " tracks";
	}

	@Override
	public UserDisplayComponent getComponent(UserDisplayControl userDisplayControl, String uniqueDisplayName) {
		return new GeminiTrackDisplay(geminiControl, userDisplayControl, uniqueDisplayName);
	}

	@Override
	public Class getComponentClass() {
		return GeminiTrackDisplay.class;
	}

	@Override
	public int getMaxDisplays() {
		return 0;
	}

	@Override
	public boolean canCreate() {
		return true;
	}

	@Override
	public void removeDisplay(UserDisplayComponent component) {
		// TODO Auto-generated method stub
		
	}

}
