# AI Advent Kotlin Monorepo

## Стек
- Kotlin/JVM + Gradle
- Ktor HTTP client (внутри провайдера Gemini)
- kotlinx.serialization, coroutines
- Архитектура: провайдеры моделей по контракту `LlmProvider`, инструменты через `ToolExecutor`.

## Структура
```
ai-advent-kotlin/
  core/                      # контракт LLM + агент и тулзы
  provider-gemini/           # реализация провайдера Gemini через REST
  provider-deepseek/         # заглушка (под DeepSeek)
  day01-agent-cli/           # День 1: CLI чат
  day01-agent-web/           # День 1: Web чат
  day02-agent-web/           # День 2: JSON Schema ответы
  day03-agent-web/           # День 3: Сбор требований для ТЗ
```

## Запуск

### День 1 (CLI)
```bash
export GEMINI_API_KEY=***
export GEMINI_MODEL=gemini-2.0-flash
./gradlew :day01-agent-cli:run
```

### День 1 (Web)
```bash
export GEMINI_API_KEY=***
./gradlew :day01-agent-web:run
# Открыть http://localhost:8081
```

### День 2 (JSON Schema)
```bash
export GEMINI_API_KEY=***
./gradlew :day02-agent-web:run
# Открыть http://localhost:8082
```

### День 3 (Сбор требований)
```bash
export GEMINI_API_KEY=***
./gradlew :day03-agent-web:run
# Открыть http://localhost:8083
```