package com.goodrequest.hiring.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.goodrequest.hiring.model.PagingState
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> PullToRefreshLazyColumnWithStateIndicator(
    modifier: Modifier = Modifier,
    items: List<T>,
    isRefreshing: Boolean,
    pagingState: PagingState? = null,
    onRefresh: (type: RefreshType) -> Unit,
    content: @Composable (T) -> Unit,
) {
    val state = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            onRefresh(RefreshType.Pull)
        })
    val lazyListState = rememberLazyListState()
    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems == 0) {
                return@derivedStateOf false
            } else {
                lastVisibleIndex > totalItems - LOAD_BUFFER_SIZE
            }
        }
    }
    LaunchedEffect(true) {
        snapshotFlow {
            loadMore.value
        }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                onRefresh(RefreshType.Paging)
            }
    }

    Box {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .pullRefresh(state),
            state = lazyListState
        ) {
            this.items(items) { pokemon ->
                content(pokemon)
            }
            pagingState?.let {
                when (it) {
                    is PagingState.Error -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,

                                ) {
                                Text("Error")
                                Button(onClick = { onRefresh(RefreshType.Retry) }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }

                    is PagingState.Refreshing -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CircularProgressIndicator(
                                    modifier.padding(vertical = 8.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = state,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

private const val LOAD_BUFFER_SIZE = 3

sealed class RefreshType {
    data object Pull : RefreshType()
    data object Paging : RefreshType()
    data object Retry : RefreshType()
}

