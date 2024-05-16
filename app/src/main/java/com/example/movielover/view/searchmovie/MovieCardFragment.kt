package com.example.movielover.view.searchmovie

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.movielover.databinding.FragmentMovieCardBinding
import com.example.movielover.model.dataclasses.Doc
import com.example.movielover.viewModel.SearchViewModel
import com.squareup.picasso.Picasso

class MovieCardFragment : Fragment() {

    private var _binding: FragmentMovieCardBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var currentMovie: Doc
    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMovieCardBinding.inflate(inflater, container, false)

        currentMovie = viewModel.getMoreDocPostAboutFragment()!!
        mBinding.textViewMovieCard.text = currentMovie.name
        mBinding.movieCardYear.text = currentMovie.year.toString()
        mBinding.movieCardDescription.text = currentMovie.description
        if (currentMovie.country != null) {
            mBinding.movieCardCountry.text = currentMovie.country
        } else {
            mBinding.movieCardCountry.text = currentMovie.countries!![0].name
        }
        Picasso.get().load(currentMovie.poster?.previewUrl).into(mBinding.movieCardPoster)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.backFromMovieCardBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        mBinding.addToMyFavouriteBtn.setOnClickListener {
            if (mBinding.addToMyFavouriteBtn.text == "Добавить") {
                viewModel.addToMyFavouriteList(currentMovie)
                mBinding.addToMyFavouriteBtn.text = "Удалить"
                Toast.makeText(context, "Фильм успешно добавлен", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.deleteMovieFromFavourite(currentMovie)
                mBinding.addToMyFavouriteBtn.text = "Добавить"
                Toast.makeText(context, "Фильм успешно удален", Toast.LENGTH_SHORT).show()
            }
        }

        for (movies in viewModel.getMyFavouriteMoviesList()) {
            if (movies.id == currentMovie.id) {
                mBinding.addToMyFavouriteBtn.text = "Удалить"
            } else {
                Log.d("testLog", "else --- ${movies.id == currentMovie.id}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}