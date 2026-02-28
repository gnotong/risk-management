import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
    url: 'http://localhost:8080',
    realm: 'risk-realm',
    clientId: 'risk-frontend'
});

export default keycloak;
