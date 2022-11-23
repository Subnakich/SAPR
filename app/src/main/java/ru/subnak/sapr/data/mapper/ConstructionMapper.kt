package ru.subnak.sapr.data.mapper

import ru.subnak.sapr.data.database.entity.ConstructionDbModel
import ru.subnak.sapr.data.database.entity.KnotDbModel
import ru.subnak.sapr.data.database.entity.RodDbModel
import ru.subnak.sapr.data.database.relation.ConstructionWithValuesDbModel
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.domain.model.Knot
import ru.subnak.sapr.domain.model.Rod
import javax.inject.Inject

class ConstructionMapper @Inject constructor() {

    fun mapListConstructionToEntity(list: List<ConstructionDbModel>) = list.map {
        mapConstructionDbModelToEntity(it)
    }

    private fun mapConstructionDbModelToEntity(construction: ConstructionDbModel): Construction {
        val emptyListRod: List<Rod> = emptyList()
        val emptyListKnot: List<Knot> = emptyList()
        return Construction(
            id = construction.id,
            date = construction.date,
            knotValues = emptyListKnot,
            rodValues = emptyListRod,
            img = construction.img,
        )
    }

    fun mapConstructionWithValuesEntityToDbModel(construction: Construction) =
        ConstructionWithValuesDbModel(
            mapConstructionEntityToDbModel(construction),
            mapListRodEntityToDbModel(construction.rodValues),
            mapListKnotEntityToDbModel(construction.knotValues)
        )

    private fun mapConstructionEntityToDbModel(construction: Construction) = ConstructionDbModel(
        id = construction.id,
        date = construction.date,
        img = construction.img
    )

    private fun mapListRodEntityToDbModel(list: List<Rod>) = list.map {
        mapRodEntityToDbModel(it)
    }

    private fun mapRodEntityToDbModel(rod: Rod) = RodDbModel(
        square = rod.square,
        elasticModule = rod.elasticModule,
        voltage = rod.voltage,
        loadRunning = rod.loadRunning,
        rodId = rod.rodId,
        constructionId = rod.constructionId,
        rodNumber = rod.rodNumber
    )

    private fun mapListKnotEntityToDbModel(list: List<Knot>) = list.map {
        mapKnotEntityToDbModel(it)
    }

    private fun mapKnotEntityToDbModel(knot: Knot) = KnotDbModel(
        x = knot.x,
        loadConcentrated = knot.loadConcentrated,
        prop = knot.prop,
        knotId = knot.knotId,
        constructionId = knot.constructionId,
        knotNumber = knot.knotNumber
    )

    fun mapConstructionDbModelWithValuesToEntity(construction: ConstructionWithValuesDbModel) =
        Construction(
            id = construction.constructionDbModel.id,
            date = construction.constructionDbModel.date,
            rodValues = mapListRodValueDbModelToEntity(construction.rodDbModels),
            knotValues = mapListKnotValueDbModelToEntity(construction.knotDbModels),
            img = construction.constructionDbModel.img,
        )

    private fun mapListRodValueDbModelToEntity(list: List<RodDbModel>) = list.map {
        mapRodDbModelToEntity(it)
    }

    private fun mapRodDbModelToEntity(rod: RodDbModel) = Rod(
        square = rod.square,
        elasticModule = rod.elasticModule,
        voltage = rod.voltage,
        loadRunning = rod.loadRunning,
        rodId = rod.rodId,
        constructionId = rod.constructionId,
        rodNumber = rod.rodNumber
    )

    private fun mapListKnotValueDbModelToEntity(list: List<KnotDbModel>) = list.map {
        mapKnotDbModelToEntity(it)
    }

    private fun mapKnotDbModelToEntity(knot: KnotDbModel) = Knot(
        x = knot.x,
        loadConcentrated = knot.loadConcentrated,
        prop = knot.prop,
        knotId = knot.knotId,
        constructionId = knot.constructionId,
        knotNumber = knot.knotNumber
    )
}