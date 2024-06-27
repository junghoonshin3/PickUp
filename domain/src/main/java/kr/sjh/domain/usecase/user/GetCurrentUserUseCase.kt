package kr.sjh.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.UserModel

fun interface GetCurrentUserUseCase {
    operator fun invoke(): Flow<ResultState<UserModel?>>
}