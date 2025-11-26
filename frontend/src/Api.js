// src/Api.js
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:9090/api/wordle';

export const api = {
  // startGame(playerId) -> POST /play?playerId=...
  async startGame(playerId) {
    const url = `${API_BASE_URL}/play${playerId ? `?playerId=${playerId}` : ''}`;
    const response = await fetch(url, { method: 'POST' });

    // try to parse JSON error body if available
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      throw new Error(data.error || 'Failed to start game');
    }

    return response.json();
  },

  // submitGuess(guess, gameId) -> POST /sendFeedback?guess=...&gameId=...
  async submitGuess(guess, gameId) {
    if (!gameId) {
      // helpful guard – match old code expectation where gameId is required
      throw new Error('Missing gameId for submitGuess');
    }

    const url = `${API_BASE_URL}/sendFeedback?guess=${encodeURIComponent(guess)}&gameId=${encodeURIComponent(gameId)}`;
    const response = await fetch(url, { method: 'POST' });

    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
      throw new Error(data.error || 'Failed to submit guess');
    }

    return data;
  },
};