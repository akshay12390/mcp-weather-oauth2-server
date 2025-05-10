import requests

# Client ID and Secret
client_id = 'mcp-client'
client_secret = 'secret'

# Authorization code received from authorization server
authorization_code = 'dEP35W7nJWfVAy15_2bjW1Kh4ZLkIkRRP1M26-amzj8a6b7ADKGP9_bw8ji0lkTmnGuaSOjqMNXa9v5KvVxQ3uJRotwbLyOsLAhV0_2_sEpQQ_0v6P_k1Wx9YuJJXPeu'
# Code verifier that matches the code_challenge used in authorization request
code_verifier = 'gK6LM_mcyj9ak8klXPz-DEWXI2npfJXCY5We80-vex4'

# Token endpoint
token_url = 'http://localhost:8080/oauth2/token'

# Headers
headers = {
    'Content-Type': 'application/x-www-form-urlencoded'
}

# Request body
data = {
    'grant_type': 'authorization_code',
    'code': authorization_code,
    'redirect_uri': 'http://localhost:8080/display-code',
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