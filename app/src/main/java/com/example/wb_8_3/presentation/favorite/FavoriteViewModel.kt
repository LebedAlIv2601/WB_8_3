package com.example.wb_8_3.presentation.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.wb_8_3.data.network.Environment.IS_DATA_CHANGED
import com.example.wb_8_3.domain.model.CatModelDomain
import com.example.wb_8_3.domain.usecase.GetFavoriteCatsUseCase
import com.example.wb_8_3.utils.Resource
import kotlinx.coroutines.*

class FavoriteViewModel (private val getFavoriteCatsUseCase: GetFavoriteCatsUseCase): ViewModel(){

    private var _favoriteCatsList = MutableLiveData<Resource<List<CatModelDomain>>>()
    val favoriteCatsList: LiveData<Resource<List<CatModelDomain>>>
        get() = _favoriteCatsList

    private val vmJob = Job()
    private val vmScope = CoroutineScope(Dispatchers.Main + vmJob)

    fun getFavoriteCats(){
        vmScope.launch {
            getFavoriteCatsFromNet()
        }
    }

    private suspend fun getFavoriteCatsFromNet(){
        _favoriteCatsList.value = Resource.Loading(data = null)
        _favoriteCatsList.value = getFavoriteCatsUseCaseExecuting()
        Log.e("Loading",  "Data loaded")
    }

    private suspend fun getFavoriteCatsUseCaseExecuting(): Resource<List<CatModelDomain>> {
        return withContext(Dispatchers.IO){
            try {
                Log.e("Loading",  "Try to load data")
                Resource.Success(data = getFavoriteCatsUseCase.execute())
            } catch (e: java.lang.Exception){
                Resource.Error(data = null, message = e.message ?: "Error Occurred!")
            }
        }
    }

    fun updateFavoriteCatsList() {
        IS_DATA_CHANGED = true
        getFavoriteCats()
    }
}