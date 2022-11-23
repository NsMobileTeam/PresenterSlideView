package com.nextsense.sample

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nextsense.presenterslideview.PresenterSlideView

@SuppressLint("NotifyDataSetChanged")
class NumbersAdapter: PresenterSlideView.Adapter<NumbersAdapter.NumberViewHolder>() {
    private var itemList = arrayListOf<Int>()

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup): NumberViewHolder {
        val itemView: View = inflater.inflate(R.layout.item_number, parent, false)
        return NumberViewHolder(itemView)
    }

    override fun onBindView(holder: NumberViewHolder, position: Int) {
        holder.tvNumber.text = "${itemList[position]}"
        holder.itemView.setOnClickListener {
            Toast.makeText(context, "${itemList[position]}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getCount(): Int {
        return itemList.size
    }

    fun setItems(items: ArrayList<Int>) {
        this.itemList = items
        notifyDataSetChanged()
    }

    class NumberViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvNumber: TextView

        init {
            tvNumber = view.findViewById(R.id.tvNumber)
        }
    }
}
