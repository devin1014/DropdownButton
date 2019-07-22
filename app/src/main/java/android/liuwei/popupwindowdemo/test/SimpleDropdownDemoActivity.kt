package android.liuwei.popupwindowdemo.test

import android.annotation.SuppressLint
import android.graphics.Rect
import android.liuwei.popupwindowdemo.ListProvider
import android.liuwei.popupwindowdemo.R.id
import android.liuwei.popupwindowdemo.R.layout
import android.liuwei.popupwindowdemo.core.SimpleDropdownWindow
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.TextView

class SimpleDropdownDemoActivity : AppCompatActivity() {

    private var mDropDownWindow: SimpleDropdownWindow? = null

    private val mHandler = Handler()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_simple_dropdown)

        val displayView = findViewById<TextView>(id.display_info)
        displayView.text = resources.displayMetrics.toString()

        val windowInfoView = findViewById<TextView>(id.window_info)
        windowInfoView.text = "window size info"

        val titleView = findViewById<TextView>(id.show_drop_window)
        titleView.setOnClickListener {
            if (mDropDownWindow == null) {
                mDropDownWindow = SimpleDropdownWindow(this, layout.item_simple_dropdown)
                mDropDownWindow!!.setOnItemSelectListener(
                        object : SimpleDropdownWindow.SimpleDropdownItemSelectListener {
                            override fun onItemSelect(data: String, index: Int) {
                                titleView.text = data
                            }
                        })
            }
            mDropDownWindow!!.setDataList(ListProvider.get())
            mDropDownWindow!!.showWindowBelowOfAnchorView(titleView, 0, 0, Gravity.CENTER)

            mHandler.postDelayed({
                val rect = Rect()
                mDropDownWindow!!.background.getPadding(rect)
                windowInfoView.text = "popWindow: width=${mDropDownWindow!!.width},height=${mDropDownWindow!!.height}\n" +
                        "contentView: width=${mDropDownWindow!!.contentView.width},height=${mDropDownWindow!!.contentView.height}\n" +
                        "background: bounds=${mDropDownWindow!!.background.bounds}\n" +
                        "background: size=${mDropDownWindow!!.background.intrinsicWidth},${mDropDownWindow!!.background.intrinsicHeight}\n" +
                        "background: padding=$rect"

            }, 200L)
        }
    }
}
