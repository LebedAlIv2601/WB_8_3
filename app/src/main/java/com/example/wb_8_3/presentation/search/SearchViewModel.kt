package com.example.wb_8_3.presentation.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.wb_8_3.domain.model.CatModelDomain
import com.example.wb_8_3.domain.usecase.GetCatUseCase
import com.example.wb_8_3.domain.usecase.PostFavoriteCatUseCase
import com.example.wb_8_3.utils.Resource
import kotlinx.coroutines.*

class SearchViewModel(
    private val getCatUseCase: GetCatUseCase,
    private val postFavoriteCatUseCase: PostFavoriteCatUseCase
) : ViewModel() {

    private val _currentCat = MutableLiveData<Resource<CatModelDomain>>()
    val currentCat: LiveData<Resource<CatModelDomain>>
        get() = _currentCat

    private val _isCatAdded = MutableLiveData<Resource<Boolean>>()
    val isCatAdded: LiveData<Resource<Boolean>>
        get() = _isCatAdded

    private var enableToTouchLike = true

    private val vmJob = Job()
    private val vmScope = CoroutineScope(Dispatchers.Main + vmJob)

    init {
        getCat()
    }

    fun getCat(){
        vmScope.launch {
            getCatFromNet()
        }
    }

    private suspend fun getCatFromNet(){
        _currentCat.value = Resource.Loading(data = null)
        _currentCat.value = getCatUseCaseExecuting()
        enableToTouchLike = true
        Log.e("Loading",  "Data loaded")
    }

    private suspend fun getCatUseCaseExecuting(): Resource<CatModelDomain> {
        return withContext(Dispatchers.IO){
            try {
                Log.e("Loading",  "Try to load data")
                Resource.Success(data = getCatUseCase.execute())
            } catch (e: java.lang.Exception){
                Resource.Error(data = null, message = e.message ?: "Error Occurred!")
            }
        }
    }

    fun postCat(){
        vmScope.launch {
            if(enableToTouchLike) {
                enableToTouchLike = false
                postCatToNet()
            }
        }
    }

    private suspend fun postCatToNet(){
        _isCatAdded.value = Resource.Loading(data = null)
        _isCatAdded.value = postCatUseCaseExecuting()
        Log.e("Loading",  "Data loaded")
        getCat()
    }

    private suspend fun postCatUseCaseExecuting(): Resource<Boolean> {
        return withContext(Dispatchers.IO){
            try {
                Log.e("Loading",  "Try to load data")
                Resource.Success(data = currentCat.value?.data?.let {
                    postFavoriteCatUseCase.execute(
                        it
                    )
                })
            } catch (e: java.lang.Exception){
                Resource.Error(data = null, message = e.message ?: "Error Occurred!")
            }
        }
    }

//    fun setEnableTouchLike(b: Boolean){
//        _enableToTouchLike.value = b
//    }
}