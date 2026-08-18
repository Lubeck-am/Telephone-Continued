package de.leximon.telephone.web

import com.sun.net.httpserver.HttpServer
import de.leximon.telephone.LOGGER
import de.leximon.telephone.shardManager
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

fun startDashboard(port: Int = 8080) {
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", port), 0)
    server.createContext("/") { exchange ->
        val html = renderDashboard()
        val bytes = html.toByteArray(StandardCharsets.UTF_8)
        exchange.responseHeaders.add("Content-Type", "text/html; charset=utf-8")
        exchange.sendResponseHeaders(200, bytes.size.toLong())
        exchange.responseBody.use { it.write(bytes) }
    }
    server.executor = null
    server.start()
    LOGGER.info("Dashboard available at http://localhost:$port")
}

private fun renderDashboard(): String {
    val guilds = shardManager.guilds.sortedBy { it.name }
    val rows = guilds.joinToString("\n") { guild ->
        val icon = guild.iconUrl
        val avatar = if (icon != null)
            "<img src=\"$icon\" width=\"32\" height=\"32\" class=\"icon\">"
        else
            "<div class=\"icon icon-placeholder\">${guild.name.take(1).uppercase()}</div>"
        """
        <tr>
            <td><div class="guild">$avatar <span>${escapeHtml(guild.name)}</span></div></td>
            <td><code>${guild.id}</code></td>
            <td>${guild.memberCount}</td>
        </tr>
        """.trimIndent()
    }

    return """
        <!DOCTYPE html>
        <html lang="fr">
        <head>
            <meta charset="UTF-8">
            <meta http-equiv="refresh" content="15">
            <title>Telephone Bot — Serveurs</title>
            <style>
                body { font-family: -apple-system, "Segoe UI", sans-serif; background: #2b2d31; color: #f2f3f5; padding: 32px; margin: 0; }
                h1 { color: #fff; margin: 0 0 4px; }
                .count { color: #96989d; margin: 0 0 24px; }
                table { border-collapse: collapse; width: 100%; max-width: 720px; }
                th, td { text-align: left; padding: 10px 14px; border-bottom: 1px solid #3f4147; }
                th { color: #96989d; text-transform: uppercase; font-size: 11px; letter-spacing: 0.03em; }
                tr:hover td { background: #313338; }
                .guild { display: flex; align-items: center; gap: 10px; }
                .icon { border-radius: 50%; display: inline-block; }
                .icon-placeholder { width: 32px; height: 32px; background: #5865f2; color: #fff; text-align: center; line-height: 32px; font-weight: 600; }
                code { color: #96989d; font-size: 12px; }
            </style>
        </head>
        <body>
            <h1>📞 Telephone Bot</h1>
            <p class="count">${guilds.size} serveur${if (guilds.size > 1) "s" else ""} connecté${if (guilds.size > 1) "s" else ""} · rafraîchi automatiquement toutes les 15s</p>
            <table>
                <tr><th>Serveur</th><th>ID / numéro</th><th>Membres</th></tr>
                $rows
            </table>
        </body>
        </html>
    """.trimIndent()
}

private fun escapeHtml(text: String) = text
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
