package ru.subnak.sapr.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.multik.api.linalg.solve
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.domain.usecase.GetConstructionUseCase
import javax.inject.Inject

typealias ConstructionFunction = (x: Double) -> Double

class CalculatingViewModel @Inject constructor(
    private val getConstructionUseCase: GetConstructionUseCase
) : ViewModel() {

    data class Result(
        val Ux: List<ConstructionFunction>,
        val Nx: List<ConstructionFunction>,
        val Sx: List<ConstructionFunction>
    )

    private val _construction = MutableLiveData<Construction>()
    val construction: LiveData<Construction>
        get() = _construction

    fun getConstruction(constructionId: Int) {
        viewModelScope.launch {
            _construction.value = getConstructionUseCase.invoke(constructionId)
        }
    }

    fun calculateComponents(construction: Construction): Result {
        val nodes = construction.nodeValues
        val rods = construction.rodValues
        val b = nodes.map {
            it.loadConcentrated
        }.toMutableList()

        val length = mutableListOf<Double>()
        for ((index, node) in nodes.withIndex()) {
            if (index + 1 != nodes.size) {
                length.add(nodes[index + 1].x - node.x)
            }
        }

        val EAL = rods.mapIndexed { index, rod ->
            (rod.elasticModule * rod.square) / length[index]
        }

        val A = mk.zeros<Double>(nodes.size, nodes.size)

        for (i in 0..nodes.size - 2) {
            A[i + 1, i + 1] = EAL[i]
            if (nodes[i].prop) {
                A[i, i] = 1.0
                A[i, i + 1] = 0.0
                A[i + 1, i] = 0.0
                if (i > 0) {
                    A[i, i - 1] = 0.0
                    A[i - 1, i] = 0.0
                }
            } else {
                A[i, i] += EAL[i]
                A[i, i + 1] = -EAL[i]
                A[i + 1, i] = -EAL[i]
            }
        }

        val lastNode = nodes.lastIndex
        if (nodes[lastNode].prop) {
            A[lastNode,lastNode] = 1.0
            A[lastNode,lastNode - 1] = 0.0
            A[lastNode - 1,lastNode] = 0.0
        }

        b[0] += (rods[0].loadRunning * length[0]) / 2
        for (i in 1 until b.size - 1) {
            b[i] += (rods[i - 1].loadRunning * length[i - 1] + rods[i].loadRunning * length[i]) / 2
        }
        b[b.size - 1] += rods[b.size - 2].loadRunning * length[b.size - 2] / 2

        for (i in 0 until b.size) {
            if (nodes[i].prop) {
                b[i] = 0.0
            }
        }

        val solve = mk.linalg.solve(A, mk.ndarray(b))

        val Nx = mutableListOf<ConstructionFunction>()
        val Ux = mutableListOf<ConstructionFunction>()
        val Sx = mutableListOf<ConstructionFunction>()

        for(i in 0 until solve.size-1) {
            val rod = rods[i]
            val l = length[i]
            val e = rod.elasticModule
            val a = rod.square
            val q = rod.loadRunning

            val u0 = solve[i]
            val ul = solve[i+1]

            Ux.add {
                u0 + (it / l) * (ul - u0) + ((q * l * it) / (2 * e * a)) * (1 - it / l)
            }

            Nx.add {
                ((e * a) / l) * (ul - u0) + ((q * l) / 2) * (1 - (2 * it) / l)
            }

            Sx.add {
                Nx[i].invoke(it) / a
            }

//            Ux.add(u0 + (x / l) * (ul - u0) + ((q * l * x) / (2 * e * a)) * (1 - x / l))
//            Nx.add(((e * a) / l) * (ul - u0) + ((q * l) / 2) * (1 - (2 * x) / l))
//            Sx.add(Nx[i] / a)
        }
        val res = Result(Ux, Nx, Sx)
        Log.d("kek", res.toString())
        for (i in Ux){
            Log.d("kek", i.invoke(0.0).toString())
        }
        return res

    }
}

