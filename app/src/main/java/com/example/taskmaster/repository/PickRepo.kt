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

class PickRepo(application: Application) {

    private val taskDao = PickDB.getInstance(application).pickDao


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

    fun getTaskList(isAsc : Boolean, sortByName:String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _tSFlow.emit(Resource.Loading())
                delay(500)
                val result = if (sortByName == "title"){
                    taskDao.getTaskListSortByTaskTitle(isAsc)
                }else{
                    taskDao.getTaskListSortByTaskDate(isAsc)
                }
                _tSFlow.emit(Resource.Success("loading_dialog", result))
            } catch (e: Exception) {
                _tSFlow.emit(Resource.Error(e.message.toString()))
            }
        }
    }


    fun insertTask(pick: Pick) {
        try {
            _sLiData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = taskDao.insertTask(pick)
                handleResult(result.toInt(), "Inserted Pick Successfully", StatusResult.Added)
            }
        } catch (e: Exception) {
            _sLiData.postValue(Resource.Error(e.message.toString()))
        }
    }


    fun deleteTask(pick: Pick) {
        try {
            _sLiData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = taskDao.deleteTask(pick)
                handleResult(result, "Deleted Pick Successfully", StatusResult.Deleted)

            }
        } catch (e: Exception) {
            _sLiData.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun deleteTaskUsingId(taskId: String) {
        try {
            _sLiData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = taskDao.deleteTaskUsingId(taskId)
                handleResult(result, "Deleted Pick Successfully", StatusResult.Deleted)

            }
        } catch (e: Exception) {
            _sLiData.postValue(Resource.Error(e.message.toString()))
        }
    }


    fun updateTask(pick: Pick) {
        try {
            _sLiData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = taskDao.updateTask(pick)
                handleResult(result, "Updated Pick Successfully", StatusResult.Updated)

            }
        } catch (e: Exception) {
            _sLiData.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun updateTaskPaticularField(taskId: String, title: String, description: String) {
        try {
            _sLiData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = taskDao.updateTaskPaticularField(taskId, title, description)
                handleResult(result, "Updated Pick Successfully", StatusResult.Updated)

            }
        } catch (e: Exception) {
            _sLiData.postValue(Resource.Error(e.message.toString()))
        }
    }

    fun searchTaskList(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _tSFlow.emit(Resource.Loading())
                val result = taskDao.searchTaskList("%${query}%")
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