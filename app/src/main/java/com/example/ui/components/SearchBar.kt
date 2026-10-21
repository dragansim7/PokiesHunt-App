package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Amber400
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.ForegroundWhite
import com.example.ui.theme.InterFamily
import com.example.ui.theme.MutedForeground

@Composable
fun SearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(48.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(Color.White.copy(alpha = 0.03f))
      .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
      .padding(horizontal = 14.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = null,
        tint = MutedForeground,
        modifier = Modifier.size(18.dp)
      )

      Spacer(modifier = Modifier.width(10.dp))

      Box(modifier = Modifier.weight(1f)) {
        if (query.isEmpty()) {
          Text(
            text = "Search a site or domain…",
            color = MutedForeground.copy(alpha = 0.7f),
            fontSize = 14.sp
          )
        }
        BasicTextField(
          value = query,
          onValueChange = onQueryChange,
          singleLine = true,
          textStyle = TextStyle(
            color = ForegroundWhite,
            fontSize = 14.sp,
            fontFamily = InterFamily
          ),
          cursorBrush = SolidColor(Amber400),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_text_field")
        )
      }

      if (query.isNotEmpty()) {
        IconButton(
          onClick = { onQueryChange("") },
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Clear search",
            tint = MutedForeground,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
