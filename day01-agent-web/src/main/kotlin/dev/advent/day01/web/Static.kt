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
    body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;margin:0;padding:0;height:100vh;display:flex;flex-direction:column;background:#343541;overflow:hidden}
    .header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff;padding:20px;text-align:center;box-shadow:0 4px 20px rgba(102,126,234,.3);flex-shrink:0;position:relative;overflow:hidden;animation:slideDown 0.6s cubic-bezier(0.34,1.56,0.64,1)}
    @keyframes slideDown{from{transform:translateY(-100%);opacity:0}to{transform:translateY(0);opacity:1}}
    .header::before{content:'';position:absolute;top:-50%;left:-50%;width:200%;height:200%;background:radial-gradient(circle,rgba(255,255,255,0.1) 0%,transparent 70%);animation:pulse 4s ease-in-out infinite}
    @keyframes pulse{0%,100%{transform:scale(1);opacity:0.5}50%{transform:scale(1.1);opacity:0.8}}
    .header h1{margin:0;font-size:24px;font-weight:600;position:relative;z-index:1}
    .header p{margin:8px 0 0;font-size:14px;opacity:0.9;position:relative;z-index:1}
    .chat-container{flex:1;overflow-y:auto;padding:20px;display:flex;flex-direction:column;gap:16px}
    .chat-container::-webkit-scrollbar{width:8px}
    .chat-container::-webkit-scrollbar-track{background:transparent}
    .chat-container::-webkit-scrollbar-thumb{background:#565869;border-radius:4px;transition:background 0.3s}
    .chat-container::-webkit-scrollbar-thumb:hover{background:#6b6d7d}
    .input-wrapper{background:#40414f;padding:16px 20px;border-top:1px solid #565869;flex-shrink:0;animation:slideUp 0.6s cubic-bezier(0.34,1.56,0.64,1)}
    @keyframes slideUp{from{transform:translateY(100%);opacity:0}to{transform:translateY(0);opacity:1}}
    .input-container{max-width:800px;margin:0 auto;display:flex;gap:12px;align-items:center}
    input{flex:1;background:#40414f;border:1px solid #565869;color:#ececf1;padding:14px 16px;border-radius:8px;font-size:15px;outline:none;transition:all 0.3s cubic-bezier(0.4,0,0.2,1)}
    input:focus{border-color:#667eea;box-shadow:0 0 0 3px rgba(102,126,234,.15),0 0 20px rgba(102,126,234,.1);transform:translateY(-1px)}
    input::placeholder{color:#8e8ea0}
    button{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff;border:none;padding:14px 24px;border-radius:8px;font-size:15px;font-weight:500;cursor:pointer;transition:all 0.3s cubic-bezier(0.4,0,0.2,1);position:relative;overflow:hidden}
    button::before{content:'';position:absolute;top:50%;left:50%;width:0;height:0;border-radius:50%;background:rgba(255,255,255,0.3);transform:translate(-50%,-50%);transition:width 0.6s,height 0.6s}
    button:hover::before{width:300px;height:300px}
    button:hover{transform:translateY(-2px);box-shadow:0 8px 24px rgba(102,126,234,.5)}
    button:active{transform:translateY(0);box-shadow:0 4px 12px rgba(102,126,234,.3)}
    .message{max-width:800px;margin:0 auto;width:100%;display:flex;gap:12px;animation:messageSlideIn 0.5s cubic-bezier(0.34,1.56,0.64,1)}
    @keyframes messageSlideIn{from{opacity:0;transform:translateY(30px) scale(0.9)}to{opacity:1;transform:translateY(0) scale(1)}}
    .message.user{flex-direction:row-reverse}
    .avatar{width:36px;height:36px;border-radius:50%;flex-shrink:0;display:flex;align-items:center;justify-content:center;font-size:18px;font-weight:600;animation:avatarBounce 0.6s cubic-bezier(0.68,-0.55,0.265,1.55);box-shadow:0 4px 12px rgba(0,0,0,.2);user-select:none}
    @keyframes avatarBounce{0%{transform:scale(0) rotate(-180deg)}60%{transform:scale(1.2) rotate(20deg)}100%{transform:scale(1) rotate(0)}}
    .message.user .avatar{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff}
    .message.bot .avatar{background:linear-gradient(135deg,#19c37d 0%,#0f9960 100%);color:#fff;animation:avatarPulse 2s ease-in-out infinite}
    @keyframes avatarPulse{0%,100%{box-shadow:0 4px 12px rgba(0,0,0,.2)}50%{box-shadow:0 4px 20px rgba(25,195,125,.5)}}
    .bubble{background:#444654;color:#ececf1;padding:12px 16px;border-radius:12px;max-width:70%;word-wrap:break-word;white-space:pre-wrap;line-height:1.6;box-shadow:0 2px 8px rgba(0,0,0,.15);position:relative;animation:bubblePop 0.4s cubic-bezier(0.68,-0.55,0.265,1.55) 0.1s backwards;user-select:text;cursor:default}
    @keyframes bubblePop{0%{transform:scale(0);opacity:0}70%{transform:scale(1.05)}100%{transform:scale(1);opacity:1}}
    .message.user .bubble{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff;box-shadow:0 4px 16px rgba(102,126,234,.3)}
    .message.bot .bubble{background:linear-gradient(135deg,#444654 0%,#3a3b4a 100%)}
    .loading{display:flex;gap:6px;padding:8px 0}
    .loading-dot{width:10px;height:10px;border-radius:50%;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);animation:loadingBounce 1.4s infinite ease-in-out;box-shadow:0 2px 8px rgba(102,126,234,.4)}
    .loading-dot:nth-child(1){animation-delay:-0.32s}
    .loading-dot:nth-child(2){animation-delay:-0.16s}
    .loading-dot:nth-child(3){animation-delay:0s}
    @keyframes loadingBounce{0%,80%,100%{transform:scale(0.6) translateY(0);opacity:0.5}40%{transform:scale(1) translateY(-10px);opacity:1}}
    button:disabled{opacity:0.6;cursor:not-allowed;transform:none}
    button:disabled::before{display:none}
    input:disabled{opacity:0.6}
    .typing-cursor{display:inline-block;width:2px;height:1em;background:#ececf1;margin-left:2px;animation:blink 1s step-end infinite;vertical-align:text-bottom}
    @keyframes blink{0%,100%{opacity:1}50%{opacity:0}}
    @media (max-width: 768px){
      .header h1{font-size:20px}
      .header p{font-size:12px}
      .header{padding:16px}
      .chat-container{padding:12px;gap:12px}
      .input-wrapper{padding:12px}
      .input-container{gap:8px}
      input{padding:12px 14px;font-size:14px}
      button{padding:12px 18px;font-size:14px}
      .message{gap:8px}
      .avatar{width:32px;height:32px;font-size:16px}
      .bubble{padding:10px 14px;font-size:14px;max-width:75%}
    }
    @media (max-width: 480px){
      .header{padding:14px 12px}
      .header h1{font-size:18px}
      .header p{font-size:11px}
      .chat-container{padding:10px 8px;gap:10px}
      .input-wrapper{padding:10px 12px}
      .input-container{gap:6px}
      .message{gap:6px}
      .avatar{width:28px;height:28px;font-size:14px}
      .bubble{padding:8px 12px;font-size:13px;max-width:82%;line-height:1.5}
      input{padding:10px 12px;font-size:14px;border-radius:6px}
      button{padding:10px 16px;font-size:13px;border-radius:6px}
    }
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

    function add(role, text, animate = false){
      const msg = document.createElement('div');
      msg.className = 'message ' + (role==='me'?'user':'bot');

      const avatar = document.createElement('div');
      avatar.className = 'avatar';
      avatar.textContent = role==='me'?'👤':'🤖';

      const bubble = document.createElement('div');
      bubble.className = 'bubble';

      msg.appendChild(avatar);
      msg.appendChild(bubble);
      chat.appendChild(msg);
      chat.scrollTop = chat.scrollHeight;

      if(animate && role==='bot'){
        let i = 0;
        const speed = 20;
        function typeWriter(){
          if(i < text.length){
            bubble.textContent += text.charAt(i);
            i++;
            chat.scrollTop = chat.scrollHeight;
            setTimeout(typeWriter, speed);
          }
        }
        typeWriter();
      } else {
        bubble.textContent = text;
      }
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

    function replaceLoadingWithTyping(text){
      const loadingMsg = document.getElementById('loading-msg');
      if(!loadingMsg) return;

      const bubble = loadingMsg.querySelector('.bubble');
      bubble.innerHTML = '';

      let i = 0;
      const speed = 20;
      function typeWriter(){
        if(i < text.length){
          bubble.textContent += text.charAt(i);
          i++;
          chat.scrollTop = chat.scrollHeight;
          setTimeout(typeWriter, speed);
        }
      }
      typeWriter();
      loadingMsg.id = '';
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
        replaceLoadingWithTyping(data.reply);
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

    // Auto-focus input on page load
    window.addEventListener('load', () => q.focus());
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