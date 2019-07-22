package android.liuwei.popupwindowdemo.core

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.*
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import android.widget.PopupWindow
import android.widget.TextView
import kotlin.math.max
import kotlin.math.roundToInt

class SimpleDropdownWindow(context: Context,
                           private val itemViewId: Int) : PopupWindow(context),
        OnClickListener {

    companion object {
        const val DEFAULT_SHOW_COUNT = 6.5F
        const val DEFAULT_WINDOW_LOCATION = 4 //WindowLocation.BOTTOM.location
    }

    //private val TAG = javaClass.simpleName + "@${hashCode().toString(16)}"

    init {
        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        contentView = recyclerView
        val backgroundDrawable = GradientDrawable()
        backgroundDrawable.setColor(Color.WHITE)
        setBackgroundDrawable(backgroundDrawable)
        isFocusable = true
        isOutsideTouchable = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = context.resources.displayMetrics.density * 6 //6dp
        }
    }

    private var mShowItemCount = 6.5F

    fun setShowItemCount(count: Float) {
        mShowItemCount = max(count, 1.5F)
    }

    private val mAdapter = ListAdapter(context, itemViewId, this)

    fun setDataList(list: List<String>) {
        mAdapter.setDataList(list)
        if ((contentView as RecyclerView).adapter == null) {
            (contentView as RecyclerView).adapter = mAdapter
        } else {
            mAdapter.notifyDataSetChanged()
        }

        resetWindowSize(list)
    }

    private var mInflaterView: View? = null
    private var mNeedResetSizeBySelf: Boolean? = null

    private fun resetWindowSize(list: List<String>) {
        if (mNeedResetSizeBySelf == null) {
            if (width == WindowManager.LayoutParams.WRAP_CONTENT
                    || height == WindowManager.LayoutParams.WRAP_CONTENT
            ) {
                mNeedResetSizeBySelf = true
            }
        }

        // the window has fixed size not need reset size by content.
        if (mNeedResetSizeBySelf == false) {
            return
        }

        var maxWidth = 0
        var maxHeight = 0

        if (mInflaterView == null) {
            val inflaterView = LayoutInflater.from(contentView.context)
                    .inflate(itemViewId, contentView as RecyclerView, false)
            mInflaterView = inflaterView
            //Log.i("testLog_$TAG", "inflaterView LayoutParam: width=${inflaterView.layoutParams.width}, height=${inflaterView.layoutParams.height}")
        }

        val inflaterView = mInflaterView!!
        if (inflaterView.layoutParams.width > 0) {
            maxWidth = inflaterView.layoutParams.width
        }

        if (inflaterView.layoutParams.height > 0) {
            maxHeight = inflaterView.layoutParams.height
        }

        val resetWidth = maxWidth <= 0 //TODO:
        val resetHeight = maxHeight <= 0

        if (resetWidth || resetHeight) {
            val textView = TextViewHelper.findTextView(inflaterView) ?: throw IllegalArgumentException("can not find TextView!!!")
            val rect = Rect()
            for (str in list) {
                textView.paint.getTextBounds(str, 0, str.length, rect)
                //Log.i("testLog_$TAG", "getBounds: $str -> ${rect.width()}")
                val w = textView.paint.measureText(str)
                //Log.i("testLog_$TAG", "measureText: $str -> ${textView.paint.measureText(str)}")
                if (resetWidth && w > maxWidth) {
                    maxWidth = w.roundToInt()
                }
                if (resetHeight && rect.height() > maxHeight) {
                    maxHeight = rect.height() + textView.paint.fontSpacing.roundToInt()
                }
            }
        }

        val backgroundRect = Rect()
        if (background != null) {
            background.getPadding(backgroundRect)
        }
        if (maxWidth > 0) {
            var padding = inflaterView.paddingLeft + inflaterView.paddingRight
            if (inflaterView.layoutParams is MarginLayoutParams) {
                padding += (inflaterView.layoutParams as MarginLayoutParams).leftMargin + (inflaterView.layoutParams as MarginLayoutParams).rightMargin
            }
            width = maxWidth + padding + backgroundRect.left + backgroundRect.right
        }
        if (maxHeight > 0) {
            val count = Math.min(list.size.toFloat(), mShowItemCount)

            var padding = inflaterView.paddingTop + inflaterView.paddingBottom
            if (inflaterView.layoutParams is MarginLayoutParams) {
                padding += (inflaterView.layoutParams as MarginLayoutParams).topMargin + (inflaterView.layoutParams as MarginLayoutParams).bottomMargin
            }
            height = ((maxHeight + padding) * count).roundToInt() + backgroundRect.top + backgroundRect.bottom
        }
    }

    fun setSelection(selection: String) {
        mAdapter.setSelection(selection)
        mAdapter.notifyDataSetChanged()
    }

    /**
     * @param gravity: left|right|center
     */
    @SuppressLint("RtlHardcoded")
    fun showWindowBelowOfAnchorView(anchor: View,
                                    xoff: Int = 0,
                                    yoff: Int = 0,
                                    gravity: Int = Gravity.LEFT) {
        when (gravity) {
            Gravity.LEFT, Gravity.START -> {
                showAsDropDown(anchor, xoff, yoff)
            }
            Gravity.RIGHT, Gravity.END -> {
                showAsDropDown(anchor, xoff, yoff, Gravity.RIGHT.and(Gravity.END))
            }
            Gravity.CENTER, Gravity.CENTER_HORIZONTAL -> {
                showAsDropDown(anchor, -(width - anchor.width) / 2 + xoff, yoff)
            }
            else -> {
                showAsDropDown(anchor, xoff, yoff)
            }
        }
    }

    /**
     * @param gravity: left|right|center
     */
    @SuppressLint("RtlHardcoded")
    fun showWindowAboveOfAnchorView(anchor: View,
                                    xoff: Int = 0,
                                    yoff: Int = 0,
                                    gravity: Int = Gravity.LEFT) {
        when (gravity) {
            Gravity.LEFT, Gravity.START -> {
                showAsDropDown(anchor, xoff, -(height + anchor.height) + yoff)
            }
            Gravity.RIGHT, Gravity.END -> {
                showAsDropDown(anchor, xoff, -(height + anchor.height) + yoff, Gravity.RIGHT.and(Gravity.END))
            }
            Gravity.CENTER, Gravity.CENTER_HORIZONTAL -> {
                showAsDropDown(anchor, -(width - anchor.width) / 2 + xoff, -(height + anchor.height) + yoff)
            }
            else -> {
                showAsDropDown(anchor, xoff, -(height + anchor.height) + yoff)
            }
        }
    }

    /**
     * @param gravity: left|right|center
     */
    @SuppressLint("RtlHardcoded")
    fun showWindowLeftOfAnchorView(anchor: View,
                                   xoff: Int = 0,
                                   yoff: Int = 0,
                                   gravity: Int = Gravity.LEFT) {
        when (gravity) {
            Gravity.TOP -> {
                showAsDropDown(anchor, -width + xoff, -anchor.height + yoff)
            }
            Gravity.BOTTOM -> {
                showAsDropDown(anchor, -width + xoff, -height + yoff)
            }
            Gravity.CENTER, Gravity.CENTER_VERTICAL -> {
                showAsDropDown(anchor, -width + xoff, -anchor.height - (height - anchor.height) / 2 + yoff)
            }
            else -> {
                showAsDropDown(anchor, -width + xoff, -anchor.height + yoff)
            }
        }
    }

    /**
     * @param gravity: left|right|center
     */
    @SuppressLint("RtlHardcoded")
    fun showWindowRightOfAnchorView(anchor: View,
                                    xoff: Int = 0,
                                    yoff: Int = 0,
                                    gravity: Int = Gravity.LEFT) {
        when (gravity) {
            Gravity.TOP -> {
                showAsDropDown(anchor, width + xoff, -anchor.height + yoff, Gravity.RIGHT)
            }
            Gravity.BOTTOM -> {
                showAsDropDown(anchor, width + xoff, -height + yoff, Gravity.RIGHT)
            }
            Gravity.CENTER, Gravity.CENTER_VERTICAL -> {
                showAsDropDown(anchor, width + xoff, -anchor.height - (height - anchor.height) / 2 + yoff, Gravity.RIGHT)
            }
            else -> {
                showAsDropDown(anchor, width + xoff, -anchor.height + yoff, Gravity.RIGHT)
            }
        }
    }

    private var mMinWidthMatchAsAnchorView = true

    fun setMinWidthMatchAsAnchorView(matchAs: Boolean) {
        mMinWidthMatchAsAnchorView = matchAs
    }

    override fun showAsDropDown(anchor: View,
                                xoff: Int,
                                yoff: Int,
                                gravity: Int) {
        //Log.i("testLog_$TAG", "try to showAsDropDown: width=$width, height=$height")
        if (mMinWidthMatchAsAnchorView) {
            if (width > 0 && anchor.width > width) {
                width = anchor.width
            }
        }

        super.showAsDropDown(anchor, xoff, yoff, gravity)
    }

    override fun onClick(v: View) {
        val data = v.tag as String
        mSelectListener?.onItemSelect(data, mAdapter.findData(data))
        dismiss()
    }

    private var mSelectListener: SimpleDropdownItemSelectListener? = null

    fun setOnItemSelectListener(listener: SimpleDropdownItemSelectListener) {
        mSelectListener = listener
    }

    interface SimpleDropdownItemSelectListener {
        fun onItemSelect(data: String, index: Int)
    }

    private class ListAdapter(context: Context, val itemViewId: Int, val listener: OnClickListener) :
            Adapter<Holder>() {

        private var mDataList = listOf<String>()

        private val mInflater = LayoutInflater.from(context)

        private var mSelection: String? = null

        fun findData(data: String): Int {
            return mDataList.indexOf(data)
        }

        fun setDataList(list: List<String>) {
            mDataList = list
        }

        fun setSelection(selection: String) {
            mSelection = selection
        }

        override fun getItemCount(): Int {
            return mDataList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(mInflater.inflate(itemViewId, parent, false), listener)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.onBind(mDataList[position], mDataList[position] == mSelection)
        }
    }

    private class Holder(itemView: View, listener: OnClickListener) : ViewHolder(itemView) {
        private val textView: TextView? = TextViewHelper.findTextView(itemView)

        init {
            itemView.setOnClickListener(listener)
        }

        fun onBind(data: String, selected: Boolean) {
            itemView.tag = data
            itemView.isSelected = selected
            textView?.text = data
        }
    }

    private object TextViewHelper {
        fun findTextView(contentView: View): TextView? {
            if (contentView is TextView) return contentView
            else if (contentView is ViewGroup) {
                for (index in 0 until contentView.childCount) {
                    val textView = findTextView(contentView.getChildAt(index))
                    if (textView != null) {
                        return textView
                    }
                }
            }
            return null
        }
    }
}