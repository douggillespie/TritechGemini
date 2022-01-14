package tritechplugin;

import java.io.Serializable;

import PamUtils.Coordinate3d;

public class GeminiLocationParams implements Serializable, Cloneable {

	public static final long serialVersionUID = 1L;

	/**
	 * Lat Long of sonar (includes height === -Depth)
	 * relative to the main system origin (from array manager). 
	 */
	private Coordinate3d sonarXYZ;
	
	/**
	 * Offset in sonar data (needed slightly differently from XYZ to 
	 * correct issue of the display offset being added to the data. 
	 */
	private double offsetX, offsetY;
	
	/**
	 * Heading of Sonar (Degrees true)
	 */
	private double sonarHeadingD = 0;
	
	/**
	 * Pitch of sonar in degrees, 0 - 90 tilted up, 0 to -90 down;
	 */
	private double sonarPitchD = 0;
	
	private boolean flipLeftRight  = false;;


	@Override
	public GeminiLocationParams clone() {
		try {
			GeminiLocationParams newParams = (GeminiLocationParams) super.clone();
			return newParams;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * @return the sonarXYZ
	 */
	public Coordinate3d getSonarXYZ() {
		if (sonarXYZ == null) {
			sonarXYZ = new Coordinate3d();
		}
		return sonarXYZ;
	}


	/**
	 * @param sonarXYZ the sonarXYZ to set
	 */
	public void setSonarXYZ(Coordinate3d sonarXYZ) {
		this.sonarXYZ = sonarXYZ;
	}


	/**
	 * @return the sonarHeadingD
	 */
	public double getSonarHeadingD() {
		return sonarHeadingD;
	}


	/**
	 * @param sonarHeadingD the sonarHeadingD to set
	 */
	public void setSonarHeadingD(double sonarHeadingD) {
		this.sonarHeadingD = sonarHeadingD;
	}


	/**
	 * @return the sonarPitchD
	 */
	public double getSonarPitchD() {
		return sonarPitchD;
	}


	/**
	 * @param sonarPitchD the sonarPitchD to set
	 */
	public void setSonarPitchD(double sonarPitchD) {
		this.sonarPitchD = sonarPitchD;
	}


	/**
	 * @return the flipLeftRight
	 */
	public boolean isFlipLeftRight() {
		return flipLeftRight;
	}


	/**
	 * @param flipLeftRight the flipLeftRight to set
	 */
	public void setFlipLeftRight(boolean flipLeftRight) {
		this.flipLeftRight = flipLeftRight;
	}


	/**
	 * @return the offsetX
	 */
	public double getOffsetX() {
		return offsetX;
	}


	/**
	 * @param offsetX the offsetX to set
	 */
	public void setOffsetX(double offsetX) {
		this.offsetX = offsetX;
	}


	/**
	 * @return the offsetY
	 */
	public double getOffsetY() {
		return offsetY;
	}


	/**
	 * @param offsetY the offsetY to set
	 */
	public void setOffsetY(double offsetY) {
		this.offsetY = offsetY;
	}
	
	
}
