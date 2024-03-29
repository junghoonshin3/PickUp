package kr.sjh.data.repository

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo
import com.kakao.sdk.user.model.User
import kr.sjh.domain.repository.LoginRepository
import kr.sjh.domain.usecase.login.model.UserInfo
import javax.inject.Inject
import javax.security.auth.login.LoginException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoginRepositoryImpl @Inject constructor(
    private val context: Context,
    private val db: FirebaseDatabase,
    private val authApiClient: AuthApiClient,
    private val userApiClient: UserApiClient
) : LoginRepository {

    override suspend fun signInForKakao() = runCatching {
        if (userApiClient.isKakaoTalkLoginAvailable(context)) {
            loginWithKakaoTalk()
        } else {
            loginWithKakaoAccount()
        }
    }.recoverCatching { error ->
        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
            throw error
        } else {
            loginWithKakaoAccount()
        }
    }

    override suspend fun validateTokenForKakao() = runCatching {
        if (authApiClient.hasToken()) {
            accessTokenInfoForKakao()
        } else {
            //로그인 필요
            throw LoginException("Token is not exist. Are you First Login?")
        }
    }.recoverCatching { error ->
        throw error
    }

    override suspend fun userInfoForKakao() = runCatching {
        bringMe()
    }.recoverCatching {
        throw it
    }

    override suspend fun createUser(user: UserInfo) = runCatching {
        create(user)
    }.recoverCatching {
        throw it
    }

    override suspend fun readUser(id: String?) = runCatching {
        read(id)
    }.recoverCatching {
        throw it
    }

    override suspend fun deleteUser(id: String?) = runCatching {
        delete(id)
    }.recoverCatching {
        throw it
    }

    private suspend fun create(
        user: UserInfo
    ) = suspendCoroutine { continuation ->
        db.reference.child("users").child(user.id.toString()).setValue(
            user
        ).addOnSuccessListener {
            continuation.resume(true)
        }.addOnFailureListener {
            continuation.resumeWithException(it)
        }

    }

    private suspend fun read(
        id: String?,
    ) = suspendCoroutine { continuation ->
        if (!id.isNullOrBlank()) {
            db.reference.child("users").child(id)
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(UserInfo::class.java)
                            if (user != null) {
                                continuation.resume(user)
                            } else {
                                continuation.resumeWithException(NotFoundException(("Not Register User")))
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            continuation.resumeWithException(error.toException())
                        }

                    }
                )
        } else {
            continuation.resumeWithException(NullPointerException("ID is NullOrBlank"))
        }

    }

    private suspend fun delete(
        id: String?,
    ) = suspendCoroutine { continuation ->
        if (!id.isNullOrBlank()) {
            db.reference.child("users").child(id).removeValue { error, ref ->
                if (error != null) {
                    continuation.resumeWithException(error.toException())
                } else {
                    continuation.resume(true)
                }
            }
        } else {
            continuation.resumeWithException(Exception(RuntimeException("ID is NullOrBlank")))
        }
    }

    private suspend fun bringMe(): User = suspendCoroutine { continuation ->
        UserApiClient.Companion.instance.me { user, error ->
            if (error != null) {
                continuation.resumeWithException(error)
            } else {
                user?.let {
                    continuation.resume(it)
                } ?: let {
                    continuation.resumeWithException(Exception("User does not exist"))
                }
            }
        }
    }

    private suspend fun loginWithKakaoTalk(): OAuthToken = suspendCoroutine { continuation ->
        // 카카오톡으로 로그인
        userApiClient.loginWithKakaoTalk(context) { token, error ->
            continuation.resumeTokenOrException(token, error)
        }
    }

    private suspend fun loginWithKakaoAccount(): OAuthToken = suspendCoroutine { continuation ->
        // 카카오톡으로 로그인
        userApiClient.loginWithKakaoAccount(context) { token, error ->
            continuation.resumeTokenOrException(token, error)
        }
    }

    private suspend fun accessTokenInfoForKakao(): AccessTokenInfo =
        suspendCoroutine { continuation ->
            userApiClient.accessTokenInfo { info, error ->
                continuation.resumeTokenOrException(info, error)
            }
        }

    private fun Continuation<OAuthToken>.resumeTokenOrException(
        token: OAuthToken?,
        error: Throwable?
    ) {
        if (error != null) {
            resumeWithException(error)
        } else if (token != null) {
            resume(token)
        } else {
            resumeWithException(RuntimeException("Can't Receive Kakao Access Token"))
        }
    }

    private fun Continuation<AccessTokenInfo>.resumeTokenOrException(
        token: AccessTokenInfo?,
        error: Throwable?
    ) {
        if (error != null) {
            if (error is KakaoSdkError && error.isInvalidTokenError()) {
                //로그인 필요
                resumeWithException(Exception("InvalidTokenError"))
            } else {
                resumeWithException(error)
            }
        } else if (token != null) {
            resume(token)
        } else {
            resumeWithException(RuntimeException("Can't Receive Kakao Access Token"))
        }
    }
}