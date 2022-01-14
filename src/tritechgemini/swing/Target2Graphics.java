package tritechgemini.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import Array.ArrayManager;
import Array.SnapshotGeometry;
import Array.Streamer;
import PamUtils.Coordinate3d;
import PamUtils.LatLong;
import PamView.GeneralProjector;
import PamView.GeneralProjector.ParameterType;
import PamView.GeneralProjector.ParameterUnits;
import PamView.PamColors;
import PamView.PamDetectionOverlayGraphics;
import PamView.PamKeyItem;
import PamView.PamSymbol;
import PamView.PamSymbolType;
import PamView.PanelOverlayDraw;
import PamView.symbol.PamSymbolChooser;
import PamguardMVC.PamDataUnit;
import pamMaths.PamQuaternion;
import pamMaths.PamVector;
import tritechgemini.target.Target2DataUnit;
import tritechplugin.GeminiControl;
import tritechplugin.GeminiLocationParams;
import tritechplugin.GeminiParameters;

public class Target2Graphics extends PanelOverlayDraw {

	private GeminiControl geminiControl;
	public static PamSymbol defaultSymbol = new PamSymbol(PamSymbolType.SYMBOL_CIRCLE, 4, 4, true, Color.RED, Color.RED);

	public Target2Graphics(GeminiControl geminiControl) {
		super(defaultSymbol);
		this.geminiControl = geminiControl;
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
	public PamKeyItem createKeyItem(GeneralProjector arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle drawDataUnit(Graphics g, PamDataUnit dataUnit, GeneralProjector projector) {
		Target2DataUnit t2du = (Target2DataUnit) dataUnit;
		
		String sonar = t2du.getSonar();
		int sonarId = 0;
		if (sonar != null && sonar.contains("1")) {
//			g.setColor(PamColors.getInstance().getWhaleColor(1));
			sonarId = 0;
			dataUnit.setChannelBitmap(1);
		}
		else {
//			g.setColor(PamColors.getInstance().getWhaleColor(2));
			sonarId = 1;
			dataUnit.setChannelBitmap(2);
		}
		
		double verticalSwath = 0; // Vetical swath n degrees for 3D plotting.
		int boxOption = GeminiSymbolOptions.DRAW_2D_BOX;
		// work out wha twe actually want to draw
		PamSymbolChooser symbolChooser = projector.getPamSymbolChooser();
		if (symbolChooser instanceof GeminiSymbolChooser) {
			GeminiSymbolOptions geminiOptions = (GeminiSymbolOptions) symbolChooser.getSymbolOptions();
			boxOption = geminiOptions.boxOption;
			if (boxOption == GeminiSymbolOptions.DRAW_3D_BOX) {
				verticalSwath = geminiOptions.beamHalfHeightAngle;
			}
		}  

		GeminiParameters geminiParams = geminiControl.getGeminiParameters();
		GeminiLocationParams geminiLocation = geminiParams.getGeminiLocation(sonarId);
		
		/**
		 * First get the correct coordinated within the Gemin Frame
		 * This requires adding offsets from the SeaTec display
		 * and possibly flipping the x coordinate.  
		 */
		double xOffset = geminiLocation.getOffsetX();
		double yOffset = geminiLocation.getOffsetY();
		
		double x0 = t2du.getX_fl()+xOffset;
		double x1 = t2du.getX_fr()+xOffset;
		double x2 = t2du.getX_nr()+xOffset;
		double x3 = t2du.getX_nl()+xOffset;
		double y0 = t2du.getY_fl()+yOffset;
		double y1 = t2du.getY_fr()+yOffset;
		double y2 = t2du.getY_nr()+yOffset;
		double y3 = t2du.getY_nl()+yOffset;
		
		if (geminiLocation.isFlipLeftRight()) {
			x0 = -x0;
			x1 = -x1;
			x2 = -x2;
			x3 = -x3;
		}
		PamVector[] corners = new PamVector[4];
		PamVector[] topCorners = new PamVector[4];
		PamVector[] botCorners = new PamVector[4];
		LatLong[] cornerLL = new LatLong[4];
		Coordinate3d[] screenCoord = new Coordinate3d[4];
		Coordinate3d[] topScreenCoord = new Coordinate3d[4];
		Coordinate3d[] botScreenCoord = new Coordinate3d[4];
		
		corners[0] = new PamVector(x0, y0, 0);
		corners[1] = new PamVector(x1, y1, 0);
		corners[2] = new PamVector(x2, y2, 0);
		corners[3] = new PamVector(x3, y3, 0);
		/**
		 * End of calculating the coordinates in the Gemini frame in metres. 
		 */
		/**
		 * Now rotate the four corners by the heading and pitch. 
		 */
		SnapshotGeometry arrayGeometry = ArrayManager.getArrayManager().getCurrentArray().getSnapshotGeometry(dataUnit.getTimeMilliseconds());
		double[] arrayAngles = arrayGeometry.getArrayAngles();
		double streamerHead = 0;
		try {
			streamerHead = arrayGeometry.getCentreGPS().getHeading();
		}
		catch (Exception e) {
			
		}
		double h = Math.toRadians(geminiLocation.getSonarHeadingD()+streamerHead);
		double p = Math.toRadians(geminiLocation.getSonarPitchD());
		for (int i = 0; i < 4; i++) {
			if (verticalSwath > 0) {
				double verticalSwathR = Math.toRadians(verticalSwath);
				topCorners[i] = PamVector.rotateVector(corners[i], h, p+verticalSwathR);
				botCorners[i] = PamVector.rotateVector(corners[i], h, p-verticalSwathR);
			}
			corners[i] = PamVector.rotateVector(corners[i], h, p);
		}
		/**
		 * Get the absolute origin of the sonar. 
		 */
		LatLong cent = getStreamerOrigin(0, dataUnit.getTimeMilliseconds());
		Coordinate3d sonarXYZ = geminiLocation.getSonarXYZ();
		cent = cent.addDistanceMeters(sonarXYZ.x, sonarXYZ.y, sonarXYZ.z);
//		cent.setHeight(sonarXYZ.z);		
				
		/**
		 * Now add the relative coordinates to the absolute coordinates
		 * and also calculate screen coordinates. 
		 */
		for (int i = 0; i < 4; i++) {
			cornerLL[i] = cent.addDistanceMeters(corners[i]);
			screenCoord[i] = projector.getCoord3d(cornerLL[i]);
			if (verticalSwath > 0) {
				cornerLL[i] = cent.addDistanceMeters(topCorners[i]);
				topScreenCoord[i] = projector.getCoord3d(cornerLL[i]);
				cornerLL[i] = cent.addDistanceMeters(botCorners[i]);
				botScreenCoord[i] = projector.getCoord3d(cornerLL[i]);
			}
		}

		PamSymbol selSymbol = getPamSymbol(dataUnit, projector);
		if (boxOption == GeminiSymbolOptions.DRAW_NO_BOX) {
			// work out the mean x and y coordinates. 
			double x = 0, y = 0;
			for (int i = 0; i < 4; i++) {
				x += screenCoord[i].x;
				y += screenCoord[i].y;
			}
			x /= 4;
			y /= 4;
			Rectangle r = selSymbol.draw(g, new Point((int) x, (int) y));
			projector.addHoverData(r, dataUnit);
			return r;
		}
		
		// draw the plane box. 
		if (selSymbol.isFill()) {
			g.setColor(selSymbol.getFillColor());
			fillRectangle(g, screenCoord);
		}
		g.setColor(selSymbol.getLineColor());
		double[] centre = drawRectangle(g, screenCoord);
		
		if (verticalSwath > 0 && boxOption == GeminiSymbolOptions.DRAW_3D_BOX) {
//			Graphics2D g2d = (Graphics2D) g;
			Color col = selSymbol.getLineColor();
			Color opCol = new Color(col.getRed(), col.getGreen(),  col.getBlue(), 55);
			g.setColor(opCol);
			drawRectangle(g, topScreenCoord);
			drawRectangle(g, botScreenCoord);	
			for (int i = 0; i < 4; i++) {
				g.drawLine((int) topScreenCoord[i].x, (int) topScreenCoord[i].y, (int) screenCoord[i].x, (int) screenCoord[i].y);
				g.drawLine((int) botScreenCoord[i].x, (int) botScreenCoord[i].y, (int) screenCoord[i].x, (int) screenCoord[i].y);
			}
			
		}
		projector.addHoverData(new Coordinate3d(centre[0], centre[1]), dataUnit);
		
		return null;
	}
	
	private double[] drawRectangle(Graphics g, Coordinate3d[] square) {
//		g.drawPolygon(arg0, arg1, arg2);
		double mx = 0, my = 0;
		for (int i = 0, j = 1; i < 4; i++, j++) {
			if (i == 3) j = 0;
			g.drawLine((int)square[i].x, (int) square[i].y, (int) square[j].x, (int) square[j].y);
			mx += square[i].x;
			my += square[i].y;
		}
		double[] centre = {mx/4, my/4};
		return centre;
	}
	private double[] fillRectangle(Graphics g, Coordinate3d[] square) {
//		g.drawPolygon(arg0, arg1, arg2);
		int[] x = new int[4];
		int[] y = new int[4];
		double mx = 0, my = 0;
		for (int i = 0, j = 1; i < 4; i++) {
			x[i] = (int) square[i].x;
			y[i] = (int) square[i].y;
			mx += square[i].x;
			my += square[i].y;
		}
		g.fillPolygon(x, y, 4);
		double[] centre = {mx/4, my/4};
		return centre;
	}
	
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

	@Override
	public String getHoverText(GeneralProjector projector, PamDataUnit dataUnit, int arg2) {
		return dataUnit.getSummaryString();
	} 
}
