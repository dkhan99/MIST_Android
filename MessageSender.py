
'''
***https://firebase.google.com/docs/cloud-messaging/android/send-multiple

Sample script that can be used to send notifications. Script sends post request to google's
	firebase server to send notifications. This works for when the app is in the foreground as 
	well as the background., and the code-handling can be done in the same "onMessageReceived" method.

To test on your device: 
	1.) Change the to field to your individual user's user_token. 
	2.) Run with python MessageSender.py	

*payload needs to have fields: "to" (list of user_token(s)), "data"	
*headers must contain fields: 'authorization' (this is the server key for the app), "content-type"
'''

import requests
import json
import time

#milliseconds since Unix epoch -> in accuracy of a second
millis = int(round(time.time() * 1000))

url = 'https://fcm.googleapis.com/fcm/send'
payload = {
		"to":"c3KQTieWX-g:APA91bHDNSTP2wGFycE9zYkPskRoS12fjhemrUYXVe0dkIDccAcV0VoJ_7tjaRMquzYeYKokY5o2JnmdoZStKPBcBby5mOt3xBnLLpTBqpfINWAzd86u6_XxAcGb5BPyRGZ4FVa4HlxD",
    "Content-Type": "application/json",
		"data": {
		        "title": "Ok Les goo",
		        "body" : "This is the body and shouldnt show up on main page",
		        "time" : millis
		     }
}
# Adding header as parameters -> being sent in payload
headers = {"Authorization":"key=AAAAMPaimYs:APA91bFNaUuMoj5a7hNj-JhIQXIR6SOtygzO7n1JTQxm5xd2fGHFsoXaDZV0J3OP_DKq-GUTIgbYsAa8pGDm0245BfOGLdp9-WMnYAkvn_c8Y80mzaZuzK0h56E_tzt98GfswtDiKCWpfarRNGB2agGGJ-yBE4hULg","Content-Type":"application/json"}
r = requests.post(url, data=json.dumps(payload), headers=headers)
print(r.content)


'''
Topics To send to:
	-coach
	-competitor
	-guest
	-all
	-school_team_name
	-spirit_team_name

'''


def sendToTopic():
	pass