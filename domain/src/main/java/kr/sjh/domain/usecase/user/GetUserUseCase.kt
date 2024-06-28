package kr.sjh.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.UserModel

fun interface GetUserUseCase {
    suspend operator fun invoke(uid: String): Flow<ResultState<UserModel>>
}