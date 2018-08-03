package com.ct.ctmagnifier;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * Created by ChenTao on 2018/8/1.
 * 还可以写一个自定义的imageview，用于带放大镜功能的imageview，可以直接根据源bm进行放大功能，放大效果会更加清晰
 */

public class MagnifierView extends View {
    private float  locationX = 0,locationY=0;//记录当前的原始位置。
    private float  downX = 0,downY=0;//记录开始按下的位置。
    private Bitmap bm;//获得当前activity的截图或需要放大的图片
    private Activity activity;
    private ViewTreeObserver viewTreeObserver = null;
    private BitmapShader bitmapShader;
    private ViewGroup rootVg;
    //属性配置
    private int initLeft,initTop;//初始位置，相对于父控件的位置
    private int viewW,viewH;//控件宽高
    private float scaleX,scaleY;//x,y的放大倍数
    private String magnifierColor;//放大镜颜色
    private int magnifierAlpha;//放大镜透明度
    private float magnifierLen;//放大镜正方形边长

    public MagnifierView(Builder builder,Context context) {//对象初始化一次就行了
        super(context);
        activity = (Activity) context;
        this.rootVg = builder.rootVg;
        if(rootVg==null)
            rootVg = (ViewGroup) (activity.findViewById(android.R.id.content));
        this.viewH = builder.viewH;
        this.viewW = builder.viewW;
        this.scaleX = builder.scaleX;
        this.scaleY = builder.scaleY;
        this.magnifierColor = builder.magnifierColor;
        this.magnifierAlpha = builder.magnifierAlpha;
        this.initLeft = builder.initLeft;
        this.initTop = builder.initTop;

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(viewW,viewH);
        this.setLayoutParams(lp);

        magnifierLen = viewH>viewW?viewW:viewH;
    }

    public MagnifierView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MagnifierView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 把当前view设置到根布局中
     */
    public void startViewToRoot()
    {
        if(activity!=null && rootVg!=null && this.getParent()==null)//getParent()防止当前控件没有加入别的父
        {
            rootVg.addView(this);
            //view加载完成调用,防止直接在activity create方法里调用无法使用,因为create里，绘制还没有完成
            viewTreeObserver = rootVg.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(bm==null) {
                        //初始化控件位置
                        MagnifierView.this.setX(initLeft);
                        MagnifierView.this.setY(initTop);
                        bm = getScreenBm(rootVg);//获得指定布局的截图
                        bitmapShader = new BitmapShader(bm, Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);//利用BitmapShader画圆,模式可以查询用法
                        invalidate();
                    }
                }
            });
        }
    }

    /**
     * 移除放大镜
     */
    public void closeViewToRoot()
    {
        if(rootVg!=null && this.getParent()!=null)
        {
            rootVg.removeView(this);
        }
    }

    /**
     * 放大镜位置复位
     */
    public void resetXY()
    {
        this.setX(initLeft);
        this.setY(initTop);
        invalidate();
    }

    /**
     * 创建建造者，用于构建当前对象。多用于复杂构建
     */
    public static class Builder
    {
        private Context context;
        private int initLeft=0,initTop=0;//初始位置，相对于父控件的位置
        private int viewW=300,viewH=300;//控件宽高
        private float scaleX=1.5f,scaleY=1.5f;//x,y的放大倍数
        private String magnifierColor = "#ff0000";//放大镜颜色
        private int magnifierAlpha = 32;//放大镜透明度

        private ViewGroup rootVg;

        public Builder(Context context) {
            this.context = context;
        }
        public Builder intiLT(int initLeft,int initTop)
        {
            if(initLeft>0)
                this.initLeft = initLeft;
            if(initTop>0)
                this.initTop = initTop;
            return this;
        }
        public Builder viewWH(int viewW,int viewH)
        {
            this.viewW = viewW;
            this.viewH = viewH;
            return this;
        }
        public Builder rootVg(ViewGroup rootVg)
        {
            this.rootVg = rootVg;
            return this;
        }
        public Builder scale(float scale)//放大镜放大倍数
        {
            this.scaleX = scale;
            this.scaleY = scale;
            return this;
        }
        public Builder color(String color)
        {
            this.magnifierColor = color;
            return this;
        }
        public Builder alpha(int alpha)
        {
            if(alpha>=200)
            {
                this.magnifierAlpha = 200;
            }
            else if(alpha<0)
            {
                this.magnifierAlpha = 0;
            }
            else
            {
                this.magnifierAlpha = alpha;
            }
            return this;
        }
        public MagnifierView build()
        {
            return new MagnifierView(this,context);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK )
        {
            case MotionEvent.ACTION_DOWN:
                locationX = getX();
                locationY = getY();
                downX = event.getRawX();
                downY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE://随手移动，getRawX()与getX()有区别
                setX(locationX+(event.getRawX()-downX));
                setY(locationY+(event.getRawY()-downY));
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {//一般viewgroup中用于确定子view的位置
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(bm!=null)
        {
            Paint paintBg = new Paint();//背景防止加载自带透明的图片时，放大图片后面能看到原来的图片
            paintBg.setAntiAlias(true);//抗锯齿
            paintBg.setColor(Color.parseColor("#ffffff"));
            canvas.drawCircle(magnifierLen/2,magnifierLen/2,magnifierLen/2,paintBg);

            Paint paint = new Paint();
            paint.setAntiAlias(true);//抗锯齿
            paint.setShader(bitmapShader);//bitmapShader画圆形图片

            //创建矩阵，缩放平移图片
            Matrix matrix = new Matrix();
            matrix.setScale(scaleX, scaleY);
            matrix.postTranslate(-(scaleX*getX()+(scaleX-1)*magnifierLen/2), -(scaleY*getY()+(scaleY-1)*magnifierLen/2));//为了放大效果是取放大镜中心开始放大的效果
            bitmapShader.setLocalMatrix(matrix);//利用bitmapShader画圆形图片
            canvas.drawCircle(magnifierLen/2,magnifierLen/2,magnifierLen/2,paint);

            Paint paintShade = new Paint();//外层遮罩
            paintShade.setAntiAlias(true);//抗锯齿
            paintShade.setColor(Color.parseColor(magnifierColor));//设置边框
            paintShade.setAlpha(magnifierAlpha);
            canvas.drawCircle(magnifierLen/2,magnifierLen/2,magnifierLen/2,paintShade);
        }
    }

    private Bitmap getScreenBm(View contentView)
    {
        Bitmap bm;
        contentView.setDrawingCacheEnabled(true);
        contentView.buildDrawingCache();
        bm = contentView.getDrawingCache();//指向的当前view的显示对象的缓存bm，如果view里控件增加等改变，bm相应改变
        //contentView.setDrawingCacheEnabled(false);
        //contentView.destroyDrawingCache();//释放缓存
        return bm;
    }
}
