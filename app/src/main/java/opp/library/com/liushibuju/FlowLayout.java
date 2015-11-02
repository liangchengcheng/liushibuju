package opp.library.com.liushibuju;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:  梁铖城
 * Email:   1038127753@qq.com
 * Date:    2015年11月2日16:42:54
 * Description: 关于慕课网的流式布局热门标签的一次练习
 */

public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        super(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //开始测量具体的宽度和高度
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //wrap_content
        int width = 0;
        int height = 0;
        //记录每一行的看度和高度
        int lineWidth = 0;
        int lineHeight = 0;

        //得到内部元素的个数
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            //Viewgroup需要测量子view的宽度和高度
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            ;
            //得到布局的参数
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            //计算子view占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            //计算子view的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.topMargin;
            //换行
            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                //对比得到最大的宽度
                width = Math.max(width, lineWidth);
                //重置linewidth
                lineWidth = childWidth;
                //记录高
                height += lineHeight;
                lineHeight = childHeight;
            } else {
                //不换行顺便叠加行款
                lineWidth += childWidth;
                //得到当前行最大高度
                lineHeight = Math.max(lineHeight, childHeight);
            }
            //要是是最后一个空间的话
            if (i == cCount - 1) {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }

        }

        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom());
    }

    //存储每一行的view
    private List<List<View>> mAllViews = new ArrayList<>();
    //每行的高度
    private List<Integer> mLineHeight = new ArrayList<>();

    //确定位置和大小
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        //当前Viewgroup的宽度
        int width = getWidth();


        int lineWidth = 0;
        int lineHeight = 0;
        List<View> lineViews = new ArrayList<>();
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            //要是需要换行的话
            if (childWidth + lineWidth + layoutParams.leftMargin + layoutParams.rightMargin > width - getPaddingLeft() - getPaddingRight()) {
                //记录下lineheight
                mLineHeight.add(lineHeight);
                //记录当前的views
                mAllViews.add(lineViews);

                //重置我们的行宽和行高
                lineWidth = 0;
                lineHeight = childHeight + layoutParams.topMargin + layoutParams.bottomMargin;
                //重置我们的view集合
                lineViews = new ArrayList<>();
            }
            lineWidth += childWidth + layoutParams.leftMargin + layoutParams.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + layoutParams.topMargin
                    + layoutParams.bottomMargin);
            lineViews.add(child);
        }
        //下面是处理最后的一行
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);
        //设置了子VIEW的位置
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int linenum = mAllViews.size();
        for (int i = 0; i < linenum; i++) {
            //当前行的所有的view
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                //判断child的状态
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams layoutParam= (MarginLayoutParams) child.getLayoutParams();
                int lc=left+layoutParam.leftMargin;
                int tc=top+layoutParam.topMargin;
                int rc=lc+child.getMeasuredWidth();
                int bc=tc+child.getMeasuredHeight();
                //为子view布局
                child.layout(lc,tc,rc,bc);

                left+=child.getMeasuredWidth()+layoutParam.leftMargin+layoutParam.rightMargin;
            }
            left=getPaddingLeft();
            top+=lineHeight;
        }
    }

    //与当前的ViewGroup对应的LayoutParams
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
