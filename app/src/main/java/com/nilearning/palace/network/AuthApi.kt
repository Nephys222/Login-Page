package com.nilearning.palace.network

import com.nilearning.palace.models.CheckResponse
import com.nilearning.palace.models.SignInResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi {

    @FormUrlEncoded
    @POST("sign-in")
    suspend fun signIn(
        @Field("phone") phone: Long
    ): Response<SignInResponse>

    @FormUrlEncoded
    @POST("checkCode")
    suspend fun checkCode(
        @Field("session") session: String,
        @Field("code") code: String
    ): Response<CheckResponse>
}