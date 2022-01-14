package tritechplugin.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import Array.ArrayManager;
import Array.SnapshotGeometry;
import Array.Streamer;
import Jama.Matrix;
import Map.MapRectProjector;
import PamUtils.Coordinate3d;
import PamUtils.LatLong;
import PamView.PamSymbol;
import PamView.PamSymbolType;
import PamView.PanelOverlayDraw;
import PamView.symbol.PamSymbolChooser;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.dataSelector.DataSelector;
import tritechgemini.imagedata.FanImageData;
import tritechgemini.imagedata.FanPicksFromData;
import tritechgemini.imagedata.GeminiImageRecordI;
import tritechgemini.imagedata.ImageFanMaker;
import tritechplugin.GeminiControl;
import tritechplugin.GeminiLocationParams;
import tritechplugin.GeminiParameters;
import tritechplugin.dataselect.ECDDataSelectParams;
import tritechplugin.dataselect.ECDDataSelector;
import tritechplugin.tritech.ECDDataBlock;
import PamView.GeneralProjector;
import PamView.GeneralProjector.ParameterType;
import PamView.GeneralProjector.ParameterUnits;
import PamView.PamKeyItem;

public class ECDOverlayGraphics extends PanelOverlayDraw {

	private GeminiControl geminiControl;
	private ECDDataBlock ecdDataBlock;
	public static PamSymbol defaultSymbol = new PamSymbol(PamSymbolType.SYMBOL_SQUARE, 10, 10, true, Color.RED, Color.WHITE);

	private GeminiImageRecordI[] lastECDRecord = new GeminiImageRecordI[GeminiControl.MAX_SONARS];

	private FanImageData[] lastImage = new FanImageData[GeminiControl.MAX_SONARS];
	
	private BufferedImage[] images = new BufferedImage[GeminiControl.MAX_SONARS];
	
	private ImageFanMaker imageFanMaker;
	
	private GeminiImageMaker imageMaker;
	private BufferedImage singleImage;

//	private ECDImageMaker defaultImageMaker;

	public ECDOverlayGraphics(GeminiControl geminiControl, ECDDataBlock ecdDataBlock) {
		super(defaultSymbol);
		this.geminiControl = geminiControl;
		this.ecdDataBlock = ecdDataBlock;
		imageFanMaker = new FanPicksFromData(2);
		imageMaker = new GeminiImageMaker();
	}

	@Override
	public boolean canDraw(ParameterType[] parameterTypes, ParameterUnits[] parameterUnits) {
		if (parameterTypes[0] == ParameterType.LATITUDE
				&& parameterTypes[1] == ParameterType.LONGITUDE) {
			return true;
		}
		else if (parameterTypes[0] == ParameterType.TIME
				&& parameterTypes[1] == ParameterType.FREQUENCY) {
			return false;
		}
		else if (parameterTypes[0] == ParameterType.BEARING &&
				parameterTypes[1] == ParameterType.RANGE) {
			return true;
		}
		return false;
	}

	@Override
	public Rectangle drawDataUnit(Graphics g, PamDataUnit pamDataUnit, GeneralProjector generalProjector) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PamKeyItem createKeyItem(GeneralProjector generalProjector, int keyType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHoverText(GeneralProjector generalProjector, PamDataUnit dataUnit, int iSide) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean preDrawAnything(Graphics g, PamDataBlock pamDataBlock, GeneralProjector projector) {
		if (projector instanceof MapRectProjector == false) {
			return true;
		}
		//		defaultSymbol = new PamSymbol(PamSymbolType.SYMBOL_SQUARE, 10, 10, true, Color.RED, Color.WHITE);
		MapRectProjector mapProj = (MapRectProjector) projector;
		long mapTime = mapProj.getMapPanelRef().getSimpleMapRef().getMapTime();
		GeminiParameters geminiParams = geminiControl.getGeminiParameters();
		int nSonar = geminiParams.nSonars;
		DataSelector ds = projector.getDataSelector();
		ECDDataSelector ecdDataSelector = null;
		if (ds instanceof ECDDataSelector) {
			ecdDataSelector = (ECDDataSelector) ds;
		}
		PamSymbolChooser symChos = projector.getPamSymbolChooser();
		ECDSymbolChooser ecdSymbolChooser;
		boolean combinedImage = false;
		if (symChos instanceof ECDSymbolChooser) {
			 ecdSymbolChooser = (ECDSymbolChooser) symChos;
			 imageMaker = ecdSymbolChooser.getImageMaker();
			 combinedImage = ecdSymbolChooser.getSymbolOptions().isCombineSingleImage();
		}

		// draw separate images for each sonar.
		// no data units here, so will have to fudge the data selector somewhet. 
		if (combinedImage) {
			drawCombinedSonars(g, mapProj, mapTime);
		}
		else {
			for (int i = 0; i < nSonar; i++) {
				if (ecdDataSelector != null && ecdDataSelector.getParams().getCombinationFlag() != ECDDataSelectParams.DATA_SELECT_DISABLE) {
					if ((ecdDataSelector.getParams().usedSonars & 1<<i) == 0) {
						continue;
					}
				}
				drawOutline(g, mapProj, mapTime, i);
			}
		}
		
		return false;

	}

	private ImageFanMaker getImageMaker(MapRectProjector mapProj) {
		return imageFanMaker;
	}

	private void drawCombinedSonars(Graphics g, MapRectProjector mapProj, long mapTime) {
		GeminiParameters geminiParams = geminiControl.getGeminiParameters();
		int nSonar = geminiParams.nSonars;
		GeminiImageRecordI[] ecdRecordSets = new GeminiImageRecordI[nSonar];
		// draw separate images for each sonar.
		boolean needImage = false;
		for (int i = 0; i < nSonar; i++) {

			ecdRecordSets[i] =  ecdDataBlock.findRecordSet(mapTime, i+1);
			//		System.out.println("Found ECD REcord " + ecdRecordSet);
			if (ecdRecordSets[i] == null) {
				continue;
			}
			if (ecdRecordSets[i] != lastECDRecord[i] || lastImage[0] == null) {
				needImage = true;
				lastECDRecord[i] = ecdRecordSets[i];
				lastImage[i] = imageFanMaker.createFanData(lastECDRecord[i]);
			}
		}
		FanImageData ecdImage;
		if (needImage) {
			singleImage = imageMaker.createDualImage(lastImage);
		}


		SnapshotGeometry arrayGeometry = ArrayManager.getArrayManager().getCurrentArray().getSnapshotGeometry(mapTime);
		double[] arrayAngles = arrayGeometry.getArrayAngles();
		double streamerHead = 0;
		try {
			streamerHead = arrayGeometry.getCentreGPS().getHeading();
		}
		catch (Exception e) {
			
		}
		
		GeminiLocationParams geminiLocation = geminiParams.getGeminiLocation(0);
		LatLong origin = getStreamerOrigin(0, mapTime);
		Coordinate3d xyz = geminiLocation.getSonarXYZ();
		origin = origin.addDistanceMeters(xyz.x, xyz.y, xyz.z); // thats the central position of the sonar. 
		double hAngle = geminiLocation.getSonarHeadingD();
		hAngle += streamerHead;
		double vAngle = geminiLocation.getSonarPitchD();
		
		drawRotatedImage(g, mapProj, lastImage[0], singleImage, lastECDRecord[0], origin, geminiLocation.isFlipLeftRight(), hAngle, vAngle);
	}

	/**
	 * Better method to work out the translation from image to screen coordinates: <br>
	 * Use the standard projector to work out where each corner goes, then solve a matrix 
	 * to get the affine transform from source to destination. 
	 * @param g
	 * @param mapProj
	 * @param mapTime
	 * @param iSonar
	 */
	private void drawOutline(Graphics g, MapRectProjector mapProj, long mapTime, int iSonar) {

		GeminiImageRecordI ecdRecordSet;
		FanImageData ecdImage;
		
		synchronized (imageFanMaker) {
			//			try {
			ecdRecordSet = ecdDataBlock.findRecordSet(mapTime,iSonar+1);
//					System.out.println("Found ECD REcord " + ecdRecordSet);
			if (ecdRecordSet == null) {
				return;
			}
			if (ecdRecordSet != lastECDRecord[iSonar] || lastImage[iSonar] == null) {
				ecdImage = imageFanMaker.createFanData(ecdRecordSet);
				images[iSonar] = imageMaker.createImage(ecdImage); 
				lastECDRecord[iSonar] = ecdRecordSet;
				lastImage[iSonar] = ecdImage;
			}
			else {
				ecdImage = lastImage[iSonar];
			}
			//			}
			//			catch (Exception e) {
			//				System.out.println(e.getMessage());
			//			}
		}
		if (ecdImage == null) {
			return;
		}

		SnapshotGeometry arrayGeometry = ArrayManager.getArrayManager().getCurrentArray().getSnapshotGeometry(mapTime);
		double[] arrayAngles = arrayGeometry.getArrayAngles();
		double streamerHead = 0;
		try {
			streamerHead = arrayGeometry.getCentreGPS().getHeading();
		}
		catch (Exception e) {
			
		}
		
		GeminiParameters geminiParams = geminiControl.getGeminiParameters();
		GeminiLocationParams geminiLocation = geminiParams.getGeminiLocation(iSonar);
		LatLong origin = getStreamerOrigin(0, mapTime);
		Coordinate3d xyz = geminiLocation.getSonarXYZ();
		origin = origin.addDistanceMeters(xyz.x, xyz.y, xyz.z); // thats the central position of the sonar. 
		double hAngle = geminiLocation.getSonarHeadingD();
		hAngle += streamerHead;
		double vAngle = geminiLocation.getSonarPitchD();
		drawRotatedImage(g, mapProj, iSonar, origin, geminiLocation.isFlipLeftRight(), hAngle, vAngle);
	}
	private void drawRotatedImage(Graphics g, MapRectProjector mapProj, int iSonar, LatLong origin, boolean flipLR, double heading, double pitch) {
		FanImageData ecdImage = lastImage[iSonar];
		BufferedImage image = images[iSonar];
		GeminiImageRecordI imageRecord = lastECDRecord[iSonar];
		drawRotatedImage(g, mapProj, ecdImage, image, imageRecord, origin, flipLR, heading, pitch);
	}
	
	private void drawRotatedImage(Graphics g, MapRectProjector mapProj, FanImageData ecdImage, BufferedImage image, GeminiImageRecordI imageRecord, LatLong origin, 
			boolean flipLR, double heading, double pitch) {
		if (ecdImage == null || image == null || imageRecord == null) {
			return;
		}
		int imW = image.getWidth();
		int imH = image.getHeight();
		// make a matric of the original coordinates
		double[][] oCoord = new double[3][4];
		oCoord[0][0] = 0;
		oCoord[1][0] = 0;
		oCoord[2][0] = 1;
		oCoord[0][1] = 0;
		oCoord[1][1] = imH;
		oCoord[2][1] = 1;
		oCoord[0][2] = +imW;
		oCoord[1][2] = imH;
		oCoord[2][2] = 1;
		oCoord[0][3] = +imW;
		oCoord[1][3] = 0;
		oCoord[2][3] = 1;

		//		GeminiParameters geminiParams = geminiControl.getGeminiParameters();
		//		GeminiLocationParams geminiLocation = geminiParams.getGeminiLocation(iSonar);
		//		LatLong origin = getStreamerOrigin(0, mapTime);
		//		Coordinate3d xyz = geminiLocation.getSonarXYZ();
		//		origin = origin.addDistanceMeters(xyz.x, xyz.y, xyz.z);
		Point2D cent = mapProj.getCoord3d(origin).getPoint2D();
		Point intPoint = mapProj.getCoord3d(origin).getXYPoint();
		//		defaultSymbol.draw(g, intPoint);
		double pixPerM = mapProj.getPixelsPerMetre();
		int[] cornerX = new int[4];
		int[] cornerY = new int[4];
		double yRang = imageRecord.getMaxRange();//ecdImage.getyRange();
		double xR = imageRecord.getMaxRange()*Math.sin(60*Math.PI/180.);
		double[] xRange = {-xR, xR};//ecdImage.getxRange();
		double xFlip = flipLR ? -1: 1;
		double[] xLims = {xRange[0]*xFlip, xRange[0]*xFlip, xRange[1]*xFlip, xRange[1]*xFlip};
		double[] yLims = {yRang, 0, 0, yRang, 0};
		double vertAng = Math.toRadians(pitch);

		double[][] dCoord = new double[3][4];

		for (int iC = 0; iC < 4; iC++) {
			double yRange = Math.cos(vertAng)*yLims[iC];
			double vRange = Math.sin(vertAng)*yLims[iC];
			LatLong lly = origin.travelDistanceMeters(heading, yRange, vRange);
			lly = lly.travelDistanceMeters(heading+90, xLims[iC], 0);
			Coordinate3d c3y = mapProj.getCoord3d(lly);
			//			  LatLong llx = origin.travelDistanceMeters(geminiLocation.getSonarHeadingD()+90, xLims[iX], vRange);
			//			  Coordinate3d c3x = mapProj.getCoord3d(lly);

			cornerX[iC] = (int) c3y.x;
			cornerY[iC] = (int) c3y.y;
			dCoord[0][iC] = c3y.x;
			dCoord[1][iC] = c3y.y;
			dCoord[2][iC] = 1;
		}
		/*
		 * Also work out the positions of the two corners of the drawn image since that's 
		 * where we're going to put the tooltips. 
		 * 
		 */
//		Coordinate3d corner = new Coordinate3d();
//		corner.x = (dCoord[0][0] + dCoord[0][1])/2.;
//		corner.y = (dCoord[1][0] + dCoord[1][1])/2.;
//		mapProj.addHoverData(corner, imageRecord);
//		corner.x = (dCoord[0][2] + dCoord[0][3])/2.;
//		corner.y = (dCoord[1][2] + dCoord[1][3])/2.;
//		mapProj.addHoverData(corner, ecdRecordSet);
		//		g.setColor(PamColors.getInstance().getWhaleColor((iSonar+1)));
		//		g.fillPolygon(cornerX, cornerY, 4);

		//		% work out the bare coordinates transformation
		Matrix orig = new Matrix(oCoord, 3, 4);
		Matrix dest = new Matrix(dCoord, 3, 4);

		Matrix transform = null;
		try {
			transform = orig.solveTranspose(dest);
			//			transform = transform.inverse();
		}
		catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return;
		}
		/* 
		 * Fair bit of confusion as to what's a row and what's a colum in the indexing into this and 
		 * it seems to be the wrong way around, having to transpose the matrix as it's used to build the 
		 * Affine Transform. It works though.  
		 */
		double[][] tr = transform.getArray();
		AffineTransform af = new AffineTransform(tr[0][0], tr[0][1], tr[1][0], tr[1][1], tr[2][0], tr[2][1]);
//				AffineTransform af = new AffineTransform(tr[0][0], tr[1][0], tr[0][1], tr[1][1], tr[0][2], tr[1][2]);
		// lets see if that dos it!
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform oldTansofrm = g2d.getTransform();
		g2d.setTransform(af);

		int xoffs = (int) cent.getX()*0;
		int yoffs = (int) cent.getY()*0;
		g2d.drawImage(image, 0+xoffs, imH+yoffs, imW+xoffs, 0+yoffs, 0, 0, imW, imH, null);


		g2d.setTransform(oldTansofrm);


	}

	//	/**
	//	 * This didn't work since I couln't ever get the transformation right for 3D rotation. 
	//	 * @param g
	//	 * @param mapProj
	//	 * @param mapTime
	//	 * @param iSonar
	//	 */
	//	private void drawSonar(Graphics g, MapRectProjector mapProj, long mapTime, int iSonar) {
	//		ECDImage ecdImage = null;
	//		synchronized (ecdImageMaker) {
	//			try {
	//				ECDRecordSet ecdRecordSet = ecdDataBlock.findRecordSet(mapTime, iSonar+1);
	//				//		System.out.println("Found ECD REcord " + ecdRecordSet);
	//				if (ecdRecordSet == null) {
	//					return;
	//				}
	//				if (ecdRecordSet != lastECDRecord[iSonar] || lastImage[iSonar] == null) {
	//					ecdImage = ecdImageMaker.extractImage(ecdRecordSet, BufferedImage.TYPE_4BYTE_ABGR);
	//					lastECDRecord[iSonar] = ecdRecordSet;
	//					lastImage[iSonar] = ecdImage;
	//				}
	//				else {
	//					ecdImage = lastImage[iSonar];
	//				}
	//			}
	//			catch (Exception e) {
	//				System.out.println(e.getMessage());
	//			}
	//		}
	//		if (ecdImage == null) {
	//			return;
	//		}
	//
	//		GeminiParameters geminiParams = geminiControl.getGeminiParameters();
	//		int sonarId = ecdImage.getEcdRecord().getSonar();
	//		GeminiLocationParams geminiLocation = geminiParams.getGeminiLocation(iSonar);
	//
	//		BufferedImage image = ecdImage.getBufferedImage();
	//		int imW = image.getWidth();
	//		int imH = image.getHeight();
	//		// find the hydrophone centre of the display...
	//		LatLong origin = getStreamerOrigin(0, mapTime);
	//		Coordinate3d xyz = geminiLocation.getSonarXYZ();
	//		origin = origin.addDistanceMeters(xyz.x, xyz.y, xyz.z);
	//		Point2D cent = mapProj.getCoord3d(origin).getPoint2D();
	//		// work out the X scale. 
	//		LatLong far = origin.travelDistanceMeters(geminiLocation.getSonarHeadingD(), 50);
	//		Point2D farPY = mapProj.getCoord3d(far).getPoint2D();
	//		double scaleX = Math.sqrt(Math.pow(farPY.getX()-cent.getX(),2) + Math.pow(farPY.getY()-cent.getY(),2))/50;
	//		//		defaultSymbol.draw(g, mapProj.getCoord3d(far).getXYPoint());
	//		int drawH = (int) (ecdImage.getyRange()*scaleX);
	//		// now the Y scale
	//		double imHalfWid = Math.abs(ecdImage.getxRange()[0]);
	//		int[] drawWid = new int[2];
	//		Point2D[] farPX = new Point2D[2];
	//		for (int i = 0; i < 2; i++) {
	//			double dir = 90*(i == 1 ? -1. : 1.)*(geminiLocation.isFlipLeftRight() ? 1 : -1);
	//			far = origin.travelDistanceMeters(geminiLocation.getSonarHeadingD()+dir, 50);
	//			farPX[i] = mapProj.getCoord3d(far).getPoint2D();
	//			double scaleY = Math.sqrt(Math.pow(farPX[i].getX()-cent.getX(),2) + Math.pow(farPX[i].getY()-cent.getY(),2))/(50);
	//			drawWid[i] = (int) (imHalfWid*scaleY);
	//			//			defaultSymbol.draw(g, mapProj.getCoord3d(far).getXYPoint());
	//		}
	//		// now need to add a transform to rotate the drawing according to the sonar rotation AND the map rotation. 
	//		Graphics2D g2d = (Graphics2D) g;
	//		AffineTransform oldTansofrm = g2d.getTransform();
	//		/*
	//		 *  scaleX and scaleY incorporate the scaling of the image, not just the scaling of the display, so need to untangle
	//		 *  that a bit to get the natural rotation of the display. 
	//		 *  
	//		 *  This seems almost impossible to acheive with basic rotations, since the entire image distorts badly when viewed from 
	//		 *  different camer angles on a rotating map. For instance, if looking from 30 deg behind, side on, the far Y point and 
	//		 *  bottom L point of a rectangle can be made to touch, but the bottom R point is miles away, i.e. there has been a triangular
	//		 *  squishing of the rectangular image.  So need to rotate, then squish. Or squish and rotate ?   
	//		 */
	//		//		double transformAngle = Math.atan2(farPX[0].getY()-cent.getY(),farPX[0].getX()-cent.getX())*180/Math.PI;
	//		//		transformAngle = geminiLocation.getSonarHeadingD();
	//		double mapAngle = Math.toRadians(mapProj.getMapRotationDegrees());
	//		double mapVertAngle = Math.toRadians(mapProj.getMapVerticalRotationDegrees());
	//		double rotAngle = 0;
	//		double shearAngle;
	//		double sonarVert = Math.toRadians(geminiLocation.getSonarPitchD());
	//		double sonarHead = Math.toRadians(geminiLocation.getSonarHeadingD());
	//		double sonarVertX = sonarVert*Math.cos(sonarHead);
	//		double sonarVertY = sonarVert*Math.sin(sonarHead);
	//		shearAngle = Math.toRadians(mapProj.getMapVerticalRotationDegrees())-sonarVertX;
	//		double vertScaleM = Math.cos(mapVertAngle-sonarVert); // horizontal squish factor
	//		AffineTransform mapVertRotate = AffineTransform.getScaleInstance(1, Math.cos(mapVertAngle));
	//		AffineTransform sonarVertSquish = AffineTransform.getScaleInstance(1, Math.cos(mapVertAngle-sonarVert));
	//		//		AffineTransform vertRotate = AffineTransform.getScaleInstance(1, Math.cos(Math.toRadians(30*sonarId)));
	//		AffineTransform sonarVertShear = AffineTransform.getShearInstance(Math.sin(-sonarVert)*Math.sin(mapAngle+sonarHead)*0, 
	//				Math.sin(-sonarVert)*Math.sin(mapAngle+sonarHead)*0);
	//		AffineTransform sonarVertTrans = new AffineTransform();
	//		if (sonarId == 5) {
	//			//		sonarVertTrans.concatenate(sonarVertStretch);
	//			/*
	//			 *  need a vertical stretch for any component behind the sonar and a rotation for the component to the side.
	//			 *  Once rotated so that we're directly behind the sonar, the vertical stretch is sin(sonarVert) 
	//			 */
	//			AffineTransform sonarVertStretch = AffineTransform.getScaleInstance(1, 1./Math.cos(sonarVert));
	//			sonarVertTrans.concatenate(sonarVertShear);
	//			sonarVertTrans = AffineTransform.getRotateInstance(Math.asin(Math.sin(-sonarVert)*Math.sin(mapAngle+sonarHead)));
	//			sonarVertTrans = AffineTransform.getRotateInstance(Math.asin(Math.sin(-sonarVert)*Math.sin(mapVertAngle)));
	//		}
	//
	//		AffineTransform mapRotate = AffineTransform.getRotateInstance(mapAngle);
	//		AffineTransform sonarRotate = AffineTransform.getRotateInstance(sonarHead);
	//		AffineTransform translate = AffineTransform.getTranslateInstance(cent.getX(), cent.getY());
	//		AffineTransform fullTrans = new AffineTransform();
	//		// apply the transforms in the correct order. The first one added occurs first. 
	//		fullTrans.concatenate(translate); // move sonar origin to 0,0 for all subsequent rotations
	//
	//		try {
	//			fullTrans.concatenate(sonarRotate.createInverse());
	//		} catch (NoninvertibleTransformException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} // rotate the sonar so that it's head up
	//		fullTrans.concatenate(sonarVertTrans);
	//		fullTrans.concatenate(sonarRotate);
	////		fullTrans.concatenate(mapVertRotate);
	//		fullTrans.concatenate(mapRotate); // rotate the map
	//		fullTrans.concatenate(sonarVertSquish); // squish in the y direction for map vertical rotation
	//		fullTrans.concatenate(sonarRotate);
	////		fullTrans.concatenate(sonarVertSquish);
	//
	//		/*
	//		 * This works on the flat, so can go back to it as required.  ...
	//		double mapAngle = mapProj.getMapRotationDegrees();
	//		double rotAngle = 0;
	//		double shearAngle;
	//		double sonarVert = Math.toRadians(geminiLocation.getSonarPitchD());
	//		double sonarHead = Math.toRadians(geminiLocation.getSonarHeadingD());
	//		double sonarVertX = sonarVert*Math.cos(sonarHead);
	//		double sonarVertY = sonarVert*Math.sin(sonarHead);
	//		shearAngle = Math.toRadians(mapProj.getMapVerticalRotationDegrees())-sonarVertX;
	//		double vertScaleM = Math.cos( Math.toRadians(mapProj.getMapVerticalRotationDegrees()));
	//		AffineTransform mapVertRotate = AffineTransform.getScaleInstance(1, vertScaleM);
	//		AffineTransform sonarVertRotate = AffineTransform.getScaleInstance(1, Math.cos(sonarVert));
	////		AffineTransform vertRotate = AffineTransform.getScaleInstance(1, Math.cos(Math.toRadians(30*sonarId)));
	//		AffineTransform vertRotate = AffineTransform.getShearInstance(0.1, -.1);
	//		AffineTransform mapRotate = AffineTransform.getRotateInstance(Math.toRadians(mapAngle));
	//		AffineTransform sonarRotate = AffineTransform.getRotateInstance(sonarHead);
	//		AffineTransform translate = AffineTransform.getTranslateInstance(cent.getX(), cent.getY());
	//		AffineTransform fullTrans = new AffineTransform();
	//		// apply the transforms in the correct order. 
	//		fullTrans.concatenate(translate); // move to sonar origin. 
	////		fullTrans.concatenate(vertRotate);
	//		fullTrans.concatenate(mapVertRotate); // squish in the y direction for map vertical rotation
	//		fullTrans.concatenate(sonarRotate); // rotate the sonar
	//		fullTrans.concatenate(mapRotate); // rotate the map
	//		 */
	//		g2d.setTransform(fullTrans);
	//
	//		double pixsPerMetre = mapProj.getPixelsPerMetre();
	//		drawH = (int) (ecdImage.getyRange()*pixsPerMetre);
	//		drawWid[0] = -(int) (ecdImage.getxRange()[0]*pixsPerMetre);
	//		drawWid[1] = (int) (ecdImage.getxRange()[1]*pixsPerMetre);
	//		//		g.drawImage(image, (int) (cent.getX()+drawWid[0]), (int) cent.getY(), (int) cent.getX()-drawWid[1], (int)cent.getY()-drawH, 0, imH, imW, 0, null);
	//		int offs = 0;
	//		g.drawImage(image, (int) (drawWid[0])+offs, offs, (int) -drawWid[1]+offs, -drawH+offs, 0, imH, imW, 0, null);
	//		defaultSymbol.draw(g, new Point(0,0));
	//
	//		if (rotAngle != 0) {
	//			g2d.rotate(-rotAngle, cent.getX(), cent.getY());
	//		}
	//		g2d.setTransform(oldTansofrm);
	//		//		g2d.setTransform(AffineTransform.);
	//	}

	/**
	 * Get the origin for the current streamer. Only want the lat long. Will take 
	 * absolute z and absolute rotation from sonar orientation data. 
	 * @param streamerInd
	 * @param timeMillis
	 * @return
	 */
	private LatLong getStreamerOrigin(int streamerInd, long timeMillis) {
		Streamer streamer = ArrayManager.getArrayManager().getCurrentArray().getStreamer(streamerInd);
		return streamer.getHydrophoneLocator().getStreamerLatLong(timeMillis);
	}

}
