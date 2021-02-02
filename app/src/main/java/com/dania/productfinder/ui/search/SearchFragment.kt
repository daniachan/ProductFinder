package com.dania.productfinder.ui.search

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isGone
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.dania.productfinder.AppExecutors
import com.dania.productfinder.R
import com.dania.productfinder.binding.FragmentDataBindingComponent
import com.dania.productfinder.databinding.SearchFragmentBinding
import com.dania.productfinder.di.Injectable
import com.dania.productfinder.ui.common.ProductListAdapter
import com.dania.productfinder.ui.common.RetryCallback
import com.dania.productfinder.ui.common.RecordListAdapter
import com.dania.productfinder.ui.common.TaskListener
import com.dania.productfinder.util.autoCleared

/**
 * A simple [Fragment] subclass.
 * Use the [search_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<SearchFragmentBinding>()

    var adapter by autoCleared<ProductListAdapter>()
    var recordAdapter by autoCleared<RecordListAdapter>()

    val searchViewModel: SearchViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.search_fragment,
                container,
                false,
                dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        initRecyclerView()
        initRecyclerViewSuggest()
        val rvAdapter = ProductListAdapter(
                dataBindingComponent = dataBindingComponent,
                appExecutors = appExecutors,
                showFullName = true
        ) { repo ->
        }
        binding.query = searchViewModel.query
        binding.productList.adapter = rvAdapter
        adapter = rvAdapter

        val rvRecordAdapter = RecordListAdapter(
                dataBindingComponent = dataBindingComponent,
                appExecutors = appExecutors,
                showFullName = true,
                taskListener = object : TaskListener {
                    override fun onTaskClick(task: String) {
                        Timber.d("Search slected ${task}");
                        binding.deleteAllButton.isGone = true
                        binding.searchEditText.setText(task);
                        searchViewModel.setQuery(task);
                        dismissKeyboard(view.windowToken)
                    }
                },
                deleteListener = object : TaskListener {
                    override fun onTaskClick(task: String) {
                        Timber.d("Delete slected ${task}");
                        GlobalScope.launch(Dispatchers.IO) {
                            searchViewModel.deleteItems.deleteItem(task)
                        }
                    }
                }
        )

        binding.backArrow.setOnClickListener{

            binding.countProductTextView.isGone = true
            binding.recordList.isGone = false
            binding.productList.isGone = true
            binding.deleteAllButton.isGone = false
            binding.backArrow.isGone = true
            binding.searchEditText.setText("")
        }

        binding.recordList.adapter = rvRecordAdapter
        binding.countProductTextView.text = "Products"
        recordAdapter = rvRecordAdapter

        initSearchInputListener()

        binding.callback = object : RetryCallback {
            override fun retry() {
                searchViewModel.refresh()
            }
        }

        binding.deleteAllButton.setOnClickListener{

            GlobalScope.launch(Dispatchers.IO) {
                searchViewModel.deleteItems.deleteAllItems();
            }
        }

    }

    private fun initSearchInputListener() {
        binding.searchEditText.setOnEditorActionListener { view: View, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(view)
                true
            } else {
                false
            }
        }
        binding.searchEditText.setOnKeyListener { view: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(view)
                true
            } else {
                false
            }
        }
    }

    private fun doSearch(v: View) {
        val query = binding.searchEditText.text.toString()
        // Dismiss keyboard
        dismissKeyboard(v.windowToken)

        val isEmptyQuery = query.isEmpty()
        binding.countProductTextView.isGone = isEmptyQuery
        binding.recordList.isGone = !isEmptyQuery
        binding.productList.isGone  = isEmptyQuery
        binding.deleteAllButton.isGone = !isEmptyQuery
        binding.backArrow.isGone = !isEmptyQuery

        if (!query.isEmpty()) {
            searchViewModel.setQuery(query)
        }

    }

    private fun initRecyclerView() {

        binding.productList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    searchViewModel.loadNextPage()
                }
            }
        })
        binding.searchResult = searchViewModel.results
        searchViewModel.results.observe(viewLifecycleOwner, Observer { result ->
            binding.recordList.isGone = true
            binding.productList.isGone = false
            binding.backArrow.isGone = false
            adapter.submitList(result?.data)
        })

        searchViewModel.loadMoreStatus.observe(viewLifecycleOwner, Observer { loadingMore ->
            if (loadingMore == null) {
                binding.loadingMore = false
            } else {
                binding.loadingMore = loadingMore.isRunning
                val error = loadingMore.errorMessageIfNotHandled
            }
        })
    }

    private fun initRecyclerViewSuggest() {

        searchViewModel.listSuggest.observe(viewLifecycleOwner, Observer { result ->
            if (result.isEmpty()){
                binding.deleteAllButton.isGone = true
            } else {
                if (binding.searchEditText.text.isEmpty()) {
                    binding.deleteAllButton.isGone = false
                }
            }

            recordAdapter.submitList(result)

        })

    }


    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}
