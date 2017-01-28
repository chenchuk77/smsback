# SmsBack (refactoring of smsback-android)
#
################################################

SmsBack is a utility to respond to incoming SMS.
developed by chen alkabets chenchuk@gmail.com

1. System logic
---------------
Incoming sms is checked aginst a policy which mades of rules.
a rule has:
	- priority ( 1-99, where 1 is the HIGHEST priority )
	- matching condition:
		- by sender address
		- by specific content
	- action:
		- reply to number with same content
		- reply with different content
		- reply with timestamp
if rule match the incoming sms (by matching condition), an action will be taken and no more rules will be evaluated.
simply think of searching for the first match, and taking an action if we found ...




2. Rules evaluation
-------------------
Rules can be expresses as json using gson library.
the structure is simple and intuitive
	- when matchAddress is '-' means any address (like 'match-any')
	- when addTimestamp is '+' means to add timestamp (epoch unix time)
	- when replaceContent is '-' means dont replace, leave as is

{"addTimestamp":"+","matchAddress":"+972xx","matchContent":"-","priority":2,"replaceContent":"-","replyTo":"+973xx"}
{"addTimestamp":"+","matchAddress":"+972xx","matchContent":"ABC","priority":5,"replaceContent":"-","replyTo":"+555xx"}
{"addTimestamp":"-","matchAddress":"-","matchContent":"ABC","priority":7,"replaceContent":"a fixed msg","replyTo":"+666xx"}

given that we can say about the policy above :
	- incoming sms of "hello" from +972xx will send "hello-1454529559" to +973xx
		- because it match rule #2 ( the highest priority )
	- incoming sms of "ABC" from +6666 will send "a fixed msg" to +666xx
		- rule #2 and #5 doesnt match because rule #5 has 2 conditions
		- rule #7 match and will do the action




3. Command executer
-------------------
SmsBack provides 2 interfaces for executing commands
	- local console
	- remotely by sms

command structure:
		- cmd:get-deployed	returns the id's (priority of existing rules)
		- cmd:get:2		returns json representation of rule #2 (nice for copy/paste)
		- cmd:remove:3
		- cmd:clear		remove ALL rules !
		- cmd:add:{"addTimestamp":"+","matchAddress":"+972xx","matchContent":"-","priority":2,"replaceContent":"-","replyTo":"+973xx"}
		- 
local commands can be run via UI button or sent by an sms in the above format. in fact a console command translated internally into sms and get processed ...




4. SmsBack logging
------------------
SmsBack logs its activity to sd/smsback/log.txt
logfile can be deleted or sent to email from the UI





TODO: use logback / slf4j
TODO: sms command to send email
TODO: sms command to handle logfiles
TODO: add managers list
TODO: add security




