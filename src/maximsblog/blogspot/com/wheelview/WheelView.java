package maximsblog.blogspot.com.wheelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class WheelView extends View implements OnTouchListener, Runnable {

	private int mFirstValue = 0;
	private int mExtNumberOfSector = 23;
	private int mTotalEnableDegree = 322;
	private int mExtAnglePerSector = mTotalEnableDegree / mExtNumberOfSector;

	private float mCurrentDegrees = 0;
	private float mExtTotalDegrees = 0;

	private float mExtTop = 0;

	private double mStartSessionAngle;
	private double mStartAngle;
	private Touchs mTouch;

	private Bitmap mExtRing;
	private Bitmap mStartButton;
	private Bitmap mExtRingTouch;
	private Bitmap mVisor;
	private int mRadiusExtRing;
	private int mRadiusStartButton;

	private Paint mBitmapPaint;
	private Paint mBitmapPaintRing;

	public final static long DEFAULT_LOOP_INTERVAL = 5; // 100 ms
	private Thread thread = new Thread(this);
	private float mEndTop;
	private boolean mClock;
	private boolean mClickVolume;
	private boolean mThreadStop = false;

	private enum AnimationViewState {
		extRing, enabling
	}

	private AnimationViewState animationViewState;

	private IRing mOnTouchRingListener;
	private boolean mEnable = true;

	private int mExtCurrentSector = 0;
	private int mEventPointerId = -1;
	private float mSigma;
	private Paint mBitmapPaintRingRadianLine;
	private Paint mBitmapPaintStart;
	private Paint mBitmapPaintStartText;
	private Paint mBitmapPaintTempText;
	private int mSelectedSector = 0;
	private Bitmap mStartButtonGreenTouch;
	private Bitmap mStartButtonGreen;
	private Bitmap mExtRingGrey;
	private Paint mBitmapPaintUnable;
	private Bitmap mStartButtonUnable;
	private int mNumberSimpleBars;

	public interface IRing {
		void onCurrentValueChanged(int currentValue);

		void onStartClick(int selectedValue);

		void onRotateChangeState();

	}

	public enum Touchs {
		nothing, startButton, extRing
	}

	public void setOnTouchRingListener(IRing onTouchRingListener) {
		this.mOnTouchRingListener = onTouchRingListener;
	}

	public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Wheel,
				defStyleAttr, 0);
		mEnable = a.getBoolean(R.styleable.Wheel_enable, true);
		mFirstValue = a.getInt(R.styleable.Wheel_start_value, 0);
		mExtNumberOfSector = a.getInt(R.styleable.Wheel_total_number_sectors, 23);
		mTotalEnableDegree = a.getInt(R.styleable.Wheel_total_enable_degree,
				322);
		mNumberSimpleBars = a.getInt(R.styleable.Wheel_number_simple_bars_interval, 7);
		a.recycle();
		init();
	}

	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Wheel);
		mEnable = a.getBoolean(R.styleable.Wheel_enable, true);
		mFirstValue = a.getInt(R.styleable.Wheel_start_value, 0);
		mExtNumberOfSector = a.getInt(R.styleable.Wheel_total_number_sectors, 23);
		mTotalEnableDegree = a.getInt(R.styleable.Wheel_total_enable_degree,
				322);
		mNumberSimpleBars = a.getInt(R.styleable.Wheel_number_simple_bars_interval, 7);
		a.recycle();
		init();
	}

	public WheelView(Context context) {
		super(context);
		mEnable = true;
		mFirstValue = 0;
		mExtNumberOfSector = 23;
		mTotalEnableDegree = 322;
		mNumberSimpleBars = 7;
		init();
	}

	private void init() {
		mExtAnglePerSector = mTotalEnableDegree / mExtNumberOfSector;
		this.setOnTouchListener(this);
		mTouch = Touchs.nothing;
		mBitmapPaint = new Paint();
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setFilterBitmap(true);
		mBitmapPaint.setDither(true);
		mBitmapPaint.setAlpha(255);

		mBitmapPaintUnable = new Paint();
		mBitmapPaintUnable.setAntiAlias(true);
		mBitmapPaintUnable.setFilterBitmap(true);
		mBitmapPaintUnable.setDither(true);

		mBitmapPaintRing = new Paint();
		mBitmapPaintRing.setAntiAlias(true);
		mBitmapPaintRing.setFilterBitmap(true);
		mBitmapPaintRing.setDither(true);
		mBitmapPaintRing.setAlpha(255);
		mBitmapPaintRing.setStyle(Style.FILL_AND_STROKE);
		mBitmapPaintRing.setColor(0xFFFF0000);

		mBitmapPaintRingRadianLine = new Paint();
		mBitmapPaintRingRadianLine.setStyle(Style.STROKE);
		mBitmapPaintRingRadianLine.setStrokeWidth(5);
		mBitmapPaintRingRadianLine.setAntiAlias(true);
		mBitmapPaintRingRadianLine.setStrokeCap(Cap.ROUND);
		mBitmapPaintRingRadianLine.setColor(0xFF0000FF);

		mBitmapPaintStart = new Paint();
		mBitmapPaintStart.setAntiAlias(true);
		mBitmapPaintStart.setFilterBitmap(true);
		mBitmapPaintStart.setDither(true);
		mBitmapPaintStart.setAlpha(255);
		mBitmapPaintStart.setTextSize(20);
		mBitmapPaintStart.setStyle(Style.FILL_AND_STROKE);
		mBitmapPaintStart.setColor(0xFFFFFFFF);

		mBitmapPaintStartText = new Paint();
		mBitmapPaintStartText.setAntiAlias(true);
		mBitmapPaintStartText.setAlpha(255);
		mBitmapPaintStartText.setTextSize(44);
		mBitmapPaintStartText.setStyle(Style.FILL_AND_STROKE);
		mBitmapPaintStartText.setColor(0xFFFFFFFF);

		mBitmapPaintTempText = new Paint();
		mBitmapPaintTempText.setAntiAlias(true);
		mBitmapPaintTempText.setFilterBitmap(true);
		mBitmapPaintTempText.setDither(true);
		mBitmapPaintTempText.setAlpha(255);
		mBitmapPaintTempText.setTextSize(30);
		mBitmapPaintTempText.setStyle(Style.FILL_AND_STROKE);
		mBitmapPaintTempText.setColor(Color.WHITE);
		mSigma = 1;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int size = 0;
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		if (width > height) {
			size = height;
		} else {
			size = width;
		}
		setMeasuredDimension(size, size);
	}

	private void drawRings(int w, int h) {
		mExtRing = Bitmap.createBitmap(w, h, Config.ARGB_4444);
		mExtRingGrey = Bitmap.createBitmap(w, h, Config.ARGB_4444);
		mRadiusExtRing = mExtRing.getWidth() / 2;
		Canvas c = new Canvas(mExtRing);
		Canvas c1 = new Canvas(mExtRingGrey);
		mBitmapPaintRing.setShader(new RadialGradient(w / 2, h / 2, w / 2 - h
				/ 16, 0xFFEA0707, 0xFFA30707, TileMode.CLAMP));
		mBitmapPaintUnable.setShader(new RadialGradient(w / 2, h / 2, w / 2 - h
				/ 16, 0xFF666666, 0xFFaaaaaa, TileMode.CLAMP));
		c.drawCircle(w / 2, h / 2, w / 2 - 16, mBitmapPaintRing);
		int a = mBitmapPaintUnable.getAlpha();
		mBitmapPaintUnable.setAlpha(255);
		c1.drawCircle(w / 2, h / 2, w / 2 - 16, mBitmapPaintUnable);
		mBitmapPaintUnable.setAlpha(a);
		mBitmapPaintRingRadianLine.setColor(0xAA000000);
		c.drawCircle(w / 2, h / 2,
				w / 2 - 16 - mBitmapPaintRingRadianLine.getStrokeWidth(),
				mBitmapPaintRingRadianLine);
		c1.drawCircle(w / 2, h / 2,
				w / 2 - 16 - mBitmapPaintRingRadianLine.getStrokeWidth(),
				mBitmapPaintRingRadianLine);
		c.save();
		c1.save();
		for (int i1 = 0; i1 <= mExtNumberOfSector; i1++) {

			c.drawLine(w / 2, 16 + mBitmapPaintRingRadianLine.getStrokeWidth(),
					w / 2, h / 12, mBitmapPaintRingRadianLine);
			c1.drawLine(w / 2,
					16 + mBitmapPaintRingRadianLine.getStrokeWidth(), w / 2,
					h / 12, mBitmapPaintRingRadianLine);
			if (i1 == mSelectedSector) {
				mBitmapPaintTempText.setColor(Color.BLACK);
			} else {
				mBitmapPaintTempText.setColor(Color.WHITE);
			}
			c.drawText(
					String.valueOf(mFirstValue + i1),
					w
							/ 2
							- mBitmapPaintTempText.measureText(String
									.valueOf(mFirstValue + i1)) / 2, h / 12
							+ mBitmapPaintRingRadianLine.getStrokeWidth()
							- mBitmapPaintTempText.getFontMetricsInt().ascent,
					mBitmapPaintTempText);
			c1.drawText(
					String.valueOf(mFirstValue + i1),
					w
							/ 2
							- mBitmapPaintTempText.measureText(String
									.valueOf(mFirstValue + i1)) / 2, h / 12
							+ mBitmapPaintRingRadianLine.getStrokeWidth()
							- mBitmapPaintTempText.getFontMetricsInt().ascent,
					mBitmapPaintTempText);
			c.drawLine(w / 2,
					h / 12 + mBitmapPaintRingRadianLine.getStrokeWidth()
							- mBitmapPaintTempText.getFontMetricsInt().ascent
							+ mBitmapPaintTempText.getFontMetricsInt().descent
							+ mBitmapPaintRingRadianLine.getStrokeWidth(),
					w / 2, h / 2, mBitmapPaintRingRadianLine);
			c1.drawLine(w / 2,
					h / 12 + mBitmapPaintRingRadianLine.getStrokeWidth()
							- mBitmapPaintTempText.getFontMetricsInt().ascent
							+ mBitmapPaintTempText.getFontMetricsInt().descent
							+ mBitmapPaintRingRadianLine.getStrokeWidth(),
					w / 2, h / 2, mBitmapPaintRingRadianLine);
			c.rotate(-mExtAnglePerSector, w / 2, h / 2);
			c1.rotate(-mExtAnglePerSector, w / 2, h / 2);
		}
		mBitmapPaintTempText.setColor(Color.WHITE);
		c.restore();
		c1.restore();
		if (mNumberSimpleBars > 0) {
			mBitmapPaintRingRadianLine.setStrokeWidth(2);
			for (int i1 = 0; i1 <= mExtNumberOfSector * mNumberSimpleBars; i1++) {

				c.drawLine(
						w / 2,
						16 + mBitmapPaintRingRadianLine.getStrokeWidth(),
						w / 2,
						h
								/ 12
								- 2
								* mBitmapPaintTempText.getFontMetricsInt().descent,
						mBitmapPaintRingRadianLine);
				c1.drawLine(
						w / 2,
						16 + mBitmapPaintRingRadianLine.getStrokeWidth(),
						w / 2,
						h
								/ 12
								- 2
								* mBitmapPaintTempText.getFontMetricsInt().descent,
						mBitmapPaintRingRadianLine);
				c.drawLine(
						w / 2,
						h
								/ 12
								+ mBitmapPaintRingRadianLine.getStrokeWidth()
								- mBitmapPaintTempText.getFontMetricsInt().ascent
								+ mBitmapPaintTempText.getFontMetricsInt().descent
								+ mBitmapPaintRingRadianLine.getStrokeWidth()
								+ 2
								* mBitmapPaintTempText.getFontMetricsInt().descent,
						w / 2, h / 2, mBitmapPaintRingRadianLine);
				c1.drawLine(
						w / 2,
						h
								/ 12
								+ mBitmapPaintRingRadianLine.getStrokeWidth()
								- mBitmapPaintTempText.getFontMetricsInt().ascent
								+ mBitmapPaintTempText.getFontMetricsInt().descent
								+ mBitmapPaintRingRadianLine.getStrokeWidth()
								+ 2
								* mBitmapPaintTempText.getFontMetricsInt().descent,
						w / 2, h / 2, mBitmapPaintRingRadianLine);
				c.rotate(-mExtAnglePerSector / mNumberSimpleBars, w / 2, h / 2);
				c1.rotate(-mExtAnglePerSector / mNumberSimpleBars, w / 2, h / 2);
			}
		}
		mBitmapPaintRingRadianLine.setStrokeWidth(5);

		final RectF r = new RectF(
				(float) (w / 2 - (mRadiusExtRing - h / 12 - (-mBitmapPaintTempText.getFontMetricsInt().ascent + mBitmapPaintTempText.getFontMetricsInt().descent) / 2)
						* Math.cos(360 - mExtAnglePerSector
								* mExtNumberOfSector)),
				(float) (h / 2 - (mRadiusExtRing - h / 12 - (-mBitmapPaintTempText
						.getFontMetricsInt().ascent) / 2)
						* Math.cos(360 - mExtAnglePerSector
								* mExtNumberOfSector)),
				(float) (w / 2 + (mRadiusExtRing - h / 12 - (-mBitmapPaintTempText
						.getFontMetricsInt().ascent) / 2)
						* Math.cos(360 - mExtAnglePerSector
								* mExtNumberOfSector)),
				(float) (h / 2 + (mRadiusExtRing - h / 12 - (-mBitmapPaintTempText
						.getFontMetricsInt().ascent) / 2)
						* Math.cos(360 - mExtAnglePerSector
								* mExtNumberOfSector)));
		c.save();
		c1.save();
		mExtRingTouch = mExtRing;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmapPaintTempText.setTextSize((h / 4 - h / 16 - h / 12) / 2);
		drawRings(w, h);
		mBitmapPaintUnable.setAlpha(mEnable ? 0 : 255);
		mBitmapPaintRingRadianLine.setColor(Color.BLACK);
		mStartButton = Bitmap.createBitmap(w / 2, h / 2, Config.ARGB_4444);
		Canvas c = new Canvas(mStartButton);

		mRadiusStartButton = mStartButton.getWidth() / 2;
		mBitmapPaintStart.setShader(new RadialGradient(w / 4, h / 4,
				mRadiusStartButton, 0xFFEA0707, 0xFFA30707, TileMode.CLAMP));
		c.drawCircle(w / 4, h / 4, w / 4, mBitmapPaintStart);
		mBitmapPaintRingRadianLine.setColor(0xAA000000);
		c.drawCircle(w / 4, h / 4,
				w / 4 - mBitmapPaintRingRadianLine.getStrokeWidth(),
				mBitmapPaintRingRadianLine);
		c.save();
		mStartButtonGreenTouch = Bitmap.createBitmap(w / 2, h / 2,
				Config.ARGB_4444);
		c = new Canvas(mStartButtonGreenTouch);
		mBitmapPaintStart.setShader(new RadialGradient(w / 4, h / 4,
				mRadiusStartButton, 0xFF449D44, 0xFF5CB85C, TileMode.CLAMP));
		c.drawCircle(w / 4, h / 4, w / 4, mBitmapPaintStart);
		c.drawCircle(w / 4, h / 4,
				w / 4 - mBitmapPaintRingRadianLine.getStrokeWidth(),
				mBitmapPaintRingRadianLine);
		c.save();
		mStartButtonGreen = Bitmap.createBitmap(w / 2, h / 2, Config.ARGB_4444);
		c = new Canvas(mStartButtonGreen);
		mBitmapPaintStart.setShader(new RadialGradient(w / 4, h / 4,
				mRadiusStartButton, 0xFF5CB85C, 0xFF449D44, TileMode.CLAMP));
		c.drawCircle(w / 4, h / 4, w / 4, mBitmapPaintStart);
		c.drawCircle(w / 4, h / 4,
				w / 4 - mBitmapPaintRingRadianLine.getStrokeWidth(),
				mBitmapPaintRingRadianLine);
		c.save();

		mStartButtonUnable = Bitmap
				.createBitmap(w / 2, h / 2, Config.ARGB_4444);
		c = new Canvas(mStartButtonUnable);

		mBitmapPaintStart.setShader(new RadialGradient(w / 4, h / 4,
				mRadiusStartButton, 0xFF666666, 0xFFaaaaaa, TileMode.CLAMP));
		c.drawCircle(w / 4, h / 4, w / 4, mBitmapPaintStart);
		c.drawCircle(w / 4, h / 4,
				w / 4 - mBitmapPaintRingRadianLine.getStrokeWidth(),
				mBitmapPaintRingRadianLine);
		c.save();

		mVisor = Bitmap.createBitmap(w, h, Config.ARGB_4444);
		c = new Canvas(mVisor);
		mBitmapPaintRingRadianLine.setColor(Color.BLACK);
		Path path = new Path();
		path.moveTo(w / 2 - 20, 0);
		path.lineTo(w / 2 + 20, 0);
		path.lineTo(w / 2 + 20,
				h / 12 - 2 * mBitmapPaintTempText.getFontMetricsInt().descent);
		path.lineTo(w / 2, h / 12);
		path.lineTo(w / 2 - 20,
				h / 12 - 2 * mBitmapPaintTempText.getFontMetricsInt().descent);
		path.lineTo(w / 2 - 20, 0);
		mBitmapPaintRingRadianLine.setShader(new LinearGradient(w / 2 - 20, 0,
				w / 2, 0, Color.BLACK, Color.GRAY, TileMode.MIRROR));
		mBitmapPaintRingRadianLine.setStyle(Style.FILL_AND_STROKE);
		c.drawPath(path, mBitmapPaintRingRadianLine);
		path.reset();
		path.moveTo(w / 2, h / 12 + mBitmapPaintRingRadianLine.getStrokeWidth()
				- mBitmapPaintTempText.getFontMetricsInt().ascent
				+ mBitmapPaintTempText.getFontMetricsInt().descent);
		path.lineTo(w / 2 + 20,
				h / 12 + mBitmapPaintRingRadianLine.getStrokeWidth()
						- mBitmapPaintTempText.getFontMetricsInt().ascent
						+ mBitmapPaintTempText.getFontMetricsInt().descent
						+ mBitmapPaintRingRadianLine.getStrokeWidth() + 2
						* mBitmapPaintTempText.getFontMetricsInt().descent);
		path.lineTo(w / 2 + 20, h / 2);
		path.lineTo(w / 2 - 20, h / 2);
		path.lineTo(w / 2 - 20,
				h / 12 + mBitmapPaintRingRadianLine.getStrokeWidth()
						- mBitmapPaintTempText.getFontMetricsInt().ascent
						+ mBitmapPaintTempText.getFontMetricsInt().descent
						+ mBitmapPaintRingRadianLine.getStrokeWidth() + 2
						* mBitmapPaintTempText.getFontMetricsInt().descent);
		path.lineTo(w / 2, h / 12 + mBitmapPaintRingRadianLine.getStrokeWidth()
				- mBitmapPaintTempText.getFontMetricsInt().ascent
				+ mBitmapPaintTempText.getFontMetricsInt().descent);
		mBitmapPaintRingRadianLine.setShader(new LinearGradient(w / 2 - 20, 0,
				w / 2, 0, Color.BLACK, Color.GRAY, TileMode.MIRROR));
		c.drawPath(path, mBitmapPaintRingRadianLine);
		mBitmapPaintRingRadianLine.setStyle(Style.STROKE);
		mBitmapPaintRingRadianLine.setShader(null);
		c.save();

	};

	private int getQuadrant(double x, double y) {
		if (x >= 0) {
			return y >= 0 ? 1 : 4;
		} else {
			return y >= 0 ? 2 : 3;
		}
	}

	public void setEnableRingWithAnim(boolean enable) {
		mEnable = enable;

		if (thread != null && thread.isAlive()) {
			thread.interrupt();
			if (animationViewState == AnimationViewState.extRing) {
				mExtTop = mEndTop % mTotalEnableDegree;
				mExtTotalDegrees = mExtTop;
			}
			invalidate();
		}
		animationViewState = AnimationViewState.enabling;
		thread = new Thread(this);
		thread.start();
	}

	public void setEnableRingWithoutAnim(boolean enable) {
		mEnable = enable;
		if (enable)
			mBitmapPaintUnable.setAlpha(0);
		else
			mBitmapPaintUnable.setAlpha(255);
		invalidate();
	}

	public void setExtCurrentState(int sector) {
		mEndTop = sector * mExtAnglePerSector;
		mExtTotalDegrees = mEndTop;
		mExtTop = mEndTop;
		mSelectedSector = sector;
		mExtCurrentSector = sector;
		int nn = Math.min(getWidth(), getHeight());
		int w = nn;
		int h = nn;
		if (w != 0)
			drawRings(w, h);
		invalidate();
	}

	public double getAngle(double xTouch, double yTouch) {
		double x = xTouch - (getWidth() / 2d);
		double y = getHeight() - yTouch - (getHeight() / 2d);

		switch (getQuadrant(x, y)) {
		case 1:
			return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
		case 2:
			return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
		case 3:
			return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
		case 4:
			return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
		default:
			return 0;
		}
	}

	public double getRadius(double xTouch, double yTouch) {
		return Math.sqrt((xTouch - getWidth() / 2) * (xTouch - getWidth() / 2)
				+ (yTouch - getHeight() / 2) * (yTouch - getHeight() / 2));
	}

	public Touchs getTouch(double radius) {
		if (radius < mRadiusStartButton) {
			return Touchs.startButton;
		} else if (radius < mRadiusExtRing) {
			return Touchs.extRing;
		} else
			return Touchs.nothing;
	}

	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getActionMasked()) {

		case MotionEvent.ACTION_DOWN:
			if (mEventPointerId == -1) {
				mEventPointerId = event.getPointerId(0);

				if (thread != null && thread.isAlive()) {
					thread.interrupt();
				}

				double radius = getRadius(event.getX(), event.getY());
				mTouch = getTouch(radius);
				if (!mEnable && (mTouch == Touchs.extRing)) {
					mTouch = Touchs.nothing;
					mCurrentDegrees = 0;
					invalidate();
					return true;
				}
				mCurrentDegrees = 0;
				mClickVolume = false;
				if (mTouch == Touchs.extRing)
					mStartAngle = mStartSessionAngle = getAngle(event.getX(),
							event.getY()) + mExtTop;
			}
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			if (!mEnable && (mTouch == Touchs.extRing)) {
				mTouch = Touchs.nothing;
				mCurrentDegrees = 0;
				invalidate();
				return true;
			}
			for (int i1 = 0; i1 < event.getPointerCount(); i1++) {
				if (event.getPointerId(i1) == mEventPointerId) {
					double currentAngle;
					currentAngle = getAngle(event.getX(), event.getY())
							+ mExtTop;
					double p = mExtTotalDegrees
							+ (mStartSessionAngle - currentAngle);
					p = p % 360;
					boolean stop = false;
					if (p < 0.0) {
						p = 0.0f;
						stop = true;
					}

					if (p > mTotalEnableDegree) {
						p = mTotalEnableDegree;
						stop = true;
					}
					if (stop) {
						mCurrentDegrees = 0;
						mStartSessionAngle = currentAngle;
						invalidate();
						return true;
					}

					mCurrentDegrees = (float) (mStartSessionAngle - currentAngle);

					if (!mClickVolume)
						mClickVolume = Math.abs(Math.abs(mStartAngle)
								- Math.abs(currentAngle)) > 10;
					invalidate();

					mStartSessionAngle = currentAngle;
					break;
				}
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			if (event.getPointerId(event.getActionIndex()) == mEventPointerId) {
				actionUp(event);
				mEventPointerId = -1;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (event.getPointerId(event.getActionIndex()) == mEventPointerId) {
				actionUp(event);
				mEventPointerId = -1;
			}
			break;
		}
		return true;
	}

	private void actionUp(MotionEvent event) {
		if (!mEnable) {
			mTouch = Touchs.nothing;
			mCurrentDegrees = 0;
			invalidate();
		}
		if (mTouch == Touchs.extRing) {
			animationViewState = animationViewState.extRing;
			mExtTotalDegrees %= 360;
			if (mExtTotalDegrees < 0) {
				mExtTotalDegrees = 360 + mExtTotalDegrees;
			}

			float endDegrees = mExtTotalDegrees;
			int currentSector = Math.round(endDegrees / getExtAnglePerSector());
			mEndTop = currentSector * getExtAnglePerSector();
			mExtTop = mExtTotalDegrees;
			if (Math.round(mExtTotalDegrees) % getExtAnglePerSector() == getExtAnglePerSector() / 2) {
				int i1 = 0;
				i1++;
			}
			if (mEndTop - mExtTotalDegrees > 0)
				mClock = true;
			else
				mClock = false;
			if (thread != null && thread.isAlive()) {
				thread.interrupt();
			}
			if (mEndTop != mExtTop) {
				thread = new Thread(this);
				thread.start();
			}
		} else if (mTouch == Touchs.startButton) {
			double r = getRadius(event.getX(), event.getY());
			if (r <= mRadiusStartButton) {
				
				drawRings(getWidth(), getHeight());
				if (mOnTouchRingListener != null && mEnable){
					if(mSelectedSector != mExtCurrentSector) {
						mSelectedSector = mExtCurrentSector;
						mOnTouchRingListener.onStartClick(mFirstValue + mExtCurrentSector);
					}
				} else
					mSelectedSector = mExtCurrentSector;
			}
		}
		mTouch = Touchs.nothing;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Bitmap startButton;
		Bitmap extRing;
		if (mTouch == Touchs.startButton) {
			extRing = mExtRing;
		} else if (mTouch == Touchs.extRing) {
			extRing = mExtRingTouch;
		} else {
			extRing = mExtRing;
		}

		if (mTouch == Touchs.extRing) {
			mExtTotalDegrees += mCurrentDegrees;
			if ((int) (mExtTotalDegrees % getExtAnglePerSector() - mExtNumberOfSector / 2) == 0) {
				if (mClickVolume && mOnTouchRingListener != null) {
					mClickVolume = false;
					mOnTouchRingListener.onRotateChangeState();
				}
			}
			float endDegrees = mExtTotalDegrees % 360;
			if (endDegrees < 0) {
				endDegrees = 360 + endDegrees;
			}
			if (endDegrees > mExtAnglePerSector * mExtNumberOfSector)
				endDegrees = mExtAnglePerSector * mExtNumberOfSector;
			int currentSector = Math.round(endDegrees / getExtAnglePerSector());
			if (currentSector > mExtNumberOfSector)
				currentSector = 0;
			
			if (mExtCurrentSector != currentSector
					&& mOnTouchRingListener != null) {
				mExtCurrentSector = currentSector;
				mOnTouchRingListener.onCurrentValueChanged(mFirstValue + currentSector);
			} else {
				mExtCurrentSector = currentSector;
			}
			drawRotateBitmap(canvas, mExtTotalDegrees, extRing,
					mBitmapPaintRing);
			drawRotateBitmap(canvas, mExtTotalDegrees, mExtRingGrey,
					mBitmapPaintUnable);
		} else {
			drawRotateBitmap(canvas, mExtTop, extRing, mBitmapPaintRing);
			drawRotateBitmap(canvas, mExtTop, mExtRingGrey, mBitmapPaintUnable);
		}
		canvas.drawBitmap(mVisor, (getWidth() - mVisor.getWidth()) / 2,
				(getHeight() - mVisor.getHeight()) / 2, mBitmapPaintRing);
		if (mEnable) {
			if ((mSelectedSector == mExtCurrentSector))
				startButton = mStartButton;
			else if (mTouch == Touchs.startButton) {
				startButton = mStartButtonGreenTouch;
			} else {
				startButton = mStartButtonGreen;
			}
		} else {
			startButton = mStartButtonUnable;
		}
		canvas.drawBitmap(startButton,
				(getWidth() - startButton.getWidth()) / 2,
				(getHeight() - startButton.getHeight()) / 2, mBitmapPaint);
		mBitmapPaintStartText.setTextSize((int) (mRadiusStartButton * Math
				.cos(Math.toRadians(45))));
		mBitmapPaintStartText.setColor(mEnable ? 0xFFFFFFFF : 0xFF000000);
		drawCenter(canvas, mBitmapPaintStartText,
				String.valueOf(mFirstValue + mExtCurrentSector));
	}

	private void drawCenter(Canvas canvas, Paint paint, String text) {
		int cHeight = canvas.getClipBounds().height();
		int cWidth = canvas.getClipBounds().width();
		Rect r = new Rect();
		paint.setTextAlign(Paint.Align.LEFT);
		paint.getTextBounds(text, 0, text.length(), r);
		float x = cWidth / 2f - r.width() / 2f - r.left;
		float y = cHeight / 2f + r.height() / 2f - r.bottom;
		canvas.drawText(text, x, y, paint);
	}

	private void drawRotateBitmap(Canvas canvas, float degree, Bitmap bitmap,
			Paint paint) {
		canvas.save();
		canvas.rotate(degree, getWidth() / 2, getHeight() / 2);
		canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2,
				(getHeight() - bitmap.getHeight()) / 2, paint);
		canvas.restore();
	}

	@Override
	public void run() {
		this.setOnTouchListener(null);
		mThreadStop = false;
		while (!Thread.interrupted()) {

			post(new Runnable() {
				public void run() {
					if (mThreadStop)
						return;
					float delta;
					float sigma;
					boolean end = false;
					if (mClock)
						sigma = mSigma;
					else
						sigma = -mSigma;
					switch (animationViewState) {
					case extRing:
						delta = Math.abs(mExtTop - mEndTop);
						if (delta > Math.abs(sigma)) {

						} else {
							boolean stop = false;
							for (int i = 1; i <= 16; i *= 2) {
								if (delta < Math.abs(sigma)) {
									sigma = mSigma / i;
								} else {
									stop = true;
									break;
								}
							}
							if (!stop)
								end = true;
						}

						if (end) {
							mThreadStop = true;
							thread.interrupt();
							// mExtTop = mEndTop % mTotalEnableDegree;
							mExtTotalDegrees = mExtTop;
							WheelView.this.setOnTouchListener(WheelView.this);
							return;
						}
						mExtTop += sigma;
						break;
					case enabling:
						if (!mEnable) {
							int currentAlpha = mBitmapPaintUnable.getAlpha();

							if (currentAlpha < 255) {
								currentAlpha += 1;
								mBitmapPaintUnable.setAlpha(currentAlpha);
							} else {
								mThreadStop = true;
								thread.interrupt();
							}
						} else {
							int currentAlpha = mBitmapPaintUnable.getAlpha();
							if (currentAlpha > 0) {
								currentAlpha -= 1;
								mBitmapPaintUnable.setAlpha(currentAlpha);
							} else {
								mThreadStop = true;
								thread.interrupt();
							}
						}
						break;
					default:
						break;
					}
					WheelView.this.postInvalidate();
				}
			});
			try {
				Thread.sleep(DEFAULT_LOOP_INTERVAL);
			} catch (InterruptedException e) {
				mThreadStop = true;
				mTouch = Touchs.nothing;
				WheelView.this.postInvalidate();
				this.setOnTouchListener(WheelView.this);
				break;
			}
		}
	}

	public int getExtAnglePerSector() {
		return mExtAnglePerSector;
	}

	public Touchs getTouchState() {
		return mTouch;
	}

	/*
	 * public void setEnabledTemp(boolean constTMP) { mConstTMP = constTMP;
	 * invalidate(); }
	 */
}
