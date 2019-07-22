package android.liuwei.popupwindowdemo

import android.annotation.SuppressLint
import android.liuwei.popupwindowdemo.core.DropdownButton
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<DropdownButton>(R.id.dropdown_btn).setDataList(ListProvider.get())
    }
}
