#!/usr/bin/env fish

mysql -uroot -proot -h127.0.0.1 -e"$argv[1]"
