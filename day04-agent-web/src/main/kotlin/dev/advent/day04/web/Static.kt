package dev.advent.day04.web

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
  <title>🔥 AI Advent — День 4: Температура</title>
  <link rel="icon" type="image/svg+xml" href="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' width='64' height='64' viewBox='0 0 64 64'><defs><linearGradient id='grad' x1='0%25' y1='0%25' x2='100%25' y2='100%25'><stop offset='0%25' style='stop-color:%23f093fb'/><stop offset='100%25' style='stop-color:%234facfe'/></linearGradient></defs><rect x='12' y='12' width='40' height='40' rx='8' fill='url(%23grad)'/><text x='32' y='42' font-size='28' text-anchor='middle' fill='white'>🌡️</text></svg>"/>
  <style>
    *{box-sizing:border-box}
    body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;margin:0;padding:0;height:100vh;display:flex;flex-direction:column;background:#1a1a2e;overflow:hidden}
    .header{background:linear-gradient(135deg,#f093fb 0%,#4facfe 100%);color:#fff;padding:20px;text-align:center;box-shadow:0 4px 20px rgba(240,147,251,.4);flex-shrink:0;position:relative;overflow:hidden;animation:slideDown 0.6s cubic-bezier(0.34,1.56,0.64,1)}
    @keyframes slideDown{from{transform:translateY(-100%);opacity:0}to{transform:translateY(0);opacity:1}}
    .header::before{content:'';position:absolute;top:-50%;left:-50%;width:200%;height:200%;background:radial-gradient(circle,rgba(255,255,255,0.15) 0%,transparent 70%);animation:pulse 4s ease-in-out infinite}
    @keyframes pulse{0%,100%{transform:scale(1);opacity:0.5}50%{transform:scale(1.1);opacity:0.8}}
    .header h1{margin:0;font-size:24px;font-weight:600;position:relative;z-index:1;display:flex;align-items:center;justify-content:center;gap:10px}
    .header p{margin:8px 0 0;font-size:14px;opacity:0.95;position:relative;z-index:1;font-weight:500}
    .temp-badge{background:rgba(255,255,255,0.25);padding:4px 12px;border-radius:12px;font-size:12px;font-weight:700;letter-spacing:1px}
    .main-content{flex:1;overflow:hidden;display:flex;gap:0}
    .chat-section{flex:1;display:flex;flex-direction:column;border-right:1px solid #2d2d44}
    .chat-container{flex:1;overflow-y:auto;padding:20px;display:flex;flex-direction:column;gap:16px}
    .chat-container::-webkit-scrollbar{width:8px}
    .chat-container::-webkit-scrollbar-track{background:transparent}
    .chat-container::-webkit-scrollbar-thumb{background:#2d2d44;border-radius:4px;transition:background 0.3s}
    .chat-container::-webkit-scrollbar-thumb:hover{background:#3d3d54}
    .comparison-panel{flex:1;background:#16213e;padding:20px;overflow-y:auto;display:flex;flex-direction:column}
    .comparison-panel::-webkit-scrollbar{width:8px}
    .comparison-panel::-webkit-scrollbar-track{background:transparent}
    .comparison-panel::-webkit-scrollbar-thumb{background:#2d2d44;border-radius:4px}
    .panel-header{color:#4facfe;font-size:14px;font-weight:600;margin-bottom:12px;display:flex;align-items:center;gap:8px}
    .temp-results{display:flex;flex-direction:column;gap:16px}
    .temp-result{background:#0f1419;border:1px solid #2d2d44;border-radius:8px;padding:16px;box-shadow:0 4px 12px rgba(0,0,0,.3)}
    .temp-label{font-size:12px;font-weight:700;color:#f093fb;margin-bottom:8px;display:flex;align-items:center;gap:8px}
    .temp-value{font-size:18px;font-weight:700;color:#4facfe}
    .temp-response{color:#e0e0e0;margin-top:8px;line-height:1.6;font-size:14px;white-space:pre-wrap}
    .empty-state{color:#6c757d;text-align:center;padding:40px;font-style:italic}
    .input-wrapper{background:#16213e;padding:20px 20px 24px;border-top:1px solid #2d2d44;flex-shrink:0;animation:slideUp 0.6s cubic-bezier(0.34,1.56,0.64,1)}
    @keyframes slideUp{from{transform:translateY(100%);opacity:0}to{transform:translateY(0);opacity:1}}
    .temp-controls{display:flex;gap:16px;margin-bottom:18px;flex-wrap:wrap;justify-content:center}
    .temp-button{background:#0f1419;color:#e0e0e0;border:2px solid #2d2d44;padding:10px 20px;border-radius:8px;font-size:14px;font-weight:600;cursor:pointer;transition:all 0.3s cubic-bezier(0.4,0,0.2,1);position:relative}
    .temp-button:hover{border-color:#4facfe;transform:translateY(-2px);box-shadow:0 4px 12px rgba(79,172,254,.3)}
    .temp-button.active{background:linear-gradient(135deg,#f093fb 0%,#4facfe 100%);color:#fff;border-color:transparent;box-shadow:0 4px 16px rgba(240,147,251,.5)}
    .input-container{max-width:100%;margin:0 auto;display:flex;gap:12px;align-items:stretch}
    textarea{flex:1;background:#0f1419;border:1px solid #2d2d44;color:#e0e0e0;padding:12px 16px;border-radius:8px;font-size:15px;outline:none;transition:all 0.3s cubic-bezier(0.4,0,0.2,1);font-family:inherit;resize:none;min-height:48px;max-height:200px;overflow-y:auto;line-height:1.5}
    textarea:focus{border-color:#4facfe;box-shadow:0 0 0 3px rgba(79,172,254,.2),0 0 20px rgba(79,172,254,.15);transform:translateY(-1px)}
    textarea::placeholder{color:#6c757d}
    textarea::-webkit-scrollbar{width:8px}
    textarea::-webkit-scrollbar-track{background:transparent;margin:4px}
    textarea::-webkit-scrollbar-thumb{background:#2d2d44;border-radius:4px}
    button{background:linear-gradient(135deg,#f093fb 0%,#4facfe 100%);color:#fff;border:none;padding:14px 24px;border-radius:8px;font-size:15px;font-weight:600;cursor:pointer;transition:all 0.3s cubic-bezier(0.4,0,0.2,1);position:relative;overflow:hidden;white-space:nowrap}
    button::before{content:'';position:absolute;top:50%;left:50%;width:0;height:0;border-radius:50%;background:rgba(255,255,255,0.3);transform:translate(-50%,-50%);transition:width 0.6s,height 0.6s}
    button:hover::before{width:300px;height:300px}
    button:hover{transform:translateY(-2px);box-shadow:0 8px 24px rgba(240,147,251,.5)}
    button:active{transform:translateY(0);box-shadow:0 4px 12px rgba(240,147,251,.3)}
    button:disabled{opacity:0.6;cursor:not-allowed;transform:none}
    button:disabled::before{display:none}
    .message{max-width:100%;width:100%;display:flex;gap:12px;animation:messageSlideIn 0.5s cubic-bezier(0.34,1.56,0.64,1)}
    @keyframes messageSlideIn{from{opacity:0;transform:translateY(30px) scale(0.9)}to{opacity:1;transform:translateY(0) scale(1)}}
    .message.user{flex-direction:row-reverse}
    .avatar{width:36px;height:36px;border-radius:50%;flex-shrink:0;display:flex;align-items:center;justify-content:center;font-size:18px;font-weight:600;animation:avatarBounce 0.6s cubic-bezier(0.68,-0.55,0.265,1.55);box-shadow:0 4px 12px rgba(0,0,0,.3)}
    @keyframes avatarBounce{0%{transform:scale(0) rotate(-180deg)}60%{transform:scale(1.2) rotate(20deg)}100%{transform:scale(1) rotate(0)}}
    .message.user .avatar{background:linear-gradient(135deg,#f093fb 0%,#4facfe 100%);color:#fff}
    .message.bot .avatar{background:linear-gradient(135deg,#00d2ff 0%,#3a7bd5 100%);color:#fff}
    .bubble{background:#0f1419;color:#e0e0e0;padding:12px 16px;border-radius:12px;word-wrap:break-word;white-space:pre-wrap;line-height:1.6;box-shadow:0 2px 8px rgba(0,0,0,.2);position:relative;animation:bubblePop 0.4s cubic-bezier(0.68,-0.55,0.265,1.55) 0.1s backwards;border:1px solid #2d2d44;flex:1;font-size:14px}
    @keyframes bubblePop{0%{transform:scale(0);opacity:0}70%{transform:scale(1.05)}100%{transform:scale(1);opacity:1}}
    .message.user .bubble{background:linear-gradient(135deg,#f093fb 0%,#4facfe 100%);color:#fff;border:none}
    .loading{display:flex;gap:6px;padding:8px 0}
    .loading-dot{width:10px;height:10px;border-radius:50%;background:linear-gradient(135deg,#f093fb 0%,#4facfe 100%);animation:loadingBounce 1.4s infinite ease-in-out;box-shadow:0 2px 8px rgba(240,147,251,.4)}
    .loading-dot:nth-child(1){animation-delay:-0.32s}
    .loading-dot:nth-child(2){animation-delay:-0.16s}
    .loading-dot:nth-child(3){animation-delay:0s}
    @keyframes loadingBounce{0%,80%,100%{transform:scale(0.6) translateY(0);opacity:0.5}40%{transform:scale(1) translateY(-10px);opacity:1}}
    @media (max-width: 1024px){
      .main-content{flex-direction:column}
      .chat-section{border-right:none;border-bottom:1px solid #2d2d44}
      .comparison-panel{max-height:300px}
    }
    @media (max-width: 768px){
      .header h1{font-size:20px}
      .header p{font-size:12px}
      .temp-controls{gap:8px}
      .temp-button{padding:8px 16px;font-size:13px}
    }
  </style>
</head>
<body>
  <div class="header">
    <h1>🔥 AI Advent — День 4 <span class="temp-badge">ТЕМПЕРАТУРА 🌡️</span></h1>
    <p>Эксперименты с температурой • Сравнение точности и креативности</p>
  </div>

  <div class="main-content">
    <div class="chat-section">
      <div class="chat-container" id="chat"></div>
      <div class="input-wrapper">
        <div class="temp-controls">
          <button class="temp-button active" data-temp="0">🧊 Температура 0.0</button>
          <button class="temp-button" data-temp="0.7">🔥 Температура 0.7</button>
          <button class="temp-button" data-temp="1.5">🌋 Температура 1.5</button>
          <button class="temp-button" data-temp="2.0">☀️ Температура 2.0</button>
        </div>
        <div class="input-container">
          <textarea id="q" placeholder="Что интересует…" rows="1"></textarea>
          <button id="send">Отправить</button>
        </div>
      </div>
    </div>

    <div class="comparison-panel">
      <div class="panel-header">📊 Сравнение ответов</div>
      <div class="temp-results" id="results">
        <div class="empty-state">Ответы появятся после отправки сообщения</div>
      </div>
    </div>
  </div>

  <script>
    const chat = document.getElementById('chat');
    const q = document.getElementById('q');
    const sendBtn = document.getElementById('send');
    const results = document.getElementById('results');
    const tempButtons = document.querySelectorAll('.temp-button');

    let selectedTemp = 0;

    tempButtons.forEach(btn => {
      btn.addEventListener('click', () => {
        tempButtons.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        selectedTemp = parseFloat(btn.dataset.temp);
      });
    });

    function add(role, text) {
      const msg = document.createElement('div');
      msg.className = 'message ' + (role === 'me' ? 'user' : 'bot');

      const avatar = document.createElement('div');
      avatar.className = 'avatar';
      avatar.textContent = role === 'me' ? '👤' : '🤖';

      const bubble = document.createElement('div');
      bubble.className = 'bubble';
      bubble.textContent = text;

      msg.appendChild(avatar);
      msg.appendChild(bubble);
      chat.appendChild(msg);
      chat.scrollTop = chat.scrollHeight;
    }

    function showLoading() {
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

    function hideLoading() {
      const loadingMsg = document.getElementById('loading-msg');
      if (loadingMsg) loadingMsg.remove();
    }

    function updateResults(temp, response) {
      // Clear empty state on first result
      if (results.querySelector('.empty-state')) {
        results.innerHTML = '';
      }

      const tempIcons = {'0': '🧊', '0.7': '🔥', '1.5': '🌋', '2': '☀️'};
      const tempLabels = {'0': 'Температура 0.0 (Детерминированная)', '0.7': 'Температура 0.7 (Сбалансированная)', '1.5': 'Температура 1.5 (Креативная)', '2': 'Температура 2.0 (Максимально креативная)'};

      const resultDiv = document.createElement('div');
      resultDiv.className = 'temp-result';
      resultDiv.innerHTML = '<div class="temp-label"><span>' + tempIcons[temp.toString()] + '</span><span>' + tempLabels[temp.toString()] + '</span></div><div class="temp-response">' + response + '</div>';

      results.appendChild(resultDiv);

      // Smooth scroll animation
      const comparisonPanel = results.parentElement;
      comparisonPanel.scrollTo({
        top: comparisonPanel.scrollHeight,
        behavior: 'smooth'
      });
    }

    async function send() {
      const text = q.value.trim();
      if (!text) return;
      q.value = '';
      q.style.height = 'auto';
      q.disabled = true;
      sendBtn.disabled = true;
      tempButtons.forEach(btn => btn.disabled = true);

      add('me', text);

      // Send 4 times for selected temperature
      for (let i = 0; i < 4; i++) {
        showLoading();

        try {
          const res = await fetch('/api/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message: text, temperature: selectedTemp })
          });
          const data = await res.json();
          hideLoading();

          if (i === 0) {
            add('bot', data.reply);
          }

          updateResults(selectedTemp, data.reply);
        } catch (e) {
          hideLoading();
          if (i === 0) {
            add('bot', 'Ошибка: ' + e.message);
          }
        }
      }

      q.disabled = false;
      sendBtn.disabled = false;
      tempButtons.forEach(btn => btn.disabled = false);
      q.focus();
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

    window.addEventListener('load', () => q.focus());
  </script>
</body>
</html>
"""