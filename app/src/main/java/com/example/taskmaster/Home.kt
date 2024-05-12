package com.example.taskmaster

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.taskmaster.adapters.PickLAd
import com.example.taskmaster.databinding.ActivityHomeBinding
import com.example.taskmaster.models.Pick
import com.example.taskmaster.utils.Status
import com.example.taskmaster.utils.StatusResult
import com.example.taskmaster.utils.clearEditText
import com.example.taskmaster.utils.hideKeyBoard
import com.example.taskmaster.utils.longToastShow
import com.example.taskmaster.utils.setupDialog
import com.example.taskmaster.utils.validateEditText
import com.example.taskmaster.viewmodels.PickView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class Home : AppCompatActivity() {

    private val homeBinding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private val addTask: Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.add_task)
        }
    }

    private val updatedTask: Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.update_task)
        }

    }

    private val loading: Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.loading)
        }
    }

    private val pickView: PickView by lazy {
        ViewModelProvider(this)[PickView::class.java]
    }

    private val isListMLData = MutableLiveData<Boolean>().apply {
        postValue(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(homeBinding.root)


        // Add task start
        val addImg = addTask.findViewById<ImageView>(R.id.cImg)
        addImg.setOnClickListener { addTask.dismiss() }

        val addtitle = addTask.findViewById<TextInputEditText>(R.id.tT)
        val addtitlev = addTask.findViewById<TextInputLayout>(R.id.tTv)

        addtitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(addtitle, addtitlev)
            }

        })

        val adddes = addTask.findViewById<TextInputEditText>(R.id.tDes)
        val adddesv = addTask.findViewById<TextInputLayout>(R.id.tDesv)

        adddes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(adddes, adddesv)
            }
        })

        homeBinding.aTB.setOnClickListener {
            clearEditText(addtitle, addtitlev)
            clearEditText(adddes, adddesv)
            addTask.show()
        }
        val saveTask = addTask.findViewById<Button>(R.id.sTB)
        saveTask.setOnClickListener {
            if (validateEditText(addtitle, addtitlev)
                && validateEditText(adddes, adddesv)
            ) {

                val newtask = Pick(
                    UUID.randomUUID().toString(),
                    addtitle.text.toString().trim(),
                    adddes.text.toString().trim(),
                    Date()
                )
                hideKeyBoard(it)
                addTask.dismiss()
                pickView.insertTask(newtask)
            }
        }
        // Add task end


        // Update Pick Start
        val updatetitle = updatedTask.findViewById<TextInputEditText>(R.id.tT)
        val updatetitlev = updatedTask.findViewById<TextInputLayout>(R.id.tTv)

        updatetitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(updatetitle, updatetitlev)
            }

        })

        val updatedes = updatedTask.findViewById<TextInputEditText>(R.id.tDes)
        val updatedesv = updatedTask.findViewById<TextInputLayout>(R.id.tDesv)

        updatedes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(updatedes, updatedesv)
            }
        })

        val updateImg = updatedTask.findViewById<ImageView>(R.id.cImg)
        updateImg.setOnClickListener { updatedTask.dismiss() }

        val updateTaskButton = updatedTask.findViewById<Button>(R.id.uTB)

        // Update Pick End

        isListMLData.observe(this){
            if (it){
                homeBinding.tRv.layoutManager = LinearLayoutManager(
                    this,LinearLayoutManager.VERTICAL,false
                )
                homeBinding.lGImg.setImageResource(R.drawable.ic_view_module)
            }else{
                homeBinding.tRv.layoutManager = StaggeredGridLayoutManager(
                    2,LinearLayoutManager.VERTICAL
                ) 
                homeBinding.lGImg.setImageResource(R.drawable.ic_view_list)
            }
        }

        homeBinding.lGImg.setOnClickListener {
            isListMLData.postValue(!isListMLData.value!!)
        }

        val pickLAd = PickLAd(isListMLData ) { type, position, task ->
            if (type == "delete") {
                pickView
                    // Deleted Pick
//                .deleteTask(task)
                    .deleteTaskUsingId(task.id)

                // Restore Deleted task
                rdTask(task)
            } else if (type == "update") {
                updatetitle.setText(task.title)
                updatedes.setText(task.description)
                updateTaskButton.setOnClickListener {
                    if (validateEditText(updatetitle, updatetitlev)
                        && validateEditText(updatedes, updatedesv)
                    ) {
                        val updatePick = Pick(
                            task.id,
                            updatetitle.text.toString().trim(),
                            updatedes.text.toString().trim(),
//                           here i Date updated
                            Date()
                        )
                        hideKeyBoard(it)
                        updatedTask.dismiss()
                        pickView
                            .updateTask(updatePick)
//                            .updateTaskPaticularField(
//                                task.id,
//                                updateETTitle.text.toString().trim(),
//                                updateETDesc.text.toString().trim()
//                            )
                    }
                }
                updatedTask.show()
            }
        }
        homeBinding.tRv.adapter = pickLAd
        ViewCompat.setNestedScrollingEnabled(homeBinding.tRv,false)
        pickLAd.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
//                homeBinding.tRv.smoothScrollToPosition(positionStart)
                homeBinding.nSview.smoothScrollTo(0,positionStart)
            }
        })
        cGTList(pickLAd)
        cSLiveData()
        sCback()

        cSearch()

    }

    private fun rdTask(deletedPick : Pick){
        val sBar = Snackbar.make(
            homeBinding.root, "Deleted '${deletedPick.title}'",
            Snackbar.LENGTH_LONG
        )
        sBar.setAction("Undo"){
            pickView.insertTask(deletedPick)
        }
        sBar.show()
    }

    private fun cSearch() {
        homeBinding.se.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(query: Editable) {
                if (query.toString().isNotEmpty()){
                    pickView.searchTaskList(query.toString())
                }else{
                    cSLiveData()
                }
            }
        })

        homeBinding.se.setOnEditorActionListener{ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                hideKeyBoard(v)
                return@setOnEditorActionListener true
            }
            false
        }

        cSBDialog()
    }
    private fun cSLiveData(){
        pickView.sBLData.observe(this){
            pickView.getTaskList(it.second,it.first)
        }
    }

    private fun cSBDialog() {
        var checkedItem = 0   // 2 is default item set
        val items = arrayOf("Title Ascending", "Title Descending","Date Ascending","Date Descending")

        homeBinding.sImg.setOnClickListener {
            MaterialAlertDialogBuilder(this, R.style.DialogCustomTheme)
                .setTitle("Sort By")
                .setPositiveButton("Ok") { _, _ ->
                    when (checkedItem) {
                        0 -> {
                            pickView.setSortBy(Pair("title",true))
                        }
                        1 -> {
                            pickView.setSortBy(Pair("title",false))
                        }
                        2 -> {
                            pickView.setSortBy(Pair("date",true))
                        }
                        else -> {
                            pickView.setSortBy(Pair("date",false))
                        }
                    }
                }
                .setSingleChoiceItems(items, checkedItem) { _, selectedItemIndex ->
                    checkedItem = selectedItemIndex
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun sCback() {
        pickView
            .sLiData
            .observe(this) {
                when (it.status) {
                    Status.LOADING -> {
                        loading.show()
                    }

                    Status.SUCCESS -> {
                        loading.dismiss()
                        when (it.data as StatusResult) {
                            StatusResult.Added -> {
                                Log.d("StatusResult", "Added")
                            }

                            StatusResult.Deleted -> {
                                Log.d("StatusResult", "Deleted")

                            }

                            StatusResult.Updated -> {
                                Log.d("StatusResult", "Updated")

                            }
                        }
                        it.message?.let { it1 -> longToastShow(it1) }
                    }

                    Status.ERROR -> {
                        loading.dismiss()
                        it.message?.let { it1 -> longToastShow(it1) }
                    }
                }
            }
    }

    private fun cGTList(taskRecyclerViewAdapter: PickLAd) {

        CoroutineScope(Dispatchers.Main).launch {
            pickView
                .tSFlow
                .collectLatest {
                    Log.d("status", it.status.toString())

                    when (it.status) {
                        Status.LOADING -> {
                            loading.show()
                        }

                        Status.SUCCESS -> {
                            loading.dismiss()
                            it.data?.collect { taskList ->
                                taskRecyclerViewAdapter.submitList(taskList)
                            }
                        }

                        Status.ERROR -> {
                            loading.dismiss()
                            it.message?.let { it1 -> longToastShow(it1) }
                        }
                    }

                }
        }
    }
}