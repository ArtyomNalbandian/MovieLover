package com.example.movielover.homepage

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movielover.databinding.FragmentHomePageBinding
import com.example.movielover.profile.ProfileMoviesAdapter
import com.example.movielover.searchmovie.SearchViewModel

class HomePageFragment : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var handler: Handler

    private val viewModel: SearchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var scrollLayout: ScrollView
    private val PREF_SCROLL_POSITION = "scroll_position"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var criminalMoviesAdapter:  HomePageAdapter
    private lateinit var thrillerMoviesAdapter:  HomePageAdapter
    private lateinit var actionMoviesAdapter:    HomePageAdapter
    private lateinit var melodramaMoviesAdapter: HomePageAdapter
    private lateinit var dramaMoviesAdapter:     HomePageAdapter
    private lateinit var fantasticMoviesAdapter: HomePageAdapter
    private lateinit var animeMoviesAdapter:     HomePageAdapter
    private lateinit var seriesMoviesAdapter:    HomePageAdapter
    private lateinit var cartoonMoviesAdapter:   HomePageAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)

        handler = Handler()

        scrollLayout = mBinding.homePageSV

        //БЛОК С ОБЪЯВЛЕНИЯМИ ВСЕХ РЕСАЙКЛЕРОВ
        val criminalRecyclerView  = mBinding.homePageCriminalRV
        val thrillerRecyclerView  = mBinding.homePageThrillerRV
        val actionRecyclerView    = mBinding.homePageActionRV
        val melodramaRecyclerView = mBinding.homePageMelodramaRV
        val dramaRecyclerView     = mBinding.homePageDramaRV
        val fantasticRecyclerView = mBinding.homePageFantasticRV
        val animeRecyclerView     = mBinding.homePageAnimeRV
        val seriesRecyclerView    = mBinding.homePageSeriesRV
        val cartoonRecyclerView   = mBinding.homePageCartoonRV

        criminalMoviesAdapter  = HomePageAdapter(this, viewModel)
        thrillerMoviesAdapter  = HomePageAdapter(this, viewModel)
        actionMoviesAdapter    = HomePageAdapter(this, viewModel)
        melodramaMoviesAdapter = HomePageAdapter(this, viewModel)
        dramaMoviesAdapter     = HomePageAdapter(this, viewModel)
        fantasticMoviesAdapter = HomePageAdapter(this, viewModel)
        animeMoviesAdapter     = HomePageAdapter(this, viewModel)
        seriesMoviesAdapter    = HomePageAdapter(this, viewModel)
        cartoonMoviesAdapter   = HomePageAdapter(this, viewModel)
        criminalRecyclerView.layoutManager  = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        thrillerRecyclerView.layoutManager  = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        actionRecyclerView.layoutManager    = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        melodramaRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        dramaRecyclerView.layoutManager     = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        fantasticRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        animeRecyclerView.layoutManager     = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        seriesRecyclerView.layoutManager    = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        cartoonRecyclerView.layoutManager   = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        criminalRecyclerView.adapter  = criminalMoviesAdapter
        thrillerRecyclerView.adapter  = thrillerMoviesAdapter
        actionRecyclerView.adapter    = actionMoviesAdapter
        melodramaRecyclerView.adapter = melodramaMoviesAdapter
        dramaRecyclerView.adapter     = dramaMoviesAdapter
        fantasticRecyclerView.adapter = fantasticMoviesAdapter
        animeRecyclerView.adapter     = animeMoviesAdapter
        seriesRecyclerView.adapter    = seriesMoviesAdapter
        cartoonRecyclerView.adapter   = cartoonMoviesAdapter

        viewModel.getCriminalMoviesLiveData().observe(viewLifecycleOwner) {
            criminalMoviesAdapter.moviesList = viewModel.getCriminalMoviesLiveData().value!!
            criminalMoviesAdapter.updateData()
        }
        viewModel.getThrillerMoviesLiveData().observe(viewLifecycleOwner) {
            thrillerMoviesAdapter.moviesList = viewModel.getThrillerMoviesLiveData().value!!
            thrillerMoviesAdapter.updateData()
        }
        viewModel.getActionMoviesLiveData().observe(viewLifecycleOwner) {
            actionMoviesAdapter.moviesList = viewModel.getActionMoviesLiveData().value!!
            actionMoviesAdapter.updateData()
        }

        viewModel.getMelodramaMoviesLiveData().observe(viewLifecycleOwner) {
            melodramaMoviesAdapter.moviesList = viewModel.getMelodramaMoviesLiveData().value!!
            melodramaMoviesAdapter.updateData()
        }

        viewModel.getDramaMoviesLiveData().observe(viewLifecycleOwner) {
            dramaMoviesAdapter.moviesList = viewModel.getDramaMoviesLiveData().value!!
            dramaMoviesAdapter.updateData()
        }

        viewModel.getFantasticMoviesLiveData().observe(viewLifecycleOwner) {
            fantasticMoviesAdapter.moviesList = viewModel.getFantasticMoviesLiveData().value!!
            fantasticMoviesAdapter.updateData()
        }

        viewModel.getAnimeMoviesLiveData().observe(viewLifecycleOwner) {
            animeMoviesAdapter.moviesList = viewModel.getAnimeMoviesLiveData().value!!
            animeMoviesAdapter.updateData()
        }

        viewModel.getSeriesMoviesLiveData().observe(viewLifecycleOwner) {
            seriesMoviesAdapter.moviesList = viewModel.getSeriesMoviesLiveData().value!!
            seriesMoviesAdapter.updateData()
        }

        viewModel.getCartoonMoviesLiveData().observe(viewLifecycleOwner) {
            cartoonMoviesAdapter.moviesList = viewModel.getCartoonMoviesLiveData().value!!
            cartoonMoviesAdapter.updateData()
        }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.homePageSwipeRefreshLayout.setOnRefreshListener {
            viewModel.getMoviesByGenre("криминал")
            viewModel.getMoviesByGenre("триллер")
            viewModel.getMoviesByGenre("боевик")
            viewModel.getMoviesByGenre("мелодрама")
            viewModel.getMoviesByGenre("драма")
            viewModel.getMoviesByGenre("фантастика")
            viewModel.getAnimeSeriesCartoon()
            Handler().postDelayed({
                mBinding.homePageSwipeRefreshLayout.isRefreshing = false
            }, 1700)
        }

        sharedPreferences = requireActivity().getSharedPreferences("scroll_prefs", Context.MODE_PRIVATE)
    }

    override fun onResume() {
        super.onResume()
        val scrollPosition = sharedPreferences.getInt(PREF_SCROLL_POSITION, 0)
    }

    override fun onPause() {
        super.onPause()
        val scrollPosition = scrollLayout.scrollY
        sharedPreferences.edit().putInt(PREF_SCROLL_POSITION, scrollPosition).apply()
    }
}