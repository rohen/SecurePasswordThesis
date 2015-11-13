package hu.bme.alit.wear.securepassword.securepassword.helper;

/**
 * Created by tamasali on 2015.11.13..
 */
public interface TimerHelper {

	void startTimer();

	void stopTimerTask();

	void initializeTimerTask(final TimerCallBack timerCallBack, int maxTick, int tickPeriod);

	interface TimerCallBack {
		void timerTick(int tickNumber);

		void timerStop();
	}
}
