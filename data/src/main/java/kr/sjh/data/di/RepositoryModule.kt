package kr.sjh.data.di

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.sjh.data.repository.BoardRepositoryImpl
import kr.sjh.data.repository.LoginRepositoryImpl
import kr.sjh.domain.repository.BoardRepository
import kr.sjh.domain.repository.KaKaoLoginRepository
import kr.sjh.data.repository.KaKaoLoginRepositoryImpl
import kr.sjh.domain.repository.LoginRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideLoginRepository(
        db: FirebaseDatabase
    ): LoginRepository {
        return LoginRepositoryImpl(
            db
        )
    }

    @Provides
    @Singleton
    fun provideKaKaoLoginRepository(
        authApiClient: AuthApiClient,
        userApiClient: UserApiClient
    ): KaKaoLoginRepository {
        return KaKaoLoginRepositoryImpl(
            authApiClient,
            userApiClient
        )
    }

    @Provides
    @Singleton
    fun provideBoardRepository(
        storage: FirebaseStorage,
        db: FirebaseDatabase,
    ): BoardRepository {
        return BoardRepositoryImpl(
            storage,
            db
        )
    }


}