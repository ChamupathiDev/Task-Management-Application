package com.example.taskmaster.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.example.taskmaster.database.PickDB
import com.example.taskmaster.models.Pick
import com.example.taskmaster.utils.Resource
import com.example.taskmaster.utils.StatusResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class  PickRepo(application: Application) {

    private val pickDao = PickDB.getInstance(application).pickDao


    private val _tSFlow = MutableStateFlow<Resource<Flow<List<Pick>>>>(Resource.Loading())
    val tSFlow: StateFlow<Resource<Flow<List<Pick>>>>
        get() = _tSFlow

    private val _sLiData = MutableLiveData<Resource<StatusResult>>()
    val sLiData: LiveData<Resource<StatusResult>>
        get() = _sLiData


    private val _sBLData = MutableLiveData<Pair<String,Boolean>>().apply {
        postValue(Pair("title",true))
    }
    val sBLData: LiveData<Pair<String,Boolean>>
        get() = _sBLData


    fun setSortBy(sort:Pair<String,Boolean>){
        _sBLData.postValue(sort)
    }

    fun getPickList(isAsc : Boolean, sortByName:String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _tSFlow.emit(Resource.Loading())
                delay(500)
                val result = if (sortByName == "title"){
                    pickDao.getPickListSortByPick(isAsc)
                }else{
                    pickDao.getPickListSortByPickDate(isAsc)
                }
                _tSFlow.emit(Resource.Success("loading_dialog", result))
            } catch (e: Exception) {
                _tSFlow.emit(Resource.Error(e.message.toString()))
            }
        }
    }


    fun insertPick(pick: Pick) {
        try {
            _sLiData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = pickDao.insertPick(pick)
                handleResult(result.toInt(), "Inserted Pick Successfully", StatusResult.Added)
            }
        } catch (e: Exception) {
            _sLiData.postValue(Resource.Error(e.message.toString()))
        }
    }


    fun deletePick(pick: Pick) {
        try {
            _sLiData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = pickDao.deletePick(pick)
                handleResult(result, "Deleted Pick Successfully", StatusResult.Deleted)

            }
        } catch (e: Exception) {
            _sLiData.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun deletePickUsingId(taskId: String) {
        try {
            _sLiData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = pickDao.deletePickUsingId(taskId)
                handleResult(result, "Deleted Pick Successfully", StatusResult.Deleted)

            }
        } catch (e: Exception) {
            _sLiData.postValue(Resource.Error(e.message.toString()))
        }
    }


    fun updatePick(pick: Pick) {
        try {
            _sLiData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = pickDao.updatePick(pick)
                handleResult(result, "Updated Pick Successfully", StatusResult.Updated)

            }
        } catch (e: Exception) {
            _sLiData.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun updatePickPaticularField(taskId: String, title: String, description: String) {
        try {
            _sLiData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = pickDao.updatePickPaticularField(taskId, title, description)
                handleResult(result, "Updated Pick Successfully", StatusResult.Updated)

            }
        } catch (e: Exception) {
            _sLiData.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun searchPickList(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _tSFlow.emit(Resource.Loading())
                val result = pickDao.searchPickList("%${query}%")
                _tSFlow.emit(Resource.Success("loading_dialog", result))
            } catch (e: Exception) {
                _tSFlow.emit(Resource.Error(e.message.toString()))
            }
        }
    }


    private fun handleResult(result: Int, message: String, statusResult: StatusResult) {
        if (result != -1) {
            _sLiData.postValue(Resource.Success(message, statusResult))
        } else {
            _sLiData.postValue(Resource.Error("Something Went Wrong", statusResult))
        }
    }
}