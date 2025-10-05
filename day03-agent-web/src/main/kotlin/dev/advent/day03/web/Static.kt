package dev.advent.day03.web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.staticIndex() {
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
  <title>🔥 AI Advent — День 3: Сказочная утка - Сбор Требований</title>
  <link rel="icon" type="image/svg+xml" href="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' width='64' height='64' viewBox='0 0 64 64'><defs><linearGradient id='grad' x1='0%25' y1='0%25' x2='100%25' y2='100%25'><stop offset='0%25' style='stop-color:%23667eea'/><stop offset='100%25' style='stop-color:%23764ba2'/></linearGradient></defs><rect x='8' y='8' width='48' height='48' rx='8' fill='url(%23grad)'/><text x='32' y='40' font-size='28' text-anchor='middle' fill='white' font-weight='bold'>🦆</text></svg>"/>
  <style>
    *{box-sizing:border-box}
    body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;margin:0;padding:0;height:100vh;display:flex;flex-direction:column;background:#1a1a2e;overflow:hidden}
    .header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff;padding:20px;text-align:center;box-shadow:0 4px 20px rgba(102,126,234,.4);flex-shrink:0;position:relative;overflow:hidden;animation:slideDown 0.6s cubic-bezier(0.34,1.56,0.64,1)}
    @keyframes slideDown{from{transform:translateY(-100%);opacity:0}to{transform:translateY(0);opacity:1}}
    .header::before{content:'';position:absolute;top:-50%;left:-50%;width:200%;height:200%;background:radial-gradient(circle,rgba(255,255,255,0.15) 0%,transparent 70%);animation:pulse 4s ease-in-out infinite}
    @keyframes pulse{0%,100%{transform:scale(1);opacity:0.5}50%{transform:scale(1.1);opacity:0.8}}
    .header h1{margin:0;font-size:24px;font-weight:600;position:relative;z-index:1;display:flex;align-items:center;justify-content:center;gap:10px}
    .header p{margin:8px 0 0;font-size:14px;opacity:0.95;position:relative;z-index:1;font-weight:500}
    .badge{background:rgba(255,255,255,0.25);padding:4px 12px;border-radius:12px;font-size:12px;font-weight:700;letter-spacing:1px}
    .chat-container{flex:1;overflow-y:auto;padding:20px;display:flex;flex-direction:column;gap:16px}
    .chat-container::-webkit-scrollbar{width:8px}
    .chat-container::-webkit-scrollbar-track{background:transparent}
    .chat-container::-webkit-scrollbar-thumb{background:#2d2d44;border-radius:4px;transition:background 0.3s}
    .chat-container::-webkit-scrollbar-thumb:hover{background:#3d3d54}
    .message{max-width:100%;width:100%;display:flex;gap:12px;animation:messageSlideIn 0.5s cubic-bezier(0.34,1.56,0.64,1)}
    @keyframes messageSlideIn{from{opacity:0;transform:translateY(30px) scale(0.9)}to{opacity:1;transform:translateY(0) scale(1)}}
    .message.user{flex-direction:row-reverse}
    .avatar{width:36px;height:36px;border-radius:50%;flex-shrink:0;display:flex;align-items:center;justify-content:center;font-size:18px;font-weight:600;animation:avatarBounce 0.6s cubic-bezier(0.68,-0.55,0.265,1.55);box-shadow:0 4px 12px rgba(0,0,0,.3)}
    @keyframes avatarBounce{0%{transform:scale(0) rotate(-180deg)}60%{transform:scale(1.2) rotate(20deg)}100%{transform:scale(1) rotate(0)}}
    .message.user .avatar{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff}
    .message.assistant .avatar{background:linear-gradient(135deg,#00d2ff 0%,#3a7bd5 100%);color:#fff}
    .bubble{background:#0f1419;color:#e0e0e0;padding:12px 16px;border-radius:12px;word-wrap:break-word;white-space:pre-wrap;line-height:1.6;box-shadow:0 2px 8px rgba(0,0,0,.2);position:relative;animation:bubblePop 0.4s cubic-bezier(0.68,-0.55,0.265,1.55) 0.1s backwards;border:1px solid #2d2d44;flex:1}
    @keyframes bubblePop{0%{transform:scale(0);opacity:0}70%{transform:scale(1.05)}100%{transform:scale(1);opacity:1}}
    .message.user .bubble{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff;border:none}
    .complete-badge{display:inline-block;background:#48bb78;color:white;padding:4px 12px;border-radius:12px;font-size:12px;font-weight:600;margin-top:8px}
    .tz-content{background:#16213e;border-left:4px solid #667eea;padding:16px;margin-top:12px;border-radius:4px;font-family:'Menlo','Monaco','Courier New',monospace;font-size:13px;line-height:1.6;border:1px solid #2d2d44}
    .tz-content h2{color:#4facfe;margin-top:12px;margin-bottom:8px;font-size:14px;font-weight:600}
    .loading{display:flex;gap:6px;padding:8px 0}
    .loading-dot{width:10px;height:10px;border-radius:50%;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);animation:loadingBounce 1.4s infinite ease-in-out;box-shadow:0 2px 8px rgba(102,126,234,.4)}
    .loading-dot:nth-child(1){animation-delay:-0.32s}
    .loading-dot:nth-child(2){animation-delay:-0.16s}
    .loading-dot:nth-child(3){animation-delay:0s}
    @keyframes loadingBounce{0%,80%,100%{transform:scale(0.6) translateY(0);opacity:0.5}40%{transform:scale(1) translateY(-10px);opacity:1}}
    .controls{display:flex;justify-content:space-between;align-items:center;padding:15px 20px;background:#16213e;border-top:1px solid #2d2d44;flex-shrink:0}
    .session-id{font-size:12px;color:#718096}
    .input-wrapper{background:#16213e;padding:16px 20px;border-top:1px solid #2d2d44;flex-shrink:0;animation:slideUp 0.6s cubic-bezier(0.34,1.56,0.64,1)}
    @keyframes slideUp{from{transform:translateY(100%);opacity:0}to{transform:translateY(0);opacity:1}}
    .input-container{max-width:100%;margin:0 auto;display:flex;gap:12px;align-items:center}
    textarea{flex:1;background:#0f1419;border:1px solid #2d2d44;color:#e0e0e0;padding:14px 16px 14px 12px;border-radius:8px;font-size:15px;outline:none;transition:all 0.3s cubic-bezier(0.4,0,0.2,1);font-family:inherit;resize:none;min-height:50px;max-height:200px;overflow-y:auto}
    textarea:focus{border-color:#667eea;box-shadow:0 0 0 3px rgba(102,126,234,.2),0 0 20px rgba(102,126,234,.15);transform:translateY(-1px)}
    textarea::placeholder{color:#6c757d}
    textarea::-webkit-scrollbar{width:8px}
    textarea::-webkit-scrollbar-track{background:transparent;margin:4px}
    textarea::-webkit-scrollbar-thumb{background:#2d2d44;border-radius:4px}
    button{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff;border:none;padding:14px 24px;border-radius:8px;font-size:15px;font-weight:600;cursor:pointer;transition:all 0.3s cubic-bezier(0.4,0,0.2,1);position:relative;overflow:hidden;white-space:nowrap}
    button::before{content:'';position:absolute;top:50%;left:50%;width:0;height:0;border-radius:50%;background:rgba(255,255,255,0.3);transform:translate(-50%,-50%);transition:width 0.6s,height 0.6s}
    button:hover::before{width:300px;height:300px}
    button:hover{transform:translateY(-2px);box-shadow:0 8px 24px rgba(102,126,234,.5)}
    button:active{transform:translateY(0);box-shadow:0 4px 12px rgba(102,126,234,.3)}
    button:disabled{opacity:0.6;cursor:not-allowed;transform:none}
    button:disabled::before{display:none}
    .reset-btn{background:#e53e3e;padding:10px 20px;font-size:13px}
    @media (max-width: 768px){
      .header h1{font-size:20px}
      .header p{font-size:12px}
    }
  </style>
</head>
<body>
  <div class="header">
    <h1>🔥 AI Advent — День 3 <span class="badge">🦆 УТКА MODE</span></h1>
    <p>Волшебный сказочник создаёт описание твоей сказочной утки • Session: <span id="sessionId"></span></p>
  </div>

  <div class="chat-container" id="chat"></div>

  <div class="controls">
    <button class="reset-btn" onclick="resetChat()">Начать заново</button>
  </div>

  <div class="input-wrapper">
    <div class="input-container">
      <textarea id="q" placeholder="Введите ваш ответ…" rows="1"></textarea>
      <button id="send">Отправить</button>
    </div>
  </div>

  <script>
    const chat = document.getElementById('chat');
    const q = document.getElementById('q');
    const sendBtn = document.getElementById('send');
    const sessionId = 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    document.getElementById('sessionId').textContent = sessionId;

    let isComplete = false;

    function escapeHtml(text) {
      const div = document.createElement('div');
      div.textContent = text;
      return div.innerHTML;
    }

    function formatTz(content) {
      return content
        .split('\n')
        .map(line => {
          if (line.startsWith('## ')) {
            return `<h2>${'$'}{escapeHtml(line.substring(3))}</h2>`;
          }
          return escapeHtml(line);
        })
        .join('<br>');
    }

    async function add(role, text, complete = false) {
      const msg = document.createElement('div');
      msg.className = 'message ' + (role === 'me' ? 'user' : 'assistant');

      const avatar = document.createElement('div');
      avatar.className = 'avatar';
      avatar.textContent = role === 'me' ? '👤' : '🦆';

      const bubble = document.createElement('div');
      bubble.className = 'bubble';

      msg.appendChild(avatar);
      msg.appendChild(bubble);
      chat.appendChild(msg);

      // Для сообщений пользователя показываем сразу
      if (role === 'me') {
        bubble.innerHTML = escapeHtml(text);
        chat.scrollTop = chat.scrollHeight;
        return;
      }

      // Для ответов бота - анимация печати
      let messageContent = text;
      let tzContent = '';

      // Проверяем наличие описания сказочной утки
      const duckMatch = text.match(/===СКАЗОЧНАЯ УТКА===([\s\S]*?)===КОНЕЦ СКАЗКИ===/);
      if (duckMatch) {
        const beforeDuck = text.substring(0, duckMatch.index);
        tzContent = duckMatch[1].trim();
        messageContent = beforeDuck + '\n\n✨ Сказочная утка создана:';
      }

      // Печатаем основной текст
      await typeText(bubble, messageContent);

      // Если есть ТЗ утки, добавляем его с анимацией
      if (tzContent) {
        const tzDiv = document.createElement('div');
        tzDiv.className = 'tz-content';
        bubble.appendChild(tzDiv);

        // Печатаем содержимое ТЗ с форматированием
        await typeMarkdown(tzDiv, tzContent);
      }

      // Добавляем badge если завершено
      if (complete) {
        const badge = document.createElement('div');
        badge.className = 'complete-badge';
        badge.textContent = '✓ Готово';
        bubble.appendChild(badge);
      }

      chat.scrollTop = chat.scrollHeight;
    }

    async function typeText(element, text) {
      let currentText = '';

      for (let i = 0; i < text.length; i++) {
        currentText += text[i];
        element.textContent = currentText;
        chat.scrollTop = chat.scrollHeight;

        // Задержка между символами (быстрая печать)
        await new Promise(resolve => setTimeout(resolve, 15));
      }
    }

    async function typeMarkdown(element, text) {
      const lines = text.split('\n');
      let html = '';

      for (let lineIndex = 0; lineIndex < lines.length; lineIndex++) {
        const line = lines[lineIndex];

        if (line.startsWith('## ')) {
          // Заголовок - печатаем посимвольно
          html += '<h2>';
          element.innerHTML = html;

          const headerText = line.substring(3);
          let currentHeader = '';
          for (let i = 0; i < headerText.length; i++) {
            currentHeader += headerText[i];
            element.innerHTML = html + escapeHtml(currentHeader);
            chat.scrollTop = chat.scrollHeight;
            await new Promise(resolve => setTimeout(resolve, 15));
          }

          html += escapeHtml(headerText) + '</h2>';
          element.innerHTML = html;
        } else if (line.trim() === '') {
          // Пустая строка
          html += '<br>';
          element.innerHTML = html;
        } else {
          // Обычный текст - печатаем посимвольно
          let currentLine = '';
          for (let i = 0; i < line.length; i++) {
            currentLine += line[i];
            element.innerHTML = html + escapeHtml(currentLine);
            chat.scrollTop = chat.scrollHeight;
            await new Promise(resolve => setTimeout(resolve, 15));
          }

          html += escapeHtml(line) + '<br>';
          element.innerHTML = html;
        }

        // Небольшая пауза между строками
        if (lineIndex < lines.length - 1) {
          await new Promise(resolve => setTimeout(resolve, 50));
        }
      }
    }

    function showLoading() {
      const msg = document.createElement('div');
      msg.className = 'message assistant';
      msg.id = 'loading-msg';

      const avatar = document.createElement('div');
      avatar.className = 'avatar';
      avatar.textContent = '🦆';

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

    function hideLoading() {
      const loadingMsg = document.getElementById('loading-msg');
      if (loadingMsg) loadingMsg.remove();
    }

    async function send() {
      if (isComplete) {
        alert('Сказочная утка уже создана! Нажмите "Начать заново" для нового диалога.');
        return;
      }

      const text = q.value.trim();
      if (!text) return;
      q.value = '';
      q.style.height = 'auto';
      q.disabled = true;
      sendBtn.disabled = true;

      add('me', text);
      showLoading();

      try {
        const res = await fetch('/api/chat', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ sessionId, message: text })
        });
        const data = await res.json();
        hideLoading();
        add('bot', data.reply, data.isComplete);

        if (data.isComplete) {
          isComplete = true;
          q.placeholder = 'Сказка готова! Нажмите "Начать заново"';
        }
      } catch (e) {
        hideLoading();
        add('bot', 'Ошибка: ' + e.message);
      }

      q.disabled = isComplete;
      sendBtn.disabled = isComplete;
      if (!isComplete) q.focus();
    }

    q.addEventListener('input', function () {
      this.style.height = 'auto';
      this.style.height = Math.min(this.scrollHeight, 200) + 'px';
    });

    q.addEventListener('keydown', e => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        send();
      }
    });
    sendBtn.addEventListener('click', send);

    async function resetChat() {
      if (!confirm('Начать новый диалог?')) return;

      try {
        await fetch('/api/reset', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ sessionId })
        });
        location.reload();
      } catch (error) {
        alert('Ошибка: ' + error.message);
      }
    }

    window.addEventListener('load', () => {
      add('bot', 'Привет! ✨ Я волшебный сказочник, и я помогу тебе создать описание твоей уникальной сказочной утки! 🦆\n\nДавай начнём: расскажи, какого цвета будут перья у твоей утки?');
      q.focus();
    });
  </script>
</body>
</html>
""".trimIndent()