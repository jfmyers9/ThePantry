package cs169.project.thepantry;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SwipeDetector implements View.OnTouchListener {

	public static enum Action {
		LR, // Left to Right
		RL, // Right to Left
		TB, // Top to Bottom
		BT, // Bottom to Top
		None // No Swipe Action
	}
	
	private static final String logTag = "SwipeDetector";
	private static final int MIN_DISTANCE = 100;
	private float downX, downY, upX, upY;
	private Action mSwipeDetected = Action.None;
	
	public boolean swipeDetected() {
		return mSwipeDetected != Action.None;
	}
	
	public Action getAction() {
		return mSwipeDetected;
	}
	
	@Override
	public boolean onTouch (View v, MotionEvent event) {
		switch (event.getAction()){
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			mSwipeDetected = Action.None;
			return false; // Allows for click handling concurrently with swipe
		case MotionEvent.ACTION_MOVE:
			upX = event.getX();
			upY = event.getY();
			
			float deltaX = downX - upX;
			float deltaY = downY - upY;
			
			// horizontal swipe detected
			if (Math.abs(deltaX) > MIN_DISTANCE) {
				// left or right
				if (deltaX < 0) {
					Log.i(logTag, "Swipe Left to Right");
					mSwipeDetected = Action.LR;
					return true;
				} else if (deltaX > 0) {
					Log.i(logTag, "Swipe Right to Left");
					mSwipeDetected = Action.RL;
					return true;
				}	
			} else if (Math.abs(deltaY) > MIN_DISTANCE) { // Vertical swipe detected
				// Up or down
				if (deltaY < 0) {
					Log.i(logTag, "Swipe Top to Bottom");
					mSwipeDetected = Action.TB;
					return false;
				} else if (deltaY > 0) {
					Log.i(logTag, "Swipe Bottom to Top");
					mSwipeDetected = Action.BT;
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
}
