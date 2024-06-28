package kr.sjh.domain.usecase.board

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.PostModel

fun interface AddPostUseCase {
    suspend operator fun invoke(postModel: PostModel): Flow<ResultState<String>>
}