package tritechplugin.target;

import java.util.ListIterator;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;
import PamguardMVC.dataSelector.DataSelectorCreator;
import PamguardMVC.superdet.SuperDetDataBlock;
import annotation.handler.AnnotationHandler;
import pamScrollSystem.ViewLoadObserver;
import tritechplugin.GeminiProcess;
import tritechplugin.annotation.TrackAnnotationHandler;
import tritechplugin.dataselect.TrackDataSelectCreator;
import tritechplugin.swing.TrackSymbolManager;

public class TrackDataBlock extends SuperDetDataBlock<TrackDataUnit, Target2DataUnit> {

	private GeminiProcess geminiProcess;
	public TrackDataBlock(GeminiProcess geminiProcess) {
		super(TrackDataUnit.class, "Gemini Tracks", geminiProcess, 0, ViewerLoadPolicy.LOAD_OVERLAPTIME);
		this.geminiProcess = geminiProcess;
		setNaturalLifetime(3600);
		setPamSymbolManager(new TrackSymbolManager(this));
	}

	/**
	 * Quick reference to re-find the same unit again. 
	 */
	private TrackDataUnit lastFound;
	private TrackDataSelectCreator dataSelectCreator;
	/**
	 * Find a track for a given target id
	 * @param targetId
	 * @return
	 */
	public TrackDataUnit findTargetTrack(long targetId) {
		if (lastFound != null && lastFound.getTargetID() == targetId) {
			return lastFound;
		}
		synchronized (this.getSynchLock()) {
			ListIterator<TrackDataUnit> it = getListIterator(PamDataBlock.ITERATOR_END);
			// search from end since we're likely looking for the last one anyway
			while (it.hasPrevious()) {
				TrackDataUnit tdu = it.previous();
				if (tdu.getTargetID() == targetId) {
					lastFound = tdu;
					return tdu;
				}
			}
		}
		return null; // nothing found
	}
	
	@Override
	public boolean reattachSubdetections(ViewLoadObserver viewLoadObserver) {
		return super.reattachSubdetections(viewLoadObserver);
	}
	
	@Override
	public DataSelectorCreator getDataSelectCreator() {
		if (dataSelectCreator == null) {
			dataSelectCreator = new TrackDataSelectCreator(this);
		}
		return dataSelectCreator;
	}

	@Override
	public PamDataBlock findSubDetDataBlock(String dataLongName) {
		if (dataLongName == null) {
			return null;
		}
		return geminiProcess.getTarget2DataBlock();
//		return super.findSubDetDataBlock(dataLongName);
	}

	@Override
	public boolean canSuperDetection(PamDataBlock subDataBlock) {
		// these can only be superdets of a target data block in the same process. 
		if (subDataBlock instanceof Target2DataBlock && subDataBlock.getParentProcess() == this.getParentProcess()) {
			return true;
		}
		else {
			return false;
		}

	}
}
