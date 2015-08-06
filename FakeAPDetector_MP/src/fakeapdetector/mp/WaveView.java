package fakeapdetector.mp;

import java.util.ArrayList;
import java.util.List;

import fakeapdetector.mp.R.color;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

public class WaveView extends View
{
    private Resources myResources;
    
    private List<Double> var_set;
 
    // 画笔，定义绘制属性
    private Paint myPaint;
    private Paint mBitmapPaint;    

    // 绘制路径
    private Path myPath;

    // 画布及其底层位图
    private Canvas myCanvas;
    private Bitmap myBitmap;
    
    double startX,stopX,startY,stopY;

    private double mX, mY;
    private static final double TOUCH_TOLERANCE = 4;
    
    private double Boundary_up;
    private double Boundary_down;
    
    private double step_H;
    private double step_V;
    
    private int window = 120;
    
    // 记录宽度和高度
    private int mWidth;
    private int mHeight; 

    public WaveView(Context context)
    {
        super(context);
        initialize();
    }
    public WaveView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize();
    }
    public WaveView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }
    /**
     * 初始化工作
     */
    private void initialize()
    {
        // Get a reference to our resource table.
        myResources = getResources();
        // 绘制自由曲线用的画笔
        myPaint = new Paint();
        myPaint.setAntiAlias(true);
        myPaint.setDither(true);
        myPaint.setColor(Color.rgb(0,255,0));
        myPaint.setStyle(Paint.Style.STROKE);
       // myPaint.setAntiAlias(true);          //防锯齿
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPaint.setStrokeWidth(2); 
        myPaint.setTextSize(20);  
        
        myPath = new Path();
        
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        
        mWidth = this.getWidth();
        mHeight = this.getHeight();        
                
        Boundary_up = 0;
        Boundary_down = -100;
        step_H = (double)mWidth/window;
        step_V = (double)mHeight/(Boundary_up-Boundary_down); 
        
        var_set = new ArrayList<Double>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        
        mWidth = this.getWidth();
        mHeight = this.getHeight();  
        
        step_H = (double)mWidth/window;
        step_V = (double)mHeight/(Boundary_up-Boundary_down);        
        myBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas(myBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        // canvas.drawColor(Color.rgb(0,0,0));        // 背景颜色    
        canvas.drawBitmap(myBitmap, 0, 0, mBitmapPaint);   // 如果不调用这个方法，绘制结束后画布将清空
       // canvas.drawPath(myPath, myPaint);   // 绘制路径
    } 
    /**
     * 清除整个图像
     */
    public void clear()
    {
        // 清除方法1：重新生成位图
        // myBitmap = Bitmap
        // .createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        // myCanvas = new Canvas(myBitmap);
        // 清除方法2：将位图清除为白色
    	myBitmap.eraseColor(Color.rgb(0,0,0));
        // 两种清除方法都必须加上后面这两步：
        // 路径重置
        myPath.reset();
        // 刷新绘制
        invalidate();
    }
    
    public void DrawThreshold(double threshold)
    {
    	startX = 0;
    	stopX = mWidth;
    	startY = step_V*(Boundary_up-(double)threshold);
    	stopY = startY;
    	myPaint.setColor(Color.RED);
    	//***********************************************************************
    	if(threshold != 0)
    	{
    		myPath.moveTo((float)startX, (float)startY);
    		myPath.lineTo((float)stopX, (float)stopY);
    		myCanvas.drawPath(myPath, myPaint);   // 绘制路径
    		 myPaint.setTextSize(20);
    		myCanvas.drawText(String.valueOf(threshold), (float)startX, (float)startY, myPaint);
    		invalidate();
    	}    	
    }
    
    public void setBoundary(int up, int down)
    {
    	this.Boundary_up = up;
    	this.Boundary_down = down;
    }
    
    public void setWindow(int window)
    {
    	this.window = window;
    }
    
    public void DrawWave(double var)
    {
    	int workset_size = var_set.size();
    	if(workset_size<window)
    		var_set.add(var);
    	else
    	{
    		var_set.remove(0);
    		var_set.add(var);
    	}
    	double startOfWave_X = step_H * (window-workset_size);
    	double startOfWave_Y = (double) (step_V * (Boundary_up-var_set.get(0)));    	
    	double X,Y;
    	X = startOfWave_X;
    	//***********************************************************************
    	myPaint.setColor(Color.rgb(0,255,0));    	
    	myPath.reset();
    	myPath.moveTo((float)startOfWave_X, (float)startOfWave_Y);
    	//***********************************************************************
    	for(int i=1;i<workset_size;i++)
    	{
    		X = X + step_H;
    		Y = (double) (step_V * (Boundary_up-var_set.get(i)));
    		myPath.lineTo((float)X, (float)Y);
    	}  
    	myCanvas.drawPath(myPath, myPaint);   // 绘制路径
    	 myPaint.setTextSize(20);
    	myCanvas.drawText("-100", 10, mHeight-10, myPaint);
    	myCanvas.drawText("0", 10, 0+20, myPaint);
    	invalidate();
    }
    
    public void DrawWave(List<Double> data)
    {
    	int workset_size = data.size();
    	
    	double startOfWave_X = step_H * (window-workset_size);
    	double startOfWave_Y = (double) (step_V * (Boundary_up-data.get(0)));    	
    	double X,Y;
    	X = startOfWave_X;
    	//***********************************************************************   	
    	myPath.reset();
    	myPath.moveTo((float)startOfWave_X, (float)startOfWave_Y);
    	//***********************************************************************
    	for(int i=1;i<workset_size;i++)
    	{
    		X = X + step_H;
    		Y = (double) (step_V * (Boundary_up-data.get(i)));
    		myPath.lineTo((float)X, (float)Y);
    	}    	
    	myCanvas.drawPath(myPath, myPaint);   // 绘制路径
    	myPaint.setTextSize(20);
    	myCanvas.drawText("-100", 10, mHeight-10, myPaint);
    	myCanvas.drawText("0", 10, 0+20, myPaint);
    	invalidate();
    }
    
    public void DrawAxis()
    {
    	myPaint.setTextSize(20);
    	setSolidPaint();
    	setPaintColor(0,255,0);
    	myPaint.setPathEffect(null);
    	myCanvas.drawText("AP1-RSSI:", mWidth-300, 25, myPaint);
    	setSolidPaint();
    	myCanvas.drawLine(mWidth-150, 25, mWidth-10, 25, myPaint);
    	myPaint.setPathEffect(null);
    	myCanvas.drawText("AP1-VAR:", mWidth-300, 50, myPaint);
    	setDashPaint();
    	myCanvas.drawLine(mWidth-150, 50, mWidth-10, 50, myPaint);
    	setPaintColor(255,0,0);
    	myPaint.setPathEffect(null);
    	myCanvas.drawText("AP2-RSSI:", mWidth-300, 75, myPaint);
    	setSolidPaint();
    	myCanvas.drawLine(mWidth-150, 75, mWidth-10, 75, myPaint);
    	myPaint.setPathEffect(null);
    	myCanvas.drawText("AP2-VAR:", mWidth-300, 100, myPaint);
    	setDashPaint();
    	myCanvas.drawLine(mWidth-150, 100, mWidth-10, 100, myPaint);
    	invalidate();
    }
    
    public void clear_data()
    {
    	var_set.clear();
    }
    
    public void setDashPaint()
    {
    	PathEffect effects = new DashPathEffect(new float[] { 1, 2, 4, 8}, 1);  
    	myPaint.setPathEffect(effects); 
    }
    
    public void setSolidPaint()
    {
    	PathEffect effects = new CornerPathEffect(10); 
    	myPaint.setPathEffect(effects); 
    }
    
    public void setPaintColor(int R, int G, int B)
    {
    	myPaint.setColor(Color.rgb(R, G, B));; 
    }
}