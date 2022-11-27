package ru.subnak.sapr.data.mapper

import ru.subnak.sapr.data.database.entity.ConstructionDbModel
import ru.subnak.sapr.data.database.entity.NodeDbModel
import ru.subnak.sapr.data.database.entity.RodDbModel
import ru.subnak.sapr.data.database.relation.ConstructionWithValuesDbModel
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.domain.model.Node
import ru.subnak.sapr.domain.model.Rod
import javax.inject.Inject

class ConstructionMapper @Inject constructor() {

    fun mapListConstructionToEntity(list: List<ConstructionDbModel>) = list.map {
        mapConstructionDbModelToEntity(it)
    }

    private fun mapConstructionDbModelToEntity(construction: ConstructionDbModel): Construction {
        val emptyListRod: List<Rod> = emptyList()
        val emptyListNode: List<Node> = emptyList()
        return Construction(
            id = construction.id,
            date = construction.date,
            nodeValues = emptyListNode,
            rodValues = emptyListRod,
            img = construction.img,
        )
    }

    fun mapConstructionWithValuesEntityToDbModel(construction: Construction) =
        ConstructionWithValuesDbModel(
            mapConstructionEntityToDbModel(construction),
            mapListRodEntityToDbModel(construction.rodValues),
            mapListNodeEntityToDbModel(construction.nodeValues)
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
        tension = rod.tension,
        loadRunning = rod.loadRunning,
        rodId = rod.rodId,
        constructionId = rod.constructionId
    )

    private fun mapListNodeEntityToDbModel(list: List<Node>) = list.map {
        mapNodeEntityToDbModel(it)
    }

    private fun mapNodeEntityToDbModel(node: Node) = NodeDbModel(
        x = node.x,
        loadConcentrated = node.loadConcentrated,
        prop = node.prop,
        nodeId = node.nodeId,
        constructionId = node.constructionId
    )

    fun mapConstructionDbModelWithValuesToEntity(construction: ConstructionWithValuesDbModel) =
        Construction(
            id = construction.constructionDbModel.id,
            date = construction.constructionDbModel.date,
            rodValues = mapListRodValueDbModelToEntity(construction.rodDbModels),
            nodeValues = mapListNodeValueDbModelToEntity(construction.nodeDbModels),
            img = construction.constructionDbModel.img,
        )

    private fun mapListRodValueDbModelToEntity(list: List<RodDbModel>) = list.map {
        mapRodDbModelToEntity(it)
    }

    private fun mapRodDbModelToEntity(rod: RodDbModel) = Rod(
        square = rod.square,
        elasticModule = rod.elasticModule,
        tension = rod.tension,
        loadRunning = rod.loadRunning,
        rodId = rod.rodId,
        constructionId = rod.constructionId
    )

    private fun mapListNodeValueDbModelToEntity(list: List<NodeDbModel>) = list.map {
        mapNodeDbModelToEntity(it)
    }

    private fun mapNodeDbModelToEntity(node: NodeDbModel) = Node(
        x = node.x,
        loadConcentrated = node.loadConcentrated,
        prop = node.prop,
        nodeId = node.nodeId,
        constructionId = node.constructionId
    )
}