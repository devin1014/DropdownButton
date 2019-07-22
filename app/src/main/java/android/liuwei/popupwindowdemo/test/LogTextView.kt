package android.liuwei.popupwindowdemo.test

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView

class LogTextView(context: Context, attrs: AttributeSet) : TextView(context, attrs) {

    private val TAG = javaClass.simpleName + "@${hashCode().toString(16)}"
    private var mFirstTime = true

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mFirstTime) {
            mFirstTime = false
            Log.i("testLog_$TAG", "onMeasure: width = ${MeasureSpec.toString(widthMeasureSpec)}")
            Log.i("testLog_$TAG", "onMeasure: height = ${MeasureSpec.toString(heightMeasureSpec)}")
        }
        Log.i("testLog_$TAG", "setDimension: ($measuredWidth,$measuredHeight)")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.i("testLog_$TAG", "onSizeChanged, width=($oldw -> $w), height=($oldh -> $h)")
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        Log.i("testLog_$TAG", "onTextChanged: $text")
        val str = text?.toString()!!
        val rect = Rect()
        paint.getTextBounds(str, 0, str.length, rect)
        Log.i("testLog_$TAG", "textBounds: ${rect.width()}")
        Log.i("testLog_$TAG", "fontSpace: ${paint.fontSpacing}")
    }

}