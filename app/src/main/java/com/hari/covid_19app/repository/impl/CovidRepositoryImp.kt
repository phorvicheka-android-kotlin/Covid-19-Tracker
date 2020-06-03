package com.hari.covid_19app.repository.impl

import com.hari.covid_19app.api.CovidApiService
import com.hari.covid_19app.api.VirusTrackerApi
import com.hari.covid_19app.db.CovidDatabase
import com.hari.covid_19app.db.entity.GlobalState
import com.hari.covid_19app.db.entity.State
import com.hari.covid_19app.mapper.DataApiResponseToStateEntity
import com.hari.covid_19app.mapper.GlobalStateResponseToGlobalState
import com.hari.covid_19app.model.ErrorResult
import com.hari.covid_19app.model.Success
import com.hari.covid_19app.repository.CovidRepository
import com.hari.covid_19app.utils.ext.executeWithRetry
import com.hari.covid_19app.utils.ext.toResult
import com.hari.tmdb.model.mapper.toLambda
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CovidRepositoryImp @Inject constructor(
    private val covidApiService: CovidApiService,
    private val virusTrackerApi: VirusTrackerApi,
    private val covidDatabase: CovidDatabase,
    private val dataApiResponseToStateEntity: DataApiResponseToStateEntity,
    private val globalStateResponseToGlobalState: GlobalStateResponseToGlobalState
) : CovidRepository {

    override fun getTotalCaseInIndia() = covidDatabase.getTotalCaseInIndia()

    override fun getGlobalState(): Flow<GlobalState> = covidDatabase.getGlobalState()

    override fun getAllStates(): Flow<List<State>> = covidDatabase.getAllStates()

    override suspend fun refreshData() {
        refreshDataOfIndia()
        refreshGlobalData()
    }

    override suspend fun refreshGlobalData() {
        try {
            when (val result = virusTrackerApi.getGlobalState()
                .executeWithRetry().toResult(globalStateResponseToGlobalState.toLambda())) {
                is Success -> {
                    covidDatabase.insertGlobalStateData(result.data)
                }
                is ErrorResult -> {
                    result.throwable.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun refreshDataOfIndia() {
        try {
            when (val result = covidApiService.getData()
                .executeWithRetry().toResult(dataApiResponseToStateEntity.toLambda())) {
                is Success -> {
                    covidDatabase.insertStateWiseData(result.data)
                }
                is ErrorResult -> {
                    result.throwable.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}