package com.hari.covid_19app.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialFadeThrough
import com.hari.covid_19app.R
import com.hari.covid_19app.databinding.FragmentHomeBinding
import com.hari.covid_19app.di.Injectable
import com.hari.covid_19app.ui.item.*
import com.hari.covid_19app.utils.ext.assistedViewModels
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.databinding.GroupieViewHolder
import javax.inject.Inject
import javax.inject.Provider

class HomeFragment : Fragment(R.layout.fragment_home), Injectable {

    @Inject
    lateinit var homeViewModelProvider: Provider<HomeViewModel>
    private val homeViewModel: HomeViewModel by assistedViewModels {
        homeViewModelProvider.get()
    }

    @Inject
    lateinit var itemHealthStatusCardFactory: ItemHealthStatusCard.Factory

    @Inject
    lateinit var itemIndiaStatusCardFactory: ItemIndiaStatusCard.Factory

    @Inject
    lateinit var itemGlobalStatusCardFactory: ItemGlobalStatusCard.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)
        val adapter = GroupAdapter<GroupieViewHolder<*>>()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(
            CardItemDecoration(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_surface
                ),
                resources.getDimensionPixelSize(
                    R.dimen.activity_horizontal_margin
                )
            )
        )
        binding.recyclerView.adapter = adapter

        homeViewModel.ui.observe(viewLifecycleOwner, Observer { uiModel ->
            val items = mutableListOf<Group>()
            items.add(itemHealthStatusCardFactory.create())

            uiModel.globalState?.let { globalState ->
                items.add(itemGlobalStatusCardFactory.create(globalState))
            }

            uiModel.totalCaseInIndia?.let { totalCaseInIndia ->
                items.add(itemIndiaStatusCardFactory.create(totalCaseInIndia))
            }

            val newsSection = Section(ItemHeader(R.string.latest_updates))
            for (i in 1..50) {
                newsSection.add(ItemNews())
            }

            items.add(newsSection)

            adapter.update(items)
        })
    }


}