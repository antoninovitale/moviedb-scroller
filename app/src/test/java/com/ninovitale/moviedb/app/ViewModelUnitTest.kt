package com.ninovitale.moviedb.app

import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.whenever
import com.ninovitale.moviedb.app.api.MovieApi
import com.ninovitale.moviedb.app.api.model.Configuration
import com.ninovitale.moviedb.app.api.model.Images
import com.ninovitale.moviedb.app.api.model.Page
import com.ninovitale.moviedb.app.api.model.Result
import com.ninovitale.moviedb.app.data.ConfigurationDao
import com.ninovitale.moviedb.app.data.ConfigurationDbItem
import com.ninovitale.moviedb.app.data.MovieDataSourceFactory
import com.ninovitale.moviedb.app.data.MovieRepository
import com.ninovitale.moviedb.app.domain.MovieUseCase
import com.ninovitale.moviedb.app.ui.MovieBoundaryCallback
import com.ninovitale.moviedb.app.ui.model.Movie
import com.ninovitale.moviedb.app.ui.model.ViewState
import com.ninovitale.moviedb.app.utils.InjectTestScheduler
import com.ninovitale.moviedb.app.utils.InstantExecutorExtension
import com.ninovitale.moviedb.app.utils.TestSchedulerExtension
import com.ninovitale.moviedb.app.viewmodel.MovieViewModel
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class, TestSchedulerExtension::class)
class ViewModelUnitTest {
    @InjectTestScheduler
    internal lateinit var scheduler: TestScheduler

    @Mock
    lateinit var api: MovieApi

    @Mock
    lateinit var repo: MovieRepository

    @Mock
    lateinit var configDao: ConfigurationDao

    @Mock
    lateinit var observer: Observer<ViewState>

    @Mock
    lateinit var moviesObserver: Observer<PagedList<Movie>>

    private lateinit var viewModel: MovieViewModel

    private lateinit var movieDataSourceFactory: MovieDataSourceFactory

    private lateinit var movieBoundaryCallback: MovieBoundaryCallback

    private lateinit var useCase: MovieUseCase

    @BeforeAll
    fun init() {
        MockitoAnnotations.initMocks(this)
    }

    @BeforeEach
    fun setUp() {
        useCase = MovieUseCase(api, repo, configDao)
        movieDataSourceFactory = MovieDataSourceFactory(useCase)
        movieBoundaryCallback = MovieBoundaryCallback(useCase)
        viewModel = MovieViewModel(movieDataSourceFactory, movieBoundaryCallback)
    }

    @Test
    fun test_loadMovies_configAvailable() {
        whenever(configDao.load()).thenReturn(Maybe.just(ConfigurationDbItem("url", "w120")))
        whenever(api.getConfiguration(any())).thenReturn(Single.just(createConfigurationForTest()))
        whenever(api.getPopularMovies(any(), anyOrNull(), anyOrNull(), page = any())).thenReturn(
            Single.just(createPageForTest())
        )
        doNothing().`when`(repo).write(any())

        viewModel.getViewState().observeForever(observer)
        viewModel.getMovies().observeForever(moviesObserver)

        Assertions.assertEquals(ViewState.Loading(true), viewModel.getViewState().value)

        scheduler.triggerActions()

        Assertions.assertEquals(ViewState.Loading(false), viewModel.getViewState().value)
        Assertions.assertTrue(viewModel.getMovies().value?.isNotEmpty() == true)
    }

    @Test
    fun test_loadMovies_loadConfig() {
        whenever(configDao.load()).thenReturn(Maybe.empty())
        whenever(api.getConfiguration(any())).thenReturn(Single.just(createConfigurationForTest()))
        whenever(api.getPopularMovies(any(), anyOrNull(), anyOrNull(), page = any())).thenReturn(
            Single.just(createPageForTest())
        )
        doNothing().`when`(repo).write(any())

        viewModel.getViewState().observeForever(observer)
        viewModel.getMovies().observeForever(moviesObserver)

        Assertions.assertEquals(ViewState.Loading(true), viewModel.getViewState().value)

        scheduler.triggerActions()

        Assertions.assertEquals(ViewState.Loading(false), viewModel.getViewState().value)
        Assertions.assertTrue(viewModel.getMovies().value?.isNotEmpty() == true)
        Assertions.assertTrue(viewModel.getMovies().value?.size == PAGE_SIZE)
    }

    @Test
    fun test_loadMovies_emptyListOfMovies() {
        whenever(configDao.load()).thenReturn(Maybe.empty())
        whenever(api.getConfiguration(any())).thenReturn(Single.just(createConfigurationForTest()))
        whenever(api.getPopularMovies(any(), anyOrNull(), anyOrNull(), page = any())).thenReturn(
            Single.just(createPageForTest(0))
        )
        doNothing().`when`(repo).write(any())

        viewModel.getViewState().observeForever(observer)
        viewModel.getMovies().observeForever(moviesObserver)

        Assertions.assertEquals(ViewState.Loading(true), viewModel.getViewState().value)

        scheduler.triggerActions()

        Assertions.assertEquals(ViewState.Loading(false), viewModel.getViewState().value)
        Assertions.assertTrue(viewModel.getMovies().value?.isEmpty() == true)
    }

    @Test
    fun test_loadMovies_errorLoadingMovies() {
        whenever(configDao.load()).thenReturn(Maybe.empty())
        whenever(api.getConfiguration(any())).thenReturn(Single.just(createConfigurationForTest()))
        whenever(api.getPopularMovies(any(), anyOrNull(), anyOrNull(), page = any())).thenReturn(
            Single.error(Exception("an error occurred!"))
        )
        doNothing().`when`(repo).write(any())

        viewModel.getViewState().observeForever(observer)
        viewModel.getMovies().observeForever(moviesObserver)

        Assertions.assertEquals(ViewState.Loading(true), viewModel.getViewState().value)

        scheduler.triggerActions()

        Assertions.assertEquals(ViewState.Error, viewModel.getViewState().value)
        Assertions.assertTrue(viewModel.getMovies().value?.isEmpty() == true)
    }

    @Test
    fun test_loadMovies_errorLoadingConfig() {
        whenever(configDao.load()).thenReturn(Maybe.empty())
        whenever(api.getConfiguration(any())).thenReturn(Single.error(Exception("an error occurred!")))
        whenever(api.getPopularMovies(any(), anyOrNull(), anyOrNull(), page = any())).thenReturn(
            Single.just(createPageForTest())
        )
        doNothing().`when`(repo).write(any())

        viewModel.getViewState().observeForever(observer)
        viewModel.getMovies().observeForever(moviesObserver)

        Assertions.assertEquals(ViewState.Loading(true), viewModel.getViewState().value)

        scheduler.triggerActions()

        Assertions.assertEquals(ViewState.Error, viewModel.getViewState().value)
        Assertions.assertTrue(viewModel.getMovies().value?.isEmpty() == true)
    }

    companion object {
        private const val PAGE_SIZE = 50

        private fun createConfigurationForTest() =
            Configuration(Images(secureBaseUrl = "url", posterSizes = listOf("w400")))

        private fun createMovieForTest(id: Int = 1) =
            Result(id, "title", "overview", "image", "2019", 5.0)

        private fun createMoviesForTest(count: Int = PAGE_SIZE): List<Result> {
            val movies = mutableListOf<Result>()
            repeat(count) {
                movies.add(createMovieForTest(it))
            }
            return movies
        }

        private fun createPageForTest(count: Int = PAGE_SIZE) =
            Page(1, createMoviesForTest(count), 500, 10000)
    }
}