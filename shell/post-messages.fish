#!/usr/bin/env fish

curl -X POST \
  http://localhost:8080/messages \
  -H 'Content-Type: application/json' \
  -d '{
	"title": "foo",
	"message": "var"
}'
