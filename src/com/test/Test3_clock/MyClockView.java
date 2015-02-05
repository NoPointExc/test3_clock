package com.test.Test3_clock;

import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

/**
 * This is a analogy clock view with ticks
 * */
@RemoteView
public class MyClockView extends View {
	private Time calendar = null;
	private Drawable mHourHand = null;
	private Drawable mMinuteHand = null;
	private Drawable mDial = null;

	private int dialWidth;
	private int dialHeight;

	private boolean isAttached;

	private float mMinutes;
	private float mHour;

	private boolean mChanged;

	private static int SECONDS_FLAG = 0;
	private Message secondsMsg;
	private float mSeconds;

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				onTimeChanged();
				invalidate();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public MyClockView(Context context) {
		this(context, null);
	}

	public MyClockView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public MyClockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Resources r = context.getResources();

		if (mDial == null) {
			mDial = r.getDrawable(R.drawable.clock_dial);
		}

		if (mHourHand == null) {
			mHourHand = r.getDrawable(R.drawable.clock_hand);
		}

		if (mMinuteHand == null) {
			mMinuteHand = r.getDrawable(R.drawable.clock_hand);
		}

		calendar = new Time();

		dialWidth = mDial.getIntrinsicWidth();
		dialHeight = mDial.getIntrinsicHeight();

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (!isAttached) {
			isAttached = true;

		}

		// NOTE: It's safe to do these after registering the receiver since the
		// receiver always runs
		// in the main thread, therefore the receiver can't run before this
		// method returns.

		// The time zone may have changed while the receiver wasn't registered,
		// so update the Time
		calendar = new Time();

		// Make sure we update to the current time
		onTimeChanged();

		initSecondsThread();
	}

	private void initSecondsThread() {
		secondsMsg = mHandler.obtainMessage(SECONDS_FLAG);
		Thread newThread = new Thread() {
			@Override
			public void run() {
				while (isAttached) {
					secondsMsg = mHandler.obtainMessage(SECONDS_FLAG);
					// /end
					mHandler.sendMessage(secondsMsg);
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		};
		newThread.start();

	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (isAttached) {

			isAttached = false;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		float hScale = 1.0f;
		float vScale = 1.0f;

		if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < dialWidth) {
			hScale = (float) widthSize / (float) dialWidth;
		}

		if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < dialHeight) {
			vScale = (float) heightSize / (float) dialHeight;
		}

		float scale = Math.min(hScale, vScale);

		setMeasuredDimension(
				resolveSize((int) (dialWidth * scale), widthMeasureSpec),
				resolveSize((int) (dialHeight * scale), heightMeasureSpec));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mChanged = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		boolean changed = mChanged;
		if (changed) {
			mChanged = false;
		}

		int availableWidth = getWidth();
		int availableHeight = getHeight();

		int x = availableWidth / 2;
		int y = availableHeight / 2;

		final Drawable dial = mDial;
		int w = dial.getIntrinsicWidth();
		int h = dial.getIntrinsicHeight();

		boolean scaled = false;

		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			canvas.save();
			canvas.scale(scale, scale, x, y);
		}

		if (changed) {
			dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}
		dial.draw(canvas);

		canvas.save();
		canvas.rotate(mHour / 12.0f * 360.0f, x, y);

		final Drawable hourHand = mHourHand;
		if (changed) {
			w = hourHand.getIntrinsicWidth();
			h = hourHand.getIntrinsicHeight();
			hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		hourHand.draw(canvas);
		canvas.restore();

		canvas.save();
		canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);

		final Drawable minuteHand = mMinuteHand;
		if (changed) {
			w = minuteHand.getIntrinsicWidth();
			h = minuteHand.getIntrinsicHeight();
			minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		minuteHand.draw(canvas);
		canvas.restore();

		canvas.save();
		canvas.rotate(mSeconds / 60.0f * 360.0f, x, y);

		final Drawable secondHand = minuteHand;
		if (changed) {
			w = secondHand.getIntrinsicWidth();
			h = secondHand.getIntrinsicHeight();
			secondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		secondHand.draw(canvas);
		canvas.restore();

		if (scaled) {
			canvas.restore();
		}
	}

	private void onTimeChanged() {
		calendar.setToNow();

		int hour = calendar.hour;
		int minute = calendar.minute;
		int second = calendar.second;

		mSeconds = second;
		mMinutes = minute + second / 60.0f;
		mHour = hour + mMinutes / 60.0f;

		mChanged = true;
	}

	/**
	 * broadcast receiver for (1)
	 * (1).Intent.ACTION_TIME_TICK;(2).Intent.ACTION_TIME_CHANGE
	 * (3).Intent.ACTION_TIMEZONE_CHANGED
	 */

	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				String tz = intent.getStringExtra("time-zone");
				calendar = new Time(TimeZone.getTimeZone(tz).getID());
			}
			onTimeChanged();
			invalidate();
		}
	};

}
