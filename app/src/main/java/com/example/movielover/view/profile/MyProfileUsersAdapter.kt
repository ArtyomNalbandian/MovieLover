package com.example.movielover.view.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.movielover.R
import com.example.movielover.databinding.CardViewDesignBinding
import com.example.movielover.viewModel.SearchViewModel
import com.squareup.picasso.Picasso

class MyProfileUsersAdapter (
    private val fragment: Fragment,
    private val viewModel: SearchViewModel
) : RecyclerView.Adapter<MyProfileUsersAdapter.ViewHolder>() {

    var usersList = ArrayList<User>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.card_view_design, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = usersList[position]
        holder.onBind(currentItem)
        holder.mBinding.movieCardLayout.setOnClickListener {
            viewModel.setUserInfoFragment(usersList[position])
            fragment.findNavController().navigate(R.id.action_myProfileFragment_to_profileFragment2)
        }
    }

    override fun getItemCount(): Int = usersList.size

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private var _binding: CardViewDesignBinding
        val mBinding get() = _binding

        init {
            _binding = CardViewDesignBinding.bind(item)
        }

        private val name = item.findViewById<TextView>(R.id.card_view_name)
        private val profileImage = item.findViewById<ImageView>(R.id.card_view_poster)
        private val year = item.findViewById<TextView>(R.id.card_view_year_)
        private val country = item.findViewById<TextView>(R.id.card_view_country_)
        private val year_ = item.findViewById<TextView>(R.id.card_view_year)
        private val country_ = item.findViewById<TextView>(R.id.card_view_country)

        fun onBind(user: User) {
            name.text = user.login
            if (user.profileImage?.isNotEmpty() == true) {
                Picasso.get().load(user.profileImage).into(profileImage)
            }
            year.text = ""
            country.text = ""
            year_.visibility = View.GONE
            country_.visibility = View.GONE
        }

    }
}