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
  <link rel="icon" type="image/svg+xml" href="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' width='64' height='64' viewBox='0 0 64 64'><defs><linearGradient id='grad' x1='0%25' y1='0%25' x2='100%25' y2='100%25'><stop offset='0%25' style='stop-color:%23667eea'/><stop offset='100%25' style='stop-color:%23764ba2'/></linearGradient></defs><circle cx='32' cy='28' r='20' fill='url(%23grad)'/><path d='M20 40 L24 48 L28 40' fill='url(%23grad)'/><circle cx='26' cy='24' r='2' fill='white'/><circle cx='32' cy='24' r='2' fill='white'/><circle cx='38' cy='24' r='2' fill='white'/><path d='M44 12 L46 16 L50 18 L46 20 L44 24 L42 20 L38 18 L42 16 Z' fill='%2319c37d'/></svg>"/>
  <style>
    *{box-sizing:border-box}
    body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;margin:0;padding:0;height:100vh;display:flex;flex-direction:column;background:#343541}
    .header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff;padding:20px;text-align:center;box-shadow:0 2px 10px rgba(0,0,0,.2);flex-shrink:0}
    .header h1{margin:0;font-size:24px;font-weight:600}
    .header p{margin:8px 0 0;font-size:14px;opacity:0.9}
    .chat-container{flex:1;overflow-y:auto;padding:20px;display:flex;flex-direction:column;gap:16px}
    .chat-container::-webkit-scrollbar{width:8px}
    .chat-container::-webkit-scrollbar-track{background:transparent}
    .chat-container::-webkit-scrollbar-thumb{background:#565869;border-radius:4px}
    .input-wrapper{background:#40414f;padding:16px 20px;border-top:1px solid #565869;flex-shrink:0}
    .input-container{max-width:800px;margin:0 auto;display:flex;gap:12px;align-items:center}
    input{flex:1;background:#40414f;border:1px solid #565869;color:#ececf1;padding:14px 16px;border-radius:8px;font-size:15px;outline:none;transition:border-color 0.2s}
    input:focus{border-color:#667eea}
    input::placeholder{color:#8e8ea0}
    button{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff;border:none;padding:14px 24px;border-radius:8px;font-size:15px;font-weight:500;cursor:pointer;transition:transform 0.1s,box-shadow 0.2s}
    button:hover{transform:translateY(-1px);box-shadow:0 4px 12px rgba(102,126,234,.4)}
    button:active{transform:translateY(0)}
    .message{max-width:800px;margin:0 auto;width:100%;display:flex;gap:12px;animation:fadeIn 0.3s ease-in}
    @keyframes fadeIn{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:translateY(0)}}
    .message.user{flex-direction:row-reverse}
    .avatar{width:36px;height:36px;border-radius:50%;flex-shrink:0;display:flex;align-items:center;justify-content:center;font-size:18px;font-weight:600}
    .message.user .avatar{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff}
    .message.bot .avatar{background:#19c37d;color:#fff}
    .bubble{background:#444654;color:#ececf1;padding:12px 16px;border-radius:12px;max-width:70%;word-wrap:break-word;white-space:pre-wrap;line-height:1.6;box-shadow:0 1px 2px rgba(0,0,0,.1)}
    .message.user .bubble{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff}
    .loading{display:flex;gap:4px;padding:8px 0}
    .loading-dot{width:8px;height:8px;border-radius:50%;background:#ececf1;animation:bounce 1.4s infinite ease-in-out}
    .loading-dot:nth-child(1){animation-delay:-0.32s}
    .loading-dot:nth-child(2){animation-delay:-0.16s}
    @keyframes bounce{0%,80%,100%{transform:scale(0)}40%{transform:scale(1)}}
  </style>
</head>
<body>
  <div class="header">
    <h1>🔥 AI Advent — День 1</h1>
    <p>Модель: Gemini • Инструмент: calc</p>
  </div>

  <div class="chat-container" id="chat"></div>

  <div class="input-wrapper">
    <div class="input-container">
      <input id="q" placeholder="Напишите сообщение…" />
      <button id="send">Отправить</button>
    </div>
  </div>

  <script>
    const chat = document.getElementById('chat');
    const q = document.getElementById('q');
    const sendBtn = document.getElementById('send');

    function add(role, text){
      const msg = document.createElement('div');
      msg.className = 'message ' + (role==='me'?'user':'bot');

      const avatar = document.createElement('div');
      avatar.className = 'avatar';
      avatar.textContent = role==='me'?'👤':'🤖';

      const bubble = document.createElement('div');
      bubble.className = 'bubble';
      bubble.textContent = text;

      msg.appendChild(avatar);
      msg.appendChild(bubble);
      chat.appendChild(msg);
      chat.scrollTop = chat.scrollHeight;
    }

    function showLoading(){
      const msg = document.createElement('div');
      msg.className = 'message bot';
      msg.id = 'loading-msg';

      const avatar = document.createElement('div');
      avatar.className = 'avatar';
      avatar.textContent = '🤖';

      const bubble = document.createElement('div');
      bubble.className = 'bubble';
      const loading = document.createElement('div');
      loading.className = 'loading';
      loading.innerHTML = '<div class="loading-dot"></div><div class="loading-dot"></div><div class="loading-dot"></div>';
      bubble.appendChild(loading);

      msg.appendChild(avatar);
      msg.appendChild(bubble);
      chat.appendChild(msg);
      chat.scrollTop = chat.scrollHeight;
    }

    function hideLoading(){
      const loadingMsg = document.getElementById('loading-msg');
      if(loadingMsg) loadingMsg.remove();
    }

    async function send(){
      const text = q.value.trim(); if(!text) return;
      q.value='';
      q.disabled = true;
      sendBtn.disabled = true;

      add('me', text);
      showLoading();

      try{
        const res = await fetch('/api/chat', {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({message:text})});
        const data = await res.json();
        hideLoading();
        add('bot', data.reply);
      }catch(e){
        hideLoading();
        add('bot','Ошибка: '+e.message);
      }

      q.disabled = false;
      sendBtn.disabled = false;
      q.focus();
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