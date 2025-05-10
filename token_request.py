import requests
import os

# Server URL (default to localhost if not set)
server_url = os.getenv('SERVER_URL', 'http://localhost:8080')

# Client ID and Secret
client_id = 'mcp-client'
client_secret = 'secret'

# Authorization code received from authorization server
authorization_code = 'lwPylG8G1PreY40Oo9TccP3W_Gq2UVZaxgi98c0mb96u24oQBDVgTcoQZ8G5Q_c0s-0dg8RH2PkXi1NLhqMTuFRK6cL2dNbZF_qsdhKKbcnnO22fqki_mxewz084K9sU'
# Code verifier that matches the code_challenge used in authorization request
code_verifier = 'wdpU-6McmpZYqFVknPJLFPxI3UPx9M_AigMz0XtdheY'

# Token endpoint
token_url = f'{server_url}/oauth2/token'

# Headers
headers = {
    'Content-Type': 'application/x-www-form-urlencoded'
}

# Request body
data = {
    'grant_type': 'authorization_code',
    'code': authorization_code,
    'redirect_uri': f'{server_url}/display-code',
    'code_verifier': code_verifier,
    'client_id': client_id,
    'client_secret': client_secret  # Added client_secret for CLIENT_SECRET_POST authentication
}

# Make the token request
response = requests.post(token_url, headers=headers, data=data)

# Print the response
print(f"Status Code: {response.status_code}")
print("Response:")
print(response.text) 