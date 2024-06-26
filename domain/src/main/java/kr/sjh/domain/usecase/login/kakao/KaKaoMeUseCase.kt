package kr.sjh.domain.usecase.login.kakao

import kr.sjh.domain.repository.KaKaoLoginRepository
import javax.inject.Inject

class KaKaoMeUseCase @Inject constructor(private val kaKaoLoginRepository: KaKaoLoginRepository) {

    suspend operator fun invoke() = kaKaoLoginRepository.kaKaoMe()
}