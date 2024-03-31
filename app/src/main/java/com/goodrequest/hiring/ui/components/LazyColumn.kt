package com.goodrequest.hiring.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> PullToRefreshLazyColumnWithState(
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
    Box {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .pullRefresh(state)
        ) {
            this.itemsIndexed(items) { index, pokemon ->
               /* if (index == items.size - 1) {
                    onRefresh(RefreshType.Paging(items.size.getPageFromIndex()))
                }*/
                content(pokemon)
            }

            pagingState?.let {
                when (it) {
                    is PagingState.Error -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
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
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
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

private fun Int.getPageFromIndex() = this / 20 + 1


sealed class RefreshType {
    data object Pull : RefreshType()
    data class Paging(val page: Int) : RefreshType()
    data object Retry : RefreshType()
}


sealed class PagingState {
    data object Refreshing : PagingState()
    data object Error : PagingState()
}