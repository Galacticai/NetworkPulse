package com.galacticai.networkpulse.ui.prepare

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.ui.CheckItem

@Composable
fun PrepareItem(
    title: String,
    subtitle: String,
    onClickListener: (() -> Boolean)? = null,
) {
    CheckItem(
        title = title,
        subtitle = subtitle,
        color = colorResource(R.color.background),
        modifier = Modifier.padding(10.dp),
    ) { isChecked: Boolean ->
        if (isChecked) onClickListener?.invoke() ?: false
        else false
    }
}

@Preview(showBackground = true)
@Composable
fun PrepareItemPreview() {
    PrepareItem(
        title = "Not a very long title",
        subtitle = "SubtitleSubt itleSubti tleSubtitl eSubtitleSubti tleSu btitleSubti tleSubtitl eSubtitleSubtitl eSubtitleSu bt itleS ubtitle Subtitle Subtitle SubtitleSubt itleSu bti tleSub tit le SubtitleS ubtitle"
    )
}
