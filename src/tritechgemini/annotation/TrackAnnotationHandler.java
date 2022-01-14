package tritechgemini.annotation;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import annotation.DataAnnotationType;
import annotation.handler.AnnotationChoiceHandler;
import annotation.handler.AnnotationChoices;
import annotation.string.StringAnnotationType;
import annotation.userforms.UserFormAnnotationType;
import tritechgemini.GeminiControl;
import tritechgemini.target.TrackDataBlock;

public class TrackAnnotationHandler extends AnnotationChoiceHandler {
	
	private TrackDataBlock trackDataBlock;
	private GeminiControl geminiControl;
	
	public TrackAnnotationHandler(GeminiControl geminiControl, PamDataBlock trackDataBlock) {
		super(trackDataBlock);
		this.geminiControl = geminiControl;
		this.trackDataBlock = (TrackDataBlock) trackDataBlock;
		
		addAnnotationType(new StringAnnotationType("Text Annotation", 80));
		addAnnotationType(new UserFormAnnotationType());
		
	}

	@Override
	public AnnotationChoices getAnnotationChoices() {
		return geminiControl.getGeminiParameters().getAnnotationChoices();
	}

	@Override
	public boolean updateAnnotation(PamDataUnit pamDataUnit, DataAnnotationType annotationType) {
		return super.updateAnnotation(pamDataUnit, annotationType);
	}

}
