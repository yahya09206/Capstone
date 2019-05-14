package com.yahya.thehorn.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yahya.thehorn.R
import com.yahya.thehorn.models.HomeMenu
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_home_menu_item.view.*

class MainActivity : AppCompatActivity() {

    private val menu = listOf(
        HomeMenu(R.drawable.ic_food, "Foods"),
        HomeMenu(R.drawable.ic_numbers, "Numbers"),
        HomeMenu(R.drawable.ic_phrase, "Phrases"),
        HomeMenu(R.drawable.ic_nouns, "Nouns, verbs, adjectives, ...")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        menuView.layoutManager = GridLayoutManager(this, 2)
        menuView.adapter = HomeMenuAdapter(menu)
    }
}

class HomeMenuAdapter(private val menu: List<HomeMenu>) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_home_menu_item, null, false))

    override fun getItemCount() = menu.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menu[position])
    }
}

class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    fun bind(homeMenu: HomeMenu) {
        itemView.menuImg.setImageResource(homeMenu.icon)
        itemView.menuLabel.text = homeMenu.title
        itemView.card.setOnClickListener {
            when (homeMenu.title){
                "Foods" -> itemView.context.startActivity(Intent(itemView.context, FoodActivity::class.java))
                "Numbers" -> itemView.context.startActivity(Intent(itemView.context, NumbersActivity::class.java))
                "Phrases" -> itemView.context.startActivity(Intent(itemView.context, PhrasesActivity::class.java))
                "Nouns, verbs, adjectives, ..." -> itemView.context.startActivity(Intent(itemView.context, NVAActivity::class.java))
            }
        }
    }
}
