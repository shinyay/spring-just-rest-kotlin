#!/usr/bin/env fish

curl -X PUT \
  http://localhost:8080/messages \
  -H 'Content-Type: application/json' \
  -d '{
	"title": "foo",
	"message": "var"
}'
