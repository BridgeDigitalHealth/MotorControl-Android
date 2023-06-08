package org.sagebionetworks.motorcontrol

import android.util.Log
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.motorcontrol_android.ContainerActivity
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.sagebionetworks.assessmentmodel.AssessmentResult
import org.sagebionetworks.assessmentmodel.BranchNodeResult
import org.sagebionetworks.assessmentmodel.CollectionResult
import org.sagebionetworks.assessmentmodel.FileResult
import org.sagebionetworks.assessmentmodel.JsonFileArchivableResult
import org.sagebionetworks.assessmentmodel.Result
import org.sagebionetworks.assessmentmodel.presentation.AssessmentActivity
import org.sagebionetworks.assessmentmodel.serialization.AnswerResultObject
import org.sagebionetworks.motorcontrol.serialization.JsonCoder
import java.io.File
import java.io.FileReader
import java.net.URI

class MotorControlUITestHelper(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ContainerActivity>, ContainerActivity>,
    private val measureId: String,
    private val currentActivity: AssessmentActivity
) {

    fun assertAndClick(buttonText: String,
                       isSubstring: Boolean = false,
                       imageNames: List<String> = listOf()) {
        assertImages(imageNames)
        composeTestRule.onNodeWithText(buttonText, substring = isSubstring)
            .assertExists()
            .performClick()
    }


    fun assertImages(imageNames: List<String>) {
        for (imageName in imageNames) {
            composeTestRule.onNodeWithTag(imageName)
                .assertExists()
        }
    }

    fun selectHand(hand: String) {
        assertAndClick(hand, true)
        assertAndClick(getString(R.string.next), true)
    }

    fun performMotionStep(nextButtonToPress: String, currentImage: String, isTwoHand: Boolean = true) {
        composeTestRule.waitUntil(6000) {
            composeTestRule
                .onAllNodesWithTag(currentImage)
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.waitUntil(40000) {
            composeTestRule
                .onAllNodesWithText(nextButtonToPress)
                .fetchSemanticsNodes().size == 1
        }
        if (nextButtonToPress == currentActivity.getString(R.string.exit)) {
            checkCurrentResults(isTwoHand)
        }
        assertAndClick(nextButtonToPress)
    }


    fun performTappingStep(nextButtonToPress: String, currentImage: String) {
        assertImages(listOf(currentImage))
        composeTestRule.waitUntil(1000) {
            composeTestRule
                .onAllNodesWithText(getString(R.string.tap_button))
                .fetchSemanticsNodes().size == 2
        }

        // Simulate random tapping
        val buttons = listOf(
            composeTestRule.onNodeWithTag("LEFT_BUTTON"),
            composeTestRule.onNodeWithTag("RIGHT_BUTTON"),
            composeTestRule.onNodeWithTag("TAP_SCREEN")
        )
        for (ii in 0..75) {
            buttons[(0..2).random()].performClick()
        }
        composeTestRule.waitUntil(40000) {
            composeTestRule
                .onAllNodesWithText(nextButtonToPress)
                .fetchSemanticsNodes().size == 1
        }

        // Test that the expected data was generated into currentResults
        if (nextButtonToPress == currentActivity.getString(R.string.exit)) {
            checkCurrentResults()
        }
        assertAndClick(nextButtonToPress)
    }


    private fun checkCurrentResults(isTwoHand: Boolean = true) {
        // Test that the expected data was generated into currentResults
        val result = currentActivity.viewModel.assessmentNodeState?.currentResult
        assertNotNull(result)
        result?.let { branchNodeResult ->
            val assessmentResult = branchNodeResult as AssessmentResult
            if (isTwoHand) {
                twoHandAssertions(assessmentResult)
            } else {
                walkAndBalanceAssertions(assessmentResult)
            }
            val resultJson = JsonCoder.default.encodeToString(assessmentResult)
            validateJson(resultJson, assessmentResult.jsonSchema!!)
            recursiveCheckResults(assessmentResult)
        }
    }


    private fun twoHandAssertions(assessmentResult: AssessmentResult) {
        val handSelectionStep = assessmentResult.pathHistoryResults.filter {
            it.identifier == "handSelection"
        }[0] as AnswerResultObject
        val activeSteps = assessmentResult.pathHistoryResults.filter{
            it.identifier == "right" || it.identifier == "left"
        }

        // Assert that the identifiers are as expected
        assert(assessmentResult.assessmentIdentifier == measureId
                && assessmentResult.identifier == measureId)
        val hand = handSelectionStep.jsonValue?.let {
            Json.decodeFromJsonElement<String>(it)
        }
        // Assert the amount of active steps is accurate
        assert(activeSteps.size == if (hand == "both") 2 else 1)
        activeSteps.forEach { motionStepResult ->
            val casted = motionStepResult as BranchNodeResult
            // Assert that the results of each active step were recorded
            assert(casted.inputResults.size == 1)
        }
    }


    private fun walkAndBalanceAssertions(assessmentResult: AssessmentResult) {
        val activeSteps = assessmentResult.pathHistoryResults.filter{
            it.identifier == "walk" || it.identifier == "balance"
        }
        assert(assessmentResult.assessmentIdentifier == measureId
                && assessmentResult.identifier == measureId)
        assert(activeSteps.size == (if (measureId == "walk-thirty-second") 1 else 2))
        activeSteps.forEach { walkStepResult ->
            val casted = walkStepResult as BranchNodeResult
            assert(casted.inputResults.size == 1)
        }
    }

    private fun recursiveCheckResults(result: Result, stepPath: String? = null) {
        val pathSuffix = stepPath?.let { "$it/" } ?: ""
        val identifier = result.identifier
        val path = "$pathSuffix$identifier"
        if (result is BranchNodeResult) {
            recursiveCheckFiles(result.pathHistoryResults, stepPath)
        }
        if (result is CollectionResult) {
            recursiveCheckFiles(result.inputResults)
        }
        if (result is JsonFileArchivableResult) {
            val jsonFileArchivable = result.getJsonArchivableFile(path)
            validateJson(jsonFileArchivable.json, jsonFileArchivable.jsonSchema!!)
        }
        if (result is FileResult) {
            val file = File(result.path!!)
            val jsonString = file.readText(Charsets.UTF_8)
            validateJson(jsonString, result.jsonSchema!!)
        }
    }

    private fun recursiveCheckFiles(results: Collection<Result>, stepPath: String? = null)  {
        results.forEach {
            recursiveCheckResults(it, stepPath)
        }
    }


    fun validateJson(jsonString: String, schemaUrl: String) {
        val jsonSchema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(
            URI(schemaUrl)
        )
        jsonSchema.initializeValidators()
        val jsonNode = ObjectMapper().readTree(jsonString)
        val errors = jsonSchema.validate(jsonNode)
        Log.d("SCHEMA", jsonString)
        Assert.assertTrue("Error validating against: $schemaUrl Errors: $errors", errors.isEmpty())
    }

    fun getString(resId: Int): String {
        return currentActivity.getString(resId)
    }

}