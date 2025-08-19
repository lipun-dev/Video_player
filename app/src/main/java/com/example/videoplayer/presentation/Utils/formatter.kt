package com.example.videoplayer.presentation.Utils

import android.icu.text.SimpleDateFormat
import java.util.Date

fun formatter(durationInmilis: Long): String{

    val seconds = (durationInmilis/1000).toInt()
    val minutes = seconds/60
    val hours = minutes/60

    return when{
        hours > 0 ->String.format("%d:%02d:%02d",hours,minutes% 60,seconds % 60)
        else->String.format("%d:%02d",minutes,seconds%60)
    }

}

fun formatFIleSize(sizeInBytes: Long): String{
    val kb = sizeInBytes/1024.0
    val mb = kb/1024.0
    val gb = mb/1024.0

    return when{
        gb >= 1 -> "%.2f GB".format(gb)
        mb >= 1 -> "%.2f MB".format(mb)
        kb >= 1 -> "%.2f KB".format(kb)
        else->"$sizeInBytes"

    }

}

fun formatDate(timestamp: Long): String{
    val sdf = SimpleDateFormat("DD:MM:YYYY,HH:MM", java.util.Locale.getDefault())

    return sdf.format(Date(timestamp*1000))
}