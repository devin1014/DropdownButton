package android.liuwei.popupwindowdemo.core

import android.annotation.SuppressLint
import android.content.Context
import android.liuwei.popupwindowdemo.R
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView

class DropdownButton : TextView,
        OnClickListener {

    constructor(context: Context) : super(context) {
        initialize(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet?) {
        setOnClickListener(this)
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.DropdownButton)
            setPopupWindowLayout(array.getResourceId(R.styleable.DropdownButton_popWindowLayout, -1))
            setPopupWindowLocationByAnchor(array.getInt(R.styleable.DropdownButton_popWindowLocation, SimpleDropdownWindow.DEFAULT_WINDOW_LOCATION))
            setPopupWindowShowCount(array.getFloat(R.styleable.DropdownButton_showItemCount, SimpleDropdownWindow.DEFAULT_SHOW_COUNT))
            array.recycle()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mDropDownWindow != null) {
            mDropDownWindow!!.dismiss()
        }
        mDropDownWindow = null
    }

    @SuppressLint("RtlHardcoded")
    override fun onClick(v: View) {
        if (mLayoutId == -1) {
            throw IllegalArgumentException("you should set popup window layout first!")
        }

        when (mLocation) {
            WindowLocation.LEFT.location -> {
                mDropDownWindow!!.showWindowLeftOfAnchorView(v, mOffsetX, mOffsetY, mGravity)
            }
            WindowLocation.TOP.location -> {
                mDropDownWindow!!.showWindowAboveOfAnchorView(v, mOffsetX, mOffsetY, mGravity)
            }
            WindowLocation.RIGHT.location -> {
                mDropDownWindow!!.showWindowRightOfAnchorView(v, mOffsetX, mOffsetY, mGravity)
            }
            WindowLocation.BOTTOM.location -> {
                mDropDownWindow!!.showWindowBelowOfAnchorView(v, mOffsetX, mOffsetY, mGravity)
            }
        }
    }

    private var mGravity = Gravity.START

    fun setPopupWindowGravity(gravity: Int) {
        mGravity = gravity
    }

    private var mLocation = WindowLocation.BOTTOM.location

    /**
     * @param location should be Gravity.LEFT|Gravity.TOP|Gravity.RIGHT|Gravity.BOTTOM
     * */
    fun setPopupWindowLocationByAnchor(location: Int) {
        mLocation = location
    }

    private var mOffsetX = 0

    private var mOffsetY = 0

    fun setOffset(offsetX: Int, offsetY: Int) {
        mOffsetX = offsetX
        mOffsetY = offsetY
    }

    fun setDefaultTitle(title: String) {
        text = title
    }

    private var mDefaultSelect = -1

    fun setDefaultSelect(index: Int) {
        mDefaultSelect = index
    }

    private var mDropDownWindow: SimpleDropdownWindow? = null

    private var mLayoutId = -1

    fun setPopupWindowLayout(layoutId: Int) {
        mLayoutId = layoutId

        if (mDropDownWindow == null) {
            mDropDownWindow = SimpleDropdownWindow(context, mLayoutId)
            mDropDownWindow!!.setOnItemSelectListener(
                    object : SimpleDropdownWindow.SimpleDropdownItemSelectListener {
                        override fun onItemSelect(data: String, index: Int) {
                            this@DropdownButton.text = data
                        }
                    })
        }
    }

    fun setDataList(list: List<String>) {
        if (mDropDownWindow != null) {
            mDropDownWindow!!.setDataList(list)
            if (mDefaultSelect != -1 && mDefaultSelect >= 0 && mDefaultSelect < list.size) {
                setDefaultTitle(list[mDefaultSelect])
                mOnDropdownButtonCallback?.onItemSelect(list[mDefaultSelect], mDefaultSelect)
            }
        }
    }

    fun setPopupWindowShowCount(size: Float) {
        if (mDropDownWindow != null) {
            mDropDownWindow!!.setShowItemCount(size)
        }
    }

    fun getDropdownWindow(): SimpleDropdownWindow? {
        return mDropDownWindow
    }

    private var mOnDropdownButtonCallback: DropdownButtonCallback? = null

    interface DropdownButtonCallback {
        fun onItemSelect(data: String, index: Int)
    }

    fun setOnDropdownButtonCallback(callback: DropdownButtonCallback?) {
        mOnDropdownButtonCallback = callback
    }

    enum class WindowLocation(val location: Int) {
        LEFT(1), TOP(2), RIGHT(3), BOTTOM(4);
    }
}