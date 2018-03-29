package org.kidinov.justweather.main.weather.view

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.common_error_layout.*
import kotlinx.android.synthetic.main.weather_activity.*
import org.kidinov.justweather.R
import org.kidinov.justweather.main.common.model.City
import org.kidinov.justweather.main.util.record
import org.kidinov.justweather.main.weather.viewmodel.*
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject
import timber.log.Timber

class WeatherActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()

    private val model: WeatherViewModel by viewModel()
    private val adapter: WeatherAdapter by inject()
    private val rxPermissions: RxPermissions by inject(parameters = { mapOf("this" to this) })
    private val rxLocation: RxLocation by inject(parameters = { mapOf("this" to this) })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity)

        initViews()
        observeViewModel()
        getLocation()
        model.onRefresh(false)
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.dispose()
    }

    fun addCityByName(name: String) {
        model.onAddCityByName(name)
    }


    private fun observeViewModel() {
        model.state.observe(this, Observer { state ->
            state?.let {
                when (it) {
                    EmptyState -> showEmptyState()
                    NetworkErrorState -> showNetworkErrorNotification()
                    ErrorState -> showError()
                    ProgressState -> showProgress()
                    CurrentCityDeletionNotification -> showCurrentCityDeletionErrorNotification()
                    is ListState -> showData(it.cities)
                }
            }
        })
    }

    private fun showProgress() {
        changeScreenState(pvWeather)
    }

    private fun showData(cities: List<City>) {
        srlCities.isRefreshing = false
        changeScreenState(srlCities)
        adapter.setWeatherInCities(cities)
    }

    private fun showError() {
        srlCities.isRefreshing = false
        changeScreenState(rlErrorView)
    }

    private fun showEmptyState() {
        srlCities.isRefreshing = false
        changeScreenState(srlEmpty)
    }

    private fun showNetworkErrorNotification() {
        Snackbar.make(vfStates, R.string.network_error_text, Snackbar.LENGTH_LONG).show()
    }

    fun hideItemAtPosition(position: Int) {
        adapter.notifyItemRemoved(position)
    }

    private fun showCurrentCityDeletionErrorNotification() {
        Snackbar.make(vfStates, R.string.removal_current_error_text, Snackbar.LENGTH_LONG).show()
    }

    private fun initViews() {
        setupList()
        setupOnRefreshListeners()
        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        btnRetry.setOnClickListener { model.onRefresh(false) }

        btnAddCity.setOnClickListener {
            WeatherAddCityDialog.newInstance().show(supportFragmentManager, "WeatherAddCityDialog")
        }
    }

    private fun setupOnRefreshListeners() {
        srlCities.setOnRefreshListener({ this.fetchAllData() })
        srlEmpty.setOnRefreshListener({ this.fetchAllData() })
    }

    private fun setupList() {
        rvCities.adapter = adapter
        rvCities.layoutManager = LinearLayoutManager(this)
        rvCities.setHasFixedSize(true)

        rvCities.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0) {
                    if (btnAddCity.isShown) {
                        btnAddCity.hide()
                    }
                } else if (dy < 0 && !btnAddCity.isShown) {
                    btnAddCity.show()
                }
            }
        })

        val itemTouchHelper = ItemTouchHelper(setupCityDeletion())
        itemTouchHelper.attachToRecyclerView(rvCities)
    }

    private fun fetchAllData() {
        getLocation()
        model.onRefresh(true)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        rxPermissions
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .flatMap { granted ->
                    val locationRequest = LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                    if (granted) {
                        rxLocation
                                .location()
                                .updates(locationRequest)
                                .firstElement()
                                .toObservable()
                    } else {
                        Observable.empty()
                    }
                }
                .doFinally { srlCities.isRefreshing = false }
                .subscribe({
                    Timber.d("location - $it")
                    model.onAddCityByCoordinates(it.latitude, it.longitude)
                }, { Timber.e(it) })
                .record(compositeDisposable)
    }

    private fun changeScreenState(state: View) {
        vfStates.displayedChild = vfStates.indexOfChild(state)
    }

    private fun setupCityDeletion(): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private var inited: Boolean = false
            lateinit var background: Drawable
            lateinit var xMark: Drawable
            private var xMarkMargin: Int = 0

            private fun init() {
                background = ColorDrawable(ContextCompat.getColor(this@WeatherActivity, R.color.colorAccent))
                xMark = ContextCompat.getDrawable(this@WeatherActivity, R.drawable.ic_delete_white_24dp)!!
                xMarkMargin = this@WeatherActivity.resources.getDimension(R.dimen.default_margin).toInt()
                inited = true
            }

            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val swipedPosition = viewHolder.adapterPosition
                model.onItemRemovedAtPosition(swipedPosition)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                if (!inited) {
                    init()
                }
                val itemView = viewHolder.itemView

                background.setBounds(itemView.right + dX.toInt(), itemView.top,
                        itemView.right, itemView.bottom)
                background.draw(c)

                // draw x mark
                val itemHeight = itemView.bottom - itemView.top
                val intrinsicWidth = xMark.intrinsicWidth
                val intrinsicHeight = xMark.intrinsicWidth

                val xMarkLeft = itemView.right - xMarkMargin - intrinsicWidth
                val xMarkRight = itemView.right - xMarkMargin
                val xMarkTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val xMarkBottom = xMarkTop + intrinsicHeight
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom)

                xMark.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
    }

}
