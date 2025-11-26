import { useState, useEffect, useRef } from 'react'
import Keycloak from 'keycloak-js';

function useKeyCloak() {
  const [keycloak, setKeycloak] = useState(null);
  const [authenticated, setAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);
  const isInitialized = useRef(false);

  useEffect(() => {
    if (isInitialized.current) return;
    isInitialized.current = true;

    const initKeycloak = async () => {
      console.log("1. Starting Keycloak initialization...");
      
      const keycloakInstance = new Keycloak({
        url: 'http://localhost:8080',  // Remove trailing slash
        realm: 'wordle-realm',
        clientId: 'wordle-app'
      });

      // Set adapter explicitly
      keycloakInstance.enableLogging = true;

      console.log("2. Calling init...");
      console.log("3. Current URL:", window.location.href);
      
      try {
        const authenticated = await keycloakInstance.init({ 
          onLoad: 'check-sso',
          silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
          checkLoginIframe: false,
          pkceMethod: 'S256',
          responseMode: 'query'
        });
        
        console.log("4. Init SUCCESS. Authenticated:", authenticated);
        console.log("5. Token exists:", !!keycloakInstance.token);
        console.log("6. Authenticated property:", keycloakInstance.authenticated);
        
        setKeycloak(keycloakInstance);
        setAuthenticated(authenticated);
        setLoading(false);
        
        if (!authenticated) {
          console.log("7. Not authenticated, redirecting to login...");
          keycloakInstance.login();
        } else {
          console.log("7. User is authenticated!");
          console.log("8. Username:", keycloakInstance.tokenParsed?.preferred_username);
          console.log("9. UUID:", keycloakInstance.tokenParsed?.sub);
        }
      } catch (error) {
        console.error("4. Init FAILED");
        console.error("Full error object:", error);
        
        // Try to get more info
        if (error && typeof error === 'object') {
          Object.keys(error).forEach(key => {
            console.error(`Error.${key}:`, error[key]);
          });
        }
        
        setKeycloak(keycloakInstance);
        setLoading(false);
      }
    };

    initKeycloak();
  }, []);

  return { keycloak, authenticated, loading }
}

export default useKeyCloak