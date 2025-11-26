import React, { createContext, useContext } from 'react';
import useKeyCloak from './UseKeyCloak.js';

const KeycloakContext = createContext(null);

export const KeycloakProvider = ({ children }) => {
  const { keycloak, authenticated, loading } = useKeyCloak();

  if (loading) {
    return <div>Loading authentication...</div>;
  }

  if (!keycloak) {
    return <div>Unable to initialize authentication</div>;
  }

  return (
    <KeycloakContext.Provider value={{ keycloak, authenticated }}>
      {children}
    </KeycloakContext.Provider>
  );
};

export const useKeycloakContext = () => {
  const context = useContext(KeycloakContext);
  if (!context) {
    throw new Error('useKeycloakContext must be used within KeycloakProvider');
  }
  return context;
};