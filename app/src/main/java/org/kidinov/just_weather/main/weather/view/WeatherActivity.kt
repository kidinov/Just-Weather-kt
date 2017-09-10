package org.kidinov.just_weather.main.weather.view

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
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
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.sdk15.listeners.onClick
import org.kidinov.just_weather.R
import org.kidinov.just_weather.main.App
import org.kidinov.just_weather.main.common.model.City
import org.kidinov.just_weather.main.weather.DaggerWeatherComponent
import org.kidinov.just_weather.main.weather.WeatherContract
import org.kidinov.just_weather.main.weather.presenter.WeatherPresenter
import org.kidinov.just_weather.main.weather.presenter.WeatherPresenterModule
import timber.log.Timber
import javax.inject.Inject

class WeatherActivity : AppCompatActivity(), WeatherContract.View {
    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var presenter: WeatherPresenter
    @Inject
    lateinit var adapter: WeatherAdapter
    @Inject
    lateinit var rxPermissions: RxPermissions
    @Inject
    lateinit var rxLocation: RxLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerWeatherComponent.builder()
                .repositoryComponent((application as App).component)
                .weatherPresenterModule(WeatherPresenterModule(this))
                .weatherActivityModule(WeatherActivityModule(this))
                .build()
                .inject(this)
        setContentView(R.layout.weather_activity)

        initViews()

        presenter.updateData(true)

        getLocation()
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.dispose()
        presenter.unsubscribe()
    }

    fun addCityByName(name: String) {
        presenter.addCityByName(name)
    }

    override fun showProgress() {
        changeScreenState(pvWeather)
    }

    override fun showData(cities: List<City>) {
        Timber.d("showData - $cities.size")
        srlCities.isRefreshing = false
        changeScreenState(srlCities)
        adapter.setWeatherInCities(cities)
    }

    override fun showError() {
        srlCities.isRefreshing = false
        changeScreenState(rlErrorView)
    }

    override fun showEmptyState() {
        srlCities.isRefreshing = false
        changeScreenState(srlEmpty)
    }

    override fun showNetworkErrorNotification() {
        longSnackbar(vfStates, R.string.network_error_text)
    }

    override fun hideItemAtPosition(position: Int) {
        adapter.notifyItemRemoved(position)
    }

    override fun showCurrentCityDeletionErrorNotification() {
        longSnackbar(vfStates, R.string.removal_current_error_text)
    }

    private fun initViews() {
        setupList()
        setupOnRefreshListeners()
        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        btnRetry.onClick { presenter.updateData(false) }

        btnAddCity.onClick { WeatherAddCityDialog.newInstance().show(supportFragmentManager, "WeatherAddCityDialog") }
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
        presenter.updateData(true)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        compositeDisposable.add(rxPermissions
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
                    presenter.addCityByCoordinates(it.latitude, it.longitude)
                }, { Timber.e(it) }))
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
                xMark = ContextCompat.getDrawable(this@WeatherActivity, R.drawable.ic_delete_white_24dp)
                xMarkMargin = this@WeatherActivity.resources.getDimension(R.dimen.default_margin).toInt()
                inited = true
            }

            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val swipedPosition = viewHolder.adapterPosition
                presenter.itemRemovedAtPosition(swipedPosition)
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
