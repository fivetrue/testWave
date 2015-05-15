package com.example.testwave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by kwonojin on 15. 4. 22..
 */
public class WaveProgress extends View{

    public static final String TAG = "WaveProgress";
    private static final int INVALID_VALUE = -1;

    private static final int[] COLOR_RESOURCE_LIST = {
            android.R.color.holo_blue_dark,
            android.R.color.holo_blue_light,
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_green_dark,
    };
    private static final int VIEW_INVALIDATE = 0x01;

    private float mWaveStartPoint = INVALID_VALUE;
    private float mWaveValue = 0.33f;
    private float mWaveGap = 0.22f;
    private float mWaveWidthValue = 0.08f;
    private float mOffset = INVALID_VALUE;

    private WaveBar[] mWaveBars = new WaveBar[5];

    private boolean isLoading = true;


    public WaveProgress(Context context) {
        super(context);
    }

    public WaveProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initData(float width, float height){
        mWaveStartPoint = height / 2;
        float minHeight = mWaveStartPoint + (height * 0.1f);
        float maxHeight = height * 0.9f;

        for(int i = 0 ; i < mWaveBars.length ; i ++){
            int waveNumber = i + 1;
            float waveHeightOffset = 0;
            if(waveNumber > Math.round(mWaveBars.length / 2)){
                waveHeightOffset = mWaveValue * (mWaveBars.length - i);
                if(waveNumber == mWaveBars.length){
                    waveHeightOffset /= 4;
                }else if(waveNumber == mWaveBars.length - 1){
                    waveHeightOffset /= 1.4;
                }
            }else{
                waveHeightOffset = mWaveValue * waveNumber;
                if(waveNumber == 1){
                    waveHeightOffset /= 4;
                }else if(waveNumber == 2){
                    waveHeightOffset /= 1.4;
                }
            }

            float waveHeight = mWaveStartPoint * waveHeightOffset;
            float left = width * i * mWaveGap;
            float right =  width * ((i * mWaveGap) + mWaveWidthValue);
            mWaveBars[i] = new WaveBar(left,
                    mWaveStartPoint - waveHeight,
                    right,
                    mWaveStartPoint + waveHeight, maxHeight, minHeight);
            mWaveBars[i].setWaveNumber(waveNumber);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(getResources().getColor(COLOR_RESOURCE_LIST[i]));
            mWaveBars[i].setPaint(paint);
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initData(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWaveBars(canvas);
    }

    private void drawWaveBars(Canvas canvas){
        if(canvas != null && mWaveBars != null && mWaveBars.length > 0){
            boolean isRequest = false;
            //first wavebar
            if(isPressed() && !isLoading){
                if(mOffset <= 1 && mOffset >= 0){
                    final int count = Math.round(mWaveBars.length * mOffset);
                    for(int i = 0 ; i < count ; i++){
                        if(i < mWaveBars.length){
                            mWaveBars[i].drawBarByOffset(canvas, null, mOffset);
                        }
                    }
                    isRequest = true;
                }
            }else{
                if(isLoading){
                    for(WaveBar wave : mWaveBars){
                        if(wave != null){
                            wave.drawBar(canvas);
                        }
                    }
                    isRequest = true;
                }else{
                    for(WaveBar wave : mWaveBars){
                        if(wave != null){
                            boolean b = wave.goneBar(canvas);
                            if(b){
                                isRequest = b;
                            }
                        }
                    }
                }
            }
            if(isRequest){
                requestInvalidate();
            }
        }
    }

//    public long[] parseColor(int color){
//        String primaryColor = Integer.toHexString(color);
//        long[] result = new long[primaryColor.length()/2];
//
//        for(int i=primaryColor.length(); i>=2 ; i= i-2 ){
//            // blue, green, red, alpha 순으로 배열 3번째 부터 삽입
//            String argb = primaryColor.substring(i-2, i);
//
//            result[(i/2)-1] = Long.valueOf(argb, 16);
//        }
//        return result;
//    }

//    public int changeColor(final long[] arrColorFrom, final long[] arrColorTo, final float value){
//        if(arrColorFrom == null || arrColorTo == null
//                || arrColorFrom.length <= 0 || arrColorTo.length <= 0 )
//            return 0xFFFFFFFF;
//
//        int color[] = new int[arrColorFrom.length];
//        for(int i = 0 ; i < arrColorFrom.length ; i ++){
//
//            if(arrColorFrom[i] > arrColorTo[i]){
//                float val = arrColorFrom[i] - value;
//
//                if(arrColorTo[i] <= val){
//                    color[i] = (int)val;
//                }
//            }else if(arrColorFrom[i] < arrColorTo[i]){
//                float val = arrColorFrom[i] + value;
//
//                if(arrColorTo[i] >= val){
//                    color[i] = (int)val;
//                }
//            }
//
//        }
//        String backround = new String();
//        backround += Integer.toHexString(0xFF);
//        for(int i = 0 ; i < color.length ; i++){
//            long val = color[i];
//            long to = 0x10;
//            if(val > 0x0F && val <= 0xFF){
//                to = val;
//            }else {
//                to = arrColorTo[i];
//            }
//            backround += Long.toHexString(to > 0x0F ? to : 0x10);
//        }
//        long setColor = Long.valueOf(backround, 16);
//
//        return (int)setColor;
//    }

    @Override
    public void setPressed(boolean pressed) {
        // TODO Auto-generated method stub
        super.setPressed(pressed);
        isLoading = !pressed;
        invalidate();
    }

    public void setPressedOffset(float offset){
        if(offset < 0){
            mOffset = 0;
        }else if(offset > 1){
            mOffset = 1;
        }else{
            mOffset = offset;
        }
        invalidate();
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        invalidate();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        isLoading = visibility == View.VISIBLE;

        if(!isLoading && mWaveBars != null){
            for(WaveBar wave : mWaveBars){
                if(wave != null){
                    wave.initValue();
                }
            }
        }
    }

    public void initWaves(){
        if(mWaveBars != null){
            for(WaveBar wave : mWaveBars){
                if(wave != null){
                    wave.initValue();
                }
            }
        }
    }

    private void requestInvalidate(){
        Message msg = Message.obtain();
        msg.what = VIEW_INVALIDATE;
        msg.obj = WaveProgress.this;
        mUIHander.sendMessageDelayed(msg, 1000000 * (long)mWaveValue);
    }

    private static class WaveBar{
        private static final float MOVE_VALUE = 0.02f;
        public float MAX_HEIGHT = 0 ;
        public float MIN_HEIGHT = 0;
        private float startTop = INVALID_VALUE;
        private float startLeft = INVALID_VALUE;
        private float startRight = INVALID_VALUE;
        private float startBottom = INVALID_VALUE;
        private float top = 0;
        private float left = 0;
        private float right = 0;
        private float bottom = 0;
        private int waveNumber = INVALID_VALUE;
        private Object Tag = null;
        private Paint paint = null;
        private boolean isIncrease = true;
        private Path mPath = null;

        public WaveBar(float left, float top, float right, float bottom, float maxHeight, float minHeight){
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            startLeft = left;
            startTop = top;
            startRight = right;
            startBottom = bottom;

            MAX_HEIGHT = maxHeight;
            MIN_HEIGHT = minHeight;
        }

        public void setLeft(float left){
            this.left = left;
        }
        public void setRight(float right){
            this.right = right;
        }

        public void setTop(float top){
            this.top = top;
        }

        public void setBottom(float bottom){
            this.bottom = bottom;
        }

        public float getTop() {
            return top;
        }

        public float getLeft() {
            return left;
        }

        public float getRight() {
            return right;
        }

        public float getBottom() {
            return bottom;
        }

        public int getWaveNumber() {
            return waveNumber;
        }

        public void setWaveNumber(int waveNumber) {
            this.waveNumber = waveNumber;
        }

        public Object getTag() {
            return Tag;
        }

        public void setTag(Object tag) {
            Tag = tag;
        }

        public Paint getPaint() {
            return paint;
        }

        public void setPaint(Paint paint) {
            this.paint = paint;
        }

        public Path addPath(Path path){
            return addPath(path, true);
        }

        public Path addPath(Path path, boolean addCurv){
            if(mPath == null){
                mPath = new Path();
            }
            mPath.reset();
            if(path != null){
                mPath.addPath(path);
            }
            float curvValue = MAX_HEIGHT * 0.08f;
            mPath.moveTo(left, top);
            mPath.lineTo(left, bottom);
            if(addCurv){
                mPath.cubicTo(left, bottom, (right + left) / 2 , bottom + curvValue, right, bottom);
            }else{
                mPath.lineTo(right, bottom);
            }
            mPath.lineTo(right, bottom);
            mPath.lineTo(right, top);
            if(addCurv){
                mPath.cubicTo(right, top, (right + left) / 2 , top - curvValue, left, top);
            }else{
                mPath.lineTo(left, top);
            }
            mPath.close();
            return mPath;
        }

        public Path getPath(){
            return addPath(null);
        }

        public Path getPath(boolean addCurv){
            return addPath(null, addCurv);
        }

        public void drawBar(Canvas canvas, Paint paint){
            if(canvas != null){
                canvas.drawPath(getPath(), paint == null ? this.paint : paint);
                moveValue();
            }
        }

        public void drawBarByOffset(Canvas canvas, Paint paint, float offset){
            if(canvas != null){
                canvas.drawPath(getPath(), paint == null ? this.paint : paint);
                moveValue(offset);
            }
        }

        public void drawBar(Canvas canvas){
            drawBar(canvas, null);
        }

        public boolean goneBar(Canvas canvas){
            return goneBar(canvas, null);
        }

        public boolean goneBar(Canvas canvas, Paint paint){
            boolean b = false;
            if(canvas != null){
                canvas.drawPath(getPath(), paint == null ? this.paint : paint);
                b = discreaseValueTo(canvas.getHeight() / 2);
            }
            return b;
        }

        public boolean showBar(Canvas canvas){
            return showBar(canvas, null);
        }

        public boolean showBar(Canvas canvas, Paint paint){
            boolean b = false;
            if(canvas != null){
                canvas.drawPath(getPath(), paint == null ? this.paint : paint);
                b = increaseValueTo(startBottom);
            }
            return b;
        }

        public boolean increaseValueTo(float targetValue){
            if(targetValue > bottom){
                bottom += MAX_HEIGHT * MOVE_VALUE;
                top -= MAX_HEIGHT * MOVE_VALUE;
                return true;
            }
            return false;
        }

        public boolean discreaseValueTo(float targetValue){
            if(targetValue < bottom){
                top += MAX_HEIGHT * MOVE_VALUE;
                bottom -= MAX_HEIGHT * MOVE_VALUE;
                return true;
            }
            return false;
        }

        public void initValue(){
            left = startLeft;
            right = startRight;
            top = startTop;
            bottom = startBottom;
        }

        public void moveValue(){
            if(isIncrease){
                if(bottom < MAX_HEIGHT){
                    if(bottom > (MAX_HEIGHT * 0.9f)){
                        top -= MAX_HEIGHT * (MOVE_VALUE / 3);
                        bottom += MAX_HEIGHT * (MOVE_VALUE / 3);
                    }else{
                        top -= MAX_HEIGHT * MOVE_VALUE;
                        bottom += MAX_HEIGHT * MOVE_VALUE;
                    }
                }else{
                    isIncrease = false;
                }
            }else{
                if(bottom > MIN_HEIGHT){
                    if(bottom < MIN_HEIGHT + (MIN_HEIGHT * 0.1f)){
                        top += MAX_HEIGHT * (MOVE_VALUE / 3);
                        bottom -= MAX_HEIGHT * (MOVE_VALUE / 3);
                    }else{
                        top += MAX_HEIGHT * MOVE_VALUE;
                        bottom -= MAX_HEIGHT * MOVE_VALUE;
                    }
                }else{
                    isIncrease = true;
                }
            }
        }

        public void moveValue(float offset){
            top = startTop * offset;
            bottom = startBottom * offset;
        }

        @Override
        public String toString() {
            return "WaveBar [top=" + top + ", left=" + left + ", right="
                    + right + ", bottom=" + bottom + ", waveNumber="
                    + waveNumber + ", Tag=" + Tag + "]";
        }

    }

    private static Handler mUIHander = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            if(msg != null){
                switch(msg.what){
                    case VIEW_INVALIDATE :
                        if(msg.obj != null && msg.obj instanceof View){
                            ((View)msg.obj).invalidate();
                        }
                        break;
                }
            }
        }

    };
}
