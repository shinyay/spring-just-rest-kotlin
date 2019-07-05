#!/usr/bin/env fish

curl -X POST \
  http://localhost:8080/messages/id_is \
  -H 'Content-Type: application/json' \
  -d '{
	"id": "12345678-e9d9-4d1e-ba79-01f8b8715ba9"
}'
