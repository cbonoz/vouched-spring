
# read files in data to dataframes
# http post to server

import os
import pandas as pd
import requests

VOUCH_SECRET = os.environ.get('VOUCH_SECRET')
VOUCH_URL = os.environ.get('VOUCH_URL')
print('secret', VOUCH_SECRET, VOUCH_URL)

user_df = pd.read_csv('data/users.csv')

def convert_standard_spaced_words_to_camel_case(s):
    word = ''.join([word.capitalize() for word in s.split(' ')])
    return word[0].lower() + word[1:]


# http post to server
url = f"{VOUCH_URL}/admin/users/upload"

# send user data
user_df   = user_df.rename(columns=convert_standard_spaced_words_to_camel_case)
user_df = user_df.apply(lambda x: x.str.strip() if x.dtype == "object" else x)
# filter empty cols
user_df = user_df.dropna(axis=1, how='all')
# convert keys in object list using convert function above
user_data = {u['email']: u for u in user_df.to_dict(orient='records')}
print('data', user_data)



# add bearer token
headers = {
    'Authorization': f'Bearer {VOUCH_SECRET}',
    'X-Email': 'superuser@example.com'
}

try:
    response = requests.post(url, json=user_data, headers=headers)
    print('response', response.text)
except Exception as e:
    print('error' ,e)






