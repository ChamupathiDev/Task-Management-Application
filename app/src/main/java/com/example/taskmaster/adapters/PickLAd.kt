package com.example.taskmaster.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView



import com.example.taskmaster.databinding.ViewTaskGridBinding
import com.example.taskmaster.databinding.ViewTaskListBinding
import com.example.taskmaster.models.Pick
import java.text.SimpleDateFormat
import java.util.Locale

class PickLAd(
    private val isL: MutableLiveData<Boolean>,
    private val ducBack: (type: String, position: Int, pick: Pick) -> Unit,
) :
    ListAdapter<Pick,RecyclerView.ViewHolder>(DiffCallback()) {



    class ListTaskViewHolder(private val viewTaskListLayoutBinding: ViewTaskListBinding) :
        RecyclerView.ViewHolder(viewTaskListLayoutBinding.root) {

        fun bind(
            pick: Pick,
            ducBack: (type: String, position: Int, pick: Pick) -> Unit,
        ) {
            viewTaskListLayoutBinding.tiTxt.text = pick.title
            viewTaskListLayoutBinding.dTxt.text = pick.description

            val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())

            viewTaskListLayoutBinding.dateT.text = dateFormat.format(pick.date)

            viewTaskListLayoutBinding.dImg.setOnClickListener {
                if (adapterPosition != -1) {
                    ducBack("delete", adapterPosition, pick)
                }
            }
            viewTaskListLayoutBinding.eImg.setOnClickListener {
                if (adapterPosition != -1) {
                    ducBack("update", adapterPosition, pick)
                }
            }
        }
    }


    class GridTaskViewHolder(private val viewTaskGridLayoutBinding: ViewTaskGridBinding) :
        RecyclerView.ViewHolder(viewTaskGridLayoutBinding.root) {

        fun bind(
            pick: Pick,
            ducBack: (type: String, position: Int, pick: Pick) -> Unit,
        ) {
            viewTaskGridLayoutBinding.tiTxt.text = pick.title
            viewTaskGridLayoutBinding.dTxt.text = pick.description

            val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())

            viewTaskGridLayoutBinding.dateT.text = dateFormat.format(pick.date)

            viewTaskGridLayoutBinding.dImg.setOnClickListener {
                if (adapterPosition != -1) {
                    ducBack("delete", adapterPosition, pick)
                }
            }
            viewTaskGridLayoutBinding.eImg.setOnClickListener {
                if (adapterPosition != -1) {
                    ducBack("update", adapterPosition, pick)
                }
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return if (viewType == 1){  // Grid_Item
            GridTaskViewHolder(
                ViewTaskGridBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }else{  // List_Item
            ListTaskViewHolder(
                ViewTaskListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = getItem(position)

        if (isL.value!!){
            (holder as ListTaskViewHolder).bind(task,ducBack)
        }else{
            (holder as GridTaskViewHolder).bind(task,ducBack)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (isL.value!!){
            0 // List_Item
        }else{
            1 // Grid_Item
        }
    }



    class DiffCallback : DiffUtil.ItemCallback<Pick>() {
        override fun areItemsTheSame(oldItem: Pick, newItem: Pick): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Pick, newItem: Pick): Boolean {
            return oldItem == newItem
        }

    }

}