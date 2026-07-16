package com.example.sniper

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FavoritesActivity : AppCompatActivity() {

    private lateinit var rvFavorites: RecyclerView
    private lateinit var emptyView: View
    private lateinit var btnBack: ImageButton
    private lateinit var adapter: FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        rvFavorites = findViewById(R.id.rvFavorites)
        emptyView = findViewById(R.id.emptyView)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        // Get favorites from global list or Intent
        val favs = intent.getStringArrayListExtra("favorites") ?: arrayListOf<String>()
        
        adapter = FavoritesAdapter(favs) { hex ->
            // Copy or show professional card if needed
            Toast.makeText(this, "Color: $hex", Toast.LENGTH_SHORT).show()
        }

        rvFavorites.layoutManager = LinearLayoutManager(this)
        rvFavorites.adapter = adapter

        updateVisibility(favs)
    }

    private fun updateVisibility(favs: List<String>) {
        if (favs.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            rvFavorites.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            rvFavorites.visibility = View.VISIBLE
        }
    }

    inner class FavoritesAdapter(
        private val items: MutableList<String>,
        private val onClick: (String) -> Unit
    ) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val preview: View = view.findViewById(R.id.itemColorPreview)
            val hexText: TextView = view.findViewById(R.id.itemHexText)
            val rgbText: TextView = view.findViewById(R.id.itemRgbText)
            val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favorite_color, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val hex = items[position]
            val color = try { Color.parseColor(hex) } catch (e: Exception) { Color.BLACK }
            
            holder.preview.setBackgroundColor(color)
            holder.hexText.text = hex
            
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            holder.rgbText.text = "RGB($r, $g, $b)"

            holder.itemView.setOnClickListener { onClick(hex) }
            
            holder.btnDelete.setOnClickListener {
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, items.size)
                updateVisibility(items)
                
                // Note: In a real app, we'd persist this change
                MainActivity.favorites.clear()
                MainActivity.favorites.addAll(items)
            }
        }

        override fun getItemCount() = items.size
    }
}
