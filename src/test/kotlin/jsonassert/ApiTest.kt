package jsonassert


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.portfoligno.json.ast.Json
import org.junit.Test

class ApiTest {
    @Test
    fun `json object assertion`() {
        val json = """
        {
          "array": [
            {
              "abc": "def"
            },
            {
              "that": 3,
              "foo": false,
              "bar": null
            },
            "baz",
            4,
            true,
            null
          ]
        }
        """.trimIndent()

        objectMapper.readValue<Json>(json) shouldBe json {
            "array" {
                0 {
                    "abc" shouldBe "def"
                }
                1 {
                    "that" shouldBe 3
                    "this" shouldBe Undefined
                    "foo" shouldBe false
                    "bar" shouldBe null
                }
                2 shouldBe "baz"
                3 shouldBe 4
                4 shouldBe true
                5 shouldBe null
                6 shouldBe Undefined
            }
        }
    }

    @Test
    fun `json primitive value assertion` () {
        val cases: Map<String, JsonMatcher.JsonPrimitiveMatcher> = mapOf(
                "3" to json(3),
                "null" to json(null),
                "true" to json(true),
                "\"baz\"" to json("baz")
        )
        cases.forEach {
            objectMapper.readValue<Json>(it.key) shouldBe it.value
        }
    }

    private val objectMapper: ObjectMapper = jacksonObjectMapper()
}
