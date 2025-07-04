package com.example.financemanager.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.financemanager.data.MyViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

@Composable
private fun JetpackComposeBasicLineChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = {_,y,_ ->
                    "${y.toInt()}"
                }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = {context,x,_ ->
                    val epochDay = x.toLong()
                    val date = java.time.LocalDate.ofEpochDay(epochDay)
                    date.toString().drop(5) // Format: "MM-DD"
                },
                guideline = remember { null },
            ),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}

@Composable
fun JetpackComposeBasicLineChart(
    modifier: Modifier = Modifier,
    viewModel: MyViewModel
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val dataPoints = viewModel.dailyBalance.map { it.date.toEpochDay().toFloat() to it.balance.toFloat() }
    LaunchedEffect(dataPoints) {
        modelProducer.runTransaction {
            val xValues = dataPoints.map { it.first }
            val yValues = dataPoints.map { it.second }

            lineSeries{
                series(
                    xValues,yValues
                )
            }
        }
    }
    JetpackComposeBasicLineChart(modelProducer, modifier)
}
