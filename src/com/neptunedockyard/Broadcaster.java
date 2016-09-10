package com.neptunedockyard;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Broadcaster {
	static long delaytime = 0;
	LoopTask task = new LoopTask();
	Timer timer = new Timer("TaskName");
	
	public Broadcaster(int delay){
		delaytime = delay;
	}
	
	public void start() {
		timer.cancel();
		timer = new Timer("TaskName");
		Date executionDate = new Date();
		timer.scheduleAtFixedRate(task, executionDate, delaytime);
	}
	
	private class LoopTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("Broadcast message");
		}
	}
	
	public static void run(){
		Broadcaster broadcaster = new Broadcaster(10);
		broadcaster.start();
	}

}
