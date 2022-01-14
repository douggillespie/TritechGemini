package tritechplugin.target;

import java.awt.Color;

/**
 * functions to switch between target types as strings and target scores. 
 * @author dg50
 *
 */
public class TargetType {

	public static final String[] types = {"Small", "Possible", "Potential", "Probable", "Large", "Group", "Static", "Prob. Grp"};

	// try to mimic colours on Gemini display. 
	public static final Color colSmall = new Color(51,102,255);
	public static final Color colLarge = new Color(102,0,204);
	public static final Color colProbable = Color.RED;
	public static final Color colPotential = Color.YELLOW;
	public static final Color colPossible = Color.GREEN;
	public static final Color colProbGrp = Color.ORANGE;
	public static final Color colStatic = Color.WHITE;
	public static final Color colGroup = Color.GRAY;

	/**
	 * Get a numeric score for the different target types. The lowest meaningful 
	 * value is 1 for Small. No idea of this order is sensible or not. 
	 * @param type String type from target2 data. 
	 * @return numeric type, -1 for a null type and 0 for unrecognised. 
	 */
	public static int getScore(String type) {
		if (type == null) {
			return -1;
		}
		type = type.trim();
		for (int i = 0; i < types.length; i++) {
			if (type.equals(types[i])) {
				return i+1;
			}
		}
		System.out.printf("Unrecognised target type: \"%s\" from Tritech\n", type);
		return 0;
	}

	public static Color getColour(String type) {
		if (type == null) {
			return Color.BLACK;
		}
		type = type.trim();
		switch (type) {
		case "Small":
			return colSmall;
		case "Possible":
			return colPossible;
		case "Potential":
			return colPotential;
		case "Probable":
			return colProbable;
		case "Large":
			return colLarge;
		case "Group":
			return colGroup;
		case "Static":
			return colStatic;
		case "Prob. Grp":
			return colProbGrp;
		}
		return Color.BLACK;
	}

	/**
	 * Scores are starting at 1, so subtract 1 off before lookup 
	 * @param score score (starting at 1 for the lowest known type)
	 * @return String type or "Unknown"
	 */
	public static String getType(int score) {
		score --;
		if (score < 0 || score >= types.length) {
			return "Unknown";
		}
		else {
			return types[score];
		}

	}
}
