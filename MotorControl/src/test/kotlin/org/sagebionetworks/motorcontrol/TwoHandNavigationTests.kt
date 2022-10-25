package org.sagebionetworks.motorcontrol

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Test
import org.junit.Assert.*
import org.sagebionetworks.assessmentmodel.*
import org.sagebionetworks.assessmentmodel.Result
import org.sagebionetworks.assessmentmodel.navigation.*
import org.sagebionetworks.assessmentmodel.serialization.*
import org.sagebionetworks.motorcontrol.navigation.TwoHandNavigator
import org.sagebionetworks.motorcontrol.serialization.TwoHandAssessmentObject


class ExampleUnitTest: NavigationTestHelper() {
    private val nodeA = OverviewStepObject("stepA")
    private val nodeB = InstructionStepObject("stepB")
    private val nodeListC = buildNodeList(2, 1, "stepC").toList()
    private val nodeC = SectionObject("left", nodeListC)
    private val nodeListD: MutableList<Node> = buildNodeList(2, 1, "stepD").toMutableList()
    private val nodeD = SectionObject("right", nodeListD)
    private val nodeE = CompletionStepObject("completion")
    private val nodeList = listOf(nodeA, nodeB, nodeC, nodeD, nodeE)

    /**
     * TwoHandNavigator - randomization of hand order
     */

    @Test
    fun testTwoHandForwardNavigation_RandomizationOfHands() {
        val nodeStates = mutableSetOf<List<String>>()
        var iterationCount = 0
        while (nodeStates.size < 2 && iterationCount < 20) {
            val assessmentObject = TwoHandAssessmentObject("foo", nodeList)
            val nodeState = BranchNodeStateImpl(assessmentObject)
            val testRootNodeController = TestRootNodeController(
                mapOf(NavigationPoint.Direction.Forward to "stepE"),
                7
            )
            nodeState.rootNodeController = testRootNodeController
            nodeState.nodeUIController = testRootNodeController
            nodeState.goForward()
            nodeStates.add(nodeState.currentResult.pathHistoryResults.map { it.identifier })
            iterationCount++
        }
        assertEquals(2, nodeStates.size)
    }

    /**
     * TwoHandNavigator - node(withIdentifier:)
     */

    @Test
    fun testTwoHandForwardNavigation_WithIdentifier() {
        val assessmentObject = TwoHandAssessmentObject("foo", nodeList)
        val nodeState = BranchNodeStateImpl(assessmentObject)
        val navigator = assessmentObject.createNavigator(nodeState)
        assertNotNull(navigator)
        assertTrue(navigator is TwoHandNavigator)

        val stepA = navigator.node("stepA")
        assertNotNull(stepA)
        assertEquals("stepA", stepA?.identifier)

        val stepF = navigator.node("stepF")
        assertNull(stepF)
    }

    @Test
    fun testTwoHandForwardNavigation_SectionWithIdentifier() {
        val assessmentObject = TwoHandAssessmentObject("foo", nodeList)
        val nodeState = BranchNodeStateImpl(assessmentObject)
        val navigator = assessmentObject.createNavigator(nodeState)
        assertNotNull(navigator)
        assertTrue(navigator is TwoHandNavigator)

        val stepC = navigator.node("left")
        assertNotNull(stepC)
        assertEquals("left", stepC?.identifier)

        val stepD = navigator.node("right")
        assertNotNull(stepD)
        assertEquals("right", stepD?.identifier)
    }

    /**
     * TwoHandNavigator - nodeAfter
     */

    @Test
    fun testTwoHandForwardNavigation_NodeAfter() {
        val assessmentObject = TwoHandAssessmentObject("foo", nodeList)
        val nodeState = BranchNodeStateImpl(assessmentObject)
        val navigator = assessmentObject.createNavigator(nodeState)
        assertNotNull(navigator)
        assertTrue(navigator is TwoHandNavigator)

        val point = navigator.nodeAfter(null, assessmentObject.createResult())
        assertEquals(nodeA, point.node)
        assertEquals(NavigationPoint.Direction.Forward, point.direction)

        val result = point.branchResult
        assertTrue(result is AssessmentResultObject)
        assertEquals(0, result.inputResults.count())
        assertEquals(0, result.pathHistoryResults.count())
    }

    @Test
    fun testTwoHandForwardNavigation_SectionNodeAfter() {
        val assessmentObject = TwoHandAssessmentObject("foo", nodeList)
        val nodeState = BranchNodeStateImpl(assessmentObject)
        val navigator = assessmentObject.createNavigator(nodeState)
        assertNotNull(navigator)
        assertTrue(navigator is TwoHandNavigator)

        val point = navigator.nodeAfter(nodeC, assessmentObject.createResult())
        assertTrue(point.node?.identifier == "right" || point.node?.identifier == "completion")
        assertEquals(NavigationPoint.Direction.Forward, point.direction)
        val point2 = navigator.nodeAfter(nodeD, assessmentObject.createResult())
        assertTrue(point2.node?.identifier == "left" || point2.node?.identifier == "completion")
        assertEquals(NavigationPoint.Direction.Forward, point2.direction)

        val result = point.branchResult
        assertTrue(result is AssessmentResultObject)
        assertEquals(0, result.inputResults.count())
        assertEquals(0, result.pathHistoryResults.count())
    }

    @Test
    fun testTwoHandForwardNavigation_CompletionNodeAfter() {
        val assessmentObject = TwoHandAssessmentObject("foo", nodeList)
        val nodeState = BranchNodeStateImpl(assessmentObject)
        val navigator = assessmentObject.createNavigator(nodeState)
        assertNotNull(navigator)
        assertTrue(navigator is TwoHandNavigator)

        val result = buildResult(assessmentObject, 4)
        assertFalse(navigator.hasNodeAfter(nodeE, result))

        val point = navigator.nodeAfter(nodeE, result)
        assertNull(point.node)
        assertEquals(NavigationPoint.Direction.Forward, point.direction)
        assertEquals(result, point.branchResult)
    }

    /**
     * TwoHandNavigator - nodeBefore
     */

    @Test
    fun testTwoHandForwardNavigation_NodeBefore() {
        val assessmentObject = TwoHandAssessmentObject("foo", nodeList)
        val nodeState = BranchNodeStateImpl(assessmentObject)
        val navigator = assessmentObject.createNavigator(nodeState)
        assertNotNull(navigator)
        assertTrue(navigator is TwoHandNavigator)

        val point = navigator.nodeBefore(nodeB, assessmentObject.createResult())
        assertEquals(nodeA, point.node)
        assertEquals(NavigationPoint.Direction.Backward, point.direction)

        val result = point.branchResult
        assertTrue(result is AssessmentResultObject)
        assertEquals(0, result.inputResults.count())
        assertEquals(0, result.pathHistoryResults.count())
    }

    @Test
    fun testTwoHandForwardNavigation_SectionNodeBefore() {
        val assessmentObject = TwoHandAssessmentObject("foo", nodeList)
        val nodeState = BranchNodeStateImpl(assessmentObject)
        val navigator = assessmentObject.createNavigator(nodeState)
        assertNotNull(navigator)
        assertTrue(navigator is TwoHandNavigator)

        val point = navigator.nodeBefore(nodeC, assessmentObject.createResult())
        assertNull(point.node)
        assertEquals(NavigationPoint.Direction.Backward, point.direction)
        val point2 = navigator.nodeBefore(nodeD, assessmentObject.createResult())
        assertNull(point2.node)
        assertEquals(NavigationPoint.Direction.Backward, point2.direction)

        val result = point.branchResult
        assertTrue(result is AssessmentResultObject)
        assertEquals(0, result.inputResults.count())
        assertEquals(0, result.pathHistoryResults.count())
    }

    @Test
    fun testTwoHandForwardNavigation_StartNodeBefore() {
        val assessmentObject = TwoHandAssessmentObject("foo", nodeList)
        val nodeState = BranchNodeStateImpl(assessmentObject)
        val navigator = assessmentObject.createNavigator(nodeState)
        assertNotNull(navigator)
        assertTrue(navigator is TwoHandNavigator)

        val result = buildResult(assessmentObject, 0)
        assertFalse(navigator.allowBackNavigation(nodeA, result))

        val point = navigator.nodeBefore(nodeA, result)
        assertNull(point.node)
        assertEquals(NavigationPoint.Direction.Backward, point.direction)
        assertEquals(result, point.branchResult)
    }

    /**
     * TwoHandNavigator - isCompleted
     */

    @Test
    fun testTwoHandForwardNavigation_StartIsCompleted() {
        val assessmentObject = TwoHandAssessmentObject("foo", nodeList)
        val nodeState = BranchNodeStateImpl(assessmentObject)
        val navigator = assessmentObject.createNavigator(nodeState)
        assertNotNull(navigator)
        assertTrue(navigator is TwoHandNavigator)

        val result = buildResult(assessmentObject, 0)
        assertFalse(navigator.isCompleted(nodeList.first(), result))
    }

    @Test fun testTwoHandForwardNavigation_SectionIsCompeleted() {
        val assessmentObject = TwoHandAssessmentObject("foo", nodeList)
        val nodeState = BranchNodeStateImpl(assessmentObject)
        val navigator = assessmentObject.createNavigator(nodeState)
        assertNotNull(navigator)
        assertTrue(navigator is TwoHandNavigator)

        val result = buildResult(assessmentObject, 0)
        assertFalse(navigator.isCompleted(nodeC, result))
    }

    @Test
    fun testTwoHandForwardNavigation_FinishIsCompleted() {
        val assessmentObject = TwoHandAssessmentObject("foo", nodeList)
        val nodeState = BranchNodeStateImpl(assessmentObject)
        val navigator = assessmentObject.createNavigator(nodeState)
        assertNotNull(navigator)
        assertTrue(navigator is TwoHandNavigator)

        val result = buildResult(assessmentObject, 0)
        assertTrue(navigator.isCompleted(nodeList.last(), result))
    }
}

open class NavigationTestHelper {

    /**
     * Helper methods
     */

    data class TestResult(override val identifier: String,
                          val answer: String
    ) : Result {

        // TODO: syoung 06/16/2020 Once timestamp generation is implemented for Android (which is the platform used for test)
        // then add checks that the dates are being updated properly to mark begin/end of steps.

        override var startDateTime: Instant = Clock.System.now()
        override var endDateTime: Instant? = null

        override fun copyResult(identifier: String): Result = copy(identifier = identifier)
    }

    class TestRootNodeController(var stepTo: Map<NavigationPoint.Direction, String> = mapOf(), var expectedCount: Int) : NodeUIController,
        RootNodeController {

        var infiniteLoop = false
        var nodeChain: MutableList<NodeState> = mutableListOf()
        var finished_called = false
        var finished_nodeState: NodeState? = null
        var finished_reason: FinishedReason? = null
        var readyToSave_called = false
        var readyToSave_nodeState: NodeState? = null
        var readyToSave_reason: FinishedReason? = null
        var finishedCalledBeforeSave = false

        override fun canHandle(node: Node): Boolean {
            return (node is Step)
        }

        override fun handleGoBack(nodeState: NodeState) {
            show(nodeState, NavigationPoint.Direction.Backward)
        }

        override fun handleGoForward(nodeState: NodeState) {
            show(nodeState, NavigationPoint.Direction.Forward)
        }

        override fun handleReadyToSave(reason: FinishedReason, nodeState: NodeState) {
            readyToSave_called = true
            readyToSave_nodeState = nodeState
            readyToSave_reason = reason
        }

        private fun show(nodeState: NodeState, direction: NavigationPoint.Direction) {
            nodeChain.add(nodeState)
            if (nodeChain.count() > expectedCount) {
                infiniteLoop = true
                return
            }
            val stepToIdentifier = stepTo[direction]
            if ((stepToIdentifier != null) && (nodeState.node.identifier != stepToIdentifier)) {
                nodeState.goIn(direction)
            }
        }

        override fun handleFinished(reason: FinishedReason, nodeState: NodeState) {
            finished_called = true
            finished_nodeState = nodeState
            finished_reason = reason
            finishedCalledBeforeSave = !readyToSave_called
        }
    }

    fun buildResult(assessmentObject: TwoHandAssessmentObject, toIndex: Int) : AssessmentResult {
        val result = assessmentObject.createResult()
        addResults(result, assessmentObject.children, 0, toIndex)
        return result
    }

    fun addResults(result: BranchNodeResult, nodeList: List<Node>, fromIndex: Int, toIndex: Int) {
        val direction = if (fromIndex <= toIndex) NavigationPoint.Direction.Forward else NavigationPoint.Direction.Backward
        val range = if (fromIndex < toIndex) (fromIndex..toIndex) else (fromIndex downTo toIndex)
        range.forEach {
            result.pathHistoryResults.add(nodeList[it].createResult())
            result.path.add(PathMarker(nodeList[it].identifier, direction))
        }
    }

    fun buildNodeList(nodeCount: Int, start: Int, prefix: String) : Sequence<InstructionStepObject>
            = generateSequence(start, { if ((it + 1) < (nodeCount + start)) (it + 1) else null }).map { InstructionStepObject(identifier = "$prefix$it") }
}