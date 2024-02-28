package kr.sjh.presentation.ui.board

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.MainActivity
import kr.sjh.presentation.ui.theme.backgroundColor
import kotlin.math.absoluteValue


val COLLAPSED_TOP_BAR_HEIGHT = 50.dp
val EXPANDED_TOP_BAR_HEIGHT = 400.dp

@Composable
fun BoardDetailScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
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

    Box {
        //접힌 상태 탑바
        DetailCollapsedTopBar(
            Modifier
                .zIndex(2f)
                .fillMaxWidth()
                .height(COLLAPSED_TOP_BAR_HEIGHT),
            isCollapsed, onBack
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
            items(50) {
                Text(color = Color.White, text = "item >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> $it")
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
    isCollapsed: Boolean, onBack: () -> Unit
) {

    val color by animateColorAsState(
        if (isCollapsed) {
            backgroundColor
        } else {
            Color.Transparent
        }, label = ""
    )
    Box(
        modifier = modifier
            .background(color),
        contentAlignment = Alignment.CenterStart
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