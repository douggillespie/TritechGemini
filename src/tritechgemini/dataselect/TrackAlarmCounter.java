package tritechgemini.dataselect;

import PamguardMVC.PamDataUnit;
import alarm.AlarmControl;
import alarm.AlarmCounter;

public class TrackAlarmCounter extends AlarmCounter {

	public TrackAlarmCounter(AlarmControl alarmControl) {
		super(alarmControl);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getValue(int countType, PamDataUnit dataUnit) {
		return 1;
	}

	@Override
	public void resetCounter() {

	}

}
