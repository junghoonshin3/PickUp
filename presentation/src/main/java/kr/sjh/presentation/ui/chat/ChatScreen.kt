package kr.sjh.presentation.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.glide.GlideImage
import kr.sjh.domain.model.UserInfo
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.theme.backgroundColor

@Composable
fun ChatRoute() {

}

@Composable
fun ChatScreen(navController: NavController, userInfo: UserInfo?, modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(2) {
                ChatList(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(10.dp),
                    profileUrl = null,
                    nickname = userInfo?.nickName,
                    onClick = {

                    }
                )
            }
        }
    }
}

@Composable
fun ChatList(
    modifier: Modifier = Modifier,
    profileUrl: String?,
    nickname: String?,
    onClick: () -> Unit
) {
    Row(modifier = modifier) {
        GlideImage(
            modifier = Modifier
                .clip(CircleShape),
            requestOptions = {
                RequestOptions().override(200, 200).centerInside().circleCrop()
            },
            imageModel = {
                //대화 상대의 이미지 Url
                profileUrl ?: R.drawable.baseline_face_24
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .clickable {
                    onClick()
                },
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = nickname ?: "이름없음")
            Text(text = "안녕하세요.")
        }

    }
}
