package hu.bme.alit.wear.common.helper;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tamasali on 2015.11.13..
 */
public class DefaultTimerHelper implements TimerHelper {

	private Timer timer;
	private TimerTask timerTask;
	private Handler handler = new Handler();

	private int tickNumber;

	private TimerCallBack timerCallBack;
	private int maxTick;
	private int tickPeriod;


	@Override
	public void startTimer() {
		timer = new Timer();

		timer.schedule(timerTask, 0, tickPeriod);
	}

	@Override
	public void stopTimerTask() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		timerCallBack.timerStop();
	}

	@Override
	public void initializeTimerTask(final TimerCallBack timerCallBack, final int maxTick, int tickPeriod) {

		this.timerCallBack = timerCallBack;
		this.maxTick = maxTick;
		this.tickPeriod = tickPeriod;

		timerTask = new TimerTask() {
			public void run() {

				//use a handler to run a toast that shows the current timestamp
				handler.post(new Runnable() {
					public void run() {
						tickNumber++;
						if (tickNumber > maxTick) {
							stopTimerTask();
							return;
						}
						timerCallBack.timerTick(tickNumber);
					}
				});
			}
		};
	}
}
