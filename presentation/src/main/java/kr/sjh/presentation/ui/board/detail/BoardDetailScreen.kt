package kr.sjh.presentation.ui.board.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.glide.GlideImage
import kr.sjh.domain.usecase.login.model.Post
import kr.sjh.domain.usecase.login.model.UserInfo
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.MainViewModel
import kr.sjh.presentation.ui.board.BoardViewModel
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kotlin.math.absoluteValue


val COLLAPSED_TOP_BAR_HEIGHT = 50.dp
val EXPANDED_TOP_BAR_HEIGHT = 400.dp

@Composable
fun BoardDetailScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    post: Post,
    userInfo: UserInfo?,
    detailViewModel: BoardDetailViewModel = hiltViewModel(),
    mainViewModel: MainViewModel,
    onBack: () -> Unit,
) {
    val listState = rememberLazyListState()

    val overlapHeightPx = with(LocalDensity.current) {
        EXPANDED_TOP_BAR_HEIGHT.toPx() - COLLAPSED_TOP_BAR_HEIGHT.toPx()
    }

    val isCollapsed by remember {
        derivedStateOf {
            val isFirstItemHidden =
                listState.firstVisibleItemScrollOffset > overlapHeightPx
            isFirstItemHidden || listState.firstVisibleItemIndex > 0
        }
    }

    var isLike by remember {
        mutableStateOf(userInfo?.likePosts?.contains(post.key) ?: false)
    }


    Box(modifier = modifier) {
        //접힌 상태 탑바
        DetailCollapsedTopBar(
            Modifier
                .zIndex(2f)
                .fillMaxWidth()
                .height(COLLAPSED_TOP_BAR_HEIGHT),
            isCollapsed = isCollapsed,
            isLike = isLike,
            onLikeChange = { like ->
                val count = if (like) {
                    post.likeCount.plus(1)
                } else {
                    post.likeCount.minus(1)
                }
                detailViewModel.updatePostLikeCount(
                    mapOf(
                        "/${post.key}/likeCount" to count
                    )
                )
                userInfo?.let {
                    val likes = userInfo.copy().likePosts
                    if (like) {
                        likes.add(post.key)
                    } else {
                        likes.remove(post.key)
                    }
                    mainViewModel.updateUserInfo(it.copy(likePosts = likes))
                }

            },
            onBack = onBack
        )
        LazyColumn(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxSize(), state = listState
        ) {
            item {
                //확장된 상태 탑바
                DetailExpendedTopBar(
                    Modifier
                        .fillMaxWidth()
                        .height(EXPANDED_TOP_BAR_HEIGHT)
                )
            }
            item {
                DetailWriterProfile(
                    profileImageUrl = userInfo?.profileImageUrl,
                    nickName = userInfo?.nickName ?: "닉네임이 없어요",
                    readCount = post.readCount,
                    postCount = userInfo?.postCount ?: 0
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color.Gray
                )
            }
            item {
                DetailTitle(modifier = Modifier.padding(10.dp), title = post.title ?: "")
            }
            item {
                DetailContent(Modifier.padding(10.dp), content = post.content ?: "")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailExpendedTopBar(modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        val pagerState = rememberPagerState {
            5
        }
        HorizontalPager(state = pagerState) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.test_image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }
        Indicator(
            5, pagerState,
            Modifier
                .height(20.dp)
        )
    }
}


@Composable
fun DetailCollapsedTopBar(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean,
    isLike: Boolean,
    onLikeChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    var isLike by remember {
        mutableStateOf(isLike)
    }
    val color by animateColorAsState(
        if (isCollapsed) {
            backgroundColor
        } else {
            Color.Transparent
        }, label = ""
    )
    Row(
        modifier = modifier
            .background(color),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(10.dp)
                .clickable {
                    onBack()
                },
            colorFilter = ColorFilter.tint(
                if (isCollapsed) {
                    Color.White
                } else {
                    Color.Black
                }
            ),
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back"
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            modifier = Modifier
                .padding(10.dp)
                .clickable {
                    isLike = !isLike
                    onLikeChange(isLike)
                },
            colorFilter = ColorFilter.tint(
                if (isLike) {
                    carrot
                } else if (isCollapsed) {
                    Color.White
                } else {
                    Color.Black
                }
            ),
            imageVector = ImageVector.vectorResource(
                id = if (isLike) {
                    R.drawable.baseline_favorite_24
                } else {
                    R.drawable.baseline_favorite_border_24
                }
            ),
            contentDescription = "Like"
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Indicator(
    count: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            modifier = modifier
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(count) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        )
                )
            }
        }
        Box(
            Modifier
                .jumpingDotTransition(pagerState, 0.8f)
                .size(8.dp)
                .background(
                    color = Color(0xFFE78111),
                    shape = CircleShape
                )
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.jumpingDotTransition(pagerState: PagerState, jumpScale: Float) =
    graphicsLayer {
        val pageOffset = pagerState.currentPageOffsetFraction
        val scrollPosition = pagerState.currentPage + pageOffset
        translationX =
            scrollPosition * (size.width + 8.dp.roundToPx()) // 8.dp - spacing between dots

        val scale: Float
        val targetScale = jumpScale - 1f

        scale = if (pageOffset.absoluteValue < .5) {
            1.0f + (pageOffset.absoluteValue * 2) * targetScale;
        } else {
            jumpScale + ((1 - (pageOffset.absoluteValue * 2)) * targetScale);
        }

        scaleX = scale
        scaleY = scale
    }

@Composable
fun DetailWriterProfile(
    profileImageUrl: String?,
    nickName: String,
    readCount: Int,
    postCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
    ) {
        GlideImage(
            modifier = Modifier.size(80.dp),
            imageModel = {
                profileImageUrl ?: R.drawable.baseline_face_24
            },
            requestOptions = {
                RequestOptions()
                    .override(
                        with(LocalDensity.current) { 80.dp.toPx() }.toInt(),
                        with(LocalDensity.current) { 80.dp.toPx() }.toInt()
                    )
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
            },
            loading = {
                Box(modifier = Modifier.matchParentSize()) {
                    CircularProgressIndicator(
                        color = carrot,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = nickName, color = Color.White, fontSize = 20.sp)
            Text(text = "내가 쓴 글 $postCount", color = Color.Gray)
            Text(text = "조회수 $readCount", color = Color.Gray)
        }
    }
}

@Composable
fun DetailTitle(modifier: Modifier = Modifier, title: String) {
    Text(modifier = modifier, text = title, fontSize = 30.sp, color = Color.White)
}

@Composable
fun DetailContent(modifier: Modifier = Modifier, content: String) {
    Text(
        modifier = modifier,
        text = content,
        fontSize = 20.sp,
        color = Color.White
    )
}