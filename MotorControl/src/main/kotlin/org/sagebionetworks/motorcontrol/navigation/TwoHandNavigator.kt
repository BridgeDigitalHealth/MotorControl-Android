//
//  TwoHandNavigator.kt
//
//

package org.sagebionetworks.motorcontrol.navigation

import org.sagebionetworks.assessmentmodel.*
import org.sagebionetworks.assessmentmodel.navigation.NavigationPoint
import org.sagebionetworks.assessmentmodel.navigation.Navigator
import org.sagebionetworks.assessmentmodel.navigation.Direction
import org.sagebionetworks.assessmentmodel.navigation.Progress

open class TwoHandNavigator(node: NodeContainer): Navigator {
    private val nodes: List<Node>
    private val handSelectionIdentifier = "handSelection"

    init {
        if (node.children.toSet().size != node.children.size) {
            throw IdentifiersNotUniqueException("Identifiers are not unique")
        }
        val swapHands: Boolean = (0..1).random() == 1
        val tempNodesList = node.children.toMutableList()

        // This assumes that the two hands are next to each other in the tempNodesList
        val firstHandIndex = tempNodesList.indexOfFirst { it.hand() != null }
        if (firstHandIndex == -1) {
            throw NoHandFoundException("No hands were found")
        }
        if (swapHands) {
            tempNodesList.swap(firstHandIndex, firstHandIndex + 1)
        }
        nodes = tempNodesList
    }

    override fun node(identifier: String): Node? {
        return nodes.find { it.identifier == identifier }
    }

    override fun hasNodeAfter(currentNode: Node, branchResult: BranchNodeResult): Boolean {
        return nextNode(currentNode.identifier, currentHandSelection(branchResult)) != null
    }

    override fun nodeAfter(currentNode: Node?, branchResult: BranchNodeResult): NavigationPoint {
        return NavigationPoint(
            nextNode(currentNode?.identifier, currentHandSelection(branchResult)),
            branchResult,
            Direction.Forward)
    }

    override fun nodeBefore(currentNode: Node?, branchResult: BranchNodeResult): NavigationPoint {
        return NavigationPoint(previousNode(currentNode), branchResult, Direction.Backward)
    }

    override fun allowBackNavigation(currentNode: Node, branchResult: BranchNodeResult): Boolean {
        return previousNode(currentNode) != null
    }

    override fun progress(currentNode: Node, branchResult: BranchNodeResult): Progress? {
        return null
    }

    override fun isCompleted(currentNode: Node, branchResult: BranchNodeResult): Boolean {
        return isCompleted(currentNode)
    }

    private fun currentHandSelection(branchResult: BranchNodeResult): HandSelection? {
        val answer = branchResult.pathHistoryResults
            .findLast { it.identifier == handSelectionIdentifier } as? AnswerResult ?: return null
        val rawValue = answer.jsonValue.toString().replace("\"", "").uppercase()
        return try {
            HandSelection.valueOf(rawValue)
        }
        catch (exception: Exception) {
            null
        }
    }

    private fun firstNode(): Node {
        return nodes.first()
    }

    private fun nextNode(identifier: String?, handSelection: HandSelection?): Node? {
        if (identifier == null) {
            return firstNode()
        }
        val currentNodeIndex = nodes.indexOfFirst { it.identifier == identifier }
        if (currentNodeIndex + 1 >= nodes.size) {
            return null
        }
        val nextNode = nodes[currentNodeIndex + 1]
        if (handSelection == null || nextNode.hand() == null || handSelection == nextNode.hand()) {
            return nextNode
        }
        if (currentNodeIndex + 2 >= nodes.size) {
            return null
        }
        return nodes[currentNodeIndex + 2]
    }

    private fun previousNode(currentNode: Node?): Node? {
        // Return null if currentNode is null or is at/passed the hand section steps
        if (currentNode == null || isCompleted(currentNode) || currentNode.hand() != null) {
            return null
        }
        val currentNodeIndex = nodes.indexOfFirst { it.identifier == currentNode.identifier }
        if (currentNodeIndex < 1) {
            return null
        }
        return nodes[currentNodeIndex - 1]
    }

    private fun isCompleted(currentNode: Node): Boolean {
        return currentNode.identifier == nodes.last().identifier && currentNode is CompletionStep
    }
}

enum class HandSelection {
    LEFT,
    RIGHT
}

fun Node.hand(): HandSelection? {
    return try {
        /**
        If this node's identifier is left or right then we are interested in hand selection for the
        navigator to handle skipping hand sections correctly. Otherwise, we can always just keep
        navigating forward.
        */
        HandSelection.valueOf(this.identifier.uppercase())
    } catch (exception: Exception) {
        null
    }
}

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

class IdentifiersNotUniqueException(message: String): Exception(message)
class NoHandFoundException(message: String): Exception(message)