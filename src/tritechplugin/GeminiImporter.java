package tritechplugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import PamController.PamController;
import PamUtils.PamCalendar;
import PamUtils.PamFileChooser;
import PamUtils.PamFileFilter;
import PamUtils.SelectFolder;
import generalDatabase.DBControlUnit;
import tritechplugin.swing.GeminiDialog;
import tritechplugin.target.Target2DataBlock;
import tritechplugin.target.Target2DataUnit;

/**
 * Functions for importing Gemini targe2 data from csv files. 
 * @author dg50
 *
 */
public class GeminiImporter {

	private GeminiControl geminiControl;
	
	private PamFileFilter fileFilter;
		
	private GeminiProcess geminiProcess;

	public GeminiImporter(GeminiControl geminiControl) {
		this.geminiControl = geminiControl;
		fileFilter = new PamFileFilter("CSV Files", "csv");
		fileFilter.setAcceptFolders(true);
		geminiProcess = geminiControl.getGeminiProcess();
	}

	public void addMenuItems(JMenu menu) {
		JMenuItem menuItem = new JMenuItem("Import old CSV data ...");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				importOldData();
			}
		});
		menu.add(menuItem);
	}

	private void importOldData() {
		JFileChooser fc = new PamFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		//		fc.setCurrentDirectory(new File(folderName.getText()));

		int ans = fc.showDialog(PamController.getMainFrame(), "Select storage folder");

		if (ans == JFileChooser.APPROVE_OPTION) {
			processFolder(fc.getSelectedFile());
		}
	}

	private void processFolder(File selectedFile) {
		if (selectedFile == null) return;
		System.out.println("Importing Gemini data from " + selectedFile.getAbsolutePath());
		//		now list all the csv files in that folder ...
		processFile(selectedFile);
	}
	
	private void processFile(File aFile) {
		if (aFile.isDirectory()) {
			File[] files = aFile.listFiles(fileFilter);
			for (int i = 0; i < files.length; i++) {
				processFile(files[i]);
			}
		}
		if (aFile.isFile()) {
			importFile(aFile);
		}
		PamController.getInstance().saveViewerData();

	}

	private long stepMilliseconds = 175;
	/**
	 * Import a file. So far as I can work out, the times in each file are all the same,and only
	 * to 1s resolution. They also seem to match the time the file was written, so the relative times
	 * of each line in the file need to be scaled by the step number * the step gradient, which is 
	 * 0.1746 seconds per step or 175 millis will do for the Davy Pier data. It may be different for a
	 * different dataset !Then take off the maximum step. 
	 * @param aFile
	 */
	private void importFile(File aFile) {
		System.out.println("Import file " + aFile.getAbsolutePath());
		ArrayList<String> allStrings = new ArrayList<>(); 
		try {
			BufferedReader dis = new BufferedReader(new FileReader(aFile));
			String aLine = dis.readLine();
//			System.out.println("Header line " + aLine);
			if (aLine == null) {
				return;
			}
			while ((aLine = dis.readLine()) != null) {
				allStrings.add(aLine);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (allStrings.size() == 0) {
			return;
		}
		String lastLine = allStrings.get(allStrings.size()-1);
		Target2DataUnit dataunit = Target2DataUnit.createFromFileLine(lastLine, 0, 0);
		long tOffset = 0;
		if (dataunit != null) {
			tOffset = dataunit.getStep()*stepMilliseconds;
		}
		for (int i = 0; i < allStrings.size(); i++) {
			dataunit = Target2DataUnit.createFromFileLine(allStrings.get(i), tOffset, stepMilliseconds);
			geminiProcess.newTargetData(dataunit);
		}
		
	}

//	private void importLine(File aFile, String aLine) {
//		Target2DataUnit dataunit = Target2DataUnit.createFromFileLine(aLine);
//		geminiProcess.newTargetData(dataunit);
//	}
}
