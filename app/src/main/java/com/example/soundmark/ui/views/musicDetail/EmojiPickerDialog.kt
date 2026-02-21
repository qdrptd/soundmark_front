package com.example.soundmark.ui.views.musicDetail

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.emoji2.emojipicker.EmojiPickerView

@Composable
fun EmojiPickerDialog(
    onDismiss: () -> Unit,
    onEmojiSelected: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        // 안드로이드 내장 이모지 피커 뷰를 Compose에서 호출
        AndroidView(
            factory = { context ->
                EmojiPickerView(context).apply {
                    // 이모지가 선택되었을 때의 리스너
                    setOnEmojiPickedListener { emojiViewItem ->
                        onEmojiSelected(emojiViewItem.emoji)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f) // 화면 절반 정도 크기로 띄움
        )
    }
}