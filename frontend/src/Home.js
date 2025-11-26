import React, { useState } from 'react';
import { api } from './Api.js';
import { showMessage } from './utils.js';
import './Home.css';

function Home({ keycloak, onStartGame }) {
  const [loading, setLoading] = useState(false);

  const startGame = async () => {
    setLoading(true);
    try {
      // Use Keycloak's user ID (UUID)
      const playerId = keycloak?.tokenParsed?.sub;
      const data = await api.startGame(playerId);
      
      onStartGame(data);
    } catch (error) {
      console.error('Error starting game:', error);
      showMessage('Error starting game. Make sure the server is running.', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="welcome-screen">
        <h1>WORDLE</h1>
        <p className="subtitle">Guess today's word in 6 tries</p>
        <button 
          className="play-button" 
          onClick={startGame}
          disabled={loading}
        >
          {loading ? 'STARTING...' : 'PLAY'}
        </button>
        
        <div style={{ marginTop: '30px', fontSize: '0.9rem', color: '#666' }}>
          <p>Playing as: <strong style={{ color: '#fff' }}>
            {keycloak?.tokenParsed?.preferred_username}
          </strong></p>
          <button
            onClick={() => keycloak.logout()}
            style={{
              marginTop: '10px',
              padding: '8px 16px',
              backgroundColor: '#dc3545',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '14px'
            }}
          >
            Logout
          </button>
        </div>
      </div>
    </div>
  );
}

export default Home;