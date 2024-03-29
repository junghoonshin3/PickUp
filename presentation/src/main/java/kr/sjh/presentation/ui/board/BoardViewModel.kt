package kr.sjh.presentation.ui.board

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.board.CreatePostUseCase
import kr.sjh.domain.usecase.board.ReadPostsUseCase
import kr.sjh.domain.usecase.board.UpdatePostUseCase
import kr.sjh.domain.usecase.login.model.Post
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val readPostsUseCase: ReadPostsUseCase,
    private val createPostsUseCase: CreatePostUseCase,
    private val updatePostUseCase: UpdatePostUseCase
) : ViewModel() {

    init {
        getPosts()
    }


    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()
    fun getPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            readPostsUseCase()
                .collect {
                    _posts.value = it
                }
        }
    }

    fun postUpdate(post: Map<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            updatePostUseCase(post)
                .onSuccess {
                }
                .onFailure {
                    it.printStackTrace()
                }
        }
    }
}