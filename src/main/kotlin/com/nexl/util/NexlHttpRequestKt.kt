/*
 * User: eldad
 * Date: 01/09/2019
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA.
 *
 */

package com.idi.astro.nexl

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Singleton
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import kotlin.reflect.KClass


/**
 *
 */
@Singleton
class NexlHttpRequestKt {
    inline fun <reified T> logger(): Logger {
        return LoggerFactory.getLogger(T::class.java)
    }

    val logger = logger<NexlHttpRequestKt>()
    fun sendGet() {
        val url = URL("'http://nexl:8181/nexl/storage/load-file-from-storage'")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"  // optional default is GET
            println("\nSent 'POST' request to URL : $url; Response Code : $responseCode")
            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    println(line)
                }
            }
        }
    }

    fun fetchIt(url: String, clazz: KClass<*>): Any {
        val openConnection = URL(url).openConnection() as HttpURLConnection
        try {
            openConnection.apply {
                readTimeout = 800
                connectTimeout = 200
                setRequestProperty("Accept", "*/*")
            }.getInputStream().use { it ->
                return parseResponseForRequestedType(openConnection, it, clazz)
            }
        } finally {
            openConnection.disconnect()
        }
    }

    private fun <T : Any> parseResponseForRequestedType(openConnection: HttpURLConnection, ins: InputStream, clazz: KClass<T>?): Any {
        if (openConnection.getHeaderField("Content-Type").startsWith("text/plain")) {
            return openConnection.inputStream.bufferedReader().use { it.readText() }
        }
        if (openConnection.getHeaderField("Content-Type").startsWith("application/json")) {
            val jsonResult = IOUtils.toString(ins, "UTF-8")
            val jsonFactory = JsonFactory()
            val jsonParser = createJsonParser(jsonFactory, InputStreamReader(ByteArrayInputStream((jsonResult as String).toByteArray())))
            val objectMapper = createJsonMapper(jsonFactory)
            return objectMapper.readValue(jsonParser, clazz!!.javaObjectType)
        }

        throw RuntimeException("Failure parsing Nexl resource for url=" + openConnection.url + " response=" + convert(ins))
    }


    @Throws(IOException::class)
    fun convert(inputStream: InputStream): String {
        return inputStream.bufferedReader().use { it.readText() }
    }

    fun createJsonMapper(jsonFactory: JsonFactory): ObjectMapper {
        val objectMapper = ObjectMapper(jsonFactory).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        return objectMapper
    }


    fun createJsonParser(jsonFactory: JsonFactory, streamReader: Reader): JsonParser {
        val parser = jsonFactory.createParser(streamReader);
        parser.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        return parser;
    }


    data class Repo(val name: String, val url: String, val topics: List<String>, val updatedAt: LocalDateTime)

    data class Nexl(val name: String, val url: String, val nexls: List<Nexl>, val resource: String)
}