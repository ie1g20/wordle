// NEW App.js
import React, { useState } from 'react';
import { useKeycloakContext } from './KeyCloakContext';
import Home from './Home';
import Wordle from './Wordle';
import './App.css';

function App() {
  const { keycloak, authenticated } = useKeycloakContext();
  const [currentView, setCurrentView] = useState('home');
  const [gameData, setGameData] = useState(null);

  const handleStartGame = (data) => {
    setGameData(data);
    setCurrentView('game');
  };

  const handleBack = () => {
    setGameData(null);
    setCurrentView('home');
  };

  if (!authenticated) {
    return (
      <div className="loading">
        Redirecting to login...
      </div>
    );
  }

  return (
    <div className="app">
      {currentView === 'home' ? (
        <Home keycloak={keycloak} onStartGame={handleStartGame} />
      ) : (
        <Wordle gameData={gameData} onBack={handleBack} />
      )}
    </div>
  );
}

export default App;