package kr.sjh.domain.usecase.login.kakao

import kr.sjh.domain.repository.LoginRepository
import javax.inject.Inject

class GetKakaoUserInfoUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
) {
    suspend operator fun invoke() = loginRepository.userInfoForKakao()
}