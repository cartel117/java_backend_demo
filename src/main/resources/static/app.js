// 使用相對路徑，自動適應不同環境（localhost 或 AWS）
const API_BASE_URL = '/api/auth';

// 切換到註冊表單
function showRegister() {
    document.getElementById('loginForm').classList.add('hidden');
    document.getElementById('registerForm').classList.remove('hidden');
    document.getElementById('welcomeScreen').classList.add('hidden');
    clearMessages();
}

// 切換到登入表單
function showLogin() {
    document.getElementById('registerForm').classList.add('hidden');
    document.getElementById('loginForm').classList.remove('hidden');
    document.getElementById('welcomeScreen').classList.add('hidden');
    clearMessages();
}

// 清除所有訊息
function clearMessages() {
    document.getElementById('loginMessage').innerHTML = '';
    document.getElementById('loginMessage').className = 'message';
    document.getElementById('registerMessage').innerHTML = '';
    document.getElementById('registerMessage').className = 'message';
}

// 顯示訊息
function showMessage(elementId, message, isError = false) {
    const messageElement = document.getElementById(elementId);
    messageElement.textContent = message;
    messageElement.className = isError ? 'message error' : 'message success';
}

// 處理註冊
async function handleRegister(event) {
    event.preventDefault();
    
    const username = document.getElementById('registerUsername').value;
    const email = document.getElementById('registerEmail').value;
    const password = document.getElementById('registerPassword').value;

    try {
        const response = await fetch(`${API_BASE_URL}/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, email, password })
        });

        const data = await response.json();

        if (response.ok) {
            showMessage('registerMessage', '註冊成功！請登入。', false);
            setTimeout(() => {
                showLogin();
                document.getElementById('loginUsername').value = username;
            }, 1500);
        } else {
            showMessage('registerMessage', data.message || '註冊失敗，請稍後再試。', true);
        }
    } catch (error) {
        showMessage('registerMessage', '無法連接到服務器，請檢查後端是否運行。', true);
        console.error('Register error:', error);
    }
}

// 處理登入
async function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    try {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok) {
            showMessage('loginMessage', '登入成功！', false);
            localStorage.setItem('username', username);
            setTimeout(() => {
                showWelcome(username);
            }, 1000);
        } else {
            showMessage('loginMessage', data.message || '登入失敗，請檢查用戶名和密碼。', true);
        }
    } catch (error) {
        showMessage('loginMessage', '無法連接到服務器，請檢查後端是否運行。', true);
        console.error('Login error:', error);
    }
}

// 顯示歡迎畫面
function showWelcome(username) {
    document.getElementById('loginForm').classList.add('hidden');
    document.getElementById('registerForm').classList.add('hidden');
    document.getElementById('welcomeScreen').classList.remove('hidden');
    document.getElementById('welcomeUsername').textContent = username;
}

// 處理登出
function handleLogout() {
    localStorage.removeItem('username');
    showLogin();
    document.getElementById('loginUsername').value = '';
    document.getElementById('loginPassword').value = '';
}

// 頁面載入時檢查是否已登入
window.addEventListener('DOMContentLoaded', () => {
    const savedUsername = localStorage.getItem('username');
    if (savedUsername) {
        showWelcome(savedUsername);
    }
});
