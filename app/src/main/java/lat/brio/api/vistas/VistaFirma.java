package lat.brio.api.vistas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import net.hova_it.barared.brio.R;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Delgadillo on 17/05/17.
 */

public class VistaFirma extends View {

    private int mCanvasColor = Color.TRANSPARENT;
    private int mStrokeColor = Color.BLACK;
    private int mStrokeWidth = 2;
    private static final float TOUCH_TOLERANCE = 4;
    private final Paint mPaint;
    private SerializablePath mPath;
    private float mX, mY;
    private final String bundleInstanceState = "instanceState";
    private final String bundleCanvasColor = "canvasColor";
    private final String bundleStrokeColor = "strokeColor";
    private final String bundleStrokeWidth = "strokeWidth";
    private final String bundlePath = "path";
    GestureDetector gestureDetector;

    private Bitmap _Firma;

    public VistaFirma(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mStrokeColor);
        mPaint.setAlpha(0xFF);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mStrokeWidth);

        mPath = new SerializablePath();

        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public void setBackgroundColor(int color) {
        mCanvasColor = color;
    }



    public void setPenSize(int size) {
        mStrokeWidth = size;
        mPaint.setStrokeWidth(mStrokeWidth);
    }


    public boolean Vacio() {
        return mPath.isEmpty();
    }

    //Obtiene o establece la firma
    public Bitmap Firma(Bitmap Firma){
        _Firma = Firma;
        if (Firma != null) {
            // Aplicar transparencia - Reemplazando pixeles blancos
            _Firma = Firma;

            int[] pixeles = new int [Firma.getWidth()*Firma.getHeight()];

            _Firma.getPixels(pixeles, 0, Firma.getWidth(), 0, 0, Firma.getWidth(), Firma.getHeight());

            for(int a = 0;a< pixeles.length; a++){
                if(pixeles[a] == Color.WHITE){
                    pixeles[a] = Color.TRANSPARENT;
                }
            }

            _Firma.setPixels(pixeles, 0, _Firma.getWidth(), 0, 0, _Firma.getWidth(), _Firma.getHeight());
            _Firma = Firma;

            // Es para redibujar
            invalidate();
            return Firma;
        }else{
            int width = getWidth();
            int height = getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            canvas.drawColor(Color.WHITE);
            mPaint.setColor(Color.BLACK);
            canvas.drawPath(mPath, mPaint);

            if (mPath.isEmpty()) {
                mPaint.setColor(mStrokeColor);
                return null;
            }

            return bitmap;
        }


    }

    public Bitmap Firma() {
        return Firma(null);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(bundleInstanceState, super.onSaveInstanceState());
        bundle.putInt(bundleCanvasColor, mCanvasColor);
        bundle.putInt(bundleStrokeColor, mStrokeColor);
        bundle.putInt(bundleStrokeWidth, mStrokeWidth);
        bundle.putSerializable(bundlePath, mPath);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCanvasColor = bundle.getInt(bundleCanvasColor);
            mStrokeColor = bundle.getInt(bundleStrokeColor);
            mStrokeWidth = bundle.getInt(bundleStrokeWidth);
            mPath = (SerializablePath) bundle.getSerializable(bundlePath);
            super.onRestoreInstanceState(bundle.getParcelable(bundleInstanceState));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // FONDO - Area de firma
        Bitmap areafondo = BitmapFactory.decodeResource(getResources(), R.drawable.areafirma );
        canvas.drawBitmap(areafondo, 0, 0, mPaint);

        // Se estableció la firma
        if(_Firma!=null){
            canvas.drawColor(mCanvasColor);
            String fileName = Environment.getExternalStorageDirectory() + "/_firma.png";
            Bitmap areafirma = BitmapFactory.decodeFile(fileName);
            canvas.drawBitmap(areafirma, 0, 0, mPaint);
            return;
        }


        //TextPaint textPaint = new TextPaint();

        //textPaint.setColor(Color.MAGENTA);
        //    textPaint.setTextSize(14);
        //textPaint.set
        //canvas.save();
        //canvas.rotate(-35.0f, 10, 10);


        //for(int a = 0;a<=2;a++){


        //}


        // canvas.drawPicture((Picture)res.getDrawable(R.drawable.app_ic_brio_logo2, null));
        // Drawable myImage = res.getDrawable(R.drawable.my_image);

        // canvas.drawText("Brío",50, 50, textPaint);
        // canvas.restore();

        //int xPos = (canvas.getWidth() / 2);
        //int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;



        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
        // canvas.rotate(45.0f);
        //canvas.drawText("Brío", xPos, yPos, textPaint);




        // Deshabilitar scroll al dibujar
        getParent().requestDisallowInterceptTouchEvent(true);
        canvas.drawColor(mCanvasColor);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // nothing
                break;
        }
        return true;
    }

    private void touchStart(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        if (mOnPathChangedListener != null) {
            mOnPathChangedListener.onPathChanged(mPath);
        }
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mPath.addPathPoints(new float[]{
                    mX, mY, (x + mX) / 2, (y + mY) / 2
            });
            mX = x;
            mY = y;
        }
    }

    public static class SerializablePath extends Path implements Serializable {

        private static final long serialVersionUID = -4914599691577104935L;

        private final ArrayList<float[]> pathPoints;

        public SerializablePath() {
            super();
            pathPoints = new ArrayList<float[]>();
        }

        public SerializablePath(SerializablePath p) {
            super(p);
            pathPoints = p.pathPoints;
        }

        public void addPathPoints(float[] points) {
            this.pathPoints.add(points);
        }

        public void clearPathPoints() {
            this.pathPoints.clear();
        }

    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mOnPathChangedListener != null) {
                mOnPathChangedListener.onPathChanged(mPath);
            }
            return true;
        }
    }

    public void Limpiar(){
        mPath.reset();
        mPath.clearPathPoints();
        invalidate();
    }

    onPathChangedListener mOnPathChangedListener;

    public void setOnPathChangedListener(onPathChangedListener listener) {
        mOnPathChangedListener = listener;
    }

    public void setOnPathChangedListener() {
    }

    public interface onPathChangedListener {
        void onPathChanged (Path p);
    }

}


