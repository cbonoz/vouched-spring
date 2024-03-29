
# read files in data to dataframes
# http post to server

import os
import pandas as pd
import requests

VOUCH_SECRET = os.environ.get('VOUCH_SECRET')
VOUCH_URL = os.environ.get('VOUCH_URL')
print('secret', VOUCH_SECRET, VOUCH_URL)

endorse_df = pd.read_csv('data/endorsements.csv')

def convert_standard_spaced_words_to_camel_case(s):
    word = ''.join([word.capitalize() for word in s.split(' ')])
    return word[0].lower() + word[1:]


# http post to server
url = f"{VOUCH_URL}/admin/endorsements/upload"

# send user data
df   = endorse_df.rename(columns=convert_standard_spaced_words_to_camel_case)
# trim whitespace off all
df = df.apply(lambda x: x.str.strip() if x.dtype == "object" else x)
# filter empty cols
df = df.dropna(axis=1, how='all')
# convert keys in object list using convert function above
print('df', df)
# replace nan with empty string
df = df.fillna('')
# group rows with matching email
records = df.to_dict(orient='records')
print('records', records)
endorse_data = {e['ownerEmail']: [] for e in records}

for e in records:
    endorse_data[e['ownerEmail']].append(e)

print('data', endorse_data)




# add bearer token
headers = {
    'Authorization': f'Bearer {VOUCH_SECRET}',
    'X-Email': 'superuser@example.com'
}
response = requests.post(url, json=endorse_data, headers=headers)
print(response.text)






