package tritechplugin;

import PamModel.PamDependency;
import PamModel.PamPluginInterface;

public class GeminiPlugin implements PamPluginInterface {

	private String jarFile;

	@Override
	public String getAboutText() {
		return "Gemini Status and Detection data gathering";
	}

	@Override
	public String getContactEmail() {
		return "dg50@st-andrews.ac.uk";
	}

	@Override
	public String getDefaultName() {
		return "Tritech Geminin Interface";
	}

	@Override
	public String getDeveloperName() {
		return "Douglas Gillespie";
	}

	@Override
	public String getHelpSetName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJarFile() {
		return jarFile;
	}

	@Override
	public String getPamVerDevelopedOn() {
		return "2.01.05fe Beta";
	}

	@Override
	public String getPamVerTestedOn() {
		return "2.02.02 Beta";
	}

	@Override
	public String getVersion() {
		return "1.09";
	}

	@Override
	public void setJarFile(String jarFile) {
		this.jarFile = jarFile;
	}

	@Override
	public int allowedModes() {
		return PamPluginInterface.ALLMODES;
	}

	@Override
	public String getClassName() {
		return GeminiControl.class.getName();
	}

	@Override
	public PamDependency getDependency() {
		return null;
	}

	@Override
	public String getDescription() {
		return "Tritech Gemini Sonar interface";
	}

	@Override
	public int getMaxNumber() {
		return 0;
	}

	@Override
	public String getMenuGroup() {
		return "Utilities";
	}

	@Override
	public int getMinNumber() {
		return 0;
	}

	@Override
	public int getNInstances() {
		return 0;
	}

	@Override
	public String getToolTip() {
		return "Tritech Gemini sonar interface";
	}

	@Override
	public boolean isItHidden() {
		return false;
	}

}
