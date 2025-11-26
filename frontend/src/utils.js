export function showMessage(message, type = 'info') {
  // Remove any existing messages
  const existingMessage = document.querySelector('.message-toast');
  if (existingMessage) {
    existingMessage.remove();
  }

  // Create message element
  const messageEl = document.createElement('div');
  messageEl.className = `message-toast message-${type}`;
  messageEl.textContent = message;
  
  // Add to body
  document.body.appendChild(messageEl);

  // Animate in
  setTimeout(() => {
    messageEl.classList.add('show');
  }, 10);

  // Remove after 3 seconds
  setTimeout(() => {
    messageEl.classList.remove('show');
    setTimeout(() => {
      messageEl.remove();
    }, 300);
  }, 3000);
}