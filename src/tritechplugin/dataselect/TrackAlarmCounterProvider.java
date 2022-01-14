package tritechplugin.dataselect;

import alarm.AlarmControl;
import alarm.AlarmCounter;
import alarm.AlarmCounterProvider;

public class TrackAlarmCounterProvider extends AlarmCounterProvider {

	@Override
	protected AlarmCounter createAlarmCounter(AlarmControl alarmControl) {
		return new TrackAlarmCounter(alarmControl);
	}

}
