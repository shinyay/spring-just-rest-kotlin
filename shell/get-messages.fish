#!/usr/bin/env fish

curl -X GET \
  http://localhost:8080/messages
  -H 'Content-Type: application/json'
