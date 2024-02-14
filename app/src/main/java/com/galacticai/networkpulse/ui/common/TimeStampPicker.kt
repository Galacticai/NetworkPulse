//package com.galacticai.networkpulse.ui.common
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.rounded.Check
//import androidx.compose.material.icons.rounded.KeyboardArrowDown
//import androidx.compose.material.icons.rounded.KeyboardArrowUp
//import androidx.compose.material3.Divider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.core.text.isDigitsOnly
//import java.text.DecimalFormat
//import java.util.Calendar
//
//@Composable
//fun TimeStampPicker(
//    initialValue: Long = System.currentTimeMillis(),
//    allowedRange: ClosedRange<Long> = 0L..Long.MAX_VALUE,
//    onChanged: (year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int, millisecond: Int) -> Unit,
//) {
//    assert(allowedRange.contains(initialValue))
//    val context = LocalContext.current
//
//    val calInitial = Calendar.getInstance().apply { timeInMillis = initialValue }
//    val cal by rememberSaveable { mutableStateOf(calInitial) }
//    val calFrom = Calendar.getInstance().apply { timeInMillis = allowedRange.start }
//    val calTo = Calendar.getInstance().apply { timeInMillis = allowedRange.endInclusive }
//
//    @Composable
//    fun TimePartPicker(
//        part: Int,
//        formatPattern: String = "00",
//        onUnderflow: (part: Int) -> Int,
//        onOverflow: (part: Int) -> Int,
//    ) {
//        var editMode by remember { mutableStateOf(false) }
//        var value by remember { mutableIntStateOf(cal[part]) }
//        fun setValue(v: Int) {
//            var vTreated = v
//            if (v < 0)vTreated= onUnderflow(part)
//            else if(v>max)vTreated= onOverflow(part)
//            val calTemp = Calendar.getInstance().apply {
//                timeInMillis = cal.timeInMillis
//                set(part, v)
//            }
//            val outOfRange = calFrom.timeInMillis > calTemp.timeInMillis
//                    || calTemp.timeInMillis > calTo.timeInMillis
//            if (outOfRange) return
//            value = v
//            cal[part] = value
//            onChanged(
//                cal[Calendar.YEAR],
//                cal[Calendar.MONTH],
//                cal[Calendar.DAY_OF_MONTH],
//                cal[Calendar.HOUR_OF_DAY],
//                cal[Calendar.MINUTE],
//                cal[Calendar.SECOND],
//                cal[Calendar.MILLISECOND],
//            )
//        }
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            val buttonSize = 40.dp
//            val buttonMod = Modifier.size(buttonSize, buttonSize / 2)
//            IconButton(
//                modifier = buttonMod,
//                enabled = !editMode,
//                onClick = { setValue(value + 1) },
//            ) {
//                if (!editMode)
//                    Icon(Icons.Rounded.KeyboardArrowUp, null)
//            }
//            Surface(
//                modifier = Modifier.width((formatPattern.length * 20).dp),
//                shape = RoundedCornerShape(20.dp),
//                onClick = { editMode = true },
//            ) {
//                if (editMode) {
//                    TextField(
//                        value = value.toString(),
//                        singleLine = true,
//                        keyboardOptions = KeyboardOptions.Default.copy(
//                            keyboardType = KeyboardType.Number
//                        ),
//                        textStyle = MaterialTheme.typography.bodyMedium.copy(
//                            textAlign = TextAlign.Center
//                        ),
//                        onValueChange = {
//                            if (it.isDigitsOnly()) setValue(it.toInt())
//                        }
//                    )
//                } else {
//                    Text(
//                        modifier = Modifier
//                            .padding(5.dp)
//                            .fillMaxWidth(),
//                        text = DecimalFormat(formatPattern).format(value),
//                        textAlign = TextAlign.Center,
//                    )
//                }
//            }
//            IconButton(
//                modifier = buttonMod,
//                onClick = {
//                    if (editMode) editMode = false
//                    else setValue(value - 1)
//                }
//            ) {
//                Icon(
//                    imageVector =
//                    if (editMode) Icons.Rounded.Check
//                    else Icons.Rounded.KeyboardArrowDown,
//                    null,
//                )
//            }
//        }
//    }
//
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        val padMod = Modifier.padding(2.dp)
//
//        @Composable
//        fun slash() = Text("/", modifier = padMod)
//
//        @Composable
//        fun colon() = Text(":", modifier = padMod)
//
//        Row(
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            TimePartPicker(Calendar.YEAR, "0000")
//            slash()
//            TimePartPicker(Calendar.MONTH, "#0")
//            slash()
//            TimePartPicker(Calendar.DAY_OF_MONTH)
//        }
//        Divider(modifier = Modifier.padding(10.dp))
//        Row(
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            TimePartPicker(Calendar.HOUR_OF_DAY)
//            colon()
//            TimePartPicker(Calendar.MINUTE)
//            colon()
//            TimePartPicker(Calendar.SECOND)
//            colon()
//            TimePartPicker(Calendar.MILLISECOND, "000")
//        }
//        Text("${cal[Calendar.YEAR]}/${cal[Calendar.MONTH]}/${cal[Calendar.DAY_OF_MONTH]}")
//        Text("${cal[Calendar.HOUR_OF_DAY]}:${cal[Calendar.MINUTE]}:${cal[Calendar.SECOND]}:${cal[Calendar.MILLISECOND]}")
//    }
//}
//
//
//@Preview(showBackground = true)
//@Composable
//fun TimeStampPickerPreview() {
//    TimeStampPicker { _, _, _, _, _, _, _ ->
//    }
//}