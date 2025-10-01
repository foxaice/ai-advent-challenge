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
  day01-agent-cli/           # CLI чат для Дня 1
```
## Запуск
```bash
# 1) Клонировать/распаковать проект
# 2) Экспортировать ключ
export GEMINI_API_KEY=***
# (опционально) выбрать модель
export GEMINI_MODEL=gemini-2.0-flash

# 3) Сборка
./gradlew :day01-agent-cli:run
```