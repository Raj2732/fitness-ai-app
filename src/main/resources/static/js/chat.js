// Global variables
let currentUserId = 'user_' + Math.random().toString(36).substr(2, 9);
let currentMessageId = null;
let conversationHistory = [];
let hasRatedThisSession = false; // Track if user has rated in this session

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    loadDailyTip();
    loadConversationHistory();

    // Show rating modal once when page loads (only if not rated yet)
    setTimeout(() => {
        if (!hasRatedThisSession) {
            document.getElementById('ratingModal').style.display = 'block';
        }
    }, 3000); // Show after 3 seconds
});

// Message handling
async function sendMessage() {
    const input = document.getElementById('userInput');
    const message = input.value.trim();

    if (!message) return;

    addMessageToChat('user', message);
    input.value = '';

    showTypingIndicator();

    try {
        const response = await fetch('/api/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId: currentUserId,
                message: message
            })
        });

        const data = await response.json();

        removeTypingIndicator();
        addMessageToChat('ai', data.message);

        conversationHistory.push({
            user: message,
            ai: data.message,
            timestamp: new Date()
        });

        // NO RATING POPUP AFTER MESSAGES

    } catch (error) {
        console.error('Error:', error);
        removeTypingIndicator();
        addMessageToChat('ai', 'Sorry, I encountered an error. Please try again.');
    }
}

function sendSuggestion(text) {
    document.getElementById('userInput').value = text;
    sendMessage();
}

function addMessageToChat(type, content) {
    const messagesDiv = document.getElementById('chatMessages');
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}-message`;

    const avatar = type === 'user' ? '👤' : '🤖';

    messageDiv.innerHTML = `
        <div class="avatar">${avatar}</div>
        <div class="message-content">
            <p>${content.replace(/\n/g, '<br>')}</p>
        </div>
    `;

    messagesDiv.appendChild(messageDiv);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

function showTypingIndicator() {
    const messagesDiv = document.getElementById('chatMessages');
    const indicator = document.createElement('div');
    indicator.className = 'message ai-message';
    indicator.id = 'typingIndicator';
    indicator.innerHTML = `
        <div class="avatar">🤖</div>
        <div class="typing-indicator">
            <span></span>
            <span></span>
            <span></span>
        </div>
    `;
    messagesDiv.appendChild(indicator);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

function removeTypingIndicator() {
    const indicator = document.getElementById('typingIndicator');
    if (indicator) indicator.remove();
}

function autoResize(textarea) {
    textarea.style.height = 'auto';
    textarea.style.height = textarea.scrollHeight + 'px';
}

function handleKeyPress(event) {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        sendMessage();
    }
}

// Profile management
function showProfileModal() {
    document.getElementById('profileModal').style.display = 'block';
}

function closeProfileModal() {
    document.getElementById('profileModal').style.display = 'none';
}

function saveProfile() {
    const profile = {
        userId: currentUserId,
        name: document.getElementById('name').value,
        age: document.getElementById('age').value,
        weight: document.getElementById('weight').value,
        height: document.getElementById('height').value,
        goal: document.getElementById('goal').value,
        level: document.querySelector('input[name="level"]:checked')?.value,
        conditions: document.getElementById('conditions').value
    };

    // Save to backend (you'll need to implement this endpoint)
    fetch('/api/profile', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(profile)
    })
    .then(response => {
        if (response.ok) {
            alert('Profile saved successfully!');
            closeProfileModal();
        }
    })
    .catch(error => {
        console.error('Error saving profile:', error);
        alert('Error saving profile. Please try again.');
    });
}

// Rating system - ONLY SHOWS ONCE PER SESSION
function closeRatingModal() {
    document.getElementById('ratingModal').style.display = 'none';
}

function rateResponse(rating) {
    // Send rating to backend
    fetch('/api/rate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            messageId: Date.now(), // You should use actual message ID
            rating: rating
        })
    })
    .then(response => response.text())
    .then(data => {
        console.log('Rating saved:', data);
        addMessageToChat('ai', 'Thank you for your feedback! It helps me improve.');
        hasRatedThisSession = true; // Mark as rated
    })
    .catch(error => {
        console.error('Error saving rating:', error);
    })
    .finally(() => {
        closeRatingModal();
    });
}

// Daily tip
function loadDailyTip() {
    const tips = [
        "Drink water before, during, and after your workout.",
        "Get at least 7-8 hours of sleep for optimal recovery.",
        "Warm up for 5-10 minutes before exercising.",
        "Include protein in every meal for muscle maintenance.",
        "Take rest days to prevent injury and burnout.",
        "Track your progress to stay motivated.",
        "Mix cardio and strength training for best results.",
        "Stretch after workouts to improve flexibility.",
        "Eat a balanced meal within 2 hours after exercise.",
        "Listen to your body - rest when needed."
    ];

    const randomTip = tips[Math.floor(Math.random() * tips.length)];
    document.getElementById('dailyTip').textContent = randomTip;
}

function loadConversationHistory() {
    const saved = localStorage.getItem('conversationHistory');
    if (saved) {
        conversationHistory = JSON.parse(saved);
    }
}

// Feature buttons
function showWorkoutPlanner() {
    sendSuggestion("I need a personalized workout plan");
}

function showNutritionGuide() {
    sendSuggestion("Give me nutrition advice for my goals");
}

function showProgress() {
    sendSuggestion("How can I track my fitness progress?");
}

function showExerciseLibrary() {
    sendSuggestion("Show me exercises for different muscle groups");
}