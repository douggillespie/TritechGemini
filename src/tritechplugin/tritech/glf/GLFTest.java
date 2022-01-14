package tritechplugin.tritech.glf;

import java.io.File;

public class GLFTest {

//	private static String tstFile = "C:\\ProjectData\\RobRiver\\glfexamples\\log_2021-04-10-085615.glf";
	private static String tstFile = "C:\\ProjectData\\RobRiver\\glfexamples\\log_2021-04-10-085615.glf";
//	private static String tstFile = "C:\\ProjectData\\RobRiver\\glfexamples\\data_2021-04-10-085615.dat";
	
	public static void main(String[] args) {

		new GLFFile(new File(tstFile));
		
	}

}
