package com.smartstyle.data.remote

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.smartstyle.domain.model.OutfitAnalysis
import org.json.JSONObject

class GeminiService(apiKey: String) {

    private val model = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = apiKey
    )

    suspend fun analyzeOutfit(bitmap: Bitmap, mlKitLabels: List<String>): OutfitAnalysis {
        val detectedHint = if (mlKitLabels.isNotEmpty())
            "On-device detected items: ${mlKitLabels.joinToString(", ")}.\n\n"
        else ""

        val prompt = """
            You are an expert fashion stylist. Analyze this outfit photo and provide structured feedback.

            ${detectedHint}Respond with valid JSON only — no markdown, no code fences, no extra text:
            {
              "assessment": "A 2-3 sentence style assessment of the overall look",
              "suggestions": [
                "Specific actionable improvement 1",
                "Specific actionable improvement 2",
                "Specific actionable improvement 3"
              ],
              "tags": ["tag1", "tag2", "tag3", "tag4", "tag5", "tag6"]
            }

            Tags must cover: style category (e.g. casual, formal, streetwear, bohemian, preppy, athleisure), main colors, season (spring/summer/autumn/winter), occasion (work/weekend/evening/gym), and notable garments. Max 8 tags.
        """.trimIndent()

        val response = model.generateContent(
            content {
                image(bitmap)
                text(prompt)
            }
        )

        val raw = response.text ?: throw Exception("Empty response from Gemini")
        return parseResponse(raw)
    }

    private fun parseResponse(raw: String): OutfitAnalysis {
        val json = raw.trim()
            .removePrefix("```json").removePrefix("```")
            .removeSuffix("```").trim()

        val obj = JSONObject(json)

        val suggestionsArr = obj.getJSONArray("suggestions")
        val suggestions = (0 until suggestionsArr.length()).map { suggestionsArr.getString(it) }

        val tagsArr = obj.getJSONArray("tags")
        val tags = (0 until tagsArr.length()).map { tagsArr.getString(it) }

        return OutfitAnalysis(
            assessment = obj.getString("assessment"),
            suggestions = suggestions,
            tags = tags,
            mlKitLabels = emptyList()
        )
    }
}
