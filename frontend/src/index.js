import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import { KeycloakProvider } from './KeyCloakContext';

ReactDOM.render(
  <KeycloakProvider>
    <App />
  </KeycloakProvider>,
  document.getElementById('root')
);