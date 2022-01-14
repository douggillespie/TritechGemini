package tritechplugin.target;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.dataSelector.DataSelectorCreator;
import PamguardMVC.datamenus.DataMenuParent;
import annotation.handler.AnnotationHandler;
import tritechplugin.GeminiProcess;
import tritechplugin.dataselect.TargetDataSelectCreator;

public class Target2DataBlock extends PamDataBlock<Target2DataUnit> {

	private GeminiProcess geminiProcess;
	
	private TargetDataSelectCreator targetDataSelectCreator;

	public Target2DataBlock(GeminiProcess geminiProcess) {
		super(Target2DataUnit.class, "Gemini Targets", geminiProcess, 0);
		this.geminiProcess = geminiProcess;
	}

	/**
	 * @return the geminiProcess
	 */
	public GeminiProcess getGeminiProcess() {
		return geminiProcess;
	}

	@Override
	public DataSelectorCreator getDataSelectCreator() {
		if (targetDataSelectCreator == null) {
			targetDataSelectCreator = new TargetDataSelectCreator(this);
		}
		return targetDataSelectCreator;
	}

	@Override
	public AnnotationHandler getAnnotationHandler() {
		return geminiProcess.getTrackDataBlock().getAnnotationHandler();
	}

	@Override
	public List<JMenuItem> getDataUnitMenuItems(DataMenuParent menuParent, Point mousePosition,
			PamDataUnit... dataUnits) {
		if (dataUnits == null || dataUnits.length == 0) {
			return null;
		}
		List<PamDataUnit> unitList = Arrays.asList(dataUnits);
		List<PamDataUnit> uniqueSuperDets = this.getUniqueParentList(unitList);
		if (uniqueSuperDets == null || uniqueSuperDets.size() == 0) {
			return null;
		}
		PamDataBlock parentBlock = uniqueSuperDets.get(0).getParentDataBlock();
		PamDataUnit[] uniqueList = new PamDataUnit[uniqueSuperDets.size()];
		uniqueSuperDets.toArray(uniqueList);
		return parentBlock.getDataUnitMenuItems(menuParent, mousePosition, (PamDataUnit[]) uniqueList);
	}


}
