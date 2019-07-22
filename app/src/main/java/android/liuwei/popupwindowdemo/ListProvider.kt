package android.liuwei.popupwindowdemo

object ListProvider {
    fun get(): List<String> {
        val list = mutableListOf<String>()
        var c = 'a'
        var index = 1
        val builder = StringBuilder()
        while (c <= 'm') {
            builder.clear()
            repeat(index) {
                builder.append(c)
            }
            list.add(builder.toString())
            index++
            c++
        }
        return list
    }
}