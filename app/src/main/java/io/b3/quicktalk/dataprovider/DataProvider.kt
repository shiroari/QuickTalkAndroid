package io.b3.quicktalk.dataprovider

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.google.common.io.LineReader
import io.b3.quicktalk.model.CardInstance
import io.b3.quicktalk.model.CardSetHeader
import io.b3.quicktalk.model.CardSetType
import io.b3.quicktalk.model.FileCardSet
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.regex.Pattern

interface DataProvider {
    fun list(): List<CardSetHeader>
    fun load(uri: String, header: CardSetHeader): FileCardSet
}

class DataProviderImpl(context: Context) : DataProvider {

    private val METADATA_PATTERN = Pattern.compile("^(\\d+)(\\s?-\\s?(.*))?$")

    private val assets: AssetManager = context.assets

    // FIXME: remove nulls
    private class Metadata {
        internal var title: String? = null
        internal var index: Int = 0
    }

    override fun list(): List<CardSetHeader> {

        val list = ArrayList<CardSetHeader>()

        try {

            for (line in readLines("data/dir.txt")) {
                val md = parseLine(line) ?: continue
                val id = String.format("file%d", md.index)
                val uri = String.format("data/d%d.txt", md.index)
                list.add(CardSetHeader(id, md.title!!, CardSetType.File, uri))
            }

        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        return list
    }

    override fun load(uri: String, header: CardSetHeader): FileCardSet {

        var cards: List<CardInstance>

        try {

            val backData = readLines(header.uri)
            val frontData = readLines(header.uri.replace(".txt", "tr.txt"))

            val count = Math.min(backData.size, frontData.size)
            cards = ArrayList(count)

            for (i in 0..count - 1) {
                cards.add(CardInstance(i, frontData[i], backData[i]))
            }

        } catch (e: Exception) {
            Log.e("resources", "Can not read catalog", e)
            cards = emptyList()
        }

        return FileCardSet(header, cards)
    }

    private fun parseLine(line: String): Metadata? {
        val m = METADATA_PATTERN.matcher(line)
        if (m.find()) {
            val md = Metadata()
            md.index = Integer.parseInt(m.group(1))
            if (m.groupCount() == 3) {
                md.title = m.group(3)
            } else {
                md.title = m.group(1)
            }
            return md
        }
        return null
    }

    private fun readLines(resource: String): List<String> {
        val lines = ArrayList<String>()
        try {
            assets.open(resource).use { io ->
                InputStreamReader(io).use { reader ->
                    val lineReader = LineReader(reader)
                    var line: String? = null
                    // TODO: better solution?
                    do {
                        line = lineReader.readLine()
                        if (line == null) {
                            break
                        }
                        lines.add(line)
                    } while (true)
                }
            }
        } catch (e: IOException) {
            Log.e("resources", "Can not read file: " + resource, e)
        }

        return lines
    }
}