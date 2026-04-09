# Connecting Claude to Weather Bridge MCP

> Make sure the server is running before adding the config (`mvn spring-boot:run` or `docker-compose up`).

## Claude Desktop

1. Open (or create) the Claude Desktop config file:

   | Platform | Path |
   |----------|------|
   | macOS    | `~/Library/Application Support/Claude/claude_desktop_config.json` |
   | Windows  | `%APPDATA%\Claude\claude_desktop_config.json` |
   | Linux    | `~/.config/claude-desktop/config.json` |

2. Merge the following into your config (or use the provided `claude_desktop_config.json` as a starting point):

   ```json
   {
     "mcpServers": {
       "weather-bridge": {
         "type": "sse",
         "url": "http://localhost:8080/sse"
       }
     }
   }
   ```

3. **Restart Claude Desktop** — the weather tools will appear automatically.

4. Try it:
   > *"What's the weather like in Tokyo right now?"*
   > *"Give me a 3-day forecast for Berlin."*

---

## Claude Code (CLI)

Add the server to your Claude Code MCP config:

```bash
claude mcp add weather-bridge --transport sse http://localhost:8080/sse
```

Or add it manually to `~/.claude/mcp.json`:

```json
{
  "mcpServers": {
    "weather-bridge": {
      "type": "sse",
      "url": "http://localhost:8080/sse"
    }
  }
}
```

Then in any Claude Code session you can ask weather questions directly in your terminal.
