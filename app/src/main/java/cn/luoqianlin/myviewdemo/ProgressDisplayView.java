package cn.luoqianlin.myviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ProgressDisplayView extends View {
    private final String TAG="ProgressDisplyView";
    private List<StepInfo> stepInfos;
    private Paint textNodePaint =new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint linePaint;

    private int strokeColor;
    private int strokeWidth;
    private int solidColor;

    private int selectedSolidColor;

    private int sTextColor;
    private int sSelectedTextColor;
    private int sTextSize;

    private int textNodeColor;
    private CharSequence[]textNodes;
    private int textNodeSize;

    private int lineHeight;
    private int lineColor;
    private int selectedPos=-1;
    private  int clickedPadd;


    class Circel {
        int x;
        int y;
        int radius;
        String text;
        Paint solidPaint;
        Paint strokPaint;
        Paint textPaint;
        boolean selected=false;

        Circel(int strokeColor, int strokeWidth,
               int solidColor,int sTextSize,int sTextColor) {
            solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            solidPaint.setColor(solidColor);
            solidPaint.setStyle(Paint.Style.FILL);

            strokPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            strokPaint.setStyle(Paint.Style.STROKE);
            strokPaint.setColor(strokeColor);
            strokPaint.setStrokeWidth(strokeWidth);


            textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(sTextSize);
            textPaint.setColor(sTextColor);
        }

        int leftX() {
            return x - radius;
        }

        int rightX() {
            return x + radius;
        }

        int topY() {
            return y - radius;
        }

        int bottomY() {
            return y + radius;
        }

        void drawMySelf(Canvas canvas){
            int sw;
            if(selected){
                sw=0;
                solidPaint.setColor(selectedSolidColor);
                textPaint.setColor(sSelectedTextColor);
            }else{
                sw=strokeWidth;
                solidPaint.setColor(solidColor);
                strokPaint.setColor(strokeColor);
                textPaint.setColor(sTextColor);
            }

            canvas.drawCircle(x ,y,radius-sw,solidPaint);
            if(sw>0) {
                strokPaint.setStrokeWidth(sw);
                canvas.drawCircle(x, y, radius, strokPaint);
            }
            float textW=  textPaint.measureText(text);
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float textHeight = fontMetrics.descent;
//            Log.e(TAG,"==>te:"+textHeight);
            float textX=x-textW/2;
            float textY= y+textHeight;
            canvas.drawText(text,textX,textY,textPaint);
        }

        boolean contaion(float x, float y) {
            return Math.pow((this.x - x), 2) + Math.pow((this.y - y), 2) <= Math.pow((radius+clickedPadd),2);
        }
    }

    class Line {
        int startX;
        int startY;
        int endX;
        int endY;
        int color;
        int height;
        void drawMySelf(Canvas canvas,Paint paint){
            paint.setStrokeWidth(height);
            canvas.drawLine(startX,startY,endX,endY,paint);
        }
    }

    class Text{
        float x;
        float y;
        int color;
        String text;
        void drawMySelf(Canvas canvas,Paint paint){
            float textW = paint.measureText(text);
            float x1= x-textW/2;
            float fontHeight = paint.getFontMetrics().descent-paint.getFontMetrics().ascent;
            float y1= y+ fontHeight;
//            Log.e(TAG,"font height:"+fontHeight+" top:"+paint.getFontMetrics().top);
            canvas.drawText(text,x1,y1,paint);
        }
    }


    public ProgressDisplayView(Context context) {
        super(context);
        init(null, 0);
    }

    public ProgressDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ProgressDisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    float dip2px(float v){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,v,getResources().getDisplayMetrics());
    }
    float sp2px(float v){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,v,getResources().getDisplayMetrics());
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressDisplayView, defStyle, 0);
        strokeColor = a.getColor(R.styleable.ProgressDisplayView_strokeColor, 0xff8b8b8b);
        strokeWidth = a.getDimensionPixelSize(R.styleable.ProgressDisplayView_strokeWidth, (int) dip2px(1));
        solidColor = a.getColor(R.styleable.ProgressDisplayView_solidColor, 0xffffffff);
        selectedSolidColor = a.getColor(R.styleable.ProgressDisplayView_selectedSolidColor, 0xff1f94fe);
        sTextColor = a.getColor(R.styleable.ProgressDisplayView_sTextColor, 0xff4a4a4a);
        sSelectedTextColor=a.getColor(R.styleable.ProgressDisplayView_sSelectedTextColor,0xffffffff);
        sTextSize=a.getDimensionPixelSize(R.styleable.ProgressDisplayView_sTextSize, (int) sp2px(14));
        textNodeColor=a.getColor(R.styleable.ProgressDisplayView_textNodeColor,0xff434343);
        textNodes=a.getTextArray(R.styleable.ProgressDisplayView_textNodes);
        textNodeSize=a.getDimensionPixelSize(R.styleable.ProgressDisplayView_textNodeSize, (int) sp2px(14));
        lineHeight=a.getDimensionPixelSize(R.styleable.ProgressDisplayView_lineHeight, (int) dip2px(1));
        lineColor=a.getColor(R.styleable.ProgressDisplayView_lineColor,0xffb8b8b8);
        a.recycle();
        stepInfos = new ArrayList<>();
        for ( CharSequence s : textNodes) {
            StepInfo stepInfo = new StepInfo();
            stepInfo.text = s.toString();
            stepInfos.add(stepInfo);
        }
        textNodePaint.setColor(textNodeColor);
        textNodePaint.setTextSize(textNodeSize);



        linePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineHeight);
        clickedPadd= (int) dip2px(5);

    }


    class StepInfo{

        String text;
    }
    List<Circel>circels=new ArrayList<>();
    List<Line>lines=new ArrayList<>();
    List<Text> texts=new ArrayList<>();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        circels.clear();
        lines.clear();
        texts.clear();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int rr = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
                getContext().getResources().getDisplayMetrics());

        if (stepInfos.size() <= 1) {
            throw new IllegalArgumentException("step size must >0.");
        }
        StepInfo stepInfo1 = stepInfos.get(0);
        StepInfo stepInfo2 = stepInfos.get(stepInfos.size() - 1);

        float textWidth1 = textNodePaint.measureText(stepInfo1.text);
        float textWidth2 = textNodePaint.measureText(stepInfo2.text);

        int w1 = (int) (textWidth1 / 2);
        int w2 = (int) (textWidth2 / 2);
        int leftDelta = w1 > rr ? w1 - rr : 0;
        int rightDelta = w2 > rr ? w2 - rr : 0;

        int lineW = (contentWidth - leftDelta - rightDelta - stepInfos.size() * 2 * rr) / (stepInfos.size() - 1);

        int left=paddingLeft+leftDelta;
        int top=paddingTop;

        for (int pos=0;pos<stepInfos.size();pos++) {
            StepInfo stepInfo =stepInfos.get(pos);
            Circel preCircel=null;
            if(!circels.isEmpty()){
                preCircel = circels.get(circels.size() - 1);
                left=preCircel.rightX()+lineW;
                top=preCircel.topY();
            }
            Circel nextCircel = new Circel(strokeColor, strokeWidth,solidColor, sTextSize, sTextColor);
            nextCircel.text=String.valueOf(pos);
            nextCircel.radius = rr;
            nextCircel.x = left + rr;
            nextCircel.y = top + rr;
            nextCircel.selected=(pos==this.selectedPos);
            circels.add(nextCircel);

            Text text=new Text();
            text.color=textNodeColor;
            text.text=stepInfo.text;
            text.x=nextCircel.x;
            text.y=nextCircel.bottomY();

            texts.add(text);

            if(preCircel!=null){
                Line line=new Line();
                line.height=lineHeight;
                line.startX=preCircel.rightX();
                line.startY= preCircel.y;
                line.endX=nextCircel.leftX();
                line.endY=nextCircel.y;
                lines.add(line);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i= 0;i<lines.size();i++){
            Line line = lines.get(i);
            line.drawMySelf(canvas,linePaint);
        }
        for(int i=0;i<circels.size();i++){
            Circel circel=circels.get(i);
            circel.drawMySelf(canvas);
        }
        for(int i=0;i<texts.size();i++){
            Text text = texts.get(i);
            text.drawMySelf(canvas, textNodePaint);
        }
    }

    public interface OnItemChangeListener {
        public void onItemChanged(int oldPos, int newPos);
    }
    private OnItemChangeListener onItemChangeListener;

    public OnItemChangeListener getOnItemChangeListener() {
        return onItemChangeListener;
    }

    public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
        this.onItemChangeListener = onItemChangeListener;
    }

    public void setSelectedPos(int pos) {
        selectedPos = pos;
        if (!circels.isEmpty()) {
            for (int i = 0; i < circels.size(); i++) {
                Circel circel = circels.get(i);
                circel.selected = (i == selectedPos);
            }
        }
        invalidate();
    }

    int newPos=-1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled())return  super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG,"ACTION_DOWN");
                for (int pos = 0; pos < circels.size(); pos++) {
                    Circel circel = circels.get(pos);
                    boolean contaion = circel.contaion(x, y);
                    if (contaion && selectedPos != pos) {
                        playSoundEffect(SoundEffectConstants.CLICK);
                        circel.selected = true;
                        newPos=pos;
                        break;
                    }
                }
                invalidate();
                return  true;
            case MotionEvent.ACTION_UP:
                Log.d(TAG,"ACTION_UP");
                if(newPos!=-1){
                    int oldPos=selectedPos;
                    selectedPos=newPos;
                    newPos=-1;
                    if (oldPos != -1) {
                        circels.get(oldPos).selected = false;
                    }
                    if(onItemChangeListener!=null){
                        onItemChangeListener.onItemChanged(oldPos,selectedPos);
                    }
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG,"ACTION_CANCEL");
                if(newPos!=-1){
                    int preePos=newPos;
                    newPos=-1;
                    circels.get(preePos).selected = false;
                }
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);

    }

}
