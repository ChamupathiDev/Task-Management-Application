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

    private val updatedPick: Dialog by lazy {
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
                pickView.insertPick(newtask)
            }
        }
        // Add task end


        // Update Pick Start
        val updatetitle = updatedPick.findViewById<TextInputEditText>(R.id.tT)
        val updatetitlev = updatedPick.findViewById<TextInputLayout>(R.id.tTv)

        updatetitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(updatetitle, updatetitlev)
            }

        })

        val updatedes = updatedPick.findViewById<TextInputEditText>(R.id.tDes)
        val updatedesv = updatedPick.findViewById<TextInputLayout>(R.id.tDesv)

        updatedes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(updatedes, updatedesv)
            }
        })

        val updateImg = updatedPick.findViewById<ImageView>(R.id.cImg)
        updateImg.setOnClickListener { updatedPick.dismiss() }

        val updatePickButton = updatedPick.findViewById<Button>(R.id.uTB)

        // Update Pick End

        isListMLData.observe(this){
            if (it){
                homeBinding.tq.layoutManager = LinearLayoutManager(
                    this,LinearLayoutManager.VERTICAL,false
                )
                homeBinding.lGImg.setImageResource(R.drawable.view_module1)
            }else{
                homeBinding.tq.layoutManager = StaggeredGridLayoutManager(
                    2,LinearLayoutManager.VERTICAL
                ) 
                homeBinding.lGImg.setImageResource(R.drawable.view_list1)
            }
        }

        homeBinding.lGImg.setOnClickListener {
            isListMLData.postValue(!isListMLData.value!!)
        }

        val pickLAd = PickLAd(isListMLData ) { type, position, pick ->
            if (type == "delete") {
                pickView
                    // Deleted Pick
//                .deletePick(task)
                    .deletePickUsingId(pick.id)

                // Restore Deleted task
                rdTask(pick)
            } else if (type == "update") {
                updatetitle.setText(pick.title)
                updatedes.setText(pick.description)
                updatePickButton.setOnClickListener {
                    if (validateEditText(updatetitle, updatetitlev)
                        && validateEditText(updatedes, updatedesv)
                    ) {
                        val updatePick = Pick(
                            pick.id,
                            updatetitle.text.toString().trim(),
                            updatedes.text.toString().trim(),
//                           here i Date updated
                            Date()
                        )
                        hideKeyBoard(it)
                        updatedPick.dismiss()
                        pickView
                            .updatePick(updatePick)
//                            .updatePickPaticularField(
//                                task.id,
//                                updateETTitle.text.toString().trim(),
//                                updateETDesc.text.toString().trim()
//                            )
                    }
                }
                updatedPick.show()
            }
        }
        homeBinding.tq.adapter = pickLAd
        ViewCompat.setNestedScrollingEnabled(homeBinding.tq,false)
        pickLAd.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
//                homeBinding.tq.smoothScrollToPosition(positionStart)
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
            pickView.insertPick(deletedPick)
        }
        sBar.show()
    }

    private fun cSearch() {
        homeBinding.se.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(query: Editable) {
                if (query.toString().isNotEmpty()){
                    pickView.searchPickList(query.toString())
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
            pickView.getPickList(it.second,it.first)
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