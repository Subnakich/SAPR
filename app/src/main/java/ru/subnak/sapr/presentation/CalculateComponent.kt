package ru.subnak.sapr.presentation

import org.jetbrains.kotlinx.multik.api.linalg.solve
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import ru.subnak.sapr.domain.model.Construction

typealias ConstructionFunction = (x: Double) -> Double

class CalculateComponent {

    data class CalculatedResult(
        val Ux: List<ConstructionFunction>,
        val Nx: List<ConstructionFunction>,
        val Sx: List<ConstructionFunction>
    )

    fun calculate(construction: Construction): CalculatedResult {
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

        val eal = rods.mapIndexed { index, rod ->
            (rod.elasticModule * rod.square) / length[index]
        }

        val a = mk.zeros<Double>(nodes.size, nodes.size)

        for (i in 0..nodes.size - 2) {
            a[i + 1, i + 1] = eal[i]
            if (nodes[i].prop) {
                a[i, i] = 1.0
                a[i, i + 1] = 0.0
                a[i + 1, i] = 0.0
                if (i > 0) {
                    a[i, i - 1] = 0.0
                    a[i - 1, i] = 0.0
                }
            } else {
                a[i, i] += eal[i]
                a[i, i + 1] = -eal[i]
                a[i + 1, i] = -eal[i]
            }
        }

        val lastNode = nodes.lastIndex
        if (nodes[lastNode].prop) {
            a[lastNode, lastNode] = 1.0
            a[lastNode, lastNode - 1] = 0.0
            a[lastNode - 1, lastNode] = 0.0
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

        val solve = mk.linalg.solve(a, mk.ndarray(b))

        val nx = mutableListOf<ConstructionFunction>()
        val ux = mutableListOf<ConstructionFunction>()
        val sx = mutableListOf<ConstructionFunction>()

        for (i in 0 until solve.size - 1) {
            val rod = rods[i]
            val l = length[i]
            val e = rod.elasticModule
            val square = rod.square
            val q = rod.loadRunning

            val u0 = solve[i]
            val ul = solve[i + 1]

            ux.add {
                u0 + (it / l) * (ul - u0) + ((q * l * it) / (2 * e * square)) * (1 - it / l)
            }

            nx.add {
                ((e * square) / l) * (ul - u0) + ((q * l) / 2) * (1 - (2 * it) / l)
            }

            sx.add {
                nx[i].invoke(it) / square
            }
        }
        return CalculatedResult(ux, nx, sx)
    }
}