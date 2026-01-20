package com.proq.cryptosignals.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun SignalRepository.dashboardSummaryFlow(): Flow<DashboardSummary> = flow {
    while (true) {
        emit(dashboardSummaryOnce())
        delay(3_000)
    }
}
