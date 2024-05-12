package com.example.movielover.view.profile

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.movielover.R
import com.example.movielover.databinding.CardViewDesignBinding
import com.example.movielover.model.dataclasses.Doc
import com.example.movielover.viewModel.SearchViewModel
import com.squareup.picasso.Picasso

class ProfileMoviesAdapter(
    private val fragment: Fragment,
    private val viewModel: SearchViewModel
) : RecyclerView.Adapter<ProfileMoviesAdapter.ViewHolder>() {

    var moviesList = ArrayList<Doc>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.card_view_design, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = moviesList[position]
        holder.onBind(currentItem)
        holder.mBinding.deleteBtn.setOnClickListener {
            viewModel.deleteMovieFromFavourite(currentItem)
            val removedPosition = position
            moviesList.removeAt(removedPosition)
            notifyItemRemoved(removedPosition)
            notifyItemRangeChanged(removedPosition, moviesList.size)
            Toast.makeText(fragment.requireContext(), "Фильм успешно удален", Toast.LENGTH_SHORT).show()
        }
        holder.mBinding.movieCardLayout.setOnClickListener {
            viewModel.setMoreDocPostAboutFragment(moviesList[position])
            Log.d("testLog", "this is adapter --- ${viewModel.getMoreDocPostAboutFragment()}")
            fragment.findNavController().navigate(R.id.action_myProfileFragment_to_movieCardFragment3)
        }
    }

    override fun getItemCount(): Int = moviesList.size

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private var _binding: CardViewDesignBinding
        val mBinding get() = _binding

        init {
            _binding = CardViewDesignBinding.bind(item)
        }

        private val name = item.findViewById<TextView>(R.id.card_view_name)
        private val country = item.findViewById<TextView>(R.id.card_view_country)
        private val year = item.findViewById<TextView>(R.id.card_view_year)
        private val moviePoster = item.findViewById<ImageView>(R.id.card_view_poster)
        private val deleteBtn = item.findViewById<ImageView>(R.id.deleteBtn)

        fun onBind(doc: Doc) {
            if (doc.name != null) {
                name.text = doc.name
            }
            country.text = doc.country
            year.text = doc.year.toString()
            Picasso.get().load(doc.poster?.url).into(moviePoster)

            deleteBtn.visibility = View.VISIBLE
        }
    }
}