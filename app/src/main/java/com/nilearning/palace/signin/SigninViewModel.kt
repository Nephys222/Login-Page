package com.nilearning.palace.signin

import android.util.Log
import androidx.lifecycle.*
import com.nilearning.palace.models.CheckResponse
import com.nilearning.palace.models.SignInResponse
import com.nilearning.palace.network.RetrofitInstance
import com.nilearning.palace.util.TAG
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

enum class SigningStatus {LOADING, ERROR, WRONG_NUMBER, DONE}

enum class CheckingStatus {LOADING, ERROR, WRONG_CODE, DONE}

class SigninViewModel : ViewModel() {

    var signinResponse: MutableLiveData<SignInResponse> = MutableLiveData<SignInResponse>()
    var checkResponse: MutableLiveData<CheckResponse> = MutableLiveData<CheckResponse>()

    private val _signingStatus = MutableLiveData<SigningStatus>()
    val signingStatus: LiveData<SigningStatus> = _signingStatus

    private val _checkingStatus = MutableLiveData<CheckingStatus>()
    val checkingStatus: LiveData<CheckingStatus> = _checkingStatus

    fun register(signin: Long) {
        viewModelScope.launch {
            _signingStatus.value = SigningStatus.LOADING
            val response = try {
                RetrofitInstance.authApi.signIn(signin)
            } catch (e: IOException){
                Log.e(TAG(), "IOException, you might not have internet connection")
                _signingStatus.value = SigningStatus.ERROR
                return@launch
            } catch (e: HttpException) {
                Log.e(TAG(), "HttpException, unexpected response")
                _signingStatus.value = SigningStatus.ERROR
                return@launch
            } catch (e: Exception) {
                Log.e(TAG(), "Amon?$e")
                _signingStatus.value = SigningStatus.ERROR
                return@launch
            }
            if (response.isSuccessful && response.body()?.status == "ok") {
                signinResponse.postValue(response.body())
                _signingStatus.value = SigningStatus.DONE
                // Log.i(TAG(), "Status: ${response.body()?.status.toString()} Message: ${response.body()?.session.toString()}")
            } else if(response.isSuccessful && response.body()?.status == "error") {
                _signingStatus.value = SigningStatus.WRONG_NUMBER
            } else {
                _signingStatus.value = SigningStatus.ERROR
                Log.e(TAG(),"${response.code()} ${response.message()}")
            }
        }
    }

    fun checkCode(session: String, code: String) {
        viewModelScope.launch {
            _checkingStatus.value = CheckingStatus.LOADING
            val response = try {
                RetrofitInstance.authApi.checkCode(session, code)
            } catch (e: IOException) {
                _checkingStatus.value = CheckingStatus.ERROR
                Log.e(TAG(), "IOException, you might not have internet connection")
                return@launch
            } catch (e: HttpException) {
                _checkingStatus.value = CheckingStatus.ERROR
                Log.e(TAG(), "HttpException, unexpected response")
                return@launch
            }
            if (response.isSuccessful && response.body()?.status == "ok") {
                checkResponse.postValue(response.body())
                _checkingStatus.value = CheckingStatus.DONE
                // Log.i(TAG(), "Status: ${response.body()?.status.toString()} Token: ${response.body()?.token.toString()}")
            } else if (response.isSuccessful && response.body()?.status == "error") {
                _checkingStatus.value = CheckingStatus.WRONG_CODE
            } else {
                _checkingStatus.value = CheckingStatus.ERROR
                Log.e(TAG(),"${response.code()} ${response.message()}")
            }
        }
    }
}