package kr.sjh.presentation.utill

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import java.util.concurrent.TimeUnit

fun calculationTime(createDateTime: Long): String {
    val nowDateTime = System.currentTimeMillis() //현재 시간 to millisecond
    var value = ""
    val differenceValue = nowDateTime - createDateTime //현재 시간 - 비교가 될 시간
    when {
        differenceValue < 60000 -> { //59초 보다 적다면
            value = "방금 전"
        }

        differenceValue < 3600000 -> { //59분 보다 적다면
            value = TimeUnit.MILLISECONDS.toMinutes(differenceValue).toString() + "분 전"
        }

        differenceValue < 86400000 -> { //23시간 보다 적다면
            value = TimeUnit.MILLISECONDS.toHours(differenceValue).toString() + "시간 전"
        }

        differenceValue < 604800000 -> { //7일 보다 적다면
            value = TimeUnit.MILLISECONDS.toDays(differenceValue).toString() + "일 전"
        }

        differenceValue < 2419200000 -> { //3주 보다 적다면
            value = (TimeUnit.MILLISECONDS.toDays(differenceValue) / 7).toString() + "주 전"
        }

        differenceValue < 31556952000 -> { //12개월 보다 적다면
            value = (TimeUnit.MILLISECONDS.toDays(differenceValue) / 30).toString() + "개월 전"
        }

        else -> { //그 외
            value = (TimeUnit.MILLISECONDS.toDays(differenceValue) / 365).toString() + "년 전"
        }
    }
    return value
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}
