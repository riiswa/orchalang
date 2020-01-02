package orcha.lang.compiler

import com.sun.net.httpserver.HttpServer
import org.springframework.boot.runApplication
import java.io.*
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Collectors

fun inputStreamToString(inputStream: InputStream): String {
    val isReader = InputStreamReader(inputStream)
    //Creating a BufferedReader object
    val reader = BufferedReader(isReader)
    return reader.lines().collect(Collectors.joining())
}

fun decodeURL(url: String): String {
    return java.net.URLDecoder.decode(url, StandardCharsets.UTF_8.name())
}

fun getQueryMap(query: String): Map<String, String> {
    val params = query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val map = HashMap<String, String>()
    for (param in params) {
        val name = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        val value = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        map[name] = value
    }
    return map
}

fun cleanDirectory(dir: File) {
    for (file in dir.listFiles()) file.delete()
}


fun main(args: Array<String>) {
    HttpServer.create(InetSocketAddress(8080), 0).apply {
        createContext("/orcha") { http ->
            val directory = "src/main/orcha/source/"
            val params = getQueryMap(decodeURL(inputStreamToString(http.requestBody)))
            if (params.containsKey("fileName") && params.containsKey("fileContent")) {
                cleanDirectory(File(directory))
                val out = PrintWriter(directory + params["fileName"])
                out.println(params["fileContent"])
                out.close()
                val baos = ByteArrayOutputStream()
                val ps = PrintStream(baos)
                val old = System.out
                System.setOut(ps)
                try {
                    runApplication<DemoApplication>(*args)
                } catch (e: Exception) {
                    println(e.message)
                } finally {
                    System.out.flush()
                    System.setOut(old)
                }
                http.responseHeaders.add("Content-type", "text/plain")
                http.sendResponseHeaders(200, 0)

                PrintWriter(http.responseBody).use { out ->
                    out.println(baos.toString())
                }
            } else {
                http.responseHeaders.add("Content-type", "text/plain")
                http.sendResponseHeaders(422, 0)
            }
        }

        start()
    }
}