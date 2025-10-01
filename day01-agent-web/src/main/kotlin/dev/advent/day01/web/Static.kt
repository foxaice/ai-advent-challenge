package dev.advent.day01.web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.staticIndex() {
    get("/") {
        call.respondText(indexHtml, ContentType.Text.Html)
    }
}

private val indexHtml = """
<!doctype html>
<html lang="ru">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>AI Advent — День 1 веб-чат</title>
  <style>
    body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Ubuntu;margin:0;padding:0;height:100vh;display:flex;flex-direction:column}
    .header{max-width:760px;width:100%;margin:0 auto;padding:40px 16px 0;flex-shrink:0}
    .chat-container{flex:1;overflow-y:auto;max-width:760px;width:100%;margin:0 auto;padding:0 16px}
    .input-wrapper{max-width:760px;width:100%;margin:0 auto;padding:0 16px 16px;flex-shrink:0}
    .card{border:1px solid #ddd;border-radius:16px;padding:16px;margin:12px 0;box-shadow:0 4px 12px rgba(0,0,0,.04)}
    .row{display:flex;gap:8px}
    input,button{font-size:16px;padding:12px;border-radius:12px;border:1px solid #ccc}
    input{flex:1}
    button{cursor:pointer}
    .msg{white-space:pre-wrap}
    .me{color:#333}
    .bot{color:#111}
    .muted{color:#666;font-size:12px}
  </style>
</head>
<body>
  <div class="header">
    <h1>🔥 AI Advent — День 1: первый агент (веб)</h1>
    <p class="muted">Модель: Gemini (через REST). Инструмент: <code>calc</code>.</p>
  </div>

  <div class="chat-container" id="chat"></div>

  <div class="input-wrapper">
    <div class="card">
      <div class="row">
        <input id="q" placeholder="Напишите сообщение…" />
        <button id="send">Отправить</button>
      </div>
    </div>
  </div>

  <script>
    const chat = document.getElementById('chat');
    const q = document.getElementById('q');
    const sendBtn = document.getElementById('send');

    function add(role, text){
      const div = document.createElement('div');
      div.className = 'card msg ' + (role==='me'?'me':'bot');
      div.textContent = (role==='me'?'👤 ':'🤖 ') + text;
      chat.appendChild(div);
      chat.scrollTop = chat.scrollHeight;
    }

    async function send(){
      const text = q.value.trim(); if(!text) return; q.value=''; add('me', text);
      try{
        const res = await fetch('/api/chat', {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({message:text})});
        const data = await res.json(); add('bot', data.reply);
      }catch(e){ add('bot','Ошибка: '+e.message); }
    }

    q.addEventListener('keydown', e=>{ if(e.key==='Enter') send(); });
    sendBtn.addEventListener('click', send);
  </script>
</body>
</html>
"""

// =============================
// README addition: web запуск
// =============================
// В README.md добавьте раздел:
// ## Веб-чат (Ktor)
// ```bash
// export GEMINI_API_KEY=***
// export GEMINI_MODEL=gemini-2.0-flash
// ./gradlew :day01-agent-web:run
// # откройте http://localhost:8080
// ```