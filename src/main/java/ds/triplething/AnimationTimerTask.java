package ds.triplething;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;


public class AnimationTimerTask extends TimerTask {
		

	public interface AnimatedIcon
	{
		
		public JLabel getLabel();
		
		/**
		 * 
		 * @return	Returns true for more frames needed, false for all done
		 */
		public boolean updatePos(int frameNum);
	}
	
	public interface AnimationControl {
		public List<AnimatedIcon> start();
		public void stop();
		// return -1 to get the default
		public int getZOrder();
		// return -1 to get the default
		public int getPeriodMs();
	}
	
	public static JLayeredPane s_layerPane;
	static void setLayerPane(JLayeredPane layerPane) {
		s_layerPane = layerPane;
	}
	
	static ArrayList<AnimationTimerTask> s_controlList = new ArrayList<AnimationTimerTask>();
	
	static void startAnimation(JLayeredPane layerPane, AnimationControl control)
	{
		AnimationTimerTask task = new AnimationTimerTask(new Timer(), layerPane, control);
		
		synchronized(s_controlList) {
			s_controlList.add(task);
		}

		int periodMs = control.getPeriodMs() == -1 ? 50 : control.getPeriodMs();
		
		task.timer.scheduleAtFixedRate(
				task, 
				0,		// initial delay 
				periodMs);	// period (ms)
	
	}

	public AnimationTimerTask(Timer _timer, JLayeredPane _layerPane, AnimationControl _control) {
		layerPane = _layerPane;
		control = _control;
		timer = _timer;
	}
	
	@Override
	public void run() {
		
		boolean finished = true;
		
		if (count == 0) {
			
			animations = control.start();
			
			for(AnimatedIcon anicon : animations)
				// get the ZOrder every time as it may
				// change per icon (up to the control to decide)
				layerPane.add(anicon.getLabel(), new Integer((control.getZOrder() == -1 ? 100 : control.getZOrder())));
			
			finished = false;
		} else {
			// update the new position
			for(AnimatedIcon anicon : animations)
				if (!anicon.updatePos(count))
					finished = false;
		}
		
		count++;
		
		if (finished) {
			finish();
		}
	}
	
	void finish() {
		timer.cancel();
		
		for(AnimatedIcon anicon : animations)
			layerPane.remove(anicon.getLabel());
		
		control.stop();
		
		synchronized(s_controlList) {
			s_controlList.remove(this);
		}
	}
	
	static public void stopAll() {
		synchronized(s_controlList) {
			for(AnimationTimerTask task : s_controlList)
				task.finish();
			
			s_controlList.clear();
		}		
	}
	
	List<AnimatedIcon> animations;
	AnimationControl control;
	JLayeredPane layerPane;

	Timer timer;
	int count = 0;
}
